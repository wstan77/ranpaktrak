import React from "react";
import { useStorageState } from "../../hooks/useStorageState";
import { STORAGE_KEYS } from "../helpers/storage";

export type AuthState = {
  accessToken: string | null;
  refreshToken: string | null;
};

export const AuthContext = React.createContext<{
  signOut: () => Promise<void>;
  setAuthState: (authState: AuthState) => void;
  authState: AuthState;
  isAuthenticated: boolean;
  isLoading: boolean;
}>({
  signOut: async () => null,
  setAuthState: () => null,
  authState: {
    accessToken: null,
    refreshToken: null,
  },
  isAuthenticated: false,
  isLoading: false,
});

// This hook can be used to access the user info.
export function useSession() {
  const value = React.useContext(AuthContext);
  if (process.env.NODE_ENV !== "production") {
    if (!value) {
      throw new Error("useSession must be wrapped in a <SessionProvider />");
    }
  }

  return value;
}

export function SessionProvider(props: React.PropsWithChildren) {
  const [[isLoading, authState], setAuthState] = useStorageState(
    STORAGE_KEYS.accessToken
  );

  return (
    <AuthContext.Provider
      value={{
        signOut: async () => {
          setAuthState({
            accessToken: null,
            refreshToken: null,
          });
        },
        authState,
        setAuthState,
        isAuthenticated: !!authState?.accessToken,
        isLoading,
      }}
    >
      {props.children}
    </AuthContext.Provider>
  );
}
