import React from "react";
import { StyleSheet } from "react-native";
import { Text, useThemeColor, View } from "./Themed";
import TrakButton from "./TrakButton";

interface TextRecognizedItemProps {
  text: string;
  onPressText: (text: string) => void;
}

export const TextRecognizedItem = ({
  text,
  onPressText,
}: TextRecognizedItemProps) => {
  const primary = useThemeColor("primary");
  return (
    <View
      key={text}
      style={[styles.textContainer, { borderBottomColor: primary }]}
    >
      <Text
        style={[
          styles.text,
          {
            color: primary,
          },
        ]}
      >
        {text}
      </Text>
      <TrakButton onPress={() => onPressText(text)} label="Use" size="small" />
    </View>
  );
};

const styles = StyleSheet.create({
  text: {
    flex: 1,
    justifyContent: "center",
    textAlignVertical: "center",
  },
  textContainer: {
    display: "flex",
    flexDirection: "row",
    paddingVertical: 10,
    alignSelf: "center",
    justifyContent: "center",
    borderBottomWidth: 1,
  },
});
