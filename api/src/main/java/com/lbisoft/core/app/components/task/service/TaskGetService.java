package com.lbisoft.core.app.components.task.service;

import java.time.LocalDateTime;
import java.util.List;

import com.lbisoft.core.app.components.task.model.Task;
import com.lbisoft.core.app.components.task.model.TaskFilters;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.custom.TaskExtendedMapper;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class TaskGetService {

  private final TaskExtendedMapper extendedMapper;

  public TaskGetService(TaskExtendedMapper extendedMapper) {
    this.extendedMapper = extendedMapper;
  }

  public List<Task> getTasksForUser(AppUser user, TaskFilters filters) {
    return extendedMapper.getTasksForUser(user.getUserId(), filters);
  }

  public Task getTaskById(Integer taskId, AppUser user) {
    return extendedMapper.getTaskById(taskId, user.getUserId());
  }

  public List<Task> getTasksForReference(String referenceType, Integer referenceId, TaskFilters filters, AppUser user) {
    return extendedMapper.getTasksForReference(referenceType, referenceId, filters, user.getUserId());
  }

  public LocalDateTime getLastCheckTime(AppUser user) {
    return extendedMapper.getLastCheckTime(user.getUserId());
  }
}
