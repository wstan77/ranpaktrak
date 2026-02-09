import { Redirect } from "expo-router";
import { StyleSheet } from "react-native";

import { TrakButton } from "@app/components";
import { LoadingIndicator } from "@app/components/LoadingIndicator";
import createThemedStackNavigator from "@app/components/StackNavigator";
import { ScrollView, Text, View, useThemeColor } from "@app/components/Themed";
import { useAzureSignIn } from "@app/hooks/useAzureSignIn";
import { useDistributorSignIn } from "@app/hooks/useDistributorSignIn";
import { useSession } from "@app/service/contexts/AuthContext";
import { useCurrentUser } from "@app/service/contexts/CurrentUserContext";

export default function UserTypeSelectionScreen() {
  const Stack = createThemedStackNavigator();
  const { isAuthenticated } = useSession();
  const { currentUser } = useCurrentUser();

  if (!isAuthenticated) {
    Stack.Navigator.defaultProps.screenOptions = {
      ...Stack.Navigator.defaultProps?.screenOptions,
      headerRight: () => <></>,
    };
  }

  if (currentUser) {
    return <Redirect href="/"></Redirect>;
  }

  return (
    <Stack.Navigator>
      <Stack.Screen
        name="/"
        component={SelectionScreenImpl}
        options={{ title: "Sign in" }}
      />
    </Stack.Navigator>
  );
}

export function SelectionScreenImpl() {
  const backgroundColor = useThemeColor("background");
  const { request: azureRequest, signIn: azureSignIn } = useAzureSignIn();
  const { request: distributorRequest, signIn: distributorSignIn } =
    useDistributorSignIn();
  const { isLoading } = useCurrentUser();

  return (
    <ScrollView>
      {isLoading ? (
        <LoadingIndicator />
      ) : (
        <View style={styles.container}>
          <Text style={styles.title}>
            Please select the option below which applies to you:{" "}
          </Text>

          <View style={[styles.section, { backgroundColor }]}>
            <Text style={styles.labelText}>
              I am a Ranpak user and I need to track the location of machines.
            </Text>
            <TrakButton
              label={"Ranpak.com User"}
              loading={!azureRequest}
              onPress={() => azureSignIn()}
              iconName="map"
            />
          </View>
          <View style={[styles.section, { backgroundColor }]}>
            <Text style={styles.labelText}>
              I am a distributor working with Ranpak, and I need to track the
              location of machines.
            </Text>
            <TrakButton
              label={"Distributor"}
              loading={!distributorRequest}
              onPress={() => distributorSignIn()}
              iconName="map-check"
            />
          </View>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
  },
  section: {
    width: "75%",
    marginTop: "5%",
    marginBottom: "2%",
    borderRadius: 5,
    padding: 20,
  },
  labelText: {
    justifyContent: "center", // Align text vertically
    alignItems: "center", // Align text horizontally
    textAlign: "center",
    marginBottom: 10,
    fontWeight: "bold",
    fontSize: 16,
  },
  title: {
    fontWeight: "bold",
    fontSize: 16,
    marginTop: 30,
    marginHorizontal: 50,
    textAlign: "center",
  },
});
