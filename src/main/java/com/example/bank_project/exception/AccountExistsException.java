package com.example.bank_project.exception;

public class AccountExistsException extends BankException {

    public AccountExistsException(String message) {
        super(message);
    }
}

