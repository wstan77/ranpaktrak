package com.lbisoft.core.framework.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

public class ConcurrentExceptions extends Exception {
  private final List<Throwable> causes;

  public ConcurrentExceptions(String message, List<Throwable> causes) {
    super(message);
    this.causes = causes;
  }

  public List<Throwable> getCauses() {
    return causes;
  }

  @Override
  public void printStackTrace(PrintStream s) {
    // Print the stack trace of this AggregateException
    super.printStackTrace(s);

    // Print the stack traces of all underlying exceptions
    for (Throwable cause : causes) {
      s.println("Caused by: ");
      cause.printStackTrace(s);
    }
  }

  @Override
  public void printStackTrace(PrintWriter s) {
    // Print the stack trace of this AggregateException
    super.printStackTrace(s);

    // Print the stack traces of all underlying exceptions
    for (Throwable cause : causes) {
      s.println("Caused by: ");
      cause.printStackTrace(s);
    }
  }
}
