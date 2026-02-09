package com.lbisoft.core.app.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "database")
public class DatabaseSettings {
  private String readerUrl;
  private String writerUrl;
  private String driverClassName;
  private String username;
  private String password;

  public String getReaderUrl() {
    return this.readerUrl;
  }

  public void setReaderUrl(String readerUrl) {
    this.readerUrl = readerUrl;
  }

  public String getWriterUrl() {
    return this.writerUrl;
  }

  public void setWriterUrl(String writerUrl) {
    this.writerUrl = writerUrl;
  }

  public String getDriverClassName() {
    return this.driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
