package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.FromAndToSameAccountException;
import com.db.awmd.challenge.exception.OperationTimeoutException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MoneyTransferService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public MoneyTransferService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  private static final long SLEEP_DELAY = 100;
  private static final long TIMEOUT_DELAY = 5000;

  private Account getAccount(final String accountId) {
    Account account = this.accountsRepository.getAccount(accountId);
    if (null == account) {
      throw new AccountDoesNotExistException(accountId);
    }
    return account;
  }

  /**
   * Transfer the amount from accountFromId to accountToId. It will try to get
   * writeLock on both accounts, if any one of them could not get locked, then it
   * will sleep for 100 ms and then try the same for next 5 s. If unsuccessful
   * then operation fails permanently.
   * 
   * @param accountFromId
   * @param accountToId
   * @param amount
   */
  public void moneyTransfer(final String accountFromId, final String accountToId, BigDecimal amount) {
    if (accountFromId.equalsIgnoreCase(accountToId)) {
      throw new FromAndToSameAccountException("Money Transfer");
    }

    Account accountFrom = getAccount(accountFromId);
    Account accountTo = getAccount(accountToId);

    long timeout = System.nanoTime() + TIMEOUT_DELAY;

    while (true) {
      // in any case, unlock the accounts
      try {
        if (accountFrom.tryWriteLock() && accountTo.tryWriteLock()) {
          // withdraw will throw exception in case of Overdraft
          accountFrom.withdraw(amount);
          accountTo.deposit(amount);

          return;
        }
      } finally {
        accountTo.writeUnlock();
        accountFrom.writeUnlock();
      }

      // Try again in sometime.
      try {
        Thread.sleep(SLEEP_DELAY);
      } catch (InterruptedException te) {
        log.info("Money Transfer operation has been interrupted. " + te.getMessage());
        Thread.currentThread().interrupt();
      }

      if (System.nanoTime() > timeout) {
        throw new OperationTimeoutException("Money Transfer");
      }
    }
  }
}
