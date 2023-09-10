package com.example.bank_project.controller;

import com.example.bank_project.dto.*;
import com.example.bank_project.exception.*;
import com.example.bank_project.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Создать Rest контроллер, который принимает все запросы по url "/bank/api"
 */

@RestController
@RequestMapping(path = "/bank/api")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Endpoint "/account/create" - Создание счета. В этот запрос будут передаваться поля: фио владельца, валюта счета.
     * Вызывается одноименный метод из account service. Ответ: созданный номер счета.
     */

    @PostMapping(path = "/account/create")
    public String createAccount(@RequestBody AccountDtoRq accountDtoRq) throws AccountExistsException, IncorrectFieldException {
        return accountService.openingBankAccount(accountDtoRq.getAccountName(), accountDtoRq.getCurrencyCode());
    }

    /**
     * Endpoint "/account/close" - Закрытие счета. Передается номер счета на закрытие.
     * Вызывается одноименный метод из account service. Ответ: успешно/ошибка
     */

    @PostMapping(path = "/account/close")
    public String closeAccount(@RequestBody AccountCloseDtoRq accountCloseDtoRq) throws AccountNotFoundException {
        return accountService.closingBankAccount(accountCloseDtoRq.getAccountId());
    }

    /**
     * Endpoint "/account/payment" - Списание со счета. Передается номер счета, сумма списания, валюта счета.
     * Вызывается одноименный метод из account service. Ответ: успешно/ошибка
     */

    @PostMapping(path = "/account/payment")
    public String paymentAccount(@RequestBody AccountPaymentDtoRq accountPaymentDtoRq) throws IncorrectFieldException, AmountDebitedExceedsAmountAccountException {
        return accountService.debitingAccount(accountPaymentDtoRq.getAccountId(), accountPaymentDtoRq.getDebitingSum(),
                accountPaymentDtoRq.getDebitingCurrency());
    }

    /**
     *  Endpoint "/account/income" - Приход на счет. Передается номер счета, сумма списания, валюта счета.
     *  Вызывается одноименный метод из account service. Ответ: успешно/ошибка
     */

    @PostMapping(path = "/account/income")
    public String incomeAccount(@RequestBody AccountIncomeDtoRq accountIncomeDtoRq) throws IncorrectFieldException {
        return accountService.addMoney(accountIncomeDtoRq.getAccountId(), accountIncomeDtoRq.getAddSum(),
                accountIncomeDtoRq.getAddCurrency());
    }

    /**
     *  Endpoint "/account/transfer" - Перевод денег между счетами.
     *  Передается номер счета отправителя, номер счета получателя, сумма перевода. Ответ: успешно/ошибка
     */
    @PostMapping(path = "/account/transfer")
    public String transfer(@RequestBody AccountTransferDtoRq accountTransferDtoRq) throws AmountDebitedExceedsAmountAccountException, IncorrectFieldException {
        return accountService.internalTransfers(accountTransferDtoRq.getAccountIdSender(),
                accountTransferDtoRq.getAccountIdRecipient(), accountTransferDtoRq.getSubtractSum());
    }

    /**
     * Endpoint "/account?account=[номер счета]" - Инфо о счете.
     * Передается номер счета отправителя. Ответ: инфо о счете/ошибка
     */

    @GetMapping(path = "/account")
    public String getInfoAccount(@RequestParam("account") String accountId) throws AccountNotFoundException {
        return accountService.infoAccount(accountId);
    }
}
