package com.lbisoft.core.app.components.task.model;

import lombok.Data;

@Data
public class TaskFilters {
  private String searchText;
  private boolean archivedFlg = false;
  private boolean allFlg = false;
  private Integer skip;
  private Integer limit;
}
