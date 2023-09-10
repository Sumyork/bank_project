package com.example.bank_project.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountIncomeDtoRq {
    private String accountId;
    private BigDecimal addSum;
    private String addCurrency;
}
