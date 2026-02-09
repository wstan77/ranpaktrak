package com.lbisoft.core.app.components.exception.model;

import java.util.Map;

public class ErrorResponse {

  public Integer status;
  public String error;
  public String message;
  public String timeStamp;
  public String trace;

  public ErrorResponse(int status, Map<String, Object> errorAttributes) {
    this.status = status;
    this.error = (String) errorAttributes.get("error");
    this.message = (String) errorAttributes.get("message");
    this.timeStamp = errorAttributes.get("timestamp").toString();
    this.trace = (String) errorAttributes.get("trace");
  }

  public ErrorResponse(int status, String error, Map<String, Object> errorAttributes) {
    this.status = status;
    this.error = error;
    this.message = (String) errorAttributes.get("message");
    this.timeStamp = errorAttributes.get("timestamp").toString();
    this.trace = (String) errorAttributes.get("trace");
  }
}
