import { useStorageStateSimple } from "@app/hooks/useStorageState";
import { AccountData } from "@app/model/AccountData";
import { CustomerData } from "@app/model/CustomerData";
import { useAxios } from "@app/service/contexts/AxiosContext";
import { QueryKey, useQuery } from "@tanstack/react-query";
import MiniSearch from "minisearch";
import { useContext } from "react";
import Toast from "react-native-toast-message";
import { AppContext } from "./contexts/AppContext";
import { sortCustomersByName } from "./helpers/arrays";
import { STORAGE_KEYS } from "./helpers/storage";

export type NewCustomer = Omit<CustomerData, "customerId">;

export const getCustomersQueryKey = (region: string): QueryKey => [
  "customers",
  region,
];

export const useCustomersQuery = () => {
  const { authAxios } = useAxios();
  const selectedRegion = useStorageStateSimple(STORAGE_KEYS.selectedRegion);

  return useQuery({
    queryKey: getCustomersQueryKey(selectedRegion),
    staleTime: 1000 * 60 * 5,
    queryFn: async () => {
      let customers: CustomerData[] = [];
      if (selectedRegion != null) {
        const res = await authAxios.get<CustomerData[]>(
          `/customers/${selectedRegion}/`
        );
        if (res.status != 200) {
          Toast.show({
            type: "error",
            text1: "Error Encountered",
            text2: `Failed to fetch customers for region ${selectedRegion}`,
            position: "bottom",
          });
        }
        console.log(
          `[RANPAK] (Region: ${selectedRegion}) Finished loading ${res?.data?.length} customers.`
        );
        customers = res?.data;
      }
      return {
        customers: sortCustomersByName(customers),
        searchIndex: getSearchIndex(customers),
      };
    },
  });
};

function getSearchIndex(customers: CustomerData[]) {
  if (customers) {
    const searchIndex = new MiniSearch({
      fields: ["accountName", "city"],
      idField: "customerId",
      searchOptions: {
        fuzzy: 0.2,
        prefix: true,
      },
      storeFields: ["accountName", "city", "customerId", "notes"],
    });
    searchIndex.addAll(customers);
    return searchIndex;
  }
  return null;
}

export const useAddCustomer = () => {
  const appContext = useContext(AppContext);

  const add = (customer: AccountData) => {
    const currentAccounts = appContext?.ranpakAccounts;

    let accounts: AccountData[] = currentAccounts ?? [];
    accounts.push(customer);
    appContext.setRanpakAccounts(accounts);

    console.log("[RANPAK] Scan has been saved locally.");
    console.log("[RANPAK] ScanData:", customer);
    console.log("[RANPAK] Updated storage state:", currentAccounts);

    return currentAccounts;
  };

  const edit = (oldCustomer: CustomerData, customer: AccountData) => {
    const currentAccounts = appContext?.ranpakAccounts;
    let accounts: AccountData[] = currentAccounts ?? [];
    const updateAccounts = accounts.map((rpk) => {
      if (
        rpk.accountName === oldCustomer?.accountName &&
        rpk.city === oldCustomer?.city
      ) {
        return customer;
      }
      return rpk;
    });

    appContext.setRanpakAccounts(updateAccounts);
    return currentAccounts;
  };

  return { add, edit };
};
