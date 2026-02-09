import { AppUser } from "@app/model/AppUser";
import React, { useEffect, useState } from "react";
import { useSession } from "./AuthContext";
import { useAxios } from "./AxiosContext";

export const CurrentUserContext = React.createContext<{
  currentUser: AppUser;
  isLoading: boolean;
}>({
  currentUser: null,
  isLoading: true,
});

// This hook can be used to access the user info.
export function useCurrentUser() {
  const value = React.useContext(CurrentUserContext);
  if (process.env.NODE_ENV !== "production") {
    if (!value) {
      throw new Error("useSession must be wrapped in a <SessionProvider />");
    }
  }

  return value;
}

export function CurrentUserProvider(props: React.PropsWithChildren) {
  const { authState, isLoading: authLoading } = useSession();
  const [ isLoading, setIsLoading ] = useState(true);
  const [currentUser, setCurrentUser] = useState<AppUser>(null);
  const { authAxios } = useAxios();

  // If accessToken is present, fetch the user info.
  useEffect(() => {
    console.log("Auth state changed", !!authState?.accessToken, authLoading);
    if (authLoading) {
      setIsLoading(true);
      return;
    }
    if (authState?.accessToken) {
        // Fetch user info
        setIsLoading(true);
        console.log("Fetching user info");
        authAxios.get<AppUser>("/users/me", {
          timeout: 10000
        }).then((res) => {
          console.log("User info", res.data);
          setCurrentUser(res.data);
        }).catch((e) => {
          console.error(e);
          console.log("Clearing user info");
          setCurrentUser(null);
        }).finally(() => {
          setIsLoading(false);
        });
    } else {
      console.log("Clearing user info");
      setIsLoading(false);
      setCurrentUser(null);
    }
  }, [authState?.accessToken, authLoading]);

  return (
    <CurrentUserContext.Provider
      value={{
        currentUser,
        isLoading,
      }}
    >
      {props.children}
    </CurrentUserContext.Provider>
  );
}
