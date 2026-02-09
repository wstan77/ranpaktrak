package com.lbisoft.core.app.components.system.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.system.service.CacheService;
import com.lbisoft.core.app.settings.CacheSettings;
import com.lbisoft.core.framework.utils.LoggerHelper;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/system", produces = "application/json")
@Tag(name = "System", description = "System Controls and Information")
@PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
public class SystemController {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Autowired
  CacheService cacheService;

  @PostMapping("/refresh-cache")
  public void reloadCache(@RequestBody CacheSettings cacheSettings, HttpServletRequest request) {
    LoggerHelper.userIs("Refreshing cache using system end-point", LOGGER, RequestHelper.getUser(request));

    cacheService.populateCaches(true, cacheSettings);
  }
}
