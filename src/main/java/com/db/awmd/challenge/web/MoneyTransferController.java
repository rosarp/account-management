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
import com.db.awmd.challenge.service.MoneyTransferService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/money-transfer")
@Slf4j
public class MoneyTransferController {

  private final MoneyTransferService moneyTransferService;

  @Autowired
  public MoneyTransferController(MoneyTransferService moneyTransferService) {
    this.moneyTransferService = moneyTransferService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> moneyTransfer(@RequestBody @Valid MoneyTransfer moneyTransfer) {
    log.info("Transferring amount {}, From {}, To {}", moneyTransfer.getAmount(), moneyTransfer.getAccountFrom(),
        moneyTransfer.getAccountTo());

    try {
      moneyTransferService.moneyTransfer(moneyTransfer.getAccountFrom(), moneyTransfer.getAccountTo(),
          moneyTransfer.getAmount());
    } catch (FromAndToSameAccountException | AccountDoesNotExistException | OperationTimeoutException
        | OverdraftException ode) {
      return new ResponseEntity<>(ode.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
