package com.example.bank_project.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDtoRq {
    String accountName;
    String currencyCode;
    BigDecimal accountBalance;
}
