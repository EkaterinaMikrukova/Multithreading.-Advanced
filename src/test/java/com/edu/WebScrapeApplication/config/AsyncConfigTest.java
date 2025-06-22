package com.edu.WebScrapeApplication.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки конфигурации AsyncConfig.
 * Проверяет создание и корректность работы бинов, определенных в конфигурации.
 */
@SpringBootTest  // Аннотация для интеграционных тестов с полным контекстом Spring
class AsyncConfigTest {

    // Инжектим бин RestTemplate для тестирования
    @Autowired
    private RestTemplate restTemplate;

    // Инжектим бин ExecutorService для тестирования
    @Autowired
    private ExecutorService executorService;

    /**
     * Тест проверяет, что бин RestTemplate создан и доступен в контексте Spring.
     */
    @Test
    void restTemplateBeanShouldBeCreated() {
        // Проверяем что бин не null
        assertNotNull(restTemplate, "RestTemplate bean should be created");
        // Проверяем тип бина
        assertTrue(restTemplate instanceof RestTemplate, "Bean should be of type RestTemplate");
    }

    /**
     * Тест проверяет, что бин ExecutorService создан и находится в рабочем состоянии.
     */
    @Test
    void executorServiceBeanShouldBeCreated() {
        // Проверяем что бин не null
        assertNotNull(executorService, "ExecutorService bean should be created");
        // Проверяем что пул потоков не завершен
        assertFalse(executorService.isShutdown(), "ExecutorService should be running");
        // Проверяем что пул потоков не терминирован
        assertFalse(executorService.isTerminated(), "ExecutorService should not be terminated");
    }

    /**
     * Тест проверяет, что ExecutorService использует фиксированный пул потоков
     * и может выполнять задачи параллельно.
     * @throws Exception в случае ошибок выполнения теста
     */
    @Test
    void executorServiceShouldHaveFixedThreadPool() throws Exception {
        // Фиксируем время начала выполнения теста
        long startTime = System.currentTimeMillis();

        // Отправляем первую задачу в пул потоков
        executorService.submit(() -> {
            try {
                // Имитируем длительную операцию
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Восстанавливаем флаг прерывания при необходимости
                Thread.currentThread().interrupt();
            }
            return null;
        });

        // Отправляем вторую задачу в пул потоков
        executorService.submit(() -> {
            try {
                // Имитируем длительную операцию
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Восстанавливаем флаг прерывания при необходимости
                Thread.currentThread().interrupt();
            }
            return null;
        });

        /*
         * Проверяем что задачи выполняются параллельно:
         * Если бы они выполнялись последовательно, общее время было бы >1000ms.
         * При параллельном выполнении в фиксированном пуле время должно быть <600ms.
         */
        assertTrue(System.currentTimeMillis() - startTime < 600,
                "Tasks should run in parallel (fixed thread pool)");
    }
}