package com.example.bank_project;

import com.example.bank_project.config.ConfigString;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class BankProjectApplication {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ConfigString.class);


        context.close();
    }
}
