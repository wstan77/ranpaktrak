import { useAxios } from "@app/service/contexts/AxiosContext";
import { useStorageStateSimple } from "@app/hooks";
import { useQuery } from "@tanstack/react-query";
import { MachineData } from "@app/model/MachineData";
import { STORAGE_KEYS } from "@app/service/helpers/storage";
import Toast from "react-native-toast-message";

export const getMachinesQueryKey = (region: string) => ["machines", region];

export const useMachinesQuery = () => {
  const { authAxios } = useAxios();
  const selectedRegion = useStorageStateSimple(STORAGE_KEYS.selectedRegion);
  return useQuery({
    queryKey: getMachinesQueryKey(selectedRegion),
    staleTime: 1000 * 60 * 5,
    queryFn: async () => {
      if (selectedRegion != null) {
        let res = await authAxios.get<MachineData[]>(
          `/machines/${selectedRegion}/`
        );
        if (res.status != 200) {
          Toast.show({
            type: "error",
            text1: "Error Encountered",
            text2: `Failed to fetch machines for region ${selectedRegion}`,
            position: "bottom",
          });
        }
        console.log(
          `[RANPAK] (Region: ${selectedRegion}) Finished loading ${res?.data?.length} machines.`
        );
        return res?.data;
      }
      console.log(
        `[RANPAK] (Region: ${selectedRegion}) Finished loading 0 machines.`
      );
      return [];
    },
  });
};
