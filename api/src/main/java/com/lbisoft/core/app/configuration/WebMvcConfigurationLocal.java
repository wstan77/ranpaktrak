package com.lbisoft.core.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfigurationLocal implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // allow CORS from any host
    registry
        .addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("*")
        .allowCredentials(false)
        .maxAge(3600);
  }
}
