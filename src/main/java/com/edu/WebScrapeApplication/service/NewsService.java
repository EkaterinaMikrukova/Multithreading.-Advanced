package com.edu.WebScrapeApplication.service;

import com.edu.WebScrapeApplication.model.NewsItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

// Интерфейс сервиса для работы с новостями
public interface NewsService {
    /**
     * Получает агрегированный список новостей из различных источников
     * @return список новостных статей
     * @throws ExecutionException если возникла ошибка при выполнении асинхронной задачи
     * @throws InterruptedException если выполнение было прервано
     */
    List<NewsItem> getAggregatedNews() throws ExecutionException, InterruptedException;
}
