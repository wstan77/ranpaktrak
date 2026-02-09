package com.lbisoft.core.app.components.user.model;

import java.math.BigDecimal;

public class UserSetting {
  private String userSettingsLk;
  private Integer userId;
  private BigDecimal numericValue;
  private String stringValue;

  public BigDecimal getNumericValue() {
    return numericValue;
  }

  public void setNumericValue(BigDecimal numericValue) {
    this.numericValue = numericValue;
  }

  public String getUserSettingsLk() {
    return userSettingsLk;
  }

  public void setUserSettingsLk(String userSettingsLk) {
    this.userSettingsLk = userSettingsLk;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }
}
