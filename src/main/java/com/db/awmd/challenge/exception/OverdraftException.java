package com.db.awmd.challenge.exception;

public class OverdraftException extends RuntimeException {

  /**
   * Generated serialVersionUID
   */
  private static final long serialVersionUID = -3209767731498001394L;

  public OverdraftException(final String message) {
    super("You can not draw more than available balance from this account : " + message);
  }
}
