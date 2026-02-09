import { useContext, useEffect, useMemo, useState } from "react";
import {
  StyleSheet,
  View,
  ScrollView,
  Alert,
  SafeAreaView,
  Platform,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { onlineManager } from "@tanstack/react-query";

import { RegionFooterComponent } from "@app/components/RegionFooterComponent";
import UploadButton from "@app/components/UploadButton";
import { Text, useThemeColor } from "@app/components/Themed";
import { Divisor, TrakButton } from "@app/components";
import { useStorageStateSimple } from "@app/hooks/useStorageState";

import eventEmitter from "@app/service/EventEmitterService";
import { EventType } from "@app/constants/EventType";
import { ScanData, SyncData } from "@app/model/ScanData";
import { STORAGE_KEYS } from "@app/service/helpers/storage";
import { AppContext } from "@app/service/contexts/AppContext";
import { LoadingIndicator } from "@app/components/LoadingIndicator";

type HomeView = "default" | "sync" | "sync-details";

type RenderView = {
  [key in HomeView]: React.ReactElement;
};

export default function HomeIndex({ navigation }) {
  const [isOnline, setIsOnline] = useState(onlineManager.isOnline());
  const [pendingScans, setPendingScans] = useState(0);
  const [disabled, setDisabled] = useState(true);
  const [homeView, setHomeView] = useState<HomeView>("default");
  const [syncResponse, setSyncResponse] = useState<SyncData>();
  const selectedRegion = useStorageStateSimple(STORAGE_KEYS.selectedRegion);

  const appContext = useContext(AppContext);

  const currentScans = appContext?.ranpakScans ?? [];

  const red = useThemeColor("red");
  const primaryColor = useThemeColor("primary");
  const secondaryColor = useThemeColor("secondary");
  const textColor = useThemeColor("text");
  const backgroundColor = useThemeColor("background");
  const zebraStripe = useThemeColor("zebraStripe");

  const onScanSave = (scan: ScanData) => {
    // We got a scan, enable the upload button
    setPendingScans(pendingScans + 1);
  };

  const onScanUpload = (scans: ScanData[]) => {
    setPendingScans(0);
  };

  useEffect(() => {
    eventEmitter.on(EventType.SCAN_SAVED, onScanSave);
    eventEmitter.on(EventType.SCANS_UPLOADED, onScanUpload);
    const getPendingScans = async () => {
      if (currentScans != null) {
        const pendingScans = currentScans.length;
        setPendingScans(pendingScans);
      }
    };
    getPendingScans();
    setDisabled(selectedRegion === null || pendingScans === 0);
    return () => {
      eventEmitter.off(EventType.SCAN_SAVED, onScanSave);
      eventEmitter.off(EventType.SCANS_UPLOADED, onScanUpload);
    };
  }, [currentScans, pendingScans, selectedRegion]);

  const handleAfterUpload = (response: SyncData) => {
    setSyncResponse(response);
    if (response) {
      setHomeView("sync");
      appContext?.setRanpakScans([]);
    } else {
      Alert.alert("Sync Failed", "Error on response");
    }
  };

  const styles = useMemo(
    () =>
      StyleSheet.create({
        container: {
          flex: 1,
          alignItems: "center",
          backgroundColor,
        },
        syncDetailsContainer: {
          paddingHorizontal: 10,
        },
        button: {
          justifyContent: "center", // Align text vertically
          alignItems: "flex-start",
          paddingVertical: 15, // Vertical padding
          paddingHorizontal: 15,
          width: "75%",
          borderRadius: 5, // Border radius for rounded corners
          marginTop: "20%",
        },
        buttonText: {
          fontSize: 28, // Text font size
        },
        section: {
          marginTop: "20%",
          backgroundColor: zebraStripe,
          width: "75%",
          padding: 20,
          borderRadius: 50,
        },
        titleText: {
          opacity: 0.7,
          textAlign: "center",
          marginBottom: 10,
        },
        dataText: {
          fontWeight: "bold",
          textAlign: "center",
        },
        offlineText: {
          color: red,
          textAlign: "center",
          fontSize: 24,
          marginTop: 30,
        },
        checkMarkIcon: {
          backgroundColor: secondaryColor,
          borderRadius: 100,
          padding: 10,
        },
        syncContainer: { justifyContent: "center" },
        syncText: {
          fontWeight: "bold",
          fontSize: 18,
          lineHeight: 34,
          marginTop: 30,
          textAlign: "center",
        },
        text: {
          marginTop: 15,
        },
        backWrapper: {
          padding: 10,
          marginHorizontal: "10%",
        },
        fontLG: {
          fontSize: 16,
        },
        fontDF: {
          fontSize: 14,
        },
        mlDF: {
          marginLeft: 20,
        },
        mlLG: {
          marginLeft: 40,
        },
        bold: {
          fontWeight: "600",
        },
        mt: {
          marginTop: 5,
        },
        mb: {
          marginBottom: 5,
        },
        accountContainer: {
          borderBottomWidth: 1.5,
          marginVertical: 10,
          paddingBottom: 10,
          marginRight: 10,
        },
        loading: {
          position: "absolute",
          justifyContent: "center",
          alignItems: "center",
          zIndex: 1,
          height: "100%",
          width: "100%",
        },
        moreDetailsBtn: {
          marginTop: 20,
        },
      }),
    [backgroundColor, primaryColor, secondaryColor, zebraStripe, red]
  );

  const layoutScreen: RenderView = {
    default: (
      <>
        {appContext.appLoading && (
          <View
            style={[
              styles.loading,
              {
                backgroundColor: `rgba(0,0,0, 0.5)`,
              },
            ]}
          >
            <LoadingIndicator />
          </View>
        )}
        <View style={styles.container}>
          <TrakButton
            disabled={selectedRegion === null}
            btnStyle={styles.button}
            textStyle={styles.buttonText}
            size="big"
            onPress={() => {
              navigation.navigate("existingProfile");
            }}
            label={"Select Account"}
          />
          {isOnline ? (
            <UploadButton
              buttonStyle={styles.button}
              buttonTextStyle={styles.buttonText}
              onAfterUpload={handleAfterUpload}
              currentScans={currentScans}
              selectedRegion={selectedRegion}
              isDisabled={disabled}
            />
          ) : (
            <Text style={styles.offlineText}>Offline</Text>
          )}
          <View style={styles.section}>
            <Text style={styles.titleText}>Pending Uploads</Text>
            <Text style={styles.dataText}>{pendingScans}</Text>
          </View>
        </View>

        <RegionFooterComponent setAppLoading={appContext.setAppLoading} />
      </>
    ),
    sync: (
      <>
        <View style={[styles.container, styles.syncContainer]}>
          <Ionicons
            name="checkmark"
            size={40}
            color={secondaryColor}
            style={styles.checkMarkIcon}
          />
          <Text style={styles.syncText}>Sync Successfully Completed</Text>
          <Text style={styles.text}>
            Machines Verified in SugarCRM ({syncResponse?.verifiedInSugarCount})
          </Text>
          <Text style={styles.text}>
            New Accounts Requested ({syncResponse?.newAccountRequestedCount})
          </Text>
          <Text style={styles.text}>
            Machines Not found in SugarCRM({syncResponse?.machineNotFoundCount})
          </Text>
          <Text style={styles.text}>
            Machines Changed Location (
            {syncResponse?.machineChangedLocationCount})
          </Text>
          <TrakButton
            label={"More Details"}
            onPress={() => setHomeView("sync-details")}
            size="small"
            textColor="primary"
            btnColor="zebraStripe"
            btnStyle={styles.moreDetailsBtn}
          />
        </View>
        <View style={styles.backWrapper}>
          <TrakButton
            label={"Return to Main Screen"}
            onPress={() => setHomeView("default")}
          />
        </View>
      </>
    ),
    "sync-details": (
      <>
        <ScrollView style={[styles.syncDetailsContainer]}>
          <Text style={styles.syncText}>Sync Details</Text>
          <Divisor />
          <View>
            {syncResponse?.newAccounts && (
              <Text
                style={[
                  { color: red },
                  styles.fontLG,
                  styles.bold,
                  styles.text,
                ]}
              >
                New Accounts Requested ({syncResponse?.newAccounts?.length})
              </Text>
            )}
            {syncResponse?.newAccounts?.map((newAccount) => (
              <View
                style={[
                  styles.accountContainer,
                  { borderBottomColor: textColor },
                ]}
                key={newAccount.accountName}
              >
                <Text
                  style={[
                    styles.fontDF,
                    styles.bold,
                    styles.mlDF,
                    styles.mb,
                    styles.mt,
                  ]}
                >
                  {newAccount.accountName} ({newAccount.scanCount} Scans)
                </Text>
              </View>
            ))}
            {syncResponse?.existingAccounts?.map((existing) => (
              <View
                style={[
                  styles.accountContainer,
                  { borderBottomColor: textColor },
                ]}
                key={existing.accountName}
              >
                <Text style={[styles.fontLG, styles.bold, styles.mb]}>
                  {existing.accountName} -{" "}
                  <Text
                    style={[
                      styles.fontDF,
                      styles.bold,
                      { color: secondaryColor },
                    ]}
                  >
                    {existing?.verified?.length} Success
                  </Text>{" "}
                  /{" "}
                  <Text style={[styles.fontDF, styles.bold, { color: red }]}>
                    {existing?.notFound?.length + existing?.moved?.length}{" "}
                    Helpdesk
                  </Text>
                </Text>
                {existing?.verified && (
                  <Text
                    style={[
                      styles.fontDF,
                      styles.bold,
                      styles.mlDF,
                      { color: secondaryColor },
                    ]}
                  >
                    Machines Verified in SugarCRM ({existing?.verified?.length})
                  </Text>
                )}
                {existing?.verified?.map((verified) => (
                  <Text style={[styles.fontDF, styles.mlLG]} key={verified}>
                    {verified}
                  </Text>
                ))}
                {existing?.notFound && (
                  <Text
                    style={[
                      styles.fontDF,
                      styles.bold,
                      styles.mlDF,
                      { color: red },
                    ]}
                  >
                    Machines not found in SugarCRM({existing?.notFound?.length})
                  </Text>
                )}
                {existing?.notFound?.map((notFound) => (
                  <Text style={[styles.fontDF, styles.mlLG]} key={notFound}>
                    {notFound}
                  </Text>
                ))}
                {existing?.moved && (
                  <Text
                    style={[
                      styles.fontDF,
                      styles.bold,
                      styles.mlDF,
                      { color: red },
                    ]}
                  >
                    Machines Changed Location({existing?.moved?.length})
                  </Text>
                )}
                {existing?.moved?.map((moved) => (
                  <Text style={[styles.fontDF, styles.mlLG]} key={moved}>
                    {moved}
                  </Text>
                ))}
              </View>
            ))}
          </View>
        </ScrollView>
        <View style={styles.backWrapper}>
          <TrakButton
            label={"Return to Main Screen"}
            onPress={() => setHomeView("default")}
          />
        </View>
      </>
    ),
  };

  return (
    <>
      <SafeAreaView style={{ flex: 1 }}>{layoutScreen[homeView]}</SafeAreaView>
      {Platform.OS === "ios" && (
        <SafeAreaView style={{ backgroundColor: backgroundColor }} />
      )}
    </>
  );
}
