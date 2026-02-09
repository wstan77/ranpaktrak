package com.lbisoft.core.app.components.buildinfo.service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.lbisoft.core.app.components.buildinfo.model.BuildInfo;
import com.lbisoft.core.app.settings.AuthenticationSettings;
import com.lbisoft.core.app.settings.BuildInfoSettings;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

import org.springframework.beans.factory.annotation.Autowired;

@CoreService
public class BuildInfoService {

  private final BuildInfoSettings settings;
  private final AuthenticationSettings authSettings;

  @Autowired
  public BuildInfoService(BuildInfoSettings settings, AuthenticationSettings authSettings) {
    this.settings = settings;
    this.authSettings = authSettings;
  }

  public BuildInfo getBuildInfo() {
    BuildInfo buildInfo = new BuildInfo();
    buildInfo.setApiVersion(settings.getApiVersion());
    buildInfo.setBuildNumber(settings.getBuildNumber());
    buildInfo.setServerStartTime(getStartTime());
    buildInfo.setAuthMethod(authSettings.getMethod());

    return buildInfo;
  }

  private ZonedDateTime getStartTime() {
    RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
    long startTime = mxBean.getStartTime();
    Instant startInstant = Instant.ofEpochMilli(startTime);
    return startInstant.atZone(ZoneId.systemDefault());
    // return
    // startInstant.atOffset(ZoneOffset.systemDefault().getRules().getOffset(startInstant)).toLocalDateTime();
  }
}
