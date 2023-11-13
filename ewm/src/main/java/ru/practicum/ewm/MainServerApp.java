package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.practicum.stats.client.StatsClient;

@SpringBootApplication
public class MainServerApp {
    public static void main(String[] args) {
        SpringApplication.run(MainServerApp.class, args);
    }

    @Bean
    public StatsClient statsClient() {
        return new StatsClient("http://localhost:9090");
    }
}
