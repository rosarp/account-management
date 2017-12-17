package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.MoneyTransferService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class MoneyTransferControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private MoneyTransferService moneyTransferService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private static ExecutorService executor;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    executor = Executors.newFixedThreadPool(10);
  }

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    moneyTransferService.getAccountsRepository().clearAccounts();
  }

  @After
  public void tearDown() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    executor.shutdown();
  }

  private void createAccount(final String content) throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content(content))
        .andExpect(status().isCreated());
  }

  private void moneyTransfer(final String content, CountDownLatch startSignal, CountDownLatch doneSignal)
      throws Exception {
    executor.submit(() -> {
      try {
        startSignal.await();
        mockMvc.perform(post("/v1/money-transfer").contentType(MediaType.APPLICATION_JSON).content(content))
            .andExpect(status().isCreated());
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        doneSignal.countDown();
      }
    });
  }

  /**
   * Testing only multi-threading with consistency in account balance. No
   * exception is expected, especially overdraft exception.
   * 
   * @throws Exception
   */
  @Test
  public void transferMoney_withMultiThreading() throws Exception {
    this.createAccount("{\"accountId\":\"Id-123\",\"balance\":6100}");
    this.createAccount("{\"accountId\":\"Id-234\",\"balance\":5700}");
    this.createAccount("{\"accountId\":\"Id-345\",\"balance\":4500}");

    CountDownLatch startSignal = new CountDownLatch(1);
    CountDownLatch doneSignal = new CountDownLatch(10);

    this.moneyTransfer("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-234\",\"amount\":700}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-234\",\"accountTo\":\"Id-123\",\"amount\":500}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-345\",\"amount\":300}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-234\",\"accountTo\":\"Id-123\",\"amount\":300}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-345\",\"amount\":500}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-234\",\"amount\":200}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-345\",\"accountTo\":\"Id-234\",\"amount\":250}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-234\",\"accountTo\":\"Id-345\",\"amount\":400}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-345\",\"accountTo\":\"Id-234\",\"amount\":115}", startSignal, doneSignal);
    this.moneyTransfer("{\"accountFrom\":\"Id-345\",\"accountTo\":\"Id-234\",\"amount\":460}", startSignal, doneSignal);

    startSignal.countDown();
    doneSignal.await();

    Account account1 = moneyTransferService.getAccountsRepository().getAccount("Id-123");
    assertThat(account1.getAccountId()).isEqualTo("Id-123");
    assertThat(account1.getBalance()).isEqualByComparingTo("5200");

    Account account2 = moneyTransferService.getAccountsRepository().getAccount("Id-234");
    assertThat(account2.getAccountId()).isEqualTo("Id-234");
    assertThat(account2.getBalance()).isEqualByComparingTo("6225");

    Account account3 = moneyTransferService.getAccountsRepository().getAccount("Id-345");
    assertThat(account3.getAccountId()).isEqualTo("Id-345");
    assertThat(account3.getBalance()).isEqualByComparingTo("4875");

  }

  @Test
  public void transferMoney_failsOnOverdraft() throws Exception {
    this.createAccount("{\"accountId\":\"Id-123\",\"balance\":1000}");
    this.createAccount("{\"accountId\":\"Id-234\",\"balance\":0}");

    mockMvc
        .perform(post("/v1/money-transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-234\",\"amount\":5000}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void transferMoney_failsOnAccountDoesNotExist() throws Exception {
    this.createAccount("{\"accountId\":\"Id-123\",\"balance\":1000}");
    this.createAccount("{\"accountId\":\"Id-234\",\"balance\":0}");

    mockMvc
        .perform(post("/v1/money-transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-345\",\"amount\":500}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void transferMoney_failsIfFromAndToIsSameAccount() throws Exception {
    this.createAccount("{\"accountId\":\"Id-123\",\"balance\":1000}");

    mockMvc
        .perform(post("/v1/money-transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-123\",\"amount\":500}"))
        .andExpect(status().isBadRequest());
  }
}
