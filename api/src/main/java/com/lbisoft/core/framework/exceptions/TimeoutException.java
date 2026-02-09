package com.lbisoft.core.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TimeoutException should be thrown when an query takes too long and times out.
 */
@ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
public class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TimeoutException() {
        super("Operation timed out.");
    }

    public TimeoutException(String message) {
        super(message);
    }
}
