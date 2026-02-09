package com.lbisoft.core.app.components.health.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.health.service.HealthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/health")
@Tag(name = "Health Check")
public class HealthController {

  @Autowired
  HealthService service;

  @GetMapping("/")
  @Operation(summary = "Returns a 200 OK if application is healthy, otherwise an error message")
  public void getBuildInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.checkHealth(response);
  }
}
