package com.lbisoft.core.app.components.scan.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lbisoft.core.app.components.customer.model.Customer;
import com.lbisoft.core.app.components.machine.model.Machine;

import lombok.Data;

@Data
public class Scan {

    private Integer scanId;
    private Integer customerId;
    private LocalDateTime scanDate;
    private String scanTypeLk;
    private String serialNumber;
    private String tagId;
    private String regionLk;
    private String notes;
    private String manualAccountName;
    private String manualLocation;
    private Double scanGpsLatitude;
    private Double scanGpsLongitude;
    private Double scanGpsAccuracy;

    private String batchUuid;

    private byte[] scanImage;

    private ScanResult scanResult;

    private Customer customer;
    private Machine machine;

    public String getScanDateForSugar() {
        return scanDate != null ? scanDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

}
