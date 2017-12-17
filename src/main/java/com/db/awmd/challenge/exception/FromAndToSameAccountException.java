package com.db.awmd.challenge.exception;

public class FromAndToSameAccountException extends RuntimeException {

  /**
   * Generated serialVersionUID
   */
  private static final long serialVersionUID = 8460896128350659414L;

  public FromAndToSameAccountException(final String message) {
    super("This operation is not supported : " + message);
  }
}
