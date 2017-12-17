package com.db.awmd.challenge.exception;

/**
 * This RuntimeException is thrown when there is an attempt to withdraw money
 * more than available balance. This is not allowed in the system.
 * 
 * @author rosarp
 *
 */
public class OverdraftException extends RuntimeException {

  /**
   * Generated serialVersionUID
   */
  private static final long serialVersionUID = -3209767731498001394L;

  public OverdraftException(final String message) {
    super("You can not draw more than available balance from this account : " + message);
  }
}
