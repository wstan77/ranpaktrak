import { Camera } from "expo-camera/next";
import { useState } from "react";
import { Linking } from "react-native";

export const usePermissions = () => {
  const [isGranted, setIsGranted] = useState(false);

  const requestBarCodePermissions = async () => {
    const { status } = await Camera.requestCameraPermissionsAsync();
    if (status === "denied") {
      setIsGranted(false);
      Linking.openURL("app-settings:");
    } else if (status === "granted") {
      setIsGranted(true);
    }
  };

  return {
    isGranted,
    requestBarCodePermissions,
  };
};
