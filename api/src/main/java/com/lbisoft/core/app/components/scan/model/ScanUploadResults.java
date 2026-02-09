package com.lbisoft.core.app.components.scan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ScanUploadResults {

    private List<ScanUploadResultNewAccount> newAccounts = new ArrayList<ScanUploadResultNewAccount>();
    private List<ScanUploadResultExistingAccount> existingAccounts = new ArrayList<ScanUploadResultExistingAccount>();

    public int getVerifiedInSugarCount() {
        return existingAccounts.stream().map(ScanUploadResultExistingAccount::getVerified).mapToInt(List::size).sum();
    }

    public int getMachineNotFoundCount() {
        return existingAccounts.stream().map(ScanUploadResultExistingAccount::getNotFound).mapToInt(List::size).sum();
    }

    public int getMachineChangedLocationCount() {
        return existingAccounts.stream().map(ScanUploadResultExistingAccount::getMoved).mapToInt(List::size).sum();
    }

    public int getNewAccountRequestedCount() {
        return newAccounts.size();
    }

    /**
     * Helper method to return an existing account record
     * If not exists yet, return a new record with the name
     * 
     * @param accountName
     * @return
     */
    @JsonIgnore
    public ScanUploadResultExistingAccount getExistingAccount(String accountName) {
        Optional<ScanUploadResultExistingAccount> e = existingAccounts.stream()
                .filter(ea -> accountName.equals(ea.getAccountName())).findFirst();
        if (e.isPresent()) {
            return e.get();
        } else {
            ScanUploadResultExistingAccount ea = new ScanUploadResultExistingAccount(accountName);
            existingAccounts.add(ea);
            return ea;
        }
    }

}
