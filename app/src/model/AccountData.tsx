export interface AccountData {
  accountName: string;
  city: string;
  notes?: string;
  regionLk: string;
  customerId?: number;
}

export const initialAccountData: AccountData = {
  accountName: "",
  city: "",
  regionLk: "",
};
