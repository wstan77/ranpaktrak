import { useContext, useEffect, useRef, useState } from "react";
import {
  Alert,
  Image,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import { CameraView } from "expo-camera/next";
import { Ionicons } from "@expo/vector-icons";
import TextRecognition from "@react-native-ml-kit/text-recognition";
import { ReactNativeModal } from "react-native-modal";
import Toast from "react-native-toast-message";

import {
  Content,
  Divisor,
  TrakButton,
  TrakInput,
  TrakBanner,
} from "@app/components";
import SelectedCompanyHeader from "@app/components/SelectedCompanyHeader";
import { Text, useThemeColor, View } from "@app/components/Themed";
import { useScanService } from "@app/service/ScanService";
import { useLocation } from "@app/hooks/useLocation";
import { useStorageStateSimple } from "@app/hooks";
import { useMachinesQuery } from "@app/service/MachineService";
import { STORAGE_KEYS } from "@app/service/helpers/storage";
import { AppContext } from "@app/service/contexts/AppContext";
import { MachineData } from "@app/model/MachineData";
import {
  initialManualEntryForm,
  ManualEntryForm,
  TextRecognizedList,
} from "@app/components/TextRecognizedList";

export default function ManualScannerScreen({ navigation, route }) {
  const [form, setForm] = useState<ManualEntryForm>(initialManualEntryForm);
  const [textRecognized, setTextRecognized] = useState<string[]>([]);
  const [serialSuggestions, setSerialSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState<boolean>(false);

  const { tagId, type, serialNumber } = route.params ? route.params : "";

  const [formErrors, setFormErrors] = useState({});
  const [cameraActive, setCameraActive] = useState<boolean>(false);
  const white = useThemeColor("background");
  const zebraStripe = useThemeColor("zebraStripe");
  const gray = useThemeColor("tint");
  const scanService = useScanService();
  const { location } = useLocation();
  const appContext = useContext(AppContext);

  const selectedCustomer = useStorageStateSimple(STORAGE_KEYS.selectedCustomer);
  const selectedRegion = useStorageStateSimple(STORAGE_KEYS.selectedRegion);
  const machines = useMachinesQuery();
  const myMachines = typeof machines?.data === "object" ? machines.data : [];
  const gpsLatitude = location?.coords?.latitude;
  const gpsLongitude = location?.coords?.longitude;
  const gpsAccuracy = location?.coords?.accuracy;

  const cameraRef = useRef(null);

  const ranpakAccounts = appContext?.ranpakAccounts ?? [];

  const customerMachines = myMachines?.filter(
    (machine) => machine?.customerId === selectedCustomer?.customerId
  );

  const remoteAndLocalMachines = [
    ...customerMachines,
    ...(appContext.ranpakScans ?? []),
  ];

  const associatedTag = remoteAndLocalMachines?.find(
    (machine) => machine.tagId === tagId
  );

  const associatedSerialAndTag = remoteAndLocalMachines?.find(
    (machine) =>
      machine?.serialNumber === form.serialNumber && machine.tagId === tagId
  );

  const associatedMachine = customerMachines?.find(
    (machine) => machine?.serialNumber === form.serialNumber
  );

  const isTagIdSame = (associatedMachine: MachineData, tagId: String) => {
    return !tagId ? false : associatedMachine?.tagId !== tagId;
  };

  const isManuallyCreated = ranpakAccounts.find(
    (rpk) =>
      rpk.accountName === selectedCustomer?.accountName &&
      rpk.city === selectedCustomer?.city
  );

  const filterOnTyping = (machine: MachineData) =>
    customerMachines.some((m) => m.serialNumber === machine.serialNumber);

  useEffect(() => {
    setForm({ ...form, serialNumber });
  }, [serialNumber]);

  const sendData = (enterAnother?: boolean) => {
    const data = associatedSerialAndTag ? { ...form, tagId: "" } : { ...form };
    const now = new Date();
    scanService
      .saveScan({
        ...data,
        ...(isManuallyCreated
          ? {
              manualAccountName: selectedCustomer.accountName,
              manualLocation: selectedCustomer.city,
            }
          : { customerId: selectedCustomer.customerId }),
        scanTypeLk: type ?? "MANUAL",
        scanGpsLatitude: gpsLatitude,
        scanGpsLongitude: gpsLongitude,
        scanGpsAccuracy: gpsAccuracy,
        regionLk: selectedRegion,
        tagId,
        scanDate: now.toISOString(),
      })
      .then(() => {
        console.log("[DEBUG] Scan was saved successfully...");
        Toast.show({
          type: "success",
          text1: "Save Successful",
          text2: "Scan Saved Successfully",
          position: "bottom",
        });

        // Navigation back to scanOption or form clean up...
        if (enterAnother) {
          setForm(initialManualEntryForm);
        } else {
          navigation.navigate("scanOption");
        }
      });
  };

  const handleValidateAndSend = (enterAnother?: boolean) => {
    if (validateInputs()) {
      if (associatedMachine && isTagIdSame(associatedMachine, tagId)) {
        Alert.alert(
          "Serial Number already associated",
          "The serial number entered is already associated with a QR/NFC tag.  Would you like to continue anyway and associate the machine with the current tag?",
          [
            {
              text: "Cancel",
              style: "cancel",
            },
            {
              text: "Continue",
              onPress: () => sendData(enterAnother),
            },
          ]
        );
      } else {
        sendData(enterAnother);
      }
    }
  };

  const validateInputs = () => {
    if (!form.serialNumber) {
      setFormErrors({ serialNumber: "This value is required" });
      return false;
    }
    return true;
  };

  const handleTakePicture = async () => {
    if (cameraRef) {
      try {
        const data = await cameraRef.current.takePictureAsync();
        const result = await TextRecognition.recognize(data.uri);
        const splittedText = result?.text?.split(/\r?\n|\r|\n/g);
        setTextRecognized(splittedText);
        setForm({ ...form, image: data.uri });
        setCameraActive(false);
      } catch (e) {
        console.log(e);
      }
    }
  };

  const handleClickSerialNumber = () => {
    setShowSuggestions(true);
  };

  const handlePressOutSerialNumber = () => {
    setShowSuggestions(false);
  };

  const handlePress = (text: string) => {
    setTextRecognized([]);
    setForm({ ...form, serialNumber: text });
  };

  return (
    <>
      <View style={styles.container}>
        <SelectedCompanyHeader />
        {cameraActive ? (
          <CameraView style={StyleSheet.absoluteFillObject} ref={cameraRef}>
            <View style={styles.cameraContainer}>
              <View style={styles.cameraOptions}>
                <TouchableOpacity
                  style={styles.option}
                  onPress={() => handleTakePicture()}
                >
                  <Ionicons name="camera" size={40} color={white} />
                </TouchableOpacity>
                <TouchableOpacity
                  onPress={() => setCameraActive(false)}
                  style={styles.option}
                >
                  <Ionicons name="close" size={40} color={white} />
                </TouchableOpacity>
              </View>
            </View>
          </CameraView>
        ) : (
          <KeyboardAvoidingView
            behavior={Platform.OS === "ios" ? "padding" : null}
            style={styles.container}
          >
            {!tagId && (
              <>
                <Text style={styles.title}>Manual Entry (No Scan)</Text>
                <Divisor />
              </>
            )}
            <Content>
              <ScrollView
                style={styles.scrollContent}
                keyboardShouldPersistTaps="handled"
              >
                {tagId && (
                  <>
                    <TrakBanner
                      text={
                        associatedTag ? "Scan Successful" : "New Tag Found!"
                      }
                      iconName={
                        associatedTag ? "check-circle" : "exclamation-circle"
                      }
                      mode={associatedTag ? "success" : "warning"}
                    />
                    <View style={styles.tagIdContainer}>
                      <Text style={styles.tagIdText}>
                        Tag ID: <Text style={styles.bold}>{tagId} </Text>
                      </Text>
                    </View>
                  </>
                )}

                <View style={styles.serialNumberWrapper}>
                  <TrakInput
                    label={"Serial Number"}
                    isRequired
                    isDisabled={serialNumber !== undefined}
                    value={form.serialNumber}
                    onChange={(value) => {
                      setForm({ ...form, serialNumber: value });
                      if (form.serialNumber) {
                        const filteredMachines = remoteAndLocalMachines?.filter(
                          (sm) =>
                            new RegExp(value, "i").test(sm?.serialNumber) ||
                            new RegExp(value, "i").test(sm?.legacySerialNumber)
                        );
                        setSerialSuggestions(filteredMachines);
                      }
                    }}
                    onPressCameraButton={() => setCameraActive(true)}
                    errorMessage={formErrors["serialNumber"]}
                    onClick={handleClickSerialNumber}
                    onBlur={handlePressOutSerialNumber}
                  />
                  {form.image && (
                    <Image
                      source={{ uri: form.image }}
                      width={240}
                      height={170}
                      style={styles.imageTaken}
                    />
                  )}
                  {showSuggestions && form?.serialNumber && (
                    <ScrollView
                      style={[
                        styles.autoCompleteView,
                        { backgroundColor: white },
                      ]}
                      keyboardShouldPersistTaps="handled"
                    >
                      {serialSuggestions?.length > 0 &&
                        serialSuggestions
                          .filter(filterOnTyping)
                          .map((machine, i) => (
                            <TouchableOpacity
                              key={i}
                              style={[
                                styles.resultsItem,
                                {
                                  borderBottomColor: gray,
                                },
                              ]}
                              onPress={() => {
                                setForm({
                                  ...form,
                                  serialNumber: machine.serialNumber,
                                });
                                setShowSuggestions(false);
                                setSerialSuggestions([]);
                              }}
                            >
                              <Text>
                                {machine.serialNumber} (
                                {machine.legacySerialNumber})
                              </Text>
                            </TouchableOpacity>
                          ))}
                    </ScrollView>
                  )}
                </View>
                <TrakInput
                  label={"Notes"}
                  value={form.notes}
                  onChange={(value) => setForm({ ...form, notes: value })}
                  isMultiline
                />
              </ScrollView>
            </Content>
          </KeyboardAvoidingView>
        )}
      </View>
      {!cameraActive && (
        <View
          style={[
            styles.screenFooter,
            { backgroundColor: white, borderColor: zebraStripe },
          ]}
        >
          <TrakButton
            label={"Save"}
            onPress={() => handleValidateAndSend(false)}
            size="small"
          />
          <TrakButton
            label={"Save and Enter Another"}
            onPress={() => handleValidateAndSend(true)}
            size="small"
          />
        </View>
      )}

      {/*  Recognized text section after press camera icon >>>>>>>>>>> */}
      {textRecognized?.length > 0 && (
        <View style={styles.container}>
          <ReactNativeModal
            isVisible={textRecognized?.length > 0}
            animationIn="slideInRight"
            animationOut="slideOutRight"
            backdropTransitionOutTiming={300}
            style={[styles.textsModal, { backgroundColor: white }]}
          >
            <Text style={styles.title}>
              Ranpaktrak detected the following information in the photo -
              please choose the serial number, if it exists.
            </Text>
            {form.image && (
              <Image
                source={{ uri: form.image }}
                width={240}
                height={170}
                style={styles.imageTaken}
              />
            )}
            <TextRecognizedList
              textRecognized={textRecognized}
              onPressText={handlePress}
            />
            <View style={styles.row}>
              <TrakButton
                onPress={() => [setTextRecognized([]), setCameraActive(true)]}
                label="Retake"
                btnStyle={styles.container}
              />
              <TrakButton
                onPress={() => setTextRecognized([])}
                label="Cancel"
                btnStyle={styles.container}
                btnColor="zebraStripe"
              />
            </View>
          </ReactNativeModal>
          {/* <<<<<<<<<<<<<<< */}
        </View>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    zIndex: 0,
  },
  row: { display: "flex", flexDirection: "row", gap: 2 },
  title: {
    fontWeight: "bold",
    fontSize: 16,
    textAlign: "center",
    marginVertical: 15,
  },
  screenFooter: {
    display: "flex",
    flexDirection: "row",
    gap: 10,
    paddingBottom: 25,
    justifyContent: "center",
    borderWidth: 1,
    paddingTop: 10,
  },
  buttonSave: {
    paddingVertical: 10,
    paddingHorizontal: 20,
  },
  textSave: {
    fontSize: 14,
    color: "#fff",
  },
  camera: { flex: 1 },
  cameraContainer: {
    position: "absolute",
    bottom: 10,
    width: "100%",
    backgroundColor: "transparent",
  },
  cameraOptions: {
    display: "flex",
    flexDirection: "row",
    alignSelf: "center",
    gap: 20,
    backgroundColor: "transparent",
  },
  option: {
    alignSelf: "center",
  },
  imageTaken: { alignSelf: "center" },
  tagIdContainer: { marginTop: 25, marginBottom: 10 },
  tagIdText: { fontSize: 16, color: "#aeaeb0" },
  bold: { fontWeight: "bold" },
  textsModal: {
    justifyContent: "flex-start",
    paddingVertical: 10,
    paddingHorizontal: 20,
  },
  text: {
    flex: 1,
    justifyContent: "center",
    textAlignVertical: "center",
  },
  textContainer: {
    display: "flex",
    flexDirection: "row",
    paddingVertical: 10,
    alignSelf: "center",
    justifyContent: "center",
    borderBottomWidth: 1,
  },
  scrollContent: {
    paddingVertical: 20,
    paddingHorizontal: 25,
  },
  autoCompleteView: {
    width: "100%",
    position: "absolute",
    top: "80%",
    zIndex: 1000,
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
    maxHeight: 150,
  },
  resultsItem: {
    paddingVertical: 15,
    paddingHorizontal: 15,
    borderBottomWidth: 0.5,
    zIndex: 1000,
  },
  serialNumberWrapper: {
    position: "relative",
    zIndex: 1000,
  },
});
