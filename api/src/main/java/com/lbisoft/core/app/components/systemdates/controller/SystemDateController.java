package com.lbisoft.core.app.components.systemdates.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.systemdates.model.SystemDate;
import com.lbisoft.core.app.components.systemdates.model.SystemDateForm;
import com.lbisoft.core.app.components.systemdates.service.SystemDateGetService;
import com.lbisoft.core.app.components.systemdates.service.SystemDateSaveService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/system-dates")
@Tag(name = SystemDateController.SYSTEM_DATES)
public class SystemDateController {
  static final String SYSTEM_DATES = "System Dates";

  private final SystemDateSaveService systemDateSaveService;
  private final SystemDateGetService systemDateGetService;
  private final UserActivitiesService activitiesService;

  @Autowired
  public SystemDateController(UserActivitiesService activitiesService,
      SystemDateSaveService systemDateSaveService, SystemDateGetService systemDateGetService) {
    this.activitiesService = activitiesService;
    this.systemDateSaveService = systemDateSaveService;
    this.systemDateGetService = systemDateGetService;
  }

  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
  @PostMapping("/")
  @Operation(summary = "Save System Dates", tags = { SYSTEM_DATES })
  public ResponseObject saveSystemDates(@RequestBody SystemDateForm form, HttpServletRequest request) {
    activitiesService.logActivity(
        new UserActivityBuilder(ActivityTypes.SAVE_SYSTEM_DATES).relatedObject(form).build(), request);
    return systemDateSaveService.saveSystemDates(form.getSystemDates(), RequestHelper.getUser(request));
  }

  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
  @GetMapping("/")
  @Operation(summary = "Get System Dates", tags = { SYSTEM_DATES })
  public List<SystemDate> getSystemDates(HttpServletRequest request) {
    return systemDateGetService.getCurrentSystemDates();
  }
}
