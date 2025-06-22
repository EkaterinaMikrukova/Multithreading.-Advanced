```markdown
# News Aggregator Application

## Конфигурация асинхронных задач (`AsyncConfig`)

Класс `AsyncConfig` содержит конфигурацию для асинхронной обработки новостных запросов.

### Основные компоненты:

1. **`RestTemplate`**:
   - Бин для выполнения HTTP-запросов к новостным API
   - Используется сервисом для получения данных от внешних источников
   - Настраивается как Spring-компонент для централизованного управления

2. **`ExecutorService`**:
   - Пул потоков фиксированного размера (5 потоков)
   - Обеспечивает параллельную загрузку новостей из разных источников
   - Позволяет ограничить количество одновременных запросов

### Особенности реализации:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public ExecutorService newsExecutorService() {
        return Executors.newFixedThreadPool(5); // Пул из 5 рабочих потоков
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // HTTP-клиент для API запросов
    }
}
```

### Рекомендации по настройке:

1. **Размер пула потоков**:
    - Текущее значение (5) оптимально для бесплатных API с лимитами
    - Для production можно увеличить (но учитывайте лимиты NewsAPI)

2. **Таймауты соединения**:
    - Рекомендуется добавить в `RestTemplate`:
   ```java
   @Bean
   public RestTemplate restTemplate() {
       return new RestTemplateBuilder()
           .setConnectTimeout(Duration.ofSeconds(5))
           .setReadTimeout(Duration.ofSeconds(10))
           .build();
   }
   ```

3. **Метрики**:
    - Для мониторинга можно добавить сбор статистики выполнения задач

### Использование в сервисе:

```java
@Service
public class NewsServiceImpl implements NewsService {
    private final ExecutorService executorService;
    
    public NewsServiceImpl(ExecutorService executorService) {
        this.executorService = executorService; // Инъекция пула потоков
    }
}
```

Эта конфигурация обеспечивает стабильную работу агрегатора новостей с защитой от перегрузки API источников.
```