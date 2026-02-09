package com.lbisoft.core.app.components.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthRequest {
  @Schema(description = "Login Email", required = true, example = "user@example.com")
  private String email;

  @Schema(description = "Login Password", required = true, example = "*******")
  private String password;

  private String newPassword;
  private String verificationCode;

  private boolean confirmForgotPasswordFlg;
  private boolean newPasswordFlg;
  private boolean verifyEmailFlg;

  // device info
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Long fingerprint;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String userAgent;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String browser;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String os;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private String device;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  private Boolean mobileFlg;

  public AuthRequest() {
    super();
  }

  public AuthRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public Long getFingerprint() {
    return fingerprint;
  }

  public void setFingerprint(Long fingerprint) {
    this.fingerprint = fingerprint;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public Boolean getMobileFlg() {
    return mobileFlg;
  }

  public void setMobileFlg(Boolean mobileFlg) {
    this.mobileFlg = mobileFlg;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }

  public boolean isNewPasswordFlg() {
    return newPasswordFlg;
  }

  public void setNewPasswordFlg(boolean newPasswordFlg) {
    this.newPasswordFlg = newPasswordFlg;
  }

  public boolean isVerifyEmailFlg() {
    return verifyEmailFlg;
  }

  public void setVerifyEmailFlg(boolean verifyEmailFlg) {
    this.verifyEmailFlg = verifyEmailFlg;
  }

  public boolean isConfirmForgotPasswordFlg() {
    return confirmForgotPasswordFlg;
  }

  public void setConfirmForgotPasswordFlg(boolean confirmForgotPasswordFlg) {
    this.confirmForgotPasswordFlg = confirmForgotPasswordFlg;
  }
}
