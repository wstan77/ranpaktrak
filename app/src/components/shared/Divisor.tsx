import { StyleSheet } from "react-native";
import { useThemeColor, View } from "../Themed";

export const Divisor = () => {
  const backgroundColor = useThemeColor("zebraStripe");
  return <View style={[styles.divisor, { backgroundColor }]} />;
};

const styles = StyleSheet.create({
  divisor: {
    height: 1,
  },
});
