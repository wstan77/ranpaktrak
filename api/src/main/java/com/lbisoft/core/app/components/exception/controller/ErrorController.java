package com.lbisoft.core.app.components.exception.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lbisoft.core.app.components.exception.model.ErrorResponse;
import com.lbisoft.core.app.components.exception.model.MultiErrorResponse;
import com.lbisoft.core.app.components.exception.model.ResourceErrorResponse;
import com.lbisoft.core.framework.exceptions.FormValidationException;
import com.lbisoft.core.framework.exceptions.InsufficientPermissionException;
import com.lbisoft.core.framework.exceptions.ModifiedSinceLoadedException;
import com.lbisoft.core.framework.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler
    implements org.springframework.boot.web.servlet.error.ErrorController {
  private static final ErrorAttributeOptions STANDARD_ATTRIBUTES = ErrorAttributeOptions.of(Include.MESSAGE);
  private static final ErrorAttributeOptions DEBUG_ATTRIBUTES = ErrorAttributeOptions.of(Include.MESSAGE,
      Include.EXCEPTION, Include.STACK_TRACE);

  private static final String PATH = "/error";

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${error.debug}")
  private boolean debug;

  @Autowired
  private ErrorAttributes errorAttributes;

  @RequestMapping(value = PATH)
  ErrorResponse error(WebRequest request, HttpServletResponse response) {
    // Appropriate HTTP response code (e.g. 404 or 500) is automatically set by
    // Spring.
    // Here we just define response body. This method handles 500 errors
    // TODO - using a TicketUtil add a ticket number to 500 exceptions
    return new ErrorResponse(response.getStatus(),
        getErrorAttributes(request, debug ? DEBUG_ATTRIBUTES : STANDARD_ATTRIBUTES));
  }

  @ExceptionHandler(value = { BadCredentialsException.class, UsernameNotFoundException.class })
  protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(ex,
        new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Bad Credentials",
            getErrorAttributes(request, debug ? DEBUG_ATTRIBUTES : STANDARD_ATTRIBUTES)),
        new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(value = { FormValidationException.class })
  protected ResponseEntity<Object> handleFormValidation(RuntimeException ex, WebRequest request) {
    FormValidationException exception = (FormValidationException) ex;
    return handleExceptionInternal(ex,
        new MultiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
            getErrorAttributes(request, debug ? DEBUG_ATTRIBUTES : STANDARD_ATTRIBUTES), exception.getErrors()),
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = { InsufficientPermissionException.class })
  protected ResponseEntity<Object> handleInsufficientPermission(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(ex,
        new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
            getErrorAttributes(request, debug ? DEBUG_ATTRIBUTES : STANDARD_ATTRIBUTES)),
        new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(value = { ResourceNotFoundException.class })
  protected ResponseEntity<Object> handleResourceNotFound(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(ex,
        new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
            getErrorAttributes(request, debug ? DEBUG_ATTRIBUTES : STANDARD_ATTRIBUTES)),
        new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(value = { ModifiedSinceLoadedException.class })
  protected ResponseEntity<Object> handleModifiedSinceLoaded(RuntimeException ex, WebRequest request) {
    ModifiedSinceLoadedException exception = (ModifiedSinceLoadedException) ex;
    return handleExceptionInternal(ex,
        new ResourceErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(),
            getErrorAttributes(request, debug ? DEBUG_ATTRIBUTES : STANDARD_ATTRIBUTES), exception.getLatestVersion()),
        new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  private Map<String, Object> getErrorAttributes(WebRequest request, ErrorAttributeOptions errorAttributeOptions) {
    Map<String, Object> error = errorAttributes.getErrorAttributes(request, errorAttributeOptions);
    if (!errorAttributeOptions.isIncluded(Include.STACK_TRACE)) {
      // Beyond hiding stack trace, the default error message often includes sensitive
      // information
      error.put("message", null);
    }
    return error;
  }
}
