package com.lbisoft.core.app.components.thread.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ThreadMessage {
  private Integer threadMessageId;
  private Integer threadId;
  private String comments;
  private LocalDateTime createDate;
  private Integer createUserId;

  private List<ThreadMessageTag> tags;
  private List<ThreadMessageAttachment> attachments;
}
