import {
  EXCLUDE_TEXTS,
  MAX_TEXT_RECOGNIZED_LENGTH,
} from "@app/service/helpers/arrays";
import { StyleSheet } from "react-native";
import { TextRecognizedItem } from "./TextRecognizedItem";
import { ScrollView } from "./Themed";

export interface ManualEntryForm {
  tagId?: string;
  serialNumber: string;
  notes: string;
  image?: any;
}

export const initialManualEntryForm: ManualEntryForm = {
  serialNumber: "",
  notes: "",
};

interface TextRecognizedListProps {
  textRecognized: string[];
  onPressText: (text: string) => void;
}

export const TextRecognizedList = ({
  textRecognized,
  onPressText,
}: TextRecognizedListProps) => {
  const filterTextRecognized = (text: string) => {
    return (
      text.length > MAX_TEXT_RECOGNIZED_LENGTH &&
      !text.toLowerCase().match(EXCLUDE_TEXTS)
    );
  };

  return (
    <ScrollView style={styles.container}>
      {textRecognized.filter(filterTextRecognized).map((text) => {
        if (text.toLowerCase().includes("serial")) {
          return text
            .split("SERIAL")
            .filter(filterTextRecognized)
            .map((tx) => (
              <TextRecognizedItem text={tx} onPressText={onPressText} />
            ));
        }
        return <TextRecognizedItem text={text} onPressText={onPressText} />;
      })}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    zIndex: 0,
  },
});
