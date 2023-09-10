package com.example.bank_project.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTransferDtoRq {
    private String accountIdSender;
    private String accountIdRecipient;
    private BigDecimal subtractSum;
}
