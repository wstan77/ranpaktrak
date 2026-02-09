import { StyleSheet, TouchableOpacity } from "react-native";
import ExistingProfileScreen from "./ExistingProfileScreen";
import StartScanScreen from "./StartScanScreen";
import CodeScannerScreen from "./CodeScannerScreen";
import MachineListScreen from "./MachineListScreen";
import ScanFormScreen from "./ScanFormScreen";
import CreateNewAccount from "./CreateNewAccount";
import createThemedStackNavigator from "@app/components/StackNavigator";
import HomeIndex from "@app/app/(app)/home";
import { MaterialCommunityIcons } from "@expo/vector-icons";

export default function Index({ navigation }) {
  const Stack = createThemedStackNavigator();
  return (
    <>
      <Stack.Navigator initialRouteName={"Home"}>
        <Stack.Screen name="Home" component={HomeIndex} />

        <Stack.Screen
          name="existingProfile"
          component={ExistingProfileScreen}
          options={{ title: "Select Account" }}
        />
        <Stack.Screen
          name="scanOption"
          component={StartScanScreen}
          options={{ title: "Scan" }}
        />
        <Stack.Screen
          name="qrCodeScanner"
          component={CodeScannerScreen}
          options={{ title: "QR Scan" }}
        />
        <Stack.Screen
          name="scanForm"
          component={ScanFormScreen}
          options={{
            title: "Scan Entry",
            headerLeft: () => (
              <TouchableOpacity
                onPress={() => navigation.navigate("scanOption")}
              >
                <MaterialCommunityIcons
                  name={"arrow-left"}
                  style={styles.leftArrow}
                  size={24}
                />
              </TouchableOpacity>
            ),
          }}
        />
        <Stack.Screen
          name="createNewAccount"
          component={CreateNewAccount}
          options={{ title: "New Account" }}
        />
        <Stack.Screen
          name="machineList"
          component={MachineListScreen}
          options={{ title: "Machine List" }}
        />
      </Stack.Navigator>
    </>
  );
}

const styles = StyleSheet.create({
  leftArrow: {
    color: "white",
    marginLeft: 10,
    marginTop: 4,
  },
});
