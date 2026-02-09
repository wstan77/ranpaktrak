import Constants from "expo-constants";
const { expoConfig } = Constants;

let debuggerHost = expoConfig?.hostUri;
if (debuggerHost) {
  if (debuggerHost.includes(':')) {
    debuggerHost = debuggerHost.split(':')[0];
  }
}
export const API_URL = process.env.EXPO_PUBLIC_API_URL.replace('{debuggerHost}', debuggerHost);

export const AZURE_AD_CLIENT_ID = process.env.EXPO_PUBLIC_AZURE_AD_CLIENT_ID;
export const AZURE_AD_TENANT_ID = process.env.EXPO_PUBLIC_AZURE_AD_TENANT_ID;

export const OPEN_ID_ISSUER_URL = process.env.EXPO_PUBLIC_OPEN_ID_ISSUER_URL;
export const OPEN_ID_CLIENT_ID = process.env.EXPO_PUBLIC_OPEN_ID_CLIENT_ID;
