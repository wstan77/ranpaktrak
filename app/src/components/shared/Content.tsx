import { PropsWithChildren } from "react";
import { StyleSheet } from "react-native";
import { useThemeColor, View } from "../Themed";

interface ContentProps {
  withBorder?: boolean;
}

export const Content = ({
  children,
  withBorder,
}: PropsWithChildren<ContentProps>) => {
  const borderColor = useThemeColor("zebraStripe");
  return (
    <View
      style={[
        styles.content,
        withBorder && { ...styles.withBorder, borderColor: borderColor },
      ]}
    >
      {children}
    </View>
  );
};

const styles = StyleSheet.create({
  content: {
    flex: 1,
  },
  withBorder: {
    borderWidth: 1,
  },
});
