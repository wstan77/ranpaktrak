import { OPEN_ID_CLIENT_ID, OPEN_ID_ISSUER_URL } from "@app/constants/Config";
import { useSession } from "@app/service/contexts/AuthContext";
import {
  exchangeCodeAsync,
  makeRedirectUri,
  useAuthRequest,
  useAutoDiscovery,
} from "expo-auth-session";

export const useDistributorSignIn = () => {
  const { setAuthState } = useSession();
  const discovery = useAutoDiscovery(OPEN_ID_ISSUER_URL);

  const redirectUri = makeRedirectUri({
    scheme: undefined,
    path: "openid-sign-in",
  });

  const [request, result, promptAsync] = useAuthRequest(
    {
      clientId: OPEN_ID_CLIENT_ID,
      scopes: ["email", "profile", "openid", "offline_access"],
      usePKCE: true,
      redirectUri,
    },
    discovery
  );

  const signIn = async () => {
    console.log("Request", request);
    const codeResponse = await promptAsync();
    console.log("Code", codeResponse);
    console.log("Result", result);
    if (request && codeResponse?.type === "success" && discovery) {
      try {
        const res = await exchangeCodeAsync(
          {
            clientId: OPEN_ID_CLIENT_ID,
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
        console.log(res);
        return res.accessToken != null;
      } catch (e) {
        console.error(e);
      }
    }
    setAuthState({
      accessToken: null,
      refreshToken: null,
    });
    return false;
  };
  return { request, signIn };
};
