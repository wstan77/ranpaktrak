package com.lbisoft.core.app.components.person.socialprofiles.model;

public class PersonSocialProfile {
    private Integer personSocialProfileId;
    private Integer personId;
    private String socialProfileLk;
    private String userName;
    
    public Integer getPersonSocialProfileId() {
        return personSocialProfileId;
    }
    public void setPersonSocialProfileId(Integer personSocialProfileId) {
        this.personSocialProfileId = personSocialProfileId;
    }
    public Integer getPersonId() {
        return personId;
    }
    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
    public String getSocialProfileLk() {
        return socialProfileLk;
    }
    public void setSocialProfileLk(String socialProfileLk) {
        this.socialProfileLk = socialProfileLk;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
