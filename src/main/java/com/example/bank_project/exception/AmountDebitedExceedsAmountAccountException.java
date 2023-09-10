package com.example.bank_project.exception;

public class AmountDebitedExceedsAmountAccountException extends BankException {

    public AmountDebitedExceedsAmountAccountException(String message) {
        super(message);
    }
}
