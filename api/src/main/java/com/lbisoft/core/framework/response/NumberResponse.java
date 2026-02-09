package com.lbisoft.core.framework.response;

public class NumberResponse extends ResponseObject {
  private Number value;

  public NumberResponse(Number value) {
    this.value = value;
  }

  public Number getValue() {
    return value;
  }

  public void setValue(Number value) {
    this.value = value;
  }
}
