import { StyleSheet } from "react-native";
import { TrakBanner, TrakButton } from "@app/components";
import SelectedCompanyHeader from "@app/components/SelectedCompanyHeader";
import { useContext } from "react";
import { AppContext } from "@app/service/contexts/AppContext";
import { Text, View } from "@app/components/Themed";
import { useStorageStateSimple } from "@app/hooks";
import { STORAGE_KEYS } from "@app/service/helpers/storage";

export default function StartScanScreen({ navigation }) {
  const appContext = useContext(AppContext);
  const currentScans = appContext?.ranpakScans ?? [];
  const ranpakAccounts = appContext?.ranpakAccounts ?? [];

  const selectedCustomer = useStorageStateSimple(STORAGE_KEYS.selectedCustomer);

  const isManuallyCreated = ranpakAccounts.find(
    (rpk) =>
      rpk.accountName === selectedCustomer?.accountName &&
      rpk.city === selectedCustomer?.city
  );

  return (
    <View style={styles.container}>
      {currentScans?.length > 0 && (
        <TrakBanner
          text={
            <>
              There are <Text style={styles.bold}>{currentScans.length}</Text>{" "}
              pending scan(s) ready to upload
            </>
          }
          onPress={() => navigation.navigate("Home")}
        />
      )}
      <SelectedCompanyHeader />
      <View style={styles.contentContain}>
        <TrakButton
          label={"Scan QR Code"}
          iconName="qrcode"
          onPress={() => {
            navigation.navigate("qrCodeScanner");
          }}
          size="big"
          fullSize
          btnColor="primary"
        />
        <TrakButton
          label={"Scan via NFC"}
          iconName="nfc"
          onPress={() => {}}
          size="big"
          fullSize
          btnColor="primary"
        />
        <TrakButton
          label={"Enter Serial #"}
          iconName="barcode"
          onPress={() => {
            navigation.navigate("scanForm");
          }}
          size="big"
          fullSize
          btnColor="primary"
        />
        {isManuallyCreated && (
          <TrakButton
            label={"Edit Account Details"}
            iconName="pencil"
            onPress={() => {
              navigation.navigate("createNewAccount");
            }}
            fullSize
            btnColor="zebraStripe"
            isBordered
            size="big"
            textColor="primary"
          />
        )}
        <TrakButton
          label={"View Machine List"}
          iconName="list-status"
          onPress={() => {
            navigation.navigate("machineList");
          }}
          fullSize
          btnColor="zebraStripe"
          isBordered
          size="big"
          textColor="primary"
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
  },
  contentContain: {
    gap: 10,
    display: "flex",
    flex: 1,
    marginHorizontal: "10%",
    alignItems: "center",
    marginTop: 15,
  },
  bold: { fontWeight: "bold" },
});
