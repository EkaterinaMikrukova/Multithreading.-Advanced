package com.edu.WebScrapeApplication.service.impl;

import com.edu.WebScrapeApplication.model.NewsApiResponse;
import com.edu.WebScrapeApplication.model.NewsItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для NewsServiceImpl.
 * Проверяет логику агрегации новостей из различных источников.
 */
@ExtendWith(MockitoExtension.class) // Интеграция Mockito с JUnit 5
class NewsServiceImplTest {

    // Мок ExecutorService для тестирования многопоточности
    @Mock
    private ExecutorService executorService;

    // Мок RestTemplate для тестирования HTTP-запросов
    @Mock
    private RestTemplate restTemplate;

    // Тестируемый сервис
    private NewsServiceImpl newsService;

    // Тестовые значения для конфигурации
    private final String testApiKey = "test-api-key";
    private final int testTimeout = 5;

    /**
     * Инициализация перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        // Создаем экземпляр тестируемого сервиса с моками
        newsService = new NewsServiceImpl(executorService, restTemplate, testApiKey, testTimeout);
    }

    /**
     * Тест проверяет корректную агрегацию новостей из нескольких источников.
     */
    @Test
    void getAggregatedNews_ShouldReturnCombinedNews() throws Exception {
        // Подготовка тестовых данных
        NewsItem item1 = new NewsItem("Title1", "Desc1", "http://1.com", new Date(), "Source1");
        NewsItem item2 = new NewsItem("Title2", "Desc2", "http://2.com", new Date(), "Source2");
        NewsItem item3 = new NewsItem("Title3", "Desc3", "http://3.com", new Date(), "Source3");

        // Настройка моков:
        // Каждый вызов submit() будет возвращать Future с тестовыми данными
        when(executorService.submit(any(Callable.class)))
                .thenReturn(completedFuture(List.of(item1)))
                .thenReturn(completedFuture(List.of(item2)))
                .thenReturn(completedFuture(List.of(item3)));

        // Вызов тестируемого метода
        List<NewsItem> result = newsService.getAggregatedNews();

        // Проверки:
        // 1. Общее количество новостей
        // 2-4. Наличие всех ожидаемых новостей в результате
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getTitle().equals("Title1")));
        assertTrue(result.stream().anyMatch(i -> i.getTitle().equals("Title2")));
        assertTrue(result.stream().anyMatch(i -> i.getTitle().equals("Title3")));
    }

    /**
     * Тест проверяет обработку таймаутов при запросах к источникам новостей.
     */
    @Test
    void getAggregatedNews_ShouldHandleTimeouts() throws Exception {
        // Подготовка моков Future для каждого источника
        Future<List<NewsItem>> future1 = mock(Future.class);
        Future<List<NewsItem>> future2 = mock(Future.class);
        Future<List<NewsItem>> future3 = mock(Future.class);

        // Настройка executorService для возврата наших Future
        when(executorService.submit(any(Callable.class)))
                .thenReturn(future1)
                .thenReturn(future2)
                .thenReturn(future3);

        // Настройка Future на выброс TimeoutException при вызове get()
        when(future1.get(testTimeout, TimeUnit.SECONDS)).thenThrow(new TimeoutException());
        when(future2.get(testTimeout, TimeUnit.SECONDS)).thenThrow(new TimeoutException());
        when(future3.get(testTimeout, TimeUnit.SECONDS)).thenThrow(new TimeoutException());

        // Вызов тестируемого метода
        List<NewsItem> result = newsService.getAggregatedNews();

        // Проверка что результат пуст при таймаутах
        assertTrue(result.isEmpty());

        // Проверка что для всех Future был вызван cancel()
        verify(future1).cancel(true);
        verify(future2).cancel(true);
        verify(future3).cancel(true);
    }

    /**
     * Тест проверяет успешное получение новостей из источника.
     */
    @Test
    void fetchNewsFromSource_ShouldReturnArticlesOnSuccess() {
        // Подготовка тестового ответа
        NewsApiResponse response = new NewsApiResponse();
        response.setArticles(List.of(new NewsItem())); // Одна пустая новость

        // Настройка RestTemplate на возврат тестового ответа
        when(restTemplate.getForObject(anyString(), eq(NewsApiResponse.class)))
                .thenReturn(response);

        // Вызов тестируемого метода
        List<NewsItem> result = newsService.fetchNewsFromSource("test-url");

        // Проверка что получена 1 новость
        assertEquals(1, result.size());
    }

    /**
     * Тест проверяет обработку ошибок при запросе к источнику новостей.
     */
    @Test
    void fetchNewsFromSource_ShouldReturnEmptyListOnError() {
        // Настройка RestTemplate на выброс исключения
        when(restTemplate.getForObject(anyString(), eq(NewsApiResponse.class)))
                .thenThrow(new RuntimeException("API error"));

        // Вызов тестируемого метода
        List<NewsItem> result = newsService.fetchNewsFromSource("test-url");

        // Проверка что возвращен пустой список при ошибке
        assertTrue(result.isEmpty());
    }

    /**
     * Тест проверяет генерацию URL для источников новостей.
     */
    @Test
    void getNewsSources_ShouldReturnCorrectUrls() {
        // Вызов тестируемого метода
        List<String> sources = newsService.getNewsSources();

        // Проверки:
        // 1. Количество источников
        // 2-4. Каждый URL содержит соответствующий параметр
        // 5. Все URL содержат API-ключ
        assertEquals(3, sources.size());
        assertTrue(sources.get(0).contains("country=ru"));
        assertTrue(sources.get(1).contains("domains=bbc.co.uk"));
        assertTrue(sources.get(2).contains("sources=techcrunch"));
        assertTrue(sources.stream().allMatch(s -> s.contains(testApiKey)));
    }

    /**
     * Вспомогательный метод для создания завершенного Future с заданным значением.
     */
    private <T> Future<T> completedFuture(T value) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.complete(value); // Немедленно завершаем Future с заданным значением
        return future;
    }
}