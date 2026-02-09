package com.lbisoft.core.app.components.buildinfo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.buildinfo.model.BuildInfo;
import com.lbisoft.core.app.components.buildinfo.service.BuildInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/build-info")
@Tag(name = "Build Info")
public class BuildInfoController {

  @Autowired
  BuildInfoService service;

  @GetMapping("/")
  @Operation(summary = "Get information about the API build currently running")
  public BuildInfo getBuildInfo(HttpServletRequest request) {
    return service.getBuildInfo();
  }
}
