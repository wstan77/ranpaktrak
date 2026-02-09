package com.lbisoft.core.app.components.btpplatform.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.lbisoft.core.app.components.btpplatform.model.BtpPlatformResponse;
import com.lbisoft.core.app.settings.BtpPlatformSettings;
import com.lbisoft.core.framework.service.CoreServices.CoreService;
import com.lbisoft.core.framework.utils.RestHelper;

@CoreService
public class BtpPlatformService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String BTP_PLATFORM_RANPAKTRAK_ROLE = "Ranpaktrak Mobile User";

    @Autowired
    private BtpPlatformSettings btpPlatformSettings;

    /**
     * Determine if a user is authorized as a distributor to access Ranpaktrak
     * Determined by Querying BTP Platform API for a specific group assignment
     */
    public boolean authorizeUser(String username) {
        LOGGER.info("Checking BTP Platform for authorization for user " + username);
        // get a rest template
        RestTemplate rt = RestHelper.getRestTemplate(btpPlatformSettings.getTimeoutInMilliseconds());
        // setup query string based on API definition
        String urlString = btpPlatformSettings.getUrl() + "scim/Users?filter=emails.value eq \"" + username
                + "\" and groups.display eq \""
                + BTP_PLATFORM_RANPAKTRAK_ROLE + "\"";
        // Query the API
        HttpEntity<String> entity = new HttpEntity<String>(
                createHeaders(btpPlatformSettings.getUsername(), btpPlatformSettings.getPassword()));
        BtpPlatformResponse resp = null;
        try {
            resp = rt
                    .exchange(
                            urlString,
                            HttpMethod.GET, entity,
                            BtpPlatformResponse.class)
                    .getBody();
        } catch (Exception e) {
            LOGGER.error("Exception querying BTP Platform for authorization for user " + username, e);
        }
        // determine if the user should be able to access Ranpaktrak
        return resp != null && resp.getTotalResults() != null && resp.getTotalResults() > 0;
    }

    HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        // setup auth header
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        // need custom content type header
        headers.set("Accept", "application/scim+json");
        return headers;
    }

}