package com.lbisoft.core.app.components.task.service;

import com.lbisoft.core.app.Constants.RecordStatusLks;
import com.lbisoft.core.app.components.task.model.Task;
import com.lbisoft.core.app.components.task.provider.TaskProvider;
import com.lbisoft.core.app.components.user.model.AppUser;
import com.lbisoft.core.app.db.mapper.generated.TaskDbMapper;
import com.lbisoft.core.app.db.model.generated.TaskDb;
import com.lbisoft.core.app.db.model.generated.TaskDbExample;
import com.lbisoft.core.framework.dao.Dao;
import com.lbisoft.core.framework.exceptions.InsufficientPermissionException;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;
import com.lbisoft.core.framework.permissions.PermissionsHelper;
import com.lbisoft.core.framework.permissions.model.Permission.PermissionLevel;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

@CoreService
public class TaskSaveService {

  private final Dao<TaskDb, Integer> dao;
  private final TaskProvider provider = new TaskProvider();
  private final TaskGetService taskGetService;

  public TaskSaveService(TaskDbMapper mapper, TaskGetService taskGetService) {
    this.taskGetService = taskGetService;
    dao = Dao.getDao(mapper, TaskDb::getTaskId);
  }

  public Task createTask(Task task, AppUser user) {
    TaskDb taskDb = provider.reverseMap(task);
    taskDb.setRecordStatusLk(RecordStatusLks.APPROVED);
    dao.insert(taskDb, user);
    return taskGetService.getTaskById(taskDb.getTaskId(), user);
  }

  public Task updateTask(Task task, AppUser user) {
    Task fromDb = dao.select(task.getTaskId()).map(provider::map)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    if (!PermissionsHelper.hasPermission(user, PermissionLevel.EDIT, fromDb)) {
      throw new InsufficientPermissionException("User does not have permission to edit this task");
    }

    TaskDb taskDb = provider.reverseMap(task);
    dao.update(taskDb, user);
    return taskGetService.getTaskById(taskDb.getTaskId(), user);
  }

  public Task assignTask(Task task, AppUser user) {
    Task fromDb = dao.select(task.getTaskId()).map(provider::map)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    if (!PermissionsHelper.hasPermission(user, PermissionLevel.EDIT, fromDb)) {
      throw new InsufficientPermissionException("User does not have permission to edit this task");
    }

    TaskDb updateAssignee = new TaskDb();
    updateAssignee.setAssignedUserId(task.getAssignedUserId());
    updateAssignee.setAssignedUserGroupId(task.getAssignedUserGroupId());

    TaskDbExample byTaskId = new TaskDbExample();
    byTaskId.createCriteria().andTaskIdEqualTo(task.getTaskId());
    dao.update(updateAssignee, byTaskId, user);
    return taskGetService.getTaskById(task.getTaskId(), user);
  }

  public Task changeTaskStatus(Integer taskId, String statusLk, AppUser user) {
    Task fromDb = dao.select(taskId).map(provider::map)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    if (!user.getUserId().equals(fromDb.getAssignedUserId())
        && !PermissionsHelper.hasPermission(user, PermissionLevel.EDIT, fromDb)) {
      throw new InsufficientPermissionException("User does not have permission to edit this task");
    }

    TaskDb updateStatus = new TaskDb();
    updateStatus.setTaskStatusLk(statusLk);

    TaskDbExample byTaskId = new TaskDbExample();
    byTaskId.createCriteria().andTaskIdEqualTo(taskId);
    dao.update(updateStatus, byTaskId, user);
    return taskGetService.getTaskById(taskId, user);
  }
}
