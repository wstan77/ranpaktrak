package com.lbisoft.core.app.components.user.controller;

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

import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.components.user.service.UserService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.response.ResponseObject;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/users/")
@Tag(name = "Users")
public class UserController {
  private static final String AUTHENTICATION = "Authentication";

  @Autowired
  private UserService service;
  @Autowired
  private UserActivitiesService activitiesService;

  private ResourceNotFoundException notFoundException(Integer userId) {
    return new ResourceNotFoundException("No user found for user id " + userId);
  }

  @GetMapping("me")
  @Operation(summary = "Get the current user's information", tags = { AUTHENTICATION })
  public AppUser getCurrentUser(final Principal principal) {
    return RequestHelper.getUser(principal);
  }

  @GetMapping("")
  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_UTILS')")
  @Operation(summary = "Get all user information", tags = { AUTHENTICATION })
  public List<AppUser> getAllUsers(HttpServletRequest request) {
    activitiesService.logActivity(
        new UserActivityBuilder(ActivityTypes.SEARCH_USERS).relatedObject(null).build(),
        request);
    return service.getAllUsers();
  }

  @PostMapping("")
  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_UTILS')")
  @Operation(summary = "Update a user", tags = { AUTHENTICATION })
  public ResponseObject updateUser(@RequestBody final AppUser user, final HttpServletRequest request) {
    return service.saveUser(user, request);
  }

  @PostMapping("/{userId}/delete")
  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_UTILS')")
  @Operation(summary = "Delete user by id", tags = { AUTHENTICATION })
  public boolean deleteUser(@PathVariable Integer userId, HttpServletRequest request) {
    return service.deleteUser(userId, request);
  }

  @PostMapping("/{userId}/disable")
  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_UTILS')")
  @Operation(summary = "Disable user by id", tags = { AUTHENTICATION })
  public AppUser disableUser(@PathVariable Integer userId, HttpServletRequest request) {
    return service.disableUser(userId, RequestHelper.getUser(request)).orElseThrow(() -> notFoundException(userId));
  }

  @PostMapping("/{userId}/enable")
  @PreAuthorize("hasAuthority('CAN_READ_MANAGE_UTILS')")
  @Operation(summary = "Enable user by id", tags = { AUTHENTICATION })
  public AppUser enableUser(@PathVariable Integer userId, HttpServletRequest request) {
    return service.enableUser(userId, RequestHelper.getUser(request)).orElseThrow(() -> notFoundException(userId));
  }
}
