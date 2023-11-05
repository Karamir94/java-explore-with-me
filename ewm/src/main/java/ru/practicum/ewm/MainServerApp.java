package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = "ru.practicum.stats.client")
public class MainServerApp {
    public static void main(String[] args) {
        SpringApplication.run(MainServerApp.class, args);
    }
}