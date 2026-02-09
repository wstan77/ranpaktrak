package com.lbisoft.core.app.components.thread.model;

import lombok.Data;

@Data
public class ThreadCreateForm {
  private String referenceTypeLk;
  private Integer referenceId;
  private String subject;
}
