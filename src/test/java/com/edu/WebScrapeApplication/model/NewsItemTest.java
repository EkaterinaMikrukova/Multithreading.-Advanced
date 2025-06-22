package com.edu.WebScrapeApplication.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для модели NewsItem.
 * Проверяет корректность работы конструкторов, геттеров и сеттеров.
 */
class NewsItemTest {

    /**
     * Тест проверяет создание объекта через конструктор с параметрами.
     */
    @Test
    void shouldCreateNewsItemWithConstructor() {
        // Подготовка тестовых данных
        Date testDate = new Date();  // Текущая дата для теста
        String expectedTitle = "Test Title";
        String expectedDescription = "Test Description";
        String expectedUrl = "http://test.com";
        String expectedSource = "Test Source";

        // Создание объекта через конструктор
        NewsItem item = new NewsItem(
                expectedTitle,
                expectedDescription,
                expectedUrl,
                testDate,
                expectedSource
        );

        // Проверка что все поля установлены правильно:
        // 1. Заголовок
        // 2. Описание
        // 3. URL
        // 4. Дата публикации
        // 5. Источник
        assertAll(
                () -> assertEquals(expectedTitle, item.getTitle()),
                () -> assertEquals(expectedDescription, item.getDescription()),
                () -> assertEquals(expectedUrl, item.getUrl()),
                () -> assertEquals(testDate, item.getPublishedAt()),
                () -> assertEquals(expectedSource, item.getSourceName())
        );
    }

    /**
     * Тест проверяет создание пустого объекта через конструктор по умолчанию.
     */
    @Test
    void shouldCreateEmptyNewsItem() {
        // Создание объекта через конструктор по умолчанию
        NewsItem item = new NewsItem();

        // Проверка что все поля null (значения по умолчанию):
        // 1. Заголовок
        // 2. Описание
        // 3. URL
        // 4. Дата публикации
        // 5. Источник
        assertAll(
                () -> assertNull(item.getTitle()),
                () -> assertNull(item.getDescription()),
                () -> assertNull(item.getUrl()),
                () -> assertNull(item.getPublishedAt()),
                () -> assertNull(item.getSourceName())
        );
    }

    /**
     * Тест проверяет установку и получение значений через сеттеры и геттеры.
     */
    @Test
    void shouldSetAndGetProperties() {
        // Подготовка
        NewsItem item = new NewsItem();  // Пустой объект
        Date testDate = new Date();      // Тестовая дата

        // Установка значений через сеттеры
        item.setTitle("New Title");
        item.setDescription("New Description");
        item.setUrl("http://newurl.com");
        item.setPublishedAt(testDate);
        item.setSourceName("New Source");

        // Проверка что значения установились правильно:
        // 1. Заголовок
        // 2. Описание
        // 3. URL
        // 4. Дата публикации
        // 5. Источник
        assertAll(
                () -> assertEquals("New Title", item.getTitle()),
                () -> assertEquals("New Description", item.getDescription()),
                () -> assertEquals("http://newurl.com", item.getUrl()),
                () -> assertEquals(testDate, item.getPublishedAt()),
                () -> assertEquals("New Source", item.getSourceName())
        );
    }

    /**
     * Тест проверяет обработку null-значений в полях объекта.
     */
    @Test
    void shouldHandleNullValues() {
        // Подготовка - создаем объект с не-null значениями
        NewsItem item = new NewsItem(
                "Title", "Desc", "http://url.com", new Date(), "Source"
        );

        // Устанавливаем null значения через сеттеры
        item.setTitle(null);
        item.setDescription(null);
        item.setUrl(null);
        item.setPublishedAt(null);
        item.setSourceName(null);

        // Проверка что все поля стали null:
        // 1. Заголовок
        // 2. Описание
        // 3. URL
        // 4. Дата публикации
        // 5. Источник
        assertAll(
                () -> assertNull(item.getTitle()),
                () -> assertNull(item.getDescription()),
                () -> assertNull(item.getUrl()),
                () -> assertNull(item.getPublishedAt()),
                () -> assertNull(item.getSourceName())
        );
    }

    /**
     * Тест проверяет обновление значений полей объекта.
     */
    @Test
    void shouldUpdateProperties() {
        // Подготовка
        NewsItem item = new NewsItem();
        Date initialDate = new Date();  // Начальная дата
        Date updatedDate = new Date(initialDate.getTime() + 1000);  // Дата +1 секунда

        // Установка начальных значений
        item.setTitle("Initial Title");
        item.setPublishedAt(initialDate);

        // Обновление значений
        item.setTitle("Updated Title");
        item.setPublishedAt(updatedDate);

        // Проверка:
        // 1. Заголовок обновился
        // 2. Дата публикации обновилась
        // 3. Новая дата не равна старой
        assertAll(
                () -> assertEquals("Updated Title", item.getTitle()),
                () -> assertEquals(updatedDate, item.getPublishedAt()),
                () -> assertNotEquals(initialDate, item.getPublishedAt())
        );
    }
}