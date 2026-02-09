import { useContext, useEffect, useState } from "react";
import {
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  ScrollView,
} from "react-native";
import Toast from "react-native-toast-message";

import { Text, useThemeColor, View } from "@app/components/Themed";
import { TrakButton, TrakInput } from "@app/components";
import { AccountData, initialAccountData } from "@app/model/AccountData";
import { useAddCustomer } from "@app/service/CustomerService";
import { AppContext } from "@app/service/contexts/AppContext";
import { useStorageState, useStorageStateSimple } from "@app/hooks";
import { STORAGE_KEYS } from "@app/service/helpers/storage";

export default function CreateNewAccount({ navigation }) {
  const [form, setForm] = useState<AccountData>(initialAccountData);
  const white = useThemeColor("background");
  const zebraStripe = useThemeColor("zebraStripe");
  const addCustomer = useAddCustomer();
  const appContext = useContext(AppContext);
  const selectedCustomer = useStorageStateSimple(STORAGE_KEYS.selectedCustomer);
  const selectedRegion = useStorageStateSimple(STORAGE_KEYS.selectedRegion);
  const [, setSelectedCustomer] = useStorageState(
    STORAGE_KEYS.selectedCustomer
  );

  const [formErrors, setFormErrors] = useState({});

  const validateInputs = () => {
    if (!form.accountName || !form.city) {
      setFormErrors({
        accountName: !form.accountName && "This value is required",
        city: !form.city && "This value is required",
      });
      return false;
    }

    return true;
  };

  const handleSendForm = () => {
    appContext.setAppLoading(true);
    if (validateInputs()) {
      if (selectedCustomer) {
        Promise.all([
          addCustomer.edit(selectedCustomer, {
            ...form,
            customerId: selectedCustomer?.customerId,
          }),
        ])
          .finally(() => {
            appContext.setAppLoading(false);
            navigation.goBack();

            setSelectedCustomer({
              ...form,
              customerId: selectedCustomer?.customerId,
            });
            Toast.show({
              type: "success",
              text1: "Account Saved Successfully",
              position: "bottom",
            });
          })
          .catch((err) => {
            console.error(err);
          });
      } else {
        Promise.all([addCustomer.add({ ...form, regionLk: selectedRegion })])
          .finally(() => {
            appContext.setAppLoading(false);
            navigation.goBack();
            Toast.show({
              type: "success",
              text1: "Account Saved Successfully",
              position: "bottom",
            });
          })
          .catch((err) => {
            console.error(err);
          });
      }
    } else {
      appContext.setAppLoading(false);
    }
  };

  useEffect(() => {
    if (selectedCustomer) {
      setForm({
        ...form,
        accountName: selectedCustomer?.accountName,
        city: selectedCustomer?.city,
        notes: selectedCustomer?.notes,
      });
    }
  }, [selectedCustomer?.accountName]);

  return (
    <>
      <View style={styles.container}>
        <KeyboardAvoidingView
          behavior={Platform.OS === "ios" ? "padding" : null}
          style={styles.container}
        >
          <Text style={styles.title}>
            {selectedCustomer?.accountName
              ? "Edit Account Details "
              : "Create a New Account"}
          </Text>
          <ScrollView style={styles.formContainer}>
            <TrakInput
              label={"Account Name"}
              isRequired
              value={form.accountName}
              onChange={(value) => setForm({ ...form, accountName: value })}
              errorMessage={formErrors["accountName"]}
            />
            <TrakInput
              label={"Location"}
              isRequired
              value={form.city}
              onChange={(value) => setForm({ ...form, city: value })}
              errorMessage={formErrors["city"]}
            />
            <TrakInput
              label={"Notes"}
              value={form.notes}
              isMultiline
              onChange={(value) => setForm({ ...form, notes: value })}
            />
          </ScrollView>
        </KeyboardAvoidingView>
      </View>
      <View
        style={[
          styles.screenFooter,
          { backgroundColor: white, borderColor: zebraStripe },
        ]}
      >
        <TrakButton
          label={appContext.appLoading ? "Saving..." : "Save"}
          onPress={appContext.appLoading ? () => null : handleSendForm}
          btnStyle={styles.buttonSave}
          disabled={appContext.appLoading}
        />
        <TrakButton
          label={"Cancel"}
          onPress={() => navigation.goBack()}
          btnStyle={{ ...styles.buttonCancel, backgroundColor: white }}
          textStyle={styles.textCancel}
        />
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 10,
    paddingBottom: 5,
  },
  title: {
    fontWeight: "bold",
    fontSize: 16,
    textAlign: "center",
  },
  formContainer: {
    flex: 1,
    marginTop: 10,
    paddingHorizontal: 40,
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
    paddingHorizontal: 35,
  },
  textSave: {
    fontSize: 14,
  },
  buttonCancel: {
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderWidth: 0.5,
  },
  textCancel: {
    fontSize: 14,
  },
});
