package com.lbisoft.core.app.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldap")
public class LDAPSettings {
  private String server;
  private String userDomain;

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getUserDomain() {
    return userDomain;
  }

  public void setUserDomain(String userDomain) {
    this.userDomain = userDomain;
  }
}
