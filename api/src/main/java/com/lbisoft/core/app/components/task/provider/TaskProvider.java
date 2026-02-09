package com.lbisoft.core.app.components.task.provider;

import com.lbisoft.core.app.components.task.model.Task;
import com.lbisoft.core.app.db.model.generated.TaskDb;
import com.lbisoft.core.framework.provider.AbstractProvider;
import com.lbisoft.core.framework.provider.ChildrenMap;

import ma.glasnost.orika.metadata.ClassMapBuilder;

public class TaskProvider extends AbstractProvider<TaskDb, Task> {

  @Override
  protected void defineClassMap(ClassMapBuilder<TaskDb, Task> classMapBuilder) {
  }

  @Override
  protected void defineChildren(ChildrenMap<Task> childrenMap) {
  }

}
