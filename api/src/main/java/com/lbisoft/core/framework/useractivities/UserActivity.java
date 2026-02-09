package com.lbisoft.core.framework.useractivities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserActivity {

    private ActivityType type;
    private Integer activityId;
    private Integer resourceId;
    private Integer relatedId;
    private BigDecimal relatedNumber;
    private String relatedString;
    private LocalDateTime relatedDate;
    private Boolean relatedBool;
    private Object relatedObject;
    private String relatedObjectString;

    // User Agent
    private String userAgent;
    private String browserName;
    private String browserVersion;
    private String osName;
    private String osVersion;
    private String deviceType;

    // Audit info
    private LocalDate createDate;
    private Integer createUserId;
    private LocalDate lastChangeDate;
    private Integer lastChangeUserId;

    public String getRelatedDateAsString() {
        return relatedDate != null ? relatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }

    public UserActivity activityLk(ActivityType type) {
        this.type = type;
        return this;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public LocalDate getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(LocalDate lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public Integer getLastChangeUserId() {
        return lastChangeUserId;
    }

    public void setLastChangeUserId(Integer lastChangeUserId) {
        this.lastChangeUserId = lastChangeUserId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }

    public BigDecimal getRelatedNumber() {
        return relatedNumber;
    }

    public void setRelatedNumber(BigDecimal relatedNum) {
        this.relatedNumber = relatedNum;
    }

    public String getRelatedString() {
        return relatedString;
    }

    public void setRelatedString(String relatedString) {
        this.relatedString = relatedString;
    }

    public LocalDateTime getRelatedDate() {
        return relatedDate;
    }

    public void setRelatedDate(LocalDateTime relatedDate) {
        this.relatedDate = relatedDate;
    }

    public Boolean getRelatedBool() {
        return relatedBool;
    }

    public void setRelatedBool(Boolean relatedBool) {
        this.relatedBool = relatedBool;
    }

    public Object getRelatedObject() {
        return relatedObject;
    }

    public void setRelatedObject(Object relatedObject) {
        this.relatedObject = relatedObject;
    }

    public String getRelatedObjectString() {
        return relatedObjectString;
    }

    public void setRelatedObjectString(String relatedObjectString) {
        this.relatedObjectString = relatedObjectString;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

}
