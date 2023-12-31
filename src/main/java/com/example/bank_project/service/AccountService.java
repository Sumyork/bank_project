package com.example.bank_project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import com.example.bank_project.entity.Account;
import com.example.bank_project.exception.AccountExistsException;
import com.example.bank_project.exception.AccountNotFoundException;
import com.example.bank_project.exception.AmountDebitedExceedsAmountAccountException;
import com.example.bank_project.exception.IncorrectFieldException;
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
     */

    @Override
    public String openingBankAccount(String accountName, String currencyCode) throws AccountExistsException, IncorrectFieldException {

        String keyAccount = generateNumberAccount(currencyCode);

        if (accounts.putIfAbsent(keyAccount, new Account(keyAccount, accountName,
                currencyCode, new BigDecimal("0"))) == null) {
            accounts.putIfAbsent(keyAccount, new Account(keyAccount, accountName,
                    currencyCode, new BigDecimal("0")));
        } else {
            throw new AccountExistsException("Аккаунт уже существует.");
        }

        return keyAccount;
    }

    public String generateNumberAccount(String currencyCode) throws IncorrectFieldException {
        checkIncorrectCurrencyCode(currencyCode);
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
            result = conversionAmount.divide(BigDecimal.valueOf(30.0), RoundingMode.HALF_UP);
        } else if (startCurrency.equals("USD") && finishCurrency.equals("RUB")) {
            result = conversionAmount.multiply(BigDecimal.valueOf(30.0));
        } else if (startCurrency.equals("RUB") && finishCurrency.equals("EUR")) {
            result = conversionAmount.divide(BigDecimal.valueOf(35.0), RoundingMode.HALF_UP);
        } else if (startCurrency.equals("EUR") && finishCurrency.equals("RUB")) {
            result = conversionAmount.multiply(BigDecimal.valueOf(35.0));
        } else if (startCurrency.equals("USD") && finishCurrency.equals("EUR")) {
            result = conversionAmount.divide(BigDecimal.valueOf(1.7), RoundingMode.HALF_UP);
        } else if (startCurrency.equals("EUR") && finishCurrency.equals("USD")) {
            result = conversionAmount.multiply(BigDecimal.valueOf(1.7));
        }

        return result;
    }

    /**
     * Создать метод "списание со счета". Сигнатура: String номер счета, BigDecimal сумма списания, валюта списания.
     * Необходимо уменьшать сумму на счете(если валюта списания отличается от валюты счета,
     * необходимо произвести конвертацию суммы списания). Списывать можно только до нуля (Иначе ошибка).
     * Если сумма списания больше суммы на счете, должна быть ошибка.
     */

    public String debitingAccount(String accountId, BigDecimal debitingSum, String debitingCurrency) throws IncorrectFieldException, AmountDebitedExceedsAmountAccountException {

        checkIncomingNumber(debitingSum);
        checkIncorrectCurrencyCode(debitingCurrency);
        checkSum(accountId, debitingSum, debitingCurrency);

        BigDecimal resultCurrency = currencyComparison(accountId, debitingSum, debitingCurrency);

        BigDecimal result = accounts.get(accountId).getAccountBalance().subtract(resultCurrency);
        accounts.get(accountId).setAccountBalance(result);

        return "Средства списаны со счёта. Баланс на счёте " + accounts.get(accountId).getAccountBalance()
                + " " + accounts.get(accountId).getCurrencyCode();
    }

    /**
     * На счет могут поступать деньги. Сигнатура: String номер счета, BigDecimal сумма прихода, валюта прихода.
     * Необходимо увеличить сумму на счете(если валюта списания отличается от валюты счета,
     * необходимо произвести конвертацию суммы списания).
     */

    public String addMoney(String accountId, BigDecimal addSum, String addCurrency) throws IncorrectFieldException {

        checkIncomingNumber(addSum);
        checkIncorrectCurrencyCode(addCurrency);

        BigDecimal addComparisonMoney = currencyComparison(accountId, addSum, addCurrency);

        BigDecimal addMoney = accounts.get(accountId).getAccountBalance().add(addComparisonMoney);

        accounts.get(accountId).setAccountBalance(addMoney);
        return "Счёт пополнился на " + addComparisonMoney + " "
                + accounts.get(accountId).getCurrencyCode() + ". Баланс на счёте "
                + addMoney + " " +accounts.get(accountId).getCurrencyCode();
    }

    /**
     * Счета могут делать переводы внутри банка. Сигнатура метода: String Номер счета отправителя,
     * String номер счета получателя, BigDecimal сумма списания. Проверить есть ли сумма на счете (метод уже есть).
     * Если валюты счетов не равны, то произвести конвертацию. Итоговую сумму уменьшить на 1% - комиссия банка.
     */

    public String internalTransfers(String accountIdSender, String accountIdRecipient, BigDecimal subtractSum) throws AmountDebitedExceedsAmountAccountException, IncorrectFieldException {

        checkIncomingNumber(subtractSum);
        checkSum(accountIdSender, subtractSum, accounts.get(accountIdSender).getCurrencyCode());

        BigDecimal transferMoney = currencyComparison(accountIdRecipient, subtractSum,
                accounts.get(accountIdSender).getCurrencyCode());

        BigDecimal commissionCurrencyConversion = subtractSum.multiply(BigDecimal.valueOf(0.01));
        BigDecimal commission = transferMoney.multiply(BigDecimal.valueOf(0.01));

        BigDecimal totalSumTransfer = transferMoney.subtract(commission);

        BigDecimal sender = accounts.get(accountIdSender).getAccountBalance()
                .subtract(subtractSum);
        accounts.get(accountIdSender).setAccountBalance(sender);

        BigDecimal recipient = accounts.get(accountIdRecipient).getAccountBalance()
                .add(totalSumTransfer);
        accounts.get(accountIdRecipient).setAccountBalance(recipient);


        String message = "Перевод " + subtractSum + " " + accounts.get(accountIdSender).getCurrencyCode()
                + " произведён. Комиссия банка: " + commissionCurrencyConversion
                + " " + accounts.get(accountIdSender).getCurrencyCode() + "\n";

        String balanceSender = "Баланс отправителя: " + accounts.get(accountIdSender).getAccountBalance()
                + " " + accounts.get(accountIdSender).getCurrencyCode() + "\n";

        String balanceRecipient = "Баланс получателя: " + accounts.get(accountIdRecipient).getAccountBalance()
                + " " + accounts.get(accountIdRecipient).getCurrencyCode() + "\n";

        return message + balanceSender + balanceRecipient;
    }

    /**
     * Создать метод "инфо по счету". Сигнатура: String номер счета. Возвращает всю информацию по счету.
     * Если счета нет, то ошибка.
     */

    public String infoAccount(String accountId) throws AccountNotFoundException {
        String info = null;

        if (accounts.get(accountId) == null) {
            throw new AccountNotFoundException("Счёт " + accountId + " не существует.");
        } else {
            info = accounts.get(accountId).toString();
        }

        return info;
    }

    /**
     * Создать метод "закрыть счет", сигнатура: String номер счета. Находит счет в хранилище счетов.
     * Обнуляет его остатки, проставляет статус "закрыт". Если счет не найден, то выдается ошибка.
     */

    @Override
    public String closingBankAccount(String accountId) throws AccountNotFoundException {

        String result;

        if (accounts.get(accountId) != null) {
            accounts.get(accountId).setAccountBalance(new BigDecimal("0"));
            accounts.remove(accountId).setStatus(false);
            result = "Счёт " + accountId + " закрыт.";
        } else {
            throw new AccountNotFoundException("Счёт " + accountId + " не существует.");
        }
        return result;
    }

    public BigDecimal currencyComparison(String accountId, BigDecimal sum, String operationCurrency) {

        BigDecimal currency = sum;

        if (!operationCurrency.equals(accounts.get(accountId).getCurrencyCode())) {
            currency = conversion(operationCurrency, sum, accounts.get(accountId).getCurrencyCode());
        }

        return currency;
    }

    /**
     * Создать метод "есть ли сумма на счете": String номер счета, BigDecimal сумма, валюта.
     * Проверить, есть ли указанная сумма на счете. Если валюты счетов различаются, происходит конвертация.
     */

    public void checkSum(String accountId, BigDecimal sum, String currencyCode) throws AmountDebitedExceedsAmountAccountException {

        BigDecimal checkMoney = currencyComparison(accountId, sum, currencyCode);

        if (accounts.get(accountId).getAccountBalance().floatValue() < checkMoney.floatValue()) {
            throw new AmountDebitedExceedsAmountAccountException("Недостаточно средств на счёте.");
        }
    }

    public void checkIncomingNumber(BigDecimal incomingNumber) throws IncorrectFieldException {
        if (incomingNumber.floatValue() <= 0.0) {
            throw new IncorrectFieldException("Вводимая сумма должна быть больше 0.");
        }
    }

    public void checkIncorrectCurrencyCode(String currencyCode) throws IncorrectFieldException {
        if (!currencyCode.equals("RUB") && !currencyCode.equals("USD") && !currencyCode.equals("EUR")) {
            throw new IncorrectFieldException("Некорректная валюта.");
        }
    }
}
