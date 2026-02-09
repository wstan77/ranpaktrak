package com.lbisoft.core.app.components.thread.model;

import lombok.Data;

@Data
public class ThreadMessageAttachment {
  private Integer threadMessageAttachmentId;
  private Integer threadMessageId;
  private String fileName;
  private String fileExt;
  private byte[] attachment;
}
