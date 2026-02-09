package com.lbisoft.core.app.components.phone.model;

public class PhoneNumber {
    private Integer phoneNumberId;
    private String phoneTypeLk;
    private String phoneNumber;
    
    public Integer getPhoneNumberId() {
        return phoneNumberId;
    }
    public void setPhoneNumberId(Integer phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }
    public String getPhoneTypeLk() {
        return phoneTypeLk;
    }
    public void setPhoneTypeLk(String phoneTypeLk) {
        this.phoneTypeLk = phoneTypeLk;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
