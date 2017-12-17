package com.db.awmd.challenge.exception;

public class OperationTimeoutException extends RuntimeException {

  /**
   * Generated serialVersionUID
   */
  private static final long serialVersionUID = -7914296987808099362L;

  public OperationTimeoutException(final String message) {
    super("Following operation has been timedout : " + message);
  }
}
