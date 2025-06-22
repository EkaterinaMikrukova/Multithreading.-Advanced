package com.edu.WebScrapeApplication.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки модели NewsApiResponse.
 * Проверяет корректность работы геттеров/сеттеров и обработку граничных случаев.
 */
class NewsApiResponseTest {

    /**
     * Тест проверяет создание объекта и доступ к полям через геттеры.
     */
    @Test
    void shouldCreateObjectAndAccessFields() {
        // Подготовка тестовых данных:
        // Создаем две тестовые новости
        NewsItem item1 = new NewsItem();
        item1.setTitle("Test Title 1");

        NewsItem item2 = new NewsItem();
        item2.setTitle("Test Title 2");

        // Создаем список статей
        List<NewsItem> articles = Arrays.asList(item1, item2);

        // Создаем и наполняем тестируемый объект
        NewsApiResponse response = new NewsApiResponse();
        response.setStatus("ok");          // Устанавливаем статус
        response.setTotalResults(2);       // Устанавливаем количество результатов
        response.setArticles(articles);    // Устанавливаем список статей

        // Проверяем все поля:
        // 1. Статус должен быть "ok"
        // 2. totalResults должно быть 2
        // 3. Количество статей должно быть 2
        // 4-5. Проверяем заголовки статей
        assertAll(
                () -> assertEquals("ok", response.getStatus()),
                () -> assertEquals(2, response.getTotalResults()),
                () -> assertEquals(2, response.getArticles().size()),
                () -> assertEquals("Test Title 1", response.getArticles().get(0).getTitle()),
                () -> assertEquals("Test Title 2", response.getArticles().get(1).getTitle())
        );
    }

    /**
     * Тест проверяет обработку null-значений.
     */
    @Test
    void shouldHandleNullValues() {
        // Создаем и наполняем тестируемый объект null-значениями
        NewsApiResponse response = new NewsApiResponse();
        response.setStatus(null);      // Устанавливаем null статус
        response.setArticles(null);    // Устанавливаем null список статей

        // Проверяем:
        // 1. Статус должен быть null
        // 2. totalResults должно быть 0 (значение по умолчанию для int)
        // 3. Список статей должен быть null
        assertAll(
                () -> assertNull(response.getStatus()),
                () -> assertEquals(0, response.getTotalResults()),
                () -> assertNull(response.getArticles())
        );
    }

    /**
     * Тест проверяет работу с пустым списком статей.
     */
    @Test
    void shouldHandleEmptyArticleList() {
        // Создаем тестируемый объект с пустым списком статей
        NewsApiResponse response = new NewsApiResponse();
        response.setArticles(List.of());  // Устанавливаем пустой список

        // Проверяем что список статей пустой
        assertTrue(response.getArticles().isEmpty());
    }

    /**
     * Тест проверяет обновление полей объекта.
     */
    @Test
    void shouldUpdateFields() {
        // Подготовка: создаем объект и устанавливаем начальные значения
        NewsApiResponse response = new NewsApiResponse();
        response.setStatus("error");      // Начальный статус
        response.setTotalResults(0);      // Начальное количество результатов

        // Обновляем значения полей
        response.setStatus("ok");         // Новый статус
        response.setTotalResults(10);     // Новое количество результатов

        // Проверяем что значения обновились:
        // 1. Статус должен быть "ok"
        // 2. totalResults должно быть 10
        assertAll(
                () -> assertEquals("ok", response.getStatus()),
                () -> assertEquals(10, response.getTotalResults())
        );
    }
}