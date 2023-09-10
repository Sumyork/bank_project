package com.example.bank_project.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountPaymentDtoRq {
    private String accountId;
    private BigDecimal debitingSum;
    private String debitingCurrency;
}
