package com.lbisoft.core.app.components.thread.model;

import lombok.Data;

@Data
public class ThreadMessageTag {
  private Integer threadMessageTagId;
  private Integer threadMessageId;
  private String referenceTypeLk;
  private Integer referenceId;

  private String description;
}
