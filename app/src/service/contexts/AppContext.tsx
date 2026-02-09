import { useStorageState } from "@app/hooks";
import { createContext, useState } from "react";
import { STORAGE_KEYS } from "../helpers/storage";
import { ScanData } from "@app/model/ScanData";
import { AccountData } from "@app/model/AccountData";

interface AppState {
  appLoading: boolean;
  setAppLoading: (value: boolean) => void;
  setRanpakScans: (value: ScanData[]) => void;
  ranpakScans: ScanData[];
  ranpakAccounts: AccountData[];
  setRanpakAccounts: (value: AccountData[]) => void;
}

export const AppContext = createContext<AppState>({
  appLoading: false,
  setAppLoading: () => null,
  setRanpakScans: () => null,
  ranpakScans: [],
  setRanpakAccounts: () => null,
  ranpakAccounts: [],
});

export default function AppProvider({ children }) {
  const [appLoading, setAppLoading] = useState<boolean>(false);

  const [[, ranpakScans], setRanpakScans] = useStorageState(STORAGE_KEYS.scans);
  const [[, ranpakAccounts], setRanpakAccounts] = useStorageState(
    STORAGE_KEYS.accounts
  );

  return (
    <AppContext.Provider
      value={{
        appLoading,
        setAppLoading,
        setRanpakScans,
        ranpakScans,
        setRanpakAccounts,
        ranpakAccounts,
      }}
    >
      {children}
    </AppContext.Provider>
  );
}
