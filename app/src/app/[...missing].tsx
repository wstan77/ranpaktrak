import { Link, Redirect, Stack, usePathname, useRouter } from "expo-router";
import { StyleSheet } from "react-native";

import { Text, View } from "@app/components/Themed";
import { useEffect, useState } from "react";

const REDIRECT_ROUTES = {
  "/ad-sign-in": "/home",
  "/openid-sign-in": "/home",
};

export default function NotFoundScreen() {
  const router = useRouter();
  const path = usePathname();
  const [redirectRoute, setRedirectRoute] = useState<string>(null);

  useEffect(() => {
    setRedirectRoute(REDIRECT_ROUTES[path]);
  }, [path]);

  return redirectRoute ? (
    <Redirect href={redirectRoute} />
  ) : (
    <>
      <Stack.Screen options={{ title: "Not Found" }} />
      <View style={styles.container}>
        <Text style={styles.title}>This screen couldn't be found.</Text>

        <Link href="/" style={styles.link} replace>
          <Text style={styles.linkText}>Go to home screen!</Text>
        </Link>
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 20,
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
  },
  link: {
    marginTop: 15,
    paddingVertical: 15,
  },
  linkText: {
    fontSize: 14,
    color: "#2e78b7",
  },
});
