package com.lbisoft.core.app.components.exception.model;

import java.util.Map;

/**
 * ResourceErrorResponse  error response to use when a resource should be returned to the client. This is useful for 
 * throwing a ModifiedSinceLoadedException, since we may want to return the latest version of the resource.
 */
public class ResourceErrorResponse extends ErrorResponse {

  private Object resource;
  
  /**
   * Constructor used to pass an object back to the client
   * @param status - HTTP response 
   * @param error - string error message
   * @param errorAttributes - error attributes
   * @param resource - resource to pass back to the client
   */
  public ResourceErrorResponse(int status, String error, Map<String, Object> errorAttributes, Object resource) {
    super(status, error, errorAttributes);
    this.resource = resource;
  }

  public Object getResource() {
    return resource;
  }

  public void setResource(Object resource) {
    this.resource = resource;
  }
}
