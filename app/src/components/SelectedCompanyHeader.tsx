import { useEffect, useMemo, useState } from "react";
import { StyleSheet } from "react-native";
import { Text, View, useThemeColor } from "@app/components/Themed";
import { useStorageStateSimple } from "@app/hooks/useStorageState";

export default function SelectedCompanyHeader() {
  const [selectedCompanyTitle, setSelectedCompanyTitle] = useState("");
  const [selectedCompanyLocation, setSelectedCompanyLocation] = useState("");

  const selectedCust = useStorageStateSimple("selectedCustomer");
  const zebraStripe = useThemeColor("zebraStripe");

  const styles = useMemo(() => StyleSheet.create({
    header: {
      width: "100%",
      backgroundColor: zebraStripe,
      padding: 8,
    },
    companyName: {
      fontSize: 18,
    },
    companyLocation: {
      fontSize: 14,
    },
  }), [zebraStripe]);

  useEffect(() => {
    // Load selected company from AsyncStorage when the component mounts
    const loadSelectedCompany = async () => {
      try {
        if (selectedCust != null) {
          setSelectedCompanyTitle(selectedCust.accountName);
          setSelectedCompanyLocation(selectedCust.city);
        }
      } catch (error) {
        console.error("Error loading selected company:", error);
      }
    };

    loadSelectedCompany();

    return () => {};
  }, [selectedCust]);
  return (
    <>
      <View style={styles.header}>
        <Text style={styles.companyName}>{selectedCompanyTitle}</Text>
        <Text style={styles.companyLocation}>{selectedCompanyLocation}</Text>
      </View>
    </>
  );
}
