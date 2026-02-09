import { useContext, useEffect, useMemo, useState } from "react";
import {
  FlatList,
  Keyboard,
  SafeAreaView,
  StyleSheet,
  TouchableOpacity,
} from "react-native";

import { TrakButton } from "@app/components";
import { Text, TextInput, View, useThemeColor } from "@app/components/Themed";
import { useDebounce } from "@app/hooks";
import { useStorageState } from "@app/hooks/useStorageState";
import { CustomerData } from "@app/model/CustomerData";
import { useCustomersQuery } from "@app/service/CustomerService";
import eventEmitter from "@app/service/EventEmitterService";
import { AppContext } from "@app/service/contexts/AppContext";
import { orderBy, sortBy } from "@app/service/helpers/arrays";
import { STORAGE_KEYS } from "@app/service/helpers/storage";
import { SearchResult } from "minisearch";

const NO_ACCOUNTS = [];

export default function ExistingProfileScreen({ navigation }) {
  const {
    data: { customers: baseCustomers, searchIndex: baseSearchIndex },
    isLoading,
  } = useCustomersQuery();
  const appContext = useContext(AppContext);

  const ranpakAccounts = appContext?.ranpakAccounts ?? NO_ACCOUNTS;

  const newAccounts = useMemo(() => {
    return ranpakAccounts?.map((rpkScan, i) => ({
      accountName: rpkScan.accountName,
      city: rpkScan.city,
      regionLk: rpkScan.regionLk,
      customerId: i,
      notes: rpkScan.notes,
    }));
  }, [ranpakAccounts]);

  const customers = useMemo(() => {
    const allCustomers = [...newAccounts, ...baseCustomers];
    return orderBy(allCustomers, "accountName");
  }, [newAccounts, baseCustomers]);

  const searchIndex = useMemo(() => {
    if (newAccounts) {
      newAccounts.forEach((account) => {
        try {
          baseSearchIndex.add(account);
        } catch (e) {}
      });
      return baseSearchIndex;
    }
    return null;
  }, [baseSearchIndex, newAccounts]);

  const [, setSelectedCustomer] = useStorageState(
    STORAGE_KEYS.selectedCustomer
  );
  const [searchText, onChangeSearch] = useState("");
  const [showAddButton, setShowAddButton] = useState(true);
  const [filteredData, setFilteredData] = useState([]);
  const debouncedSearchText = useDebounce(searchText, 25);
  const backgroundColor = useThemeColor("background");
  const zebraStripeColor = useThemeColor("zebraStripe");

  useEffect(() => {
    const listeners = [
      Keyboard.addListener("keyboardWillShow", () => {
        setShowAddButton(false);
      }),
      Keyboard.addListener("keyboardDidShow", () => {
        setShowAddButton(false);
      }),
      Keyboard.addListener("keyboardWillHide", () => {
        setShowAddButton(true);
      }),
      Keyboard.addListener("keyboardDidHide", () => {
        setShowAddButton(true);
      }),
    ];

    return () => {
      listeners.forEach((listener) => listener.remove());
    };
  }, []);

  useEffect(() => {
    if (isLoading || !searchIndex) {
      if (customers?.length > 0) {
        setFilteredData(customers.slice(0, 15));
        return;
      }
    }
    let filtered = customers;
    if (debouncedSearchText) {
      const text = debouncedSearchText.toLowerCase();
      const results = searchIndex.search(debouncedSearchText);

      filtered = sortBy(
        orderBy(orderBy(results, "city"), "accountName"),
        (r: SearchResult) => {
          if (r.accountName.toLowerCase().indexOf(text) === 0) return 1;
          if (r.city.toLowerCase().indexOf(text) === 0) return 2;
          return 3;
        }
      ).map((result) => ({
        accountName: result.accountName,
        city: result.city,
        customerId: result.customerId,
        notes: result.notes,
      }));
    }
    setFilteredData(filtered.slice(0, 15));
  }, [searchIndex, debouncedSearchText, customers]);

  const selectCustomer = async (item: CustomerData) => {
    setSelectedCustomer(item);
    eventEmitter.emit("onCompanySelection", {
      accountName: item.accountName,
      customerId: item.customerId,
      city: item.city,
      notes: item.notes,
    });
    navigation.navigate("scanOption");
  };

  const Item = ({ item, style }) => {
    if (!item) return null;
    return (
      <View style={[styles.item, style]}>
        <TouchableOpacity onPress={() => selectCustomer(item)}>
          <Text style={styles.title}>{item.accountName}</Text>
          <Text style={styles.location}>{item.city}</Text>
        </TouchableOpacity>
      </View>
    );
  };
  const renderItem = ({ item, index }) => {
    const itemBg = index % 2 === 0 ? backgroundColor : zebraStripeColor;
    return <Item item={item} style={{ backgroundColor: itemBg }} />;
  };

  const createNewAccount = async () => {
    await setSelectedCustomer(undefined);
    navigation.navigate("createNewAccount");
  };

  return (
    <SafeAreaView style={styles.flex_1}>
      <Text style={styles.header}>Select an Existing Profile</Text>
      <View style={styles.content}>
        <TextInput
          style={styles.input}
          placeholder="Type to Search"
          onChangeText={(newText) => onChangeSearch(newText)}
        />
        <View style={styles.flex_1}>
          <View style={styles.listContainer}>
            {isLoading ? (
              <Text style={styles.loadingText}>Loading Accounts...</Text>
            ) : (
              <FlatList
                data={filteredData}
                renderItem={renderItem}
                keyExtractor={(item) => item.customerId}
              />
            )}
          </View>
        </View>
      </View>
      {showAddButton && (
        <TrakButton
          label="Create New"
          iconName="plus"
          onPress={createNewAccount}
          btnStyle={styles.button}
        />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  flex_1: { flex: 1 },
  content: { flex: 1, marginHorizontal: 10 },
  header: {
    fontWeight: "bold",
    fontSize: 22,
    marginTop: 20,
    paddingBottom: 20,
    textAlign: "center",
  },
  input: {
    borderColor: "#BEC4CD",
    borderWidth: 2,
    borderTopRightRadius: 5,
    borderTopLeftRadius: 5,
    paddingVertical: 10,
    paddingHorizontal: 15,
  },
  button: { marginHorizontal: 10, marginVertical: 5 },
  listContainer: {
    marginBottom: 20,
  },
  loadingText: {
    fontSize: 20,
    textAlign: "center",
    marginTop: 20,
  },
  item: {
    padding: 10,
  },
  title: {
    fontSize: 13,
    fontWeight: "bold",
  },
  location: {
    fontSize: 12,
    marginTop: 4,
  },
});
