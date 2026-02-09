package com.lbisoft.core.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ModifiedSinceLoadedException  should be thrown when updating an entity that is out of date or stale.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ModifiedSinceLoadedException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Resource modified since loaded.";
  private static final long serialVersionUID = -5865805641834009025L;
  private Object latestVersion;

  public ModifiedSinceLoadedException() {
    super(DEFAULT_MESSAGE);
  }
  
  public ModifiedSinceLoadedException(String message) {
    super(message);
  }
  

  /**
   * Constructor used to return the latest version of the object being modified to the front-end for display.
   * 
   * @param message         message to display
   * @param latestVersion   latest version of the object being modified
   */
  public ModifiedSinceLoadedException(String message, Object latestVersion) {
    super(message);
    this.latestVersion = latestVersion;
  }
  
  /**
   * Constructor used to return the latest version of the object being modified to the front-end for display.
   * 
   * @param latestVersion  latest version of the object being modified
   */
  public ModifiedSinceLoadedException(Object latestVersion) {
    super(DEFAULT_MESSAGE);
    this.latestVersion = latestVersion;
  }

  public Object getLatestVersion() {
    return latestVersion;
  }

  public void setLatestVersion(Object latestVersion) {
    this.latestVersion = latestVersion;
  }

}
