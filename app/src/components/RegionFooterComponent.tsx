// RegionSelector.js
import React, {useContext, useEffect, useState} from "react";
import { StyleSheet, Alert } from "react-native";
import { ReactNativeModal } from "react-native-modal";

import Regions, { RegionNames } from "@app/constants/Regions";
import { TrakButton } from "@app/components";
import eventEmitter from "../service/EventEmitterService";
import {
  useStorageState,
  useStorageStateSimple,
} from "@app/hooks/useStorageState";
import { Text, View, useThemeColor } from "@app/components/Themed";
import { EventType } from "@app/constants/EventType";
import { useScanService } from "@app/service/ScanService";
import { STORAGE_KEYS } from "@app/service/helpers/storage";
import {sync, useSync} from "@app/service/SyncService";
import {useQueryClient} from "@tanstack/react-query";
import {AppContext} from "@app/service/contexts/AppContext";

interface RegionFooterProps {
  setAppLoading?: (value: boolean) => void;
}

const RegionSelectorScreen = ({ onClose, toggleModal }) => {
  return (
    <View style={styles.regionSelector}>
      <Text style={styles.selectText}>Please Select a Region</Text>
      <TrakButton
        label="North America"
        onPress={() => {
          onClose(Regions.North_America);
        }}
      />

      <TrakButton
        label="Europe/Middle East"
        onPress={() => {
          onClose(Regions.Europe_and_Middle_East);
        }}
      />

      <TrakButton
        label="Asia Pacific"
        onPress={() => {
          onClose(Regions.Asia_Pacific);
        }}
      />
      <TrakButton label="Cancel" onPress={toggleModal} btnColor="zebraStripe" />
    </View>
  );
};

const RegionFooterComponent = ({ setAppLoading }: RegionFooterProps) => {
  const scanService = useScanService();
  const queryCl = useQueryClient();

  const {isLoading} = useSync();

  const appContext = useContext(AppContext);

  const [, setLastSync] = useStorageState(STORAGE_KEYS.lastSync);
  const [isModalVisible, setModalVisible] = useState(false);

  const [[, selectedRegion], setSelectedRegion] = useStorageState(
    STORAGE_KEYS.selectedRegion
  );

  const selectedSimpleRegion = useStorageStateSimple(
    STORAGE_KEYS.selectedRegion
  );

  const backgroundColor = useThemeColor("zebraStripe");

  function getRegionDescription() {
    return selectedRegion != undefined
      ? RegionNames[selectedRegion]
      : "No region selected";
  }

  const toggleModal = () => {
    setModalVisible(!isModalVisible);
  };

  useEffect(() => {
    appContext?.setAppLoading(isLoading);
  }, [isLoading]);

  const handleRegionSelect = async (region) => {
    if (RegionNames[selectedRegion] != undefined && selectedRegion != region) {
      Alert.alert(
        "Are you sure?",
        "Changing regions will clear all downloaded account and machine " +
          'information. You will need to utilize the "Sync Data" function to get account and machine information for the updated region.',
        [
          {
            text: "Yes",
            onPress: () => {
              toggleModal();
              setSelectedRegion(region);
              setTimeout(() => {
                // Clear scans
                scanService.clearScans();
                appContext.setAppLoading(true);
                sync(region, queryCl, setLastSync).then(() => {
                  eventEmitter.emit(EventType.REGION_SELECTION, {
                    region: region,
                  });
                });
              }, 100);
            },
          },
          {
            text: "No",
            onPress: () => {
              toggleModal();
            },
          },
        ]
      );
    } else {
      toggleModal();
      setSelectedRegion(region);
      setTimeout(() => {
        appContext.setAppLoading(true);
        sync(selectedSimpleRegion, queryCl, setLastSync);
      }, 100);
    }
  };
  return (
    <View id={"regionScreen"} style={[styles.footerContain, { backgroundColor }]}>
      <View style={{backgroundColor}}>
        <Text style={styles.label}>Selected Region</Text>
        <Text style={styles.selectedRegion}>{getRegionDescription()}</Text>
      </View>
      <TrakButton
        onPress={toggleModal}
        label="Change"
        size="small"
        btnColor="background"
        isBordered
      />
      <ReactNativeModal
        isVisible={isModalVisible}
        animationIn="slideInRight"
        animationOut="slideOutRight"
        backdropTransitionOutTiming={300}
      >
        <RegionSelectorScreen
          onClose={handleRegionSelect}
          toggleModal={toggleModal}
        />
      </ReactNativeModal>
    </View>
  );
};

export { RegionSelectorScreen, RegionFooterComponent };

const styles = StyleSheet.create({
  regionSelector: {
    padding: 16,
    borderRadius: 5,
    gap: 10,
  },
  selectText: {
    fontWeight: "bold",
    textAlign: "center",
  },
  footerContain: {
    flexDirection: "row", // Horizontal arrangement
    justifyContent: "space-between", // Space evenly between View and TouchableOpacity
    padding: 16,
  },
  label: {
    opacity: 0.7,
  },
  selectedRegion: {
    fontWeight: "bold",
  },
});
