import {
  DrawerContentScrollView,
  DrawerItem,
  DrawerItemList,
} from "@react-navigation/drawer";
import { Text, View, useThemeColor } from "@app/components/Themed";
import { Linking, Platform, StyleSheet, TouchableOpacity } from "react-native";
import { useQueryClient } from "@tanstack/react-query";
import { useStorageState } from "@app/hooks/useStorageState";
import { useContext, useEffect } from "react";

import * as Application from "expo-application";

import { STORAGE_KEYS } from "@app/service/helpers/storage";
import TrakButton from "./TrakButton";
import { sync, useSync } from "@app/service/SyncService";
import { AppContext } from "@app/service/contexts/AppContext";
import { useSession } from "@app/service/contexts/AuthContext";
import { FontAwesome5 } from "@expo/vector-icons";
import { useCurrentUser } from "@app/service/contexts/CurrentUserContext";
import { getCustomersQueryKey } from "@app/service/CustomerService";
import { getMachinesQueryKey } from "@app/service/MachineService";
import { formatDate } from "@app/service/helpers/date";

export default function CustomDrawerContent(props) {
  const queryCl = useQueryClient();
  const { isLoading } = useSync();

  const appContext = useContext(AppContext);
  const { signOut } = useSession();
  const { currentUser } = useCurrentUser();

  const [[_selectedRegionSuccess, selectedRegion]] = useStorageState(
    STORAGE_KEYS.selectedRegion
  );
  const [[_lastSyncSuccess, lastSync], setLastSync] = useStorageState(
    STORAGE_KEYS.lastSync
  );

  const customerState = queryCl.getQueryState(
    getCustomersQueryKey(selectedRegion)
  );
  const machineState = queryCl.getQueryState(
    getMachinesQueryKey(selectedRegion)
  );

  const borderColor = useThemeColor("text");
  const primaryColor = useThemeColor("primary");

  useEffect(() => {
    console.log(
      "[DEBUG] (CustomDrawerContent) Region changed =>",
      selectedRegion
    );
    const lastSyncTime = Math.max(
      customerState.dataUpdatedAt,
      machineState.dataUpdatedAt
    );
    const lastSyncDate = new Date(lastSyncTime);
    console.log(
      "[DEBUG] (CustomDrawerContent) Customer last sync date =>",
      lastSyncDate
    );
    setLastSync(formatDate(lastSyncDate));
  }, [selectedRegion]);

  useEffect(() => {
    appContext?.setAppLoading(isLoading);
  }, [isLoading]);

  const handleSync = () => {
    props?.navigation?.closeDrawer();
    setTimeout(() => {
      appContext?.setAppLoading(true);
      sync(selectedRegion, queryCl, setLastSync);
    }, 100);
  };

  const getVersion = () => {
    return require("../../app.json").expo.version;
  };

  const styles = StyleSheet.create({
    container: { flex: 1, zIndex: 0 },
    footer: {
      borderTopWidth: 1,
      borderTopColor: borderColor,
      marginHorizontal: 10,
      padding: 10,
      marginBottom: Platform.OS === "ios" ? 20 : 5,
    },
    text: {
      fontSize: 14,
      marginBottom: 7,
    },
    signOut: {
      borderTopColor: borderColor,
      borderTopWidth: 1,
    },
    userGuide: {
      display: "flex",
      flexDirection: "row",
      alignItems: "center",
      borderTopWidth: 1,
      marginHorizontal: 10,
      paddingVertical: 5,
    },
    userGuideIcon: {
      fontSize: 30,
      color: primaryColor,
      marginHorizontal: 5,
    },
    userGuideText: {
      color: borderColor,
      marginHorizontal: 25,
      fontSize: 14,
      opacity: 0.6,
    },
    versionBottom: { marginVertical: 10 },
    versionText: { margin: 10, color: borderColor, textAlign: "right" },
  });

  return (
    <DrawerContentScrollView
      {...props}
      contentContainerStyle={styles.container}
    >
      {/* Normal drawer items */}
      <View style={{ flex: 1 }}>
        <DrawerItemList {...props} />
      </View>

      <TouchableOpacity
        style={styles.userGuide}
        onPress={() => {
          Linking.openURL(
            "https://ranpakcorp.sharepoint.com/:b:/r/sites/sp_g_inf_usertraining/Shared%20Documents/Ranpaktrak/Ranpaktrak_app_guide.pdf?csf=1&web=1&e=pvEiE7"
          );
        }}
      >
        <FontAwesome5 name={"home"} style={styles.userGuideIcon} />
        <Text style={styles.userGuideText}>User Guide</Text>
      </TouchableOpacity>
      <DrawerItem
        onPress={() => {
          signOut();
          console.log("Clicked switch user type");
        }}
        label={"Sign out"}
        icon={({ focused, size }) => {
          return <FontAwesome5 name="user-cog" size={24} color="#0297D9" />;
        }}
        style={styles.signOut}
      />

      {/* Custom Footer */}
      <View style={styles.footer}>
        <Text style={[styles.text, { fontWeight: "bold" }]}>
          {currentUser?.username}
        </Text>
        {lastSync && (
          <>
            <Text style={styles.text}>Last Synced: {lastSync}</Text>
          </>
        )}
        <TrakButton
          iconName="refresh"
          onPress={handleSync}
          label="Sync"
          btnColor="secondary"
          size="small"
        />

        <View style={styles.versionBottom}>
          <Text style={styles.versionText}>Version {getVersion()}</Text>
        </View>
      </View>
    </DrawerContentScrollView>
  );
}
