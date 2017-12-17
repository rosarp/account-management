package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.OverdraftException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Account {

  // Threads will contend for entry using an approximately arrival-order policy
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private volatile BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId, @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

  public synchronized boolean tryWriteLock() {
    return reentrantReadWriteLock.writeLock().tryLock();
  }

  // Only current thread should unlock this account
  public synchronized void writeUnlock() {
    if (reentrantReadWriteLock.isWriteLockedByCurrentThread()) {
      reentrantReadWriteLock.writeLock().unlock();
    }
  }

  public void withdraw(BigDecimal amount) {
    if (this.balance.compareTo(amount) < 0) {
      throw new OverdraftException(this.accountId);
    }
    this.balance = this.balance.subtract(amount);
  }

  public void deposit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
}
