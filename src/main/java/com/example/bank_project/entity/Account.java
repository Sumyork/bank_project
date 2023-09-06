package com.example.bank_project.entity;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Создать класс Account (счет), в котором будет: номер счета, валюта счета (может быть только 3 валюты доллар
 * (код 840), евро(978), рубль(810)) <- хорошо подумать про тип данных, наименование счета
 * (пример: расчетный счет Иванова ИИ), сумма счета (или по другому остаток) BigDecimal, открыт/закрыт.
 */
@Data
public class Account {
    @NonNull
    private String accountId;
    @NonNull
    private String accountName;
    @NonNull
    private String currencyCode;
    @NonNull
    private BigDecimal accountBalance;

    private boolean status = true;
}
