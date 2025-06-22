package com.edu.WebScrapeApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения, который запускает весь проект.
 * Аннотация @SpringBootApplication объединяет три основные аннотации:
 * 1. @Configuration - помечает класс как источник конфигурации бинов
 * 2. @EnableAutoConfiguration - включает автоматическую настройку Spring Boot
 * 3. @ComponentScan - включает сканирование компонентов в текущем пакете и подпакетах
 */
@SpringBootApplication
public class WebScrapeApplication {

	/**
	 * Основной метод, который запускает Spring Boot приложение.
	 *
	 * @param args аргументы командной строки, которые могут быть переданы приложению
	 */
	public static void main(String[] args) {
		// Запуск Spring приложения:
		// 1. Указываем основной класс конфигурации (WebScrapeApplication)
		// 2. Передаем аргументы командной строки
		SpringApplication.run(WebScrapeApplication.class, args);
	}
}