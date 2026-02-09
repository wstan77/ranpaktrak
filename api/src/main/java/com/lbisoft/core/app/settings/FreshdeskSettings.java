package com.lbisoft.core.app.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "freshdesk")
@Data
public class FreshdeskSettings {

    private String noamEmail;
    private String euapacEmail;
    private String fromAddress;

}
