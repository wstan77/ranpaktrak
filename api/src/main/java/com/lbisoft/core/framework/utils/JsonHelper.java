package com.lbisoft.core.framework.utils;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

  private static final int CONNECT_TIMEOUT = 15 /* seconds */ * 1000;
  private static final int READ_TIMEOUT = 180 /* seconds */ * 1000;

  public RestTemplate getRestTemplate(ObjectMapper objectMapper, int connectTimeout, int readTimeout) {
    // Replace the default Jackson Converter with the one configured in
    // JSONConfiguration used
    // throughout the app.
    // This is necessary because it prevents errors when null fields are included
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    messageConverter.setPrettyPrint(false);
    messageConverter.setObjectMapper(objectMapper);
    List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
    supportedMediaTypes.add(MediaType.TEXT_HTML);
    messageConverter.setSupportedMediaTypes(supportedMediaTypes);

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters()
        .removeIf(m -> m.getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class));
    restTemplate.getMessageConverters().add(messageConverter);

    SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
    rf.setConnectTimeout(connectTimeout);
    rf.setReadTimeout(readTimeout);

    return restTemplate;
  }

  public RestTemplate getRestTemplate(ObjectMapper objectMapper) {
    return getRestTemplate(objectMapper, READ_TIMEOUT, CONNECT_TIMEOUT);
  }
}
