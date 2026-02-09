package com.lbisoft.core.app.components.system.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.system.service.CacheService;
import com.lbisoft.core.app.settings.CacheSettings;
import com.lbisoft.core.framework.exceptions.TooManyRequestsException;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.utils.LoggerHelper;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/cache", produces = "application/json")
@Tag(name = "Cache", description = "Cache Controls")
public class CacheController {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  @Autowired
  CacheService cacheService;
  @Autowired
  CacheSettings cacheSettings;

  @GetMapping("/refresh")
  @Operation(summary = "Public end-point to refresh cache from external apps. Throttled to prevent stressing the server.")
  public ResponseObject reloadAllLookups(HttpServletRequest request) {
    ResponseObject response = new ResponseObject();

    LoggerHelper.userIs("Refreshing cache using public end-point", LOGGER, RequestHelper.getUser(request));

    boolean success = cacheService.populateCachesThrottled(true, cacheSettings);

    if (!success) {
      LoggerHelper.userIs("Prevented from refreshing cache by throttling", LOGGER, RequestHelper.getUser(request));
      throw new TooManyRequestsException();
    }
    response.addResponseInfo("Successfully refreshed cache");

    return response;
  }
}
