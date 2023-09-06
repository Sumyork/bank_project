package com.example.bank_project.service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import com.example.bank_project.entity.Account;
import com.example.bank_project.exception.AccountExists;
import com.example.bank_project.exception.AccountNotFound;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements BankService {

    Map<String, Account> accounts = new HashMap<>();

    /**
     * 4 Создать метод "открыть счет", сигнатура: String фио владельца, валюта счета.
     * Номер счета генерируется так: «40817» + код валюты + (количество открытых счетов + 1).
     * Пример: 408178101 - рублевый счет. 408178402 - долларовый счет.
     * Созданный счет (в виде экземпляра класса Account) нужно хранить в хранилище счетов
     * (аттрибут внутри AccountService). Номера счетов не могут повторяться (подумать,
     * какая структура данных нужна). Если счет уже есть, то возникает ошибка.
     * 5 Создать метод "закрыть счет", сигнатура: String номер счета. Находит счет в хранилище счетов.
     * Обнуляет его остатки, проставляет статус "закрыт". Если счет не найден, то выдается ошибка.
     */
    @Override
    public void openingBankAccount(String accountName, String currencyCode) {

        if (accounts.get(generateNumberAccount(currencyCode)) != null) {
            try {
                throw new AccountExists();
            } catch (AccountExists e) {
                System.out.println("Такой аккаунт существует.");
            }
        } else {
            accounts.putIfAbsent(generateNumberAccount(currencyCode),
                    new Account(generateNumberAccount(currencyCode),
                            accountName, currencyCode, new BigDecimal("0")));
        }
    }

    public String generateNumberAccount(String currencyCode) {
        return "40817" + Currency.getInstance(currencyCode).getNumericCodeAsString() + accounts.size();
    }

    /**
     * Создать метод "конвертация". Сигнатура: String валюта до конвертации, BigDecimal сумма, валюта после конвертации.
     * Преобразует сумму из одной валюты в другую. Пример: передано "рубль, 90, доллар". Значит произойдет перевод 90 рублей в доллары.
     * Вернется 3 доллара. Курсы: 1 доллар - 30 рублей, 1 евро - 35 рублей, 1 евро = 1,17 долларов.
     */

    @Override
    public void closingBankAccount(String accountId) {

        if (accounts.get(accountId) == null) {
            try {
                throw new AccountNotFound();
            } catch (AccountNotFound e) {
                System.out.println("Счёт не найден!");
            }
        } else {
            accounts.get(accountId).setAccountBalance(new BigDecimal("0"));
            accounts.remove(accountId).setStatus(false);
        }
    }


}
