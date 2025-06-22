package com.edu.WebScrapeApplication.controllers;

import com.edu.WebScrapeApplication.model.NewsItem;
import com.edu.WebScrapeApplication.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

// Определение REST-контроллера для работы с новостями
@RestController  // Помечает класс как контроллер, где методы возвращают данные напрямую (не представления)
@RequestMapping("/api/news")  // Базовый URL для всех эндпоинтов этого контроллера
public class NewsController {

    // Внедрение зависимости сервиса для работы с новостями
    private final NewsService newsService;

    // Конструктор с внедрением зависимости (Dependency Injection)
    public NewsController(NewsService newsService) {
        this.newsService = newsService;  // Присваивание внедренного сервиса полю класса
    }

    // Обработчик GET-запросов по пути "/api/news"
    @GetMapping  // Эквивалентно @RequestMapping(method = RequestMethod.GET)
    public List<NewsItem> getAggregatedNews() throws ExecutionException, InterruptedException {
        // Вызов сервиса для получения агрегированных новостей
        return newsService.getAggregatedNews();
        // Метод может выбрасывать исключения:
        // - ExecutionException: если возникла ошибка при выполнении асинхронной задачи
        // - InterruptedException: если выполнение было прервано
    }
}
