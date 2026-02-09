package com.lbisoft.core.app.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import com.lbisoft.core.app.components.system.service.CacheService;
import com.lbisoft.core.app.settings.AuthenticationSettings;
import com.lbisoft.core.app.settings.CacheSettings;

@Configuration
public class StartupConfiguration implements ApplicationListener<ContextRefreshedEvent> {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${env.name}")
  private String environment;

  @Value("${env.version}")
  private String version;

  private final CacheService cacheService;

  private CacheSettings settings;

  private AuthenticationSettings authSettings;

  public StartupConfiguration(CacheSettings settings, CacheService cacheService,
      AuthenticationSettings authSettings) {
    this.settings = settings;
    this.cacheService = cacheService;
    this.authSettings = authSettings;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    logger.info("\n========================"

        + "\nProfile: "

        + environment

        + "\nVersion: "

        + version

        + "\nAuth Method: "

        + authSettings.getMethod().toString()

        + "\n========================");

    populateCaches();
  }

  public void populateCaches() {
    cacheService.populateCaches(false, settings);
  }
}
