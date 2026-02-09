package com.lbisoft.core.app.components.scan.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScanUploadResultNewAccount {
    private String accountName;
    private int scanCount;
}
