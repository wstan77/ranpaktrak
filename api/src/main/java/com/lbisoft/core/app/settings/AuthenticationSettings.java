package com.lbisoft.core.app.settings;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.lbisoft.core.app.Constants.AuthMethod;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthenticationSettings {
    private AuthMethod method = AuthMethod.LDAP;
    private String jwtHeader = "Authorization";
    private String azureTenantId;
    private String azureClientId;
    private String openIdIssuerUrl;
    private String openIdClientId;
    private String anonymousIssuer;
    private String ranpakUserGroupId;

    public void setMethod(String method) {
        if (EnumUtils.isValidEnum(AuthMethod.class, method)) {
            this.method = AuthMethod.valueOf(method);
        } else {
            this.method = AuthMethod.LDAP;
        }
    }
}
