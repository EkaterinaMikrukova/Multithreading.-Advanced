package com.edu.WebScrapeApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Объявление класса конфигурации Spring
@Configuration  // Помечает класс как источник определений бинов Spring
@EnableAsync    // Включает поддержку асинхронного выполнения методов
public class AsyncConfig {

    // Определение бина RestTemplate
    @Bean       // Помечает метод как создающий бин, которым Spring будет управлять
    public RestTemplate restTemplate() {
        return new RestTemplate();  // Создает и возвращает новый экземпляр RestTemplate
        // (HTTP-клиент для взаимодействия с RESTful сервисами)
    }

    // Определение бина ExecutorService
    @Bean
    public ExecutorService newsExecutorService() {
        return Executors.newFixedThreadPool(5);  // Создает пул потоков фиксированного размера (5 потоков)
        // для асинхронного выполнения задач
    }
}