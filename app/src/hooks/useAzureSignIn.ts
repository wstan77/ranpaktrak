import { useSession } from "@app/service/contexts/AuthContext";
import { AZURE_AD_CLIENT_ID, AZURE_AD_TENANT_ID } from "@app/constants/Config";
import {
  exchangeCodeAsync,
  makeRedirectUri,
  useAuthRequest,
  useAutoDiscovery,
} from "expo-auth-session";

export const useAzureSignIn = () => {
  const { setAuthState } = useSession();

  const discovery = useAutoDiscovery(
    `https://login.microsoftonline.com/${AZURE_AD_TENANT_ID}/v2.0`
  );

  const redirectUri = makeRedirectUri({
    scheme: undefined,
    path: "ad-sign-in",
  });

  const [request, _, promptAsync] = useAuthRequest(
    {
      clientId: AZURE_AD_CLIENT_ID,
      scopes: [`${AZURE_AD_CLIENT_ID}/.default`],
      redirectUri,
    },
    discovery
  );

  const signIn = async () => {
    const codeResponse = await promptAsync();
    if (request && codeResponse?.type === "success" && discovery) {
      const res = await exchangeCodeAsync(
        {
          clientId: AZURE_AD_CLIENT_ID,
          code: codeResponse.params.code,
          extraParams: request.codeVerifier
            ? { code_verifier: request.codeVerifier }
            : undefined,
          redirectUri,
        },
        discovery
      );
      setAuthState({
        accessToken: res.accessToken,
        refreshToken: res.refreshToken,
      });
      return res.accessToken != null;
    }
    return false;
  };

  return { request, signIn };
};
