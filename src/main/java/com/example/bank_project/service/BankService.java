package com.example.bank_project.service;

import com.example.bank_project.dto.AccountDtoRq;
import com.example.bank_project.exception.AccountExistsException;
import com.example.bank_project.exception.AccountNotFoundException;
import com.example.bank_project.exception.IncorrectFieldException;

import java.util.Currency;

public interface BankService {

    String openingBankAccount(String accountName, String currencyCode) throws AccountExistsException, AccountNotFoundException, IncorrectFieldException;

    String closingBankAccount(String accountId) throws AccountNotFoundException;
}
