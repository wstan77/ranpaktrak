package com.lbisoft.core.app.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "sugarcrm")
@Data
public class SugarCRMSettings {

    private String url;
    private long timeoutInMilliseconds;
    private String username;
    private String password;

}
