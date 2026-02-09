package com.lbisoft.core.app.components.systemdates.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lbisoft.core.app.db.model.generated.LkSystemDateTypeDb;

public class SystemDate {
    private Integer systemDateYear;
    private LkSystemDateTypeDb systemDateType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String comments;
    private LocalDate createDate;
    private Integer createUserId;
    private LocalDate lastChangeDate;
    private Integer lastChangeUserId;
    private LocalDate systemLastChangeDate;

    public String getSystemDateTypeLk() {
        return systemDateType == null ? null : systemDateType.getSystemDateTypeLk();
    }

    public Integer getSystemDateYear() {
        return systemDateYear;
    }

    public void setSystemDateYear(Integer systemDateYear) {
        this.systemDateYear = systemDateYear;
    }

    public LkSystemDateTypeDb getSystemDateType() {
        return systemDateType;
    }

    public void setSystemDateType(LkSystemDateTypeDb systemDateType) {
        this.systemDateType = systemDateType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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