import "expo-dev-client";
import { SessionProvider } from "@app/service/contexts/AuthContext";
import { AxiosProvider } from "@app/service/contexts/AxiosContext";
import FontAwesome from "@expo/vector-icons/FontAwesome";
import {
  DarkTheme,
  DefaultTheme,
  ThemeProvider,
} from "@react-navigation/native";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { PersistQueryClientProvider } from "@tanstack/react-query-persist-client";
import { useFonts } from "expo-font";
import { Slot, SplashScreen } from "expo-router";
import { useEffect } from "react";
import { useColorScheme } from "react-native";
import { SafeAreaProvider } from "react-native-safe-area-context";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { createAsyncStoragePersister } from "@tanstack/query-async-storage-persister";
import AppProvider from "@app/service/contexts/AppContext";
import { CurrentUserProvider } from "@app/service/contexts/CurrentUserContext";
import { useThemeColor } from "@app/components/Themed";

export {
  // Catch any errors thrown by the Layout component.
  ErrorBoundary,
} from "expo-router";

// Prevent the splash screen from auto-hiding before asset loading is complete.
SplashScreen.preventAutoHideAsync().catch(() => {});

export default function RootLayout() {
  const [loaded, error] = useFonts({
    ...FontAwesome.font,
  });
  const colorScheme = useColorScheme();
  const bgColor = useThemeColor("background");

  // Expo Router uses Error Boundaries to catch errors in the navigation tree.
  useEffect(() => {
    if (error) throw error;
  }, [error]);

  useEffect(() => {
    if (loaded) {
      SplashScreen.hideAsync();
    }
  }, [loaded]);

  if (!loaded) {
    return null;
  }

  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        gcTime: Infinity,
        retry: false,
      },
    },
  });

  const persister = createAsyncStoragePersister({
    storage: AsyncStorage,
  });

  return (
    <ThemeProvider value={colorScheme === "dark" ? DarkTheme : DefaultTheme}>
      <PersistQueryClientProvider
        client={queryClient}
        persistOptions={{ persister }}
        onSuccess={() => {
          queryClient
            .resumePausedMutations()
            .then(() => queryClient.invalidateQueries());
        }}
      >
        <SessionProvider>
          <AxiosProvider>
            <CurrentUserProvider>
              <AppProvider>
                <SafeAreaProvider style={{ backgroundColor: bgColor }}>
                  <Slot />
                </SafeAreaProvider>
              </AppProvider>
            </CurrentUserProvider>
          </AxiosProvider>
        </SessionProvider>
      </PersistQueryClientProvider>
    </ThemeProvider>
  );
}
