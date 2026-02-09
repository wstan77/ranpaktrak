import * as SecureStore from "expo-secure-store";
import * as React from "react";
import { Platform } from "react-native";
import { AuthState } from "../service/contexts/AuthContext";
import { CustomerData } from "../model/CustomerData";
import { ScanData } from "@app/model/ScanData";
import { STORAGE_KEYS } from "@app/service/helpers/storage";
import { AccountData } from "@app/model/AccountData";

export interface StorageSchema {
  [STORAGE_KEYS.accessToken]: AuthState;
  [STORAGE_KEYS.uuid]: string;
  [STORAGE_KEYS.selectedRegion]: string;
  [STORAGE_KEYS.selectedCustomer]: CustomerData;
  [STORAGE_KEYS.scans]: ScanData[];
  [STORAGE_KEYS.lastSync]: string;
  [STORAGE_KEYS.accounts]: AccountData[];
}

type UseStateHook<T> = [[boolean, T | null], (value: T | null) => void];

function useAsyncState<T>(
  initialValue: [boolean, T | null] = [true, null]
): UseStateHook<T> {
  return React.useReducer(
    (
      state: [boolean, T | null],
      action: T | null = null
    ): [boolean, T | null] => [false, action],
    initialValue
  ) as UseStateHook<T>;
}

export async function setStorageItemAsync(key: string, value: string | null) {
  if (Platform.OS === "web") {
    try {
      if (value === null) {
        localStorage.removeItem(key);
      } else {
        localStorage.setItem(key, value);
      }
    } catch (e) {
      console.error("Local storage is unavailable:", e);
    }
  } else {
    if (value == null) {
      await SecureStore.deleteItemAsync(key);
    } else {
      await SecureStore.setItemAsync(key, value);
    }
  }
}

export function useStorageState<K extends keyof StorageSchema>(
  key: K
): UseStateHook<StorageSchema[K]> {
  // Public
  const [state, setState] = useAsyncState<StorageSchema[K]>();
  let val = null;
  if (Platform.OS != "web") val = SecureStore.getItem(key);
  else val = localStorage.getItem(key);

  // Get
  React.useEffect(() => {
    if (Platform.OS === "web") {
      try {
        if (typeof localStorage !== "undefined") {
          setState(JSON.parse(localStorage.getItem(key)));
        }
      } catch (e) {
        console.error("Local storage is unavailable:", e);
      }
    } else {
      SecureStore.getItemAsync(key).then((value) => {
        setState(JSON.parse(value));
      });
    }
  }, [key, val]);

  // Set
  const setValue = React.useCallback(
    (value: StorageSchema[K] | null) => {
      setState(value);
      setStorageItemAsync(key, value != null ? JSON.stringify(value) : null);
    },
    [key]
  );

  return [state, setValue];
}

export function useStorageStateSimple<K extends keyof StorageSchema>(key: K) {
  const [[isLoading, state]] = useStorageState(key);
  if (!isLoading) {
    return state;
  }
  return null;
}
