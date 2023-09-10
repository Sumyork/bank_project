package com.example.bank_project.exception;

public class AccountNotFoundException extends BankException {


    public AccountNotFoundException(String message) {
        super(message);
    }
}
