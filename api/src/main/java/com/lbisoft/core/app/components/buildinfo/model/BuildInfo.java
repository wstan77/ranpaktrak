package com.lbisoft.core.app.components.buildinfo.model;

import java.time.ZonedDateTime;

import com.lbisoft.core.app.Constants.AuthMethod;

public class BuildInfo {
  private String apiVersion;
  private String buildNumber;
  private ZonedDateTime serverStartTime;
  private AuthMethod authMethod;

  public String getApiVersion() {
    return this.apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getBuildNumber() {
    return this.buildNumber;
  }

  public void setBuildNumber(String buildNumber) {
    this.buildNumber = buildNumber;
  }

  public ZonedDateTime getServerStartTime() {
    return this.serverStartTime;
  }

  public void setServerStartTime(ZonedDateTime serverStartTime) {
    this.serverStartTime = serverStartTime;
  }

  public AuthMethod getAuthMethod() {
    return authMethod;
  }

  public void setAuthMethod(AuthMethod authMethod) {
    this.authMethod = authMethod;
  }

}
