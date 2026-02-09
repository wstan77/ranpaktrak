package com.lbisoft.core.app.components.scan.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ScanUploadResultExistingAccount {

    public ScanUploadResultExistingAccount(String accountName) {
        this.accountName = accountName;
    }

    private String accountName;
    private List<String> verified = new ArrayList<String>();
    private List<String> notFound = new ArrayList<String>();
    private List<String> moved = new ArrayList<String>();
}
