package com.db.awmd.challenge.exception;

/**
 * This RuntimeException is thrown when due to account being locked for more
 * than wait time defined by this application, tryLock method might fail due to
 * very busy server.
 * 
 * @author rosarp
 *
 */
public class OperationTimeoutException extends RuntimeException {

  /**
   * Generated serialVersionUID
   */
  private static final long serialVersionUID = -7914296987808099362L;

  public OperationTimeoutException(final String message) {
    super("Following operation has been timedout : " + message);
  }
}
