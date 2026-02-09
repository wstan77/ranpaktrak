package com.lbisoft.core.app.components.sugarcrm.model;

import lombok.Data;

@Data
public class SugarCustomer {
    private String id;
    private String name;
    private String account_number;
    private String street;
    private String streetName;
    private String houseNumber;
    private String numberSupplement;
    private String postalcode;
    private String city;
    private String district;
    private String state;
    private String country;
    private String region;
}
