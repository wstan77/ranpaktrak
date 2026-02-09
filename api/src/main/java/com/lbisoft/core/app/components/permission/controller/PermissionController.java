package com.lbisoft.core.app.components.permission.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.permission.service.PermissionService;
import com.lbisoft.core.framework.permissions.model.Permission;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/permissions")
@Tag(name = PermissionController.PERMISSIONS)
@PreAuthorize("hasAuthority('CAN_READ_MANAGE_UTILS')")
public class PermissionController {
  static final String PERMISSIONS = "Permissions";
  @Autowired
  private PermissionService permissionService;

  @GetMapping("/")
  @Operation(summary = "Get all permissions", tags = { PERMISSIONS })
  public List<Permission> getRolePermissions(HttpServletRequest request) {
    return permissionService.getPermissions(RequestHelper.getUser(request));
  }

  @PutMapping("/")
  @Operation(summary = "Save all permissions", tags = { PERMISSIONS })
  public ResponseObject saveRolePermissions(@RequestBody List<Permission> permissions,
      HttpServletRequest request) {
    return permissionService.savePermissions(permissions, request);
  }
}
