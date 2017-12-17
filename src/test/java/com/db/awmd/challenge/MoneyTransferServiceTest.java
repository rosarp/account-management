package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.FromAndToSameAccountException;
import com.db.awmd.challenge.exception.OperationTimeoutException;
import com.db.awmd.challenge.exception.OverdraftException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.MoneyTransferService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MoneyTransferServiceTest {

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private MoneyTransferService moneyTransferService;

  private static ExecutorService executor;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    executor = Executors.newFixedThreadPool(1000);
  }

  @Before
  public void prepareMockMvc() {
    // Reset the existing accounts before each test.
    accountsRepository.clearAccounts();
  }

  @After
  public void tearDown() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    executor.shutdown();
  }

  private void createAccount(final String accountId, final BigDecimal balance) {
    Account account = new Account(accountId, balance);
    this.moneyTransferService.getAccountsRepository().createAccount(account);
  }

  @Test
  public void moneyTransfer() throws Exception {
    this.moneyTransferService.getAccountsRepository().clearAccounts();
    this.createAccount("Id-123", new BigDecimal(4000));
    this.createAccount("Id-234", new BigDecimal(1000));

    this.moneyTransferService.moneyTransfer("Id-123", "Id-234", new BigDecimal(500));

    assertThat(this.accountsRepository.getAccount("Id-123").getBalance()).isEqualTo(new BigDecimal(3500));
    assertThat(this.accountsRepository.getAccount("Id-234").getBalance()).isEqualTo(new BigDecimal(1500));
  }

  @Test
  public void moneyTransfer_failsIfFromAndToIsSameAccount() throws Exception {
    this.createAccount("Id-123", new BigDecimal(4000));

    try {
      this.moneyTransferService.moneyTransfer("Id-123", "Id-123", new BigDecimal(500));
      fail("Should have failed when from & to account is same.");
    } catch (FromAndToSameAccountException ex) {
      assertThat(ex.getMessage()).isEqualTo("This operation is not supported : Money Transfer");
    }

  }

  @Test
  public void moneyTransfer_failsOnAccountDoesNotExist() throws Exception {
    this.createAccount("Id-123", new BigDecimal(4000));

    try {
      this.moneyTransferService.moneyTransfer("Id-123", "Id-234", new BigDecimal(500));
      fail("Should have failed when one of the account does not exist.");
    } catch (AccountDoesNotExistException ex) {
      assertThat(ex.getMessage()).isEqualTo("One of the accounts provided does not exist : Id-234");
    }

  }

  @Test
  public void moneyTransfer_failsOnOverdraft() throws Exception {
    this.createAccount("Id-123", new BigDecimal(2000));
    this.createAccount("Id-234", new BigDecimal(1000));

    try {
      this.moneyTransferService.moneyTransfer("Id-123", "Id-234", new BigDecimal(5000));
      fail("Should have failed when adding duplicate account");
    } catch (OverdraftException ex) {
      assertThat(ex.getMessage()).isEqualTo("You can not draw more than available balance from this account : Id-123");
    }

  }

  @Test
  public void moneyTransfer_failsOnOperationTimeout() throws Exception {
    this.createAccount("Id-123", new BigDecimal(10000));
    this.createAccount("Id-234", new BigDecimal(100));

    List<Future<String>> futureStr = new ArrayList<Future<String>>();
    for (int i = 0; i < 9000; i++) {
      Future<String> fut = executor.submit(() -> {
        try {
          this.moneyTransferService.moneyTransfer("Id-123", "Id-234", new BigDecimal(1));
        } catch (OperationTimeoutException ex) {
          return ex.getMessage();
        }
        return null;
      });
      futureStr.add(fut);
    }

    boolean success = false;

    for (Future<String> f : futureStr) {
      String message = f.get();
      if (null != message) {
        assertThat(message).isEqualTo("Following operation has been timedout : Money Transfer");
        success = true;
        break;
      }
    }
    if (!success) {
      fail("Should have failed when adding duplicate account");
    }

  }

}
