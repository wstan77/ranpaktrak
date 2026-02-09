package com.lbisoft.core.app.components.systemvalues.model;

import java.time.LocalDate;

public class SystemValue {
    private String systemValueKey;
    private String systemValue;
    private String description;
    private String shortDescription;
    private Integer seqno;
    private boolean activeFlg;
    private LocalDate createDate;
    private Integer createUserId;
    private LocalDate lastChangeDate;
    private Integer lastChangeUserId;
    private LocalDate systemLastChangeDate;

    public String getSystemValueKey() {
        return systemValueKey;
    }

    public void setSystemValueKey(String systemValueKey) {
        this.systemValueKey = systemValueKey;
    }

    public String getSystemValue() {
        return systemValue;
    }

    public void setSystemValue(String systemValue) {
        this.systemValue = systemValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Integer getSeqno() {
        return seqno;
    }

    public void setSeqno(Integer seqno) {
        this.seqno = seqno;
    }

    public boolean isActiveFlg() {
        return activeFlg;
    }

    public void setActiveFlg(boolean activeFlg) {
        this.activeFlg = activeFlg;
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

    public LocalDate getSystemLastChangeDate() {
        return systemLastChangeDate;
    }

    public void setSystemLastChangeDate(LocalDate systemLastChangeDate) {
        this.systemLastChangeDate = systemLastChangeDate;
    }
}