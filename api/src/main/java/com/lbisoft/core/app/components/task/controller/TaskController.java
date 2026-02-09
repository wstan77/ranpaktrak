package com.lbisoft.core.app.components.task.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.task.model.Task;
import com.lbisoft.core.app.components.task.model.TaskFilters;
import com.lbisoft.core.app.components.task.service.TaskGetService;
import com.lbisoft.core.app.components.task.service.TaskSaveService;
import com.lbisoft.core.app.components.useractivities.ActivityTypes;
import com.lbisoft.core.app.components.useractivities.service.UserActivitiesService;
import com.lbisoft.core.framework.useractivities.UserActivityBuilder;
import com.lbisoft.core.framework.utils.RequestHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/tasks/")
@Tag(name = "Tasks")
@PreAuthorize("hasAuthority('CAN_READ_TASKS')")
public class TaskController {
  static final String TASKS = "Tasks";

  @Autowired
  private TaskGetService taskGetService;
  @Autowired
  private TaskSaveService taskSaveService;
  @Autowired
  private UserActivitiesService activitiesService;

  @PostMapping("/mine")
  @Operation(summary = "Get tasks for current user", tags = { TASKS })
  public List<Task> getCurrentTasks(@RequestBody TaskFilters filters, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.SEARCH_TASKS).relatedObject(filters).build(),
        request);
    return taskGetService.getTasksForUser(RequestHelper.getUser(request), filters);
  }

  @PostMapping("/{referenceType}/{referenceId}")
  @Operation(summary = "Get tasks for a reference", tags = { TASKS })
  public List<Task> getTasksForReference(@PathVariable("referenceType") String referenceType,
      @PathVariable("referenceId") Integer referenceId, @RequestBody TaskFilters filters, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.SEARCH_TASKS).relatedObject(filters)
        .relatedString(referenceType).relatedId(referenceId).build(), request);
    return taskGetService.getTasksForReference(referenceType, referenceId, filters, RequestHelper.getUser(request));
  }

  @PostMapping("/")
  @Operation(summary = "Create a new task", tags = { TASKS })
  @PreAuthorize("hasAuthority('CAN_EDIT_OWN_TASKS')")
  public void createTask(@RequestBody Task task, HttpServletRequest request) {
    task = taskSaveService.createTask(task, RequestHelper.getUser(request));
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.CREATE_TASK).relatedObject(task).build(),
        request);
  }

  @PostMapping("/{taskId}")
  @Operation(summary = "Update a task", tags = { TASKS })
  public void updateTask(@RequestBody Task task, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.UPDATE_TASK).relatedObject(task).build(),
        request);
    taskSaveService.updateTask(task, RequestHelper.getUser(request));
  }

  @PostMapping("/{taskId}/assign")
  @Operation(summary = "Assign a task to a user", tags = { TASKS })
  public void assignTask(@RequestBody Task task, HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.ASSIGN_TASK).relatedObject(task).build(),
        request);
    taskSaveService.assignTask(task, RequestHelper.getUser(request));
  }

  @PostMapping("/{taskId}/set-status/{statusLk}")
  @Operation(summary = "Change the status of a task", tags = { TASKS })
  public void changeTaskStatus(@PathVariable("taskId") Integer taskId, @PathVariable("statusLk") String statusLk,
      HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.CHANGE_TASK_STATUS).relatedId(taskId)
        .relatedString(statusLk).build(), request);
    taskSaveService.changeTaskStatus(taskId, statusLk, RequestHelper.getUser(request));
  }

  @PostMapping("/mark-read")
  @Operation(summary = "Mark tasks as read", tags = { TASKS })
  public void markTasksRead(HttpServletRequest request) {
    activitiesService.logActivity(new UserActivityBuilder(ActivityTypes.CHECK_TASKS).build(),
        request);
  }

  @GetMapping("/last-check-time")
  @Operation(summary = "Get the last time tasks were read", tags = { TASKS })
  public LocalDateTime getLastCheckTime(HttpServletRequest request) {
    return taskGetService.getLastCheckTime(RequestHelper.getUser(request));
  }

}
