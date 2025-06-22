package com.edu.WebScrapeApplication.service.impl;

import com.edu.WebScrapeApplication.model.NewsApiResponse;
import com.edu.WebScrapeApplication.model.NewsItem;
import com.edu.WebScrapeApplication.service.NewsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service  // Помечает класс как сервисный компонент Spring
public class NewsServiceImpl implements NewsService {

    // Пул потоков для параллельного выполнения запросов
    private final ExecutorService executorService;
    // Клиент для HTTP-запросов
    private final RestTemplate restTemplate;
    // API ключ для NewsAPI (инжектится из конфигурации)
    private final String apiKey;
    // Таймаут запросов в секундах (по умолчанию 5)
    private final int requestTimeoutSeconds;

    // Конструктор с dependency injection
    public NewsServiceImpl(
            ExecutorService executorService,
            RestTemplate restTemplate,
            @Value("${news.api.key}") String apiKey,  // Значение из application.properties
            @Value("${news.api.timeout.seconds:5}") int requestTimeoutSeconds) {  // Со значением по умолчанию
        this.executorService = executorService;
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }

    @Override
    public List<NewsItem> getAggregatedNews() throws ExecutionException, InterruptedException {
        // Получаем список URL источников новостей
        List<String> sources = getNewsSources();
        // Коллекция для хранения Future-объектов асинхронных задач
        List<Future<List<NewsItem>>> futures = new ArrayList<>();

        // Запускаем задачи параллельно для каждого источника
        for (String source : sources) {
            // Создаем задачу для получения новостей из источника
            Callable<List<NewsItem>> task = () -> fetchNewsFromSource(source);
            // Отправляем задачу в пул потоков и сохраняем Future
            futures.add(executorService.submit(task));
        }

        // Собираем результаты с таймаутом
        List<NewsItem> allNews = new ArrayList<>();
        for (Future<List<NewsItem>> future : futures) {
            try {
                // Получаем результат с ограничением по времени
                allNews.addAll(future.get(requestTimeoutSeconds, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                future.cancel(true);  // Прерываем зависшие запросы
                System.err.println("Request timed out");  // Логируем таймаут
            }
        }

        // Фильтрация и сортировка результатов:
        return allNews.stream()
                .filter(Objects::nonNull)  // Удаляем null-значения
                .sorted(Comparator.comparing(NewsItem::getPublishedAt).reversed())  // Сортируем по дате (новые сначала)
                .collect(Collectors.toList());  // Преобразуем в список
    }

    // Возвращает список URL API для различных источников новостей
    List<String> getNewsSources() {
        return Arrays.asList(
                // Российские новости
                String.format("https://newsapi.org/v2/top-headlines?country=ru&apiKey=%s", apiKey),
                // BBC News
                String.format("https://newsapi.org/v2/everything?domains=bbc.co.uk&apiKey=%s", apiKey),
                // TechCrunch
                String.format("https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=%s", apiKey)
        );
    }

    // Получает новости из конкретного источника
    List<NewsItem> fetchNewsFromSource(String apiUrl) {
        try {
            // Выполняем HTTP-запрос и десериализуем ответ
            NewsApiResponse response = restTemplate.getForObject(apiUrl, NewsApiResponse.class);
            // Безопасное извлечение списка новостей (защита от NPE)
            return Optional.ofNullable(response)
                    .map(NewsApiResponse::getArticles)
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            // Логируем ошибки запросов
            System.err.printf("Error fetching news from %s: %s%n", apiUrl, e.getMessage());
            return Collections.emptyList();  // Возвращаем пустой список при ошибке
        }
    }
}