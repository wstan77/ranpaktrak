import { Text, View } from "@app/components/Themed";
import { AppContext } from "@app/service/contexts/AppContext";
import { DrawerToggleButton } from "@react-navigation/drawer";
import { createStackNavigator } from "@react-navigation/stack";
import { useContext } from "react";
import { Image, StyleSheet } from "react-native";

const LogoTitle = ({ children }) => {
  const showFullLogo = children === "Home" || children === "Start";
  return (
    <View id={"ranpakLogo"} style={styles.ranpakLogo}>
      <Image
        style={
          showFullLogo || children.length == 0
            ? styles.headerLogo
            : styles.iconLogo
        }
        source={
          showFullLogo || children.length == 0
            ? require("@assets/app-logo.png")
            : require("@assets/trak-icon-light.png")
        }
      />
      {showFullLogo || children.length == 0 ? null : (
        <Text style={styles.logoTitle}>{children}</Text>
      )}
    </View>
  );
};

export default function createThemedStackNavigator() {
  const app = useContext(AppContext);
  const Stack = createStackNavigator();
  const tintColor = "#fff";
  Stack.Navigator.defaultProps = {
    screenOptions: {
      ...Stack.Navigator.defaultProps?.screenOptions,
      headerStyle: {
        backgroundColor: "#0297D9",
      },
      headerTitleAlign: "center",
      headerTintColor: tintColor,
      headerTitle: (props) => <LogoTitle {...props} />,
      headerRight: () =>
        !app?.appLoading && <DrawerToggleButton tintColor={tintColor} />,
    },
  };
  return Stack;
}

const styles = StyleSheet.create({
  headerLogo: {
    width: 150,
    height: 30,
    justifyContent: "center",
    alignSelf: "center",
    alignItems: "center",
    resizeMode: "contain",
  },
  iconLogo: {
    height: 25,
    width: 25,
    resizeMode: "contain",
  },
  ranpakLogo: {
    flexDirection: "row",
    alignItems: "center",
    alignSelf: "center",
    justifyContent: "center",
    textAlign: "center",
    backgroundColor: "transparent",
  },
  logoTitle: {
    color: "white",
    fontSize: 22,
    fontWeight: "bold",
    marginLeft: 10,
  },
});
