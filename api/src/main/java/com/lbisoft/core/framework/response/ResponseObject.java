package com.lbisoft.core.framework.response;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lbisoft.core.framework.response.ResponseMessage.MessageLevel;

/**
 * This class should be used to communicate information or warnings to the user.
 * This SHOULD NOT be used to return
 * errors. Use the following exceptions:
 *
 * <p>
 * {@link com.lbisoft.core.framework.exceptions.FormValidationException
 * FormValidationException} - used for errors regarding form validation.
 * <br>
 * {@link com.lbisoft.core.framework.exceptions.InsufficientPermissionException
 * InsufficientPermissionException} - used for errors regarding permissions.
 * <br>
 * {@link com.lbisoft.core.framework.exceptions.ModifiedSinceLoadedException
 * ModifiedSinceLoadedException} - used for persistence issues.
 * <br>
 * {@link com.lbisoft.core.framework.exceptions.ResourceNotFoundException
 * ResourceNotFoundException} - used when an entity cannot be found because it
 * does not exist.
 */
public class ResponseObject {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  private List<ResponseMessage> messages = new ArrayList<>();

  /**
   * Given a message add an info message to a response object and log it.
   *
   * @param message - string to attach to message
   */
  public ResponseObject addResponseInfo(String message) {
    addResponseMessage(MessageLevel.INFO, message);
    return this;
  }

  /**
   * Given a message add an warning message to a response object and log it.
   *
   * @param message - string to attach to message
   */
  public ResponseObject addResponseWarning(String message) {
    addResponseMessage(MessageLevel.WARNING, message);
    return this;
  }

  /**
   * Given a message level and a message create a new message to a response object
   *
   * @param level   - level of message
   * @param message - string to attach to message
   */
  private void addResponseMessage(MessageLevel level, String message) {
    LOGGER.info(String.format("Responsed with %s: %s", level.toString(), message));
    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setLevel(level);
    responseMessage.setMessage(message);

    messages.add(responseMessage);
  }

  public List<ResponseMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ResponseMessage> messages) {
    this.messages = messages;
  }
}
