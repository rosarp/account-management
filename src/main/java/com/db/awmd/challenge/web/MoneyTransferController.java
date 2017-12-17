package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.MoneyTransfer;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.FromAndToSameAccountException;
import com.db.awmd.challenge.exception.OperationTimeoutException;
import com.db.awmd.challenge.exception.OverdraftException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.MoneyTransferService;
import com.db.awmd.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/money-transfer")
@Slf4j
public class MoneyTransferController {

  private final AccountsService accountsService;

  private final MoneyTransferService moneyTransferService;

  private final NotificationService notificationService;

  @Autowired
  public MoneyTransferController(AccountsService accountsService, MoneyTransferService moneyTransferService,
      NotificationService emailNotificationService) {
    this.accountsService = accountsService;
    this.moneyTransferService = moneyTransferService;
    this.notificationService = emailNotificationService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> moneyTransfer(@RequestBody @Valid MoneyTransfer moneyTransfer) {
    log.info("Transferring amount {}, From {}, To {}", moneyTransfer.getAmount(), moneyTransfer.getAccountFrom(),
        moneyTransfer.getAccountTo());

    try {
      moneyTransferService.moneyTransfer(moneyTransfer.getAccountFrom(), moneyTransfer.getAccountTo(),
          moneyTransfer.getAmount());

      // Success, if no exception occurred. Then send notification.
      notificationService.notifyAboutTransfer(accountsService.getAccount(moneyTransfer.getAccountFrom()),
          "Amount " + moneyTransfer.getAmount().toString() + " has been transferred to Account Id "
              + moneyTransfer.getAccountTo());
      notificationService.notifyAboutTransfer(accountsService.getAccount(moneyTransfer.getAccountTo()),
          "Amount " + moneyTransfer.getAmount().toString() + " has been received from Account Id "
              + moneyTransfer.getAccountFrom());

    } catch (FromAndToSameAccountException | AccountDoesNotExistException | OperationTimeoutException
        | OverdraftException ode) {
      log.info(ode.getMessage());
      return new ResponseEntity<>(ode.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
