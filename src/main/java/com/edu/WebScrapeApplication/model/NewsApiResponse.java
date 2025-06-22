package com.edu.WebScrapeApplication.model;

import java.util.List;

// Класс, представляющий ответ от News API
public class NewsApiResponse {
    // Поле статуса запроса (например, "ok" или "error")
    private String status;

    // Поле общего количества результатов
    private int totalResults;

    // Список новостных статей (основные данные)
    private List<NewsItem> articles;


    // Геттер для статуса
    public String getStatus() {
        return status;
    }

    // Сеттер для статуса
    public void setStatus(String status) {
        this.status = status;
    }

    // Геттер для общего количества результатов
    public int getTotalResults() {
        return totalResults;
    }

    // Сеттер для общего количества результатов
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    // Геттер для списка статей
    public List<NewsItem> getArticles() {
        return articles;
    }

    // Сеттер для списка статей
    public void setArticles(List<NewsItem> articles) {
        this.articles = articles;
    }
}
