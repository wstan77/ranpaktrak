import React from "react";
import {
  StyleSheet,
  TextStyle,
  TouchableOpacity,
  ViewStyle,
} from "react-native";

import { Text, ThemeColor, useThemeColor } from "@app/components/Themed";
import { MaterialCommunityIconName } from "@app/constants/Icons";
import { MaterialCommunityIcons } from "@expo/vector-icons";

type TextAlign = "center" | "left" | "right";
type ButtonSize = "small" | "default" | "big";

interface TrakButtonProps {
  btnStyle?: ViewStyle;
  btnColor?: ThemeColor;
  textColor?: ThemeColor;
  onPress: () => void;
  label?: string | any;
  textStyle?: TextStyle;
  iconName?: MaterialCommunityIconName;
  disabled?: boolean;
  size?: ButtonSize;
  textAlign?: TextAlign;
  fullSize?: boolean;
  isBordered?: boolean;
  borderRadius?: number;
  loading?: boolean;
}

export default function TrakButton({
  btnStyle,
  btnColor = "primary",
  onPress,
  label,
  textStyle,
  iconName,
  disabled = false,
  textAlign = "center",
  size = "default",
  fullSize,
  isBordered = false,
  textColor,
  borderRadius = 5,
  loading,
}: TrakButtonProps) {
  let fontSize = 20;
  const text = useThemeColor("text");
  const customTextColor = useThemeColor(textColor);
  const lighBackground = ["background", "primaryText", "zebraStripe"].includes(
    btnColor
  );

  const dynamicProperties: ViewStyle = {
    borderWidth: 0,
  };

  if (isBordered) {
    dynamicProperties.borderWidth = 0.5;
  }

  if (btnColor) {
    const buttonColor = useThemeColor(btnColor);
    dynamicProperties.backgroundColor = buttonColor;
  }

  if (disabled) {
    dynamicProperties.opacity = 0.5;
  }

  if (size) {
    if (size === "small") {
      fontSize = 16;
    } else if (size === "big") {
      fontSize = 24;
      dynamicProperties.paddingVertical = 20;
      dynamicProperties.paddingHorizontal = 20;
    } else {
      dynamicProperties.paddingVertical = 10;
      dynamicProperties.paddingHorizontal = 20;
    }
  }

  return (
    <>
      <TouchableOpacity
        style={[styles.button, { borderRadius }, btnStyle, dynamicProperties, loading && styles.loadingStyle]}
        onPress={onPress}
        disabled={disabled || loading}
      >
        {iconName && (
          <MaterialCommunityIcons
            name={iconName}
            style={[
              styles.buttonText,
              textStyle,
              { fontSize },
              lighBackground && { color: text },
              textColor && { color: customTextColor },
              loading && styles.loadingStyle
            ]}
          />
        )}
        {label && (
          <Text
            style={[
              styles.buttonText,
              textStyle,
              { textAlign, fontSize },
              fullSize && { flex: 1 },
              lighBackground && { color: text },
              textColor && { color: customTextColor },
              loading && styles.loadingStyle
            ]}
          >
            {label}
          </Text>
        )}
      </TouchableOpacity>
    </>
  );
}

const styles = StyleSheet.create({
  button: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "center", // Align text vertically
    alignItems: "center", // Align text horizontally
    paddingVertical: 7, // Vertical padding
    paddingHorizontal: 10, // Horizontal padding
    gap: 15,
  },
  buttonText: {
    color: "#fff", // Text color
  },
  loadingStyle: {
    color: "transparent",
    opacity: 0.5
  }
});
