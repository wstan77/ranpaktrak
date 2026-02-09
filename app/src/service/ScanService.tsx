import { AxiosInstance } from "axios";
import { ScanData } from "@app/model/ScanData";
import eventEmitter from "@app/service/EventEmitterService";
import { EventType } from "@app/constants/EventType";
import { useContext } from "react";
import { AppContext } from "./contexts/AppContext";

export const useScanService = () => {
  const appContext = useContext(AppContext);

  const saveScan = async (scan: ScanData) => {
    const currentData = appContext?.ranpakScans;

    let currentScans: ScanData[] = currentData ?? [];
    currentScans.push(scan);
    appContext.setRanpakScans(currentScans);

    eventEmitter.emit(EventType.SCAN_SAVED, { scan: scan });
    console.log("[RANPAK] Scan has been saved locally.");
    console.log("[RANPAK] ScanData:", scan);
    console.log("[RANPAK] Updated storage state:", currentScans);
  };
  const clearScans = () => {
    appContext.setRanpakScans([]);
  };
  const uploadScans = async (
    axios: AxiosInstance,
    currentScanData: ScanData[],
    currentRegion: string
  ): Promise<any> => {
    if (currentScanData != undefined && currentScanData.length > 0) {
      // We want to upload the data to the server...
      return axios
        .post(`scans/${currentRegion}/`, currentScanData)
        .then((resp) => {
          clearScans(); // Clear the scans since they have been uploaded...
          return resp;
        })
        .catch((err) => err);
    }
  };

  return {
    saveScan,
    clearScans,
    uploadScans,
  };
};
