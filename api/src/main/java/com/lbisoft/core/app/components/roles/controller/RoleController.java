package com.lbisoft.core.app.components.roles.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.roles.model.Role;
import com.lbisoft.core.app.components.roles.service.RoleService;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/roles")
@Tag(name = RoleController.ROLES)
@PreAuthorize("hasAuthority('CAN_READ_MANAGE_UTILS')")
public class RoleController {
  static final String ROLES = "Roles";

  @Autowired
  private RoleService roleService;

  @GetMapping("/")
  @Operation(summary = "Get all roles", tags = { ROLES })
  public List<Role> getRoles() {
    return roleService.getAllRoles();
  }

  @PostMapping("/")
  @Operation(summary = "Save role", tags = { ROLES })
  public Role saveRole(@RequestBody() Role role, Principal principal) {
    return roleService.saveRole(role, RequestHelper.getUser(principal));
  }

  @PostMapping("/{roleId}/disable")
  @Operation(summary = "Disable role by id", tags = { ROLES })
  public Role disableUser(@PathVariable Integer roleId, Principal principal) {
    return roleService.disableRole(roleId, RequestHelper.getUser(principal))
        .orElseThrow(() -> notFoundException(roleId));
  }

  @PostMapping("/{roleId}/enable")
  @Operation(summary = "Disable role by id", tags = { ROLES })
  public Role enableUser(@PathVariable Integer roleId, Principal principal) {
    return roleService.enableUser(roleId, RequestHelper.getUser(principal))
        .orElseThrow(() -> notFoundException(roleId));
  }

  private ResourceNotFoundException notFoundException(Integer roleId) {
    return new ResourceNotFoundException("No role found for role id " + roleId);
  }
}
