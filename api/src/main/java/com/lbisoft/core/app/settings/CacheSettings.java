package com.lbisoft.core.app.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheSettings {
  private boolean standardLookups = true;

  public boolean getStandardLookups() {
    return standardLookups;
  }

  public void setStandardLookups(boolean standardLookups) {
    this.standardLookups = standardLookups;
  }

}
