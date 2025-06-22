package com.edu.WebScrapeApplication.controllers;

import com.edu.WebScrapeApplication.model.NewsItem;
import com.edu.WebScrapeApplication.service.NewsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для NewsController.
 * Проверяет работу REST контроллера для получения новостей.
 */
@SpringBootTest // Аннотация для интеграционных тестов с поднятием контекста Spring
class NewsControllerTest {

    // Мок сервиса для работы с новостями
    @Mock
    private NewsService newsService;

    // Тестируемый контроллер с внедренными моками
    @InjectMocks
    private NewsController newsController;

    /**
     * Тест проверяет успешное получение списка новостей.
     * @throws ExecutionException при ошибках выполнения асинхронных задач
     * @throws InterruptedException при прерывании потока
     */
    @Test
    void getAggregatedNews_ShouldReturnNewsList() throws ExecutionException, InterruptedException {
        // Подготовка тестовых данных
        List<NewsItem> expectedNews = Arrays.asList(
                new NewsItem("Title 1", "Description 1", "http://example.com/1", null, "Source 1"),
                new NewsItem("Title 2", "Description 2", "http://example.com/2", null, "Source 2")
        );

        // Настройка поведения мока
        when(newsService.getAggregatedNews()).thenReturn(expectedNews);

        // Вызов тестируемого метода
        List<NewsItem> result = newsController.getAggregatedNews();

        // Проверки:
        // 1. Результат не должен быть null
        // 2. Должно вернуться 2 новости
        // 3. Возвращенный список должен соответствовать ожидаемому
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedNews, result);
    }

    /**
     * Тест проверяет обработку ExecutionException.
     * @throws ExecutionException ожидаемое исключение
     * @throws InterruptedException при прерывании потока
     */
    @Test
    void getAggregatedNews_ShouldHandleExecutionException() throws ExecutionException, InterruptedException {
        // Настройка мока на выброс исключения
        when(newsService.getAggregatedNews())
                .thenThrow(new ExecutionException("Async error", new RuntimeException()));

        // Проверка, что контроллер пробрасывает исключение
        assertThrows(ExecutionException.class, () -> {
            newsController.getAggregatedNews();
        });
    }

    /**
     * Тест проверяет обработку InterruptedException.
     * @throws ExecutionException при ошибках выполнения асинхронных задач
     * @throws InterruptedException ожидаемое исключение
     */
    @Test
    void getAggregatedNews_ShouldHandleInterruptedException() throws ExecutionException, InterruptedException {
        // Настройка мока на выброс исключения
        when(newsService.getAggregatedNews())
                .thenThrow(new InterruptedException("Thread interrupted"));

        // Проверка, что контроллер пробрасывает исключение
        assertThrows(InterruptedException.class, () -> {
            newsController.getAggregatedNews();
        });
    }

    /**
     * Тест проверяет корректность возвращаемого ответа.
     * @throws ExecutionException при ошибках выполнения асинхронных задач
     * @throws InterruptedException при прерывании потока
     */
    @Test
    void controllerShouldReturnCorrectResponseEntity() throws ExecutionException, InterruptedException {
        // Подготовка тестовых данных
        List<NewsItem> mockNews = List.of(
                new NewsItem("Test Title", "Test Desc", "http://test.com", null, "Test Source")
        );

        // Настройка поведения мока
        when(newsService.getAggregatedNews()).thenReturn(mockNews);

        // Вызов тестируемого метода
        List<NewsItem> response = newsController.getAggregatedNews();

        // Проверки:
        // 1. Ответ не должен быть null
        // 2. Должна вернуться 1 новость
        // 3. Заголовок новости должен соответствовать ожидаемому
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Title", response.get(0).getTitle());
    }
}