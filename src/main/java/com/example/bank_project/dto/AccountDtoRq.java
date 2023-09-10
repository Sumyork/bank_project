package com.example.bank_project.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDtoRq {
    private String accountName;
    private String currencyCode;
    private BigDecimal accountBalance;
}
