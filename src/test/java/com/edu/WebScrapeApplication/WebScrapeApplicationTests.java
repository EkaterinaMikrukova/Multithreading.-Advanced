package com.edu.WebScrapeApplication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WebScrapeApplicationTests {

	@Test
	void contextLoads(ApplicationContext context) {
		// Проверяем, что контекст Spring успешно загрузился
		assertNotNull(context);
	}
}
