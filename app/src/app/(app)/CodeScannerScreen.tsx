import { StyleSheet, Dimensions } from "react-native";
import { useContext, useEffect, useState } from "react";
import { BarCodeScanningResult, Point } from "expo-camera";
import { CameraView } from "expo-camera/next";
import { useMachinesQuery } from "@app/service/MachineService";
import { useStorageStateSimple } from "@app/hooks/useStorageState";
import { usePermissions } from "@app/hooks/usePermissions";
import * as Device from "expo-device";
import { Text, View } from "@app/components/Themed";
import { AppContext } from "@app/service/contexts/AppContext";

const { width, height } = Dimensions.get("window");
const squareSize = Math.min(width, height) * 0.8;

export default function CodeScannerScreen({ navigation }) {
  const [scanningEnabled, setScanningEnabled] = useState(false);

  const appContext = useContext(AppContext);

  const { requestBarCodePermissions, isGranted } = usePermissions();

  const selectedCustomer = useStorageStateSimple("selectedCustomer");
  const machines = useMachinesQuery();

  const customerMachines = machines?.data?.filter(
    (machine) => machine.customerId === selectedCustomer.customerId
  );

  const handlePermissions = async () => {
    await requestBarCodePermissions();
    setTimeout(() => {
      setScanningEnabled(true);
    }, 1500);
  };

  useEffect(() => {
    handlePermissions();
  }, []);

  const isBarcodeInsideSquare = (cornerPoints: Point[]) => {
    const squareCenter = {
      x: width / 2,
      y: height / 2,
    };

    // Assuming each point in the array has an x and y property
    const [topRight, topLeft, bottomLeft, bottomRight] = cornerPoints;
    const centerToEdge = squareSize / 2;
    return (
      topLeft.y * width <= squareCenter.x + centerToEdge &&
      topLeft.x * height >= squareCenter.y - centerToEdge && 
      bottomLeft.y * width <= squareCenter.x + centerToEdge &&
      bottomLeft.x * height <= squareCenter.y + centerToEdge &&

      topRight.y * width >= squareCenter.x - centerToEdge &&
      topRight.x * height <= squareCenter.y + centerToEdge && 
      bottomRight.y * width >= squareCenter.x - centerToEdge &&
      bottomRight.x * height >= squareCenter.y - centerToEdge
      
    );
  };

  const handleBarCodeScan = (scannedData: BarCodeScanningResult) => {
    if (isBarcodeInsideSquare(scannedData.cornerPoints) && scanningEnabled) {
      setScanningEnabled(false);
      setTimeout(() => {
        setScanningEnabled(true);
      }, 1500);
      console.log("[DEBUG] scannedData =>", scannedData);

      const tagId = scannedData.data?.toLowerCase()?.split("tagid:")[1]; // Getting the tagID from QR Code

      const allRemoteAndLocalMachines = [
        ...customerMachines,
        ...(appContext.ranpakScans ?? []),
      ];

      const associatedMachine = allRemoteAndLocalMachines?.find(
        (machine) => machine?.tagId === tagId
      );

      // Navigation to ScanForm
      navigation.navigate("scanForm", {
        tagId,
        serialNumber: associatedMachine?.serialNumber,
        type: "QR",
      });
    }
  };

  if (!Device.isDevice) {
    // Expo Camera is not supported on emulators
    return (
      <Text style={styles.permisionsPrompt}>
        Camera is not supported on this device
      </Text>
    );
  }

  return (
    <>
      <View style={styles.container}>
        {isGranted ? (
          <>
            <CameraView
              style={StyleSheet.absoluteFillObject}
              onBarcodeScanned={handleBarCodeScan}
              barcodeScannerSettings={{
                barcodeTypes: ["qr"],
              }}
            />
            <View style={styles.overlay}>
              <View style={styles.square} />
            </View>
          </>
        ) : (
          <>
            <Text style={styles.permisionsPrompt}>
              You must allow this application to use the camera to continue.
              Permit the application, then come back to this page please.
            </Text>
          </>
        )}
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: "column",
    justifyContent: "center",
  },
  camera: {
    flex: 1,
    width: "100%",
  },
  overlay: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "transparent",
  },
  square: {
    width: squareSize,
    height: squareSize,
    borderWidth: 2,
    borderColor: "white",
    backgroundColor: "transparent",
  },
  scannedText: {
    color: "white",
    fontSize: 20,
  },
  permisionsPrompt: {
    marginHorizontal: 20,
    marginTop: 25,
    textAlign: "center",
  },
});
