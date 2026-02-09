import { useEffect, useMemo, useState } from "react";
import { StyleSheet, ViewStyle } from "react-native";
import { useThemeColor } from "./Themed";
import TrakButton from "./TrakButton";
import { Text, View, TextInput } from "@app/components/Themed";

interface TrakInputProps {
  label: string;
  isRequired?: boolean;
  isDisabled?: boolean;
  onChange?: (value: string) => void;
  onFocus?: (value: any) => void;
  onClick?: (value: any) => void;
  onBlur?: (value: any) => void;
  onPressCameraButton?: () => void;
  value: string;
  isMultiline?: boolean;
  containerStyle?: ViewStyle;
  errorMessage?: string;
}

export const TrakInput = ({
  label,
  onChange,
  isRequired,
  value,
  isMultiline,
  onPressCameraButton,
  containerStyle,
  errorMessage,
  onFocus,
  onClick,
  onBlur,
  isDisabled,
}: TrakInputProps) => {
  const [error, setError] = useState("");
  const red = useThemeColor("red");
  const zebraStripe = useThemeColor("zebraStripe");
  const textAlignVertical = isMultiline ? "top" : "auto";

  useEffect(() => {
    if (!value) {
      setError(errorMessage);
    }
  }, [errorMessage, value]);

  const styles = useMemo(
    () =>
      StyleSheet.create({
        container: {
          marginVertical: 15,
          zIndex: 1,
        },
        labelWrapper: { display: "flex", flexDirection: "row", zIndex: 1 },
        label: { marginRight: 8, fontSize: 16, opacity: 0.7, zIndex: 1 },
        required: { color: red, zIndex: 1 },
        inputWrapper: {
          display: "flex",
          flexDirection: "row",
          zIndex: 1,
        },
        textInput: {
          flex: 1,
          marginVertical: 10,
          borderWidth: 1,
          borderColor: zebraStripe,
          paddingVertical: 10,
          paddingHorizontal: 15,
          zIndex: 1,
        },
        photoBtn: {
          justifyContent: "center",
          marginVertical: 10,
        },
        errorMessage: { marginTop: 3 },
      }),
    [red, zebraStripe]
  );

  return (
    <View style={[styles.container, containerStyle]}>
      <View style={styles.labelWrapper}>
        <Text style={styles.label}>{label}</Text>
        {isRequired && <Text style={styles.required}>*</Text>}
      </View>
      <View style={styles.inputWrapper}>
        <TextInput
          style={[
            styles.textInput,
            isMultiline && {
              height: 180,
            },
          ]}
          value={value}
          onChangeText={(text) => {
            setError("");
            onChange?.(text);
          }}
          multiline={isMultiline}
          onFocus={onFocus}
          onTouchStart={onClick}
          onBlur={onBlur}
          editable={!isDisabled}
          textAlignVertical={textAlignVertical}
        />
        {onPressCameraButton && (
          <TrakButton
            btnStyle={{ ...styles.photoBtn }}
            iconName="camera"
            onPress={onPressCameraButton}
            size="small"
            borderRadius={0}
          />
        )}
      </View>

      {error && (
        <Text style={[styles.errorMessage, { color: red }]}>{error}</Text>
      )}
    </View>
  );
};
