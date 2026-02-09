import React, { createContext, useContext } from "react";
import axios, { AxiosInstance } from "axios";
import createAuthRefreshInterceptor from "axios-auth-refresh";
import { useSession } from "@app/service/contexts/AuthContext";
import { API_URL } from "@app/constants/Config";

const AxiosContext = createContext<{
  publicAxios: AxiosInstance;
  authAxios: AxiosInstance;
}>(null);
const { Provider } = AxiosContext;

// This hook can be used to access axios
export function useAxios() {
  const value = useContext(AxiosContext);
  if (process.env.NODE_ENV !== "production") {
    if (!value) {
      throw new Error("useAxios must be wrapped in a <AxiosProvider />");
    }
  }

  return value;
}

const AxiosProvider = ({ children }) => {
  const authContext = useSession();

  const authAxios = axios.create({
    baseURL: API_URL,
  });

  const publicAxios = axios.create({
    baseURL: API_URL,
  });

  authAxios.interceptors.request.use(
    (config) => {
      if (!config.headers.Authorization) {
        config.headers.Authorization = `Bearer ${authContext.authState.accessToken}`;
      }

      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  const refreshAuthLogic = (failedRequest) => {
    const data = {
      refreshToken: authContext.authState.refreshToken,
    };

    const options = {
      method: "POST",
      data,
      url: `${API_URL}/auth/renew`,
    };

    return axios(options).then((tokenRefreshResponse) => {
      failedRequest.response.config.headers.Authorization =
        "Bearer " + tokenRefreshResponse.data.token;

      authContext.setAuthState({
        accessToken: tokenRefreshResponse.data.token,
        refreshToken: tokenRefreshResponse.data.refreshToken,
      });
    });
  };

  createAuthRefreshInterceptor(authAxios, refreshAuthLogic, {});

  return (
    <Provider
      value={{
        authAxios,
        publicAxios,
      }}
    >
      {children}
    </Provider>
  );
};

export { AxiosContext, AxiosProvider };
