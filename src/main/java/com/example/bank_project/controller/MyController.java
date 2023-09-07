package com.example.bank_project.controller;

import com.example.bank_project.dto.AccountDtoRq;
import org.springframework.web.bind.annotation.*;

/**
 * Создать Rest контроллер, который принимает все запросы по url "/bank/api"
 */

@RestController
@RequestMapping("/bank/api")
public class MyController {

    /**
     * Endpoint "/account/create" - Создание счета. В этот запрос будут передаваться поля: фио владельца, валюта счета.
     * Вызывается одноименный метод из account service. Ответ: созданный номер счета.
     */

    @PostMapping(path = "/account/create")
    public void createAccount(@RequestBody AccountDtoRq accountDto) {

    }

    @GetMapping("/test")
    public String test() {
        return "Hello World!!";
    }
}
