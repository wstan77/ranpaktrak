import {QueryClient, useQuery} from "@tanstack/react-query";
import {getCustomersQueryKey, useCustomersQuery} from "./CustomerService";
import { formatDate } from "./helpers/date";
import {getMachinesQueryKey, useMachinesQuery} from "./MachineService";
import {useMemo} from "react";

export function useSync() {
  const {isFetching: customersLoading} = useCustomersQuery();
  const {isFetching: machinesLoading} = useMachinesQuery();
  const isLoading = useMemo(() => {
    return (customersLoading || machinesLoading);
  }, [customersLoading, machinesLoading]);
  return {isLoading};
}

export const sync = async (
  selectedRegion: string,
  queryCl: QueryClient,
  setLastSync: (value: string) => void
) => {
  const date = new Date();

  await queryCl.invalidateQueries({queryKey: getCustomersQueryKey(selectedRegion)});
  await queryCl.invalidateQueries({queryKey: getMachinesQueryKey(selectedRegion)});

  setLastSync(formatDate(date) as any);
};
