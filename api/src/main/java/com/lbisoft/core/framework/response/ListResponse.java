package com.lbisoft.core.framework.response;

import java.util.List;

import com.lbisoft.core.app.Constants.Warnings;

public class ListResponse<T> extends ResponseObject {

  private List<T> list;

  public ListResponse() {

  }

  public ListResponse(List<T> list) {
    this.list = list;
  }

  public ListResponse(List<T> list, int resultLimit) {
    this.list = list;

    if (list.size() > resultLimit) {
      this.list = list.subList(0, resultLimit);
      addResponseWarning(String.format(Warnings.TOO_MANY_RESULTS, resultLimit));
    }
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }
}
