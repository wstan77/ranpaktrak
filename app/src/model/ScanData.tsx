export interface ScanData {
  customerId?: number;
  scanTypeLk: string;
  serialNumber: string;
  tagId?: string;
  regionLk?: string;
  notes?: string;
  manualAccountName?: string;
  manualLocation?: string;
  batchUuid?: string;
  scanImage?: string;
  scanGpsLatitude?: number;
  scanGpsLongitude?: number;
  scanGpsAccuracy?: number;
  scanDate?: string;
  legacySerialNumber?: string;
}

interface SyncDataNewAccount {
  accountName: string;
  scanCount: number;
}

interface SyncDataExistingAccount {
  accountName: string;
  verified: string[];
  notFound: string[];
  moved: string[];
}

export interface SyncData {
  existingAccounts: SyncDataExistingAccount[];
  newAccounts: SyncDataNewAccount[];
  machineChangedLocationCount: number;
  machineNotFoundCount: number;
  newAccountRequestedCount: number;
  verifiedInSugarCount: number;
}
