package com.db.awmd.challenge.exception;

public class AccountDoesNotExistException extends RuntimeException {

  /**
   * Generated serialVersionUID
   */
  private static final long serialVersionUID = 6819322128251361258L;

  public AccountDoesNotExistException(final String message) {
    super("One of the accounts provided does not exist : " + message);
  }
}
