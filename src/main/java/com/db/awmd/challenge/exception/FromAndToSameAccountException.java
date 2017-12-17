package com.db.awmd.challenge.exception;

/**
 * This RuntimeException is thrown when money is being transferred from & to the
 * same account. This should not be allowed, as it will always fail due to lock
 * on accounts, in multi-threading.
 * 
 * @author rosarp
 *
 */
public class FromAndToSameAccountException extends RuntimeException {

  /**
   * Generated serialVersionUID
   */
  private static final long serialVersionUID = 8460896128350659414L;

  public FromAndToSameAccountException(final String message) {
    super("This operation is not supported : " + message);
  }
}
