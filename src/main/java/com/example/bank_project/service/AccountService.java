package com.example.bank_project.service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import com.example.bank_project.entity.Account;
import com.example.bank_project.exception.AccountExists;
import com.example.bank_project.exception.AccountNotFound;
import com.example.bank_project.exception.AmountDebitedExceedsAmountAccount;
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

    public BigDecimal conversion(String startCurrency, BigDecimal conversionAmount, String finishCurrency) {

        BigDecimal result = null;

        if (startCurrency.equals("RUB") && finishCurrency.equals("USD")) {
            result = conversionAmount.divide(BigDecimal.valueOf(30));
        } else if (startCurrency.equals("USD") && finishCurrency.equals("RUB")) {
            result = conversionAmount.multiply(BigDecimal.valueOf(30));
        } else if (startCurrency.equals("RUB") && finishCurrency.equals("EUR")) {
            result = conversionAmount.divide(BigDecimal.valueOf(35));
        } else if (startCurrency.equals("EUR") && finishCurrency.equals("RUB")) {
            result = conversionAmount.multiply(BigDecimal.valueOf(35));
        } else if (startCurrency.equals("USD") && finishCurrency.equals("EUR")) {
            result = conversionAmount.divide(BigDecimal.valueOf(1.7));
        } else if (startCurrency.equals("EUR") && finishCurrency.equals("USD")) {
            result = conversionAmount.multiply(BigDecimal.valueOf(1.7));
        }

        return result;
    }

    /**
     * Создать метод "есть ли сумма на счете": String номер счета, BigDecimal сумма, валюта.
     * Проверить, есть ли указанная сумма на счете. Если валюты счетов различаются, происходит конвертация.
     */

    public void checkSum(String accountId, BigDecimal sum, String currencyCode) {
        currencyComparison(accountId, sum, currencyCode);

        if (accounts.get(accountId).getAccountBalance().subtract(sum).floatValue() >= 0.0) {
            System.out.println("Необходимоя сумма на счёте есть.");
        } else {
            System.out.println("Недостаточно средств на счёте.");
        }
    }

    /**
     * Создать метод "списание со счета". Сигнатура: String номер счета, BigDecimal сумма списания, валюта списания.
     * Необходимо уменьшать сумму на счете(если валюта списания отличается от валюты счета,
     * необходимо произвести конвертацию суммы списания). Списывать можно только до нуля (Иначе ошибка).
     * Если сумма списания больше суммы на счете, должна быть ошибка.
     */

    public void debitingAccount(String accountId, BigDecimal debitingSum, String debitingCurrency) {
        currencyComparison(accountId, debitingSum, debitingCurrency);

        BigDecimal result = accounts.get(accountId).getAccountBalance().subtract(debitingSum);

        if (result.floatValue() <= 0.0 || accounts.get(accountId).getAccountBalance().floatValue() <= debitingSum.floatValue()) {
            try {
                throw new AmountDebitedExceedsAmountAccount();
            } catch (AmountDebitedExceedsAmountAccount e) {
                System.out.println("Не хватает средств на счёте.");
            }
        }
    }

    /**
     * На счет могут поступать деньги. Сигнатура: String номер счета, BigDecimal сумма прихода, валюта прихода.
     * Необходимо увеличить сумму на счете(если валюта списания отличается от валюты счета,
     * необходимо произвести конвертацию суммы списания).
     */

    public void addMoney(String accountId, BigDecimal addSum, String addCurrency) {
        currencyComparison(accountId, addSum, addCurrency);

        BigDecimal addMoney = accounts.get(accountId).getAccountBalance().add(addSum);
    }

    /**
     * Счета могут делать переводы внутри банка. Сигнатура метода: String Номер счета отправителя,
     * String номер счета получателя, BigDecimal сумма списания. Проверить есть ли сумма на счете (метод уже есть).
     * Если валюты счетов не равны, то произвести конвертацию. Итоговую сумму уменьшить на 1% - комиссия банка.
     */

    public void internalTransfers(String accountIdSender, String accountIdRecipient, BigDecimal subtractSum) {
        checkSum(accountIdRecipient, subtractSum, accounts.get(accountIdRecipient).getCurrencyCode());
        currencyComparison(accountIdSender, subtractSum, accounts.get(accountIdRecipient).getCurrencyCode());

        BigDecimal commission = subtractSum.multiply(BigDecimal.valueOf(0.01));

        BigDecimal recipient = accounts.get(accountIdRecipient).getAccountBalance().subtract(subtractSum.subtract(commission));
        BigDecimal sender = accounts.get(accountIdSender).getAccountBalance().add(subtractSum.subtract(commission));
    }

    /**
     * Создать метод "инфо по счету". Сигнатура: String номер счета. Возвращает всю информацию по счету.
     * Если счета нет, то ошибка.
     */

    public String infoAccount(String accountId) {
        String info = null;

        if (accounts.get(accountId) == null) {
            try {
                throw new AccountNotFound();
            } catch (AccountNotFound e) {
                System.out.println("Такого счёта нет.");
            }
        } else {
            info = accounts.get(accountId).toString();
        }

        return info;
    }

    public void currencyComparison(String accountId, BigDecimal sum, String operationCurrency) {
        if (!operationCurrency.equals(accounts.get(accountId).getCurrencyCode())) {
            conversion(accounts.get(accountId).getCurrencyCode(), sum, operationCurrency);
        }
    }

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
