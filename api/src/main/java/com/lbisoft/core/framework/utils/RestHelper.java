package com.lbisoft.core.framework.utils;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.lbisoft.core.app.configuration.JSONConfiguration;

public final class RestHelper {
    private RestHelper() {throw new AssertionError("Instantiating utility class...");};
	public static RestTemplate getRestTemplate(long timeout) {
		return getRestTemplate(new RestTemplateBuilder().setConnectTimeout(Duration.ofMillis(timeout)).build());
	}
	public static RestTemplate getRestTemplate() {
		return getRestTemplate(new RestTemplate());
	}
	public static RestTemplate getRestTemplate(RestTemplate restTemplate){
		JSONConfiguration jsonConfig = new JSONConfiguration();
        //Replace the default Jackson Converter with the one configured in JSONConfiguration used throughout the app.
        //This is necessary because the API errors when null fields are included
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setPrettyPrint(false);
        messageConverter.setObjectMapper(jsonConfig.objectMapper());
        restTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
        restTemplate.getMessageConverters().add(messageConverter);
        
        return restTemplate;
    }
}
