import React, { useContext } from "react";
import { FontAwesome5 } from "@expo/vector-icons";
import { createDrawerNavigator } from "@react-navigation/drawer";
import { useRouter } from "expo-router";
import "react-native-gesture-handler";
import Toast from "react-native-toast-message";

import Index from "@app/app/(app)/index";
import CustomDrawerContent from "@app/components/CustomDrawerContent";
import { LoadingIndicator } from "@app/components/LoadingIndicator";
import { AppContext } from "@app/service/contexts/AppContext";
import { useCurrentUser } from "@app/service/contexts/CurrentUserContext";

export default function AppLayout() {
  const { currentUser, isLoading } = useCurrentUser();
  const app = useContext(AppContext);
  const router = useRouter();

  // You can keep the splash screen open, or render a loading screen like we do here.
  if (isLoading) {
    return <LoadingIndicator />;
  }

  // Only require authentication within the (app) group's layout as users
  // need to be able to access the (auth) group and sign in again.
  if (currentUser == null) {
    // On web, static rendering will stop here as the user is not authenticated
    // in the headless Node process that the pages are rendered in.
    router.navigate({ pathname: "UserTypeSelectionScreen" });
  }

  const Drawer = createDrawerNavigator();

  // This layout can be deferred because it's not the root layout.
  return (
    <>
      <Drawer.Navigator
        screenOptions={{
          drawerPosition: "right",
          headerShown: false,
          headerStyle: {
            borderStyle: "solid",
            borderColor: "#BEC4CD",
            borderBottomWidth: 2,
          },
          headerStatusBarHeight: 16,
          drawerContentContainerStyle: { height: "100%" },
          swipeEnabled: !app?.appLoading,
        }}
        drawerContent={(props) => <CustomDrawerContent {...props} />}
      >
        <Drawer.Screen
          name="home"
          options={{
            drawerLabel: "Home",
            drawerIcon: ({ focused, size }) => {
              return <FontAwesome5 name="home" size={24} color="#0297D9" />;
            },
          }}
          component={Index}
        />
      </Drawer.Navigator>
      <Toast />
    </>
  );
}
