package com.lbisoft.core.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.lbisoft.core.app.settings.ProfileSettings.LocalOnly;

/** Default configuration. */
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /** Configuration for local development. 
   *  NOTE: Scheduled jobs will not run locally with (lazyInit = true)*/
  @Configuration
  @LocalOnly
  @ComponentScan(lazyInit = true)
  static class LocalConfig {}
  
  public Application() {
    // Spring Entry Point
  }
}
