package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MoneyTransfer {
  @NotNull
  @NotEmpty
  private final String accountFrom;

  @NotNull
  @NotEmpty
  private final String accountTo;

  @NotNull
  private BigDecimal amount;

  @JsonCreator
  public MoneyTransfer(@JsonProperty("accountFrom") String accountFrom, @JsonProperty("accountTo") String accountTo,
      @JsonProperty("amount") BigDecimal amount) {
    this.accountTo = accountTo;
    this.accountFrom = accountFrom;
    this.amount = amount;
  }
}
