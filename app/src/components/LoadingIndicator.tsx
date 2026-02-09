import { StyleSheet, ActivityIndicator } from "react-native";
import { useThemeColor, View, Text } from "./Themed";

export interface LoadingIndicatorProps {
  text?: string;
}

export const LoadingIndicator = ({ text }: LoadingIndicatorProps) => {
  const indicatorColor = useThemeColor("primary");
  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" style={styles.indicator} color={indicatorColor} />
      <Text style={styles.text}>{text || "Loading..."}</Text>
    </View>
  );
};


const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    paddingTop: 200,
    backgroundColor: "transparent",
  },
  indicator: {
    marginBottom: 50,
    transform: [{ scale: 3 }],
  },
  text: {
    fontSize: 24,
  },
});
