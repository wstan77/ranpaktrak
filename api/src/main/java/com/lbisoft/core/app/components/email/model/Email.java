package com.lbisoft.core.app.components.email.model;

public class Email {
    private Integer emailId;
    private String emailTypeLk;
    private String email;
    
    public Integer getEmailId() {
        return emailId;
    }
    public void setEmailId(Integer emailId) {
        this.emailId = emailId;
    }
    public String getEmailTypeLk() {
        return emailTypeLk;
    }
    public void setEmailTypeLk(String emailTypeLk) {
        this.emailTypeLk = emailTypeLk;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
