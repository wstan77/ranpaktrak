package com.lbisoft.core.app.components.thread.model;

import java.util.List;

import com.lbisoft.core.app.Constants.PermissionLks;
import com.lbisoft.core.framework.permissions.model.Ownable;

import lombok.Data;

@Data
public class MessageThread implements Ownable {
  private Integer threadId;
  private String subject;
  private String referenceTypeLk;
  private Integer referenceId;
  private Integer createUserId;

  private List<ThreadMessage> messages;

  private String referenceName;
  private Integer lastReadMessageId;
  private Integer unreadCount;

  @Override
  public Integer getOwnerUserId() {
    return createUserId;
  }

  @Override
  public String getPermissionLk() {
    return PermissionLks.THREADS;
  }
}
