package com.example.bank_project.service;

import java.util.Currency;

public interface BankService {

    void openingBankAccount(String accountName, String currencyCode);

    void closingBankAccount(String accountId);
}
