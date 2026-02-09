import Toast from "react-native-toast-message";
import { useAxios } from "@app/service/contexts/AxiosContext";
import { TextStyle, ViewStyle } from "react-native";
import { TrakButton } from "@app/components";
import { useState } from "react";
import { View } from "@app/components/Themed";

import eventEmitter from "@app/service/EventEmitterService";
import { EventType } from "@app/constants/EventType";
import { ScanData, SyncData } from "@app/model/ScanData";
import { useScanService } from "@app/service/ScanService";
import { LoadingIndicator } from "./LoadingIndicator";
import { useQueryClient } from "@tanstack/react-query";
import { getMachinesQueryKey } from "@app/service/MachineService";

interface UploadButtonProps {
  buttonStyle: ViewStyle;
  buttonTextStyle: TextStyle;
  onAfterUpload: (response: SyncData) => void;
  currentScans: ScanData[];
  selectedRegion: string;
  isDisabled: boolean;
}

export default function UploadButton({
  buttonStyle,
  buttonTextStyle,
  onAfterUpload,
  currentScans,
  selectedRegion,
  isDisabled,
}: UploadButtonProps) {
  const { authAxios } = useAxios();
  const [isLoading, setLoading] = useState(false);
  const scanService = useScanService();
  const queryCl = useQueryClient();

  const throwError = (message: string) => {
    Toast.show({
      type: "error",
      text1: "Upload Failure",
      text2: "" + message,
      position: "bottom",
    });
  };

  const uploadData = async () => {
    setLoading(true);
    // Upload scans
    if (currentScans && currentScans?.length == 0) return;
    scanService
      .uploadScans(authAxios, currentScans, selectedRegion)
      .then((response) => {
        // Do not show spinner
        setTimeout(async () => {
          setLoading(false);
          await queryCl.invalidateQueries({
            queryKey: getMachinesQueryKey(selectedRegion),
          });
          if (response?.data?.status !== 200) {
            throwError(response.message);
            return;
          }
          onAfterUpload(response?.data);
        }, 1000);
      })
      .finally(() => {
        eventEmitter.emit(EventType.SCANS_UPLOADED, currentScans);
        Toast.show({
          type: "success",
          text1: "Upload Successful",
          text2: "Your scans have been uploaded successfully...",
          position: "bottom",
        });
      })
      .catch((err) => throwError(err.message));
  };
  return (
    <>
      {isLoading ? (
        <View
          style={{
            position: "absolute",
            justifyContent: "center",
            alignItems: "center",
            backgroundColor: "rgba(255, 255, 255, 0.5)",
            zIndex: 1,
            height: "100%",
            width: "100%",
          }}
        >
          <LoadingIndicator />
        </View>
      ) : (
        <></>
      )}
      <TrakButton
        disabled={isDisabled}
        btnStyle={buttonStyle}
        textStyle={buttonTextStyle}
        onPress={uploadData}
        label={"Upload Scans"}
        size="big"
      />
    </>
  );
}
