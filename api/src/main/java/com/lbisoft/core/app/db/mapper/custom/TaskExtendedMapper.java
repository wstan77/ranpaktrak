package com.lbisoft.core.app.db.mapper.custom;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lbisoft.core.app.components.task.model.Task;
import com.lbisoft.core.app.components.task.model.TaskFilters;

@Mapper
public interface TaskExtendedMapper {
  List<Task> getTasksForUser(@Param("userId") Integer userId, @Param("filters") TaskFilters filters);

  List<Task> getTasksForReference(@Param("referenceTypeLk") String referenceTypeLk,
      @Param("referenceId") Integer referenceId, @Param("filters") TaskFilters filters,
      @Param("userId") Integer userId);

  Task getTaskById(@Param("taskId") Integer taskId, @Param("userId") Integer userId);

  LocalDateTime getLastCheckTime(@Param("userId") Integer userId);
}
