package com.edu.WebScrapeApplication.model;

import java.util.Date;

// Класс, представляющий одну новостную статью/запись
public class NewsItem {
    // Заголовок новости
    private String title;

    // Краткое описание/содержание новости
    private String description;

    // URL-адрес полной новости
    private String url;

    // Дата и время публикации новости
    private Date publishedAt;

    // Название источника/издателя новости
    private String sourceName;

    public NewsItem(String title, String description, String url, Date publishedAt, String sourceName) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
        this.sourceName = sourceName;
    }

    public NewsItem() {

    }

    // Геттер и сеттер для заголовка
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Геттер и сеттер для описания
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Геттер и сеттер для URL
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    // Геттер и сеттер для даты публикации
    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }

    // Геттер и сеттер для названия источника
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
}
