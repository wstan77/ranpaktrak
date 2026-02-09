package com.lbisoft.core.app.components.systemvalues.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.systemvalues.model.SystemValue;
import com.lbisoft.core.app.components.systemvalues.model.SystemValueForm;
import com.lbisoft.core.app.components.systemvalues.service.SystemValueGetService;
import com.lbisoft.core.app.components.systemvalues.service.SystemValueSaveService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/system-values")
@Tag(name = SystemValueController.SYSTEM_VALUES)
public class SystemValueController {
  static final String SYSTEM_VALUES = "System Values";

  private final SystemValueSaveService systemValueSaveService;
  private final UserActivitiesService activitiesService;
  private final SystemValueGetService systemValueGetService;

  @Autowired
  public SystemValueController(UserActivitiesService activitiesService,
      SystemValueSaveService systemValueSaveService, SystemValueGetService systemValueGetService) {
    this.activitiesService = activitiesService;
    this.systemValueSaveService = systemValueSaveService;
    this.systemValueGetService = systemValueGetService;
  }

  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
  @PostMapping("/")
  @Operation(summary = "Save System Values", tags = { SYSTEM_VALUES })
  public ResponseObject saveSystemValues(@RequestBody SystemValueForm form, HttpServletRequest request) {
    activitiesService.logActivity(
        new UserActivityBuilder(ActivityTypes.SAVE_SYSTEM_VALUES).relatedObject(form).build(), request);
    return systemValueSaveService.saveSystemValues(form.getSystemValues(), RequestHelper.getUser(request));
  }

  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_SYSTEM')")
  @GetMapping("/")
  @Operation(summary = "Get System Values", tags = { SYSTEM_VALUES })
  public List<SystemValue> getSystemDates(HttpServletRequest request) {
    return systemValueGetService.getSystemValues();
  }
}
