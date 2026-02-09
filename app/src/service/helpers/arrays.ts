import { CustomerData } from "@app/model/CustomerData";
import { MachineData } from "@app/model/MachineData";

interface SearchResult {}

export const sortCustomersByName = (customers: CustomerData[]) => {
  return customers && typeof customers.sort === "function"
    ? customers?.sort((a, b) => (a.accountName < b.accountName ? -1 : 1))
    : [];
};

export const sortMachinesBySerial = (machines: MachineData[]) => {
  return machines && typeof machines.sort === "function"
    ? machines?.sort((a, b) => (a.serialNumber < b.serialNumber ? -1 : 1))
    : [];
};

export const orderBy = (customers: SearchResult[], key: string): any[] => {
  return customers && typeof customers.sort === "function"
    ? customers?.sort((a, b) => (a[key] < b[key] ? -1 : 1))
    : [];
};

export const sortBy = (
  list: SearchResult[],
  fn: (r: SearchResult) => number
): any[] => {
  return list.sort((a, b) => (fn(a) < fn(b) ? -1 : 1));
};

export const EXCLUDE_TEXTS =
  "th|wrappi|ranp|wrapp|gea|autop|nin|cushioni|oreign|property";

export const MAX_TEXT_RECOGNIZED_LENGTH = 4;
