package com.lbisoft.core.app.components.customer.model;

import lombok.Data;

@Data
public class Customer {

    private Integer customerId;
    private String customerKey;
    private String accountName;
    private String city;
    private String state;
    private String country;
    private String district;
    private String regionLk;
    private Boolean distributorFlg;
    private Integer distributorCustomerId;

    private Customer distributor;

}
