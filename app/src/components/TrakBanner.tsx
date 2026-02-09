import { FontAwesomeIconName } from "@app/constants/Icons";
import { FontAwesome5 } from "@expo/vector-icons";
import { StyleSheet, TouchableOpacity } from "react-native";
import { Text, View } from "@app/components/Themed";
import { useThemeColor } from "./Themed";

interface TrakBannerProps {
  text: string | React.ReactElement;
  iconName?: FontAwesomeIconName;
  onPress?: () => void;
  mode?: "warning" | "success";
}

export const TrakBanner = ({
  text,
  iconName,
  onPress,
  mode = "warning",
}: TrakBannerProps) => {
  const warning = useThemeColor("warning");
  const secondary = useThemeColor("secondary");
  const backdround = useThemeColor("background");

  return (
    <TouchableOpacity
      onPress={onPress}
      style={[
        styles.text,
        { backgroundColor: mode === "warning" ? warning : secondary },
      ]}
      activeOpacity={onPress ? 0.5 : 1}
    >
      {iconName && (
        <View
          style={[
            styles.icon,
            { backgroundColor: mode === "warning" ? warning : secondary },
          ]}
        >
          <FontAwesome5
            name={iconName}
            size={32}
            color={mode === "warning" ? "#8D510C" : backdround}
          />
        </View>
      )}
      <Text style={{ color: mode === "warning" ? "#8D510C" : backdround }}>
        {text}
      </Text>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  text: {
    width: "100%",
    padding: 12,
    display: "flex",
    flexDirection: "row",
    alignItems: "center",
  },
  icon: { marginRight: 10 },
});
