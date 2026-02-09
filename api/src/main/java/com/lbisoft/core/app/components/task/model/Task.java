package com.lbisoft.core.app.components.task.model;

import com.lbisoft.core.app.Constants.PermissionLks;
import com.lbisoft.core.app.db.model.generated.TaskDb;
import com.lbisoft.core.framework.permissions.model.Ownable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Task extends TaskDb implements Ownable {
  private String referenceName;

  @Override
  public Integer getOwnerUserId() {
    return getCreateUserId();
  }

  @Override
  public String getPermissionLk() {
    return PermissionLks.TASKS;
  }
}
