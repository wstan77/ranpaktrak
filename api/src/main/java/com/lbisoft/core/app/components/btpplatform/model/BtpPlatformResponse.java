package com.lbisoft.core.app.components.btpplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BtpPlatformResponse {
    public Integer totalResults;
}
