package com.lbisoft.core.app.components.address.model;

public class Address {
    private Integer addressId;
    private String addressTypeLk;
    private String address;
    private String city;
    private String stateLk;
    private String countryLk;
    private String zip;
    
    public Integer getAddressId() {
        return addressId;
    }
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    public String getAddressTypeLk() {
        return addressTypeLk;
    }
    public void setAddressTypeLk(String addressTypeLk) {
        this.addressTypeLk = addressTypeLk;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getStateLk() {
        return stateLk;
    }
    public void setStateLk(String stateLk) {
        this.stateLk = stateLk;
    }
    public String getCountryLk() {
        return countryLk;
    }
    public void setCountryLk(String countryLk) {
        this.countryLk = countryLk;
    }
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }
}
