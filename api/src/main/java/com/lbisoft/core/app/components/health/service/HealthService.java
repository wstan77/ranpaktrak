package com.lbisoft.core.app.components.health.service;

import java.io.IOException;

import com.lbisoft.core.framework.service.CoreServices.CoreService;

import jakarta.servlet.http.HttpServletResponse;

@CoreService
public class HealthService {

    public void checkHealth(HttpServletResponse r) throws IOException {
        r.setStatus(200);
    }
}
