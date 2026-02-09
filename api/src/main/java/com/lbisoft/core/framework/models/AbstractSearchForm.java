package com.lbisoft.core.framework.models;

public abstract class AbstractSearchForm {
  private Integer limit;

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }
}
