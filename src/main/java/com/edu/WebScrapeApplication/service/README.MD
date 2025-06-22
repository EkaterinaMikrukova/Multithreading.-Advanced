```markdown
# News Aggregator Application - Сервисный слой

## Интерфейс новостного сервиса (`NewsService`)

Определяет контракт для работы с агрегированными новостями.

```java
public interface NewsService {
    List<NewsItem> getAggregatedNews() throws ExecutionException, InterruptedException;
}
```

### Характеристики:
- **Основной метод**:
  - `getAggregatedNews()` - возвращает объединенные новости из всех источников
- **Обрабатываемые исключения**:
  - `ExecutionException` - ошибки выполнения параллельных задач
  - `InterruptedException` - прерывание потока
- **Назначение**:
  - Абстракция для работы с новостными данными
  - Позволяет легко менять реализацию сервиса

## Реализация сервиса (`NewsServiceImpl`)

Основная реализация новостного сервиса с параллельной загрузкой данных.

```java
@Service
public class NewsServiceImpl implements NewsService {
    // Внедренные зависимости
    private final ExecutorService executorService;
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final int requestTimeoutSeconds;

    // Конструктор с dependency injection
    public NewsServiceImpl(ExecutorService executorService, 
                         RestTemplate restTemplate,
                         @Value("${news.api.key}") String apiKey,
                         @Value("${news.api.timeout.seconds:5}") int timeout) {...}
}
```

### Ключевые особенности:

1. **Параллельная загрузка**:
  - Использует `ExecutorService` для одновременной загрузки из нескольких источников
  - Фиксированный пул потоков (настраивается в конфигурации)

2. **Управление таймаутами**:
   ```java
   future.get(requestTimeoutSeconds, TimeUnit.SECONDS)
   ```
  - Прерывание зависших запросов через `future.cancel(true)`
  - Настраиваемый timeout (по умолчанию 5 секунд)

3. **Обработка данных**:
  - Фильтрация null-значений: `.filter(Objects::nonNull)`
  - Сортировка по дате: `.sorted(Comparator.comparing(NewsItem::getPublishedAt).reversed()`

4. **Работа с API**:
   ```java
   private List<NewsItem> fetchNewsFromSource(String apiUrl) {
       NewsApiResponse response = restTemplate.getForObject(apiUrl, NewsApiResponse.class);
       return Optional.ofNullable(response).map(NewsApiResponse::getArticles).orElse(emptyList());
   }
   ```
  - Безопасная обработка null-ответов через `Optional`
  - Логирование ошибок запросов

### Конфигурируемые параметры:
| Параметр | Описание | Значение по умолчанию |
|----------|----------|----------------------|
| `news.api.key` | Ключ для NewsAPI | - |
| `news.api.timeout.seconds` | Таймаут запросов | 5 |

### Бизнес-логика:
1. Получает список источников из `getNewsSources()`
2. Для каждого источника создает асинхронную задачу
3. Объединяет и сортирует результаты
4. Возвращает обработанный список новостей

### Пример использования:
```java
@Autowired
private NewsService newsService;

public void showNews() {
    List<NewsItem> news = newsService.getAggregatedNews();
    news.forEach(item -> System.out.println(item.getTitle()));
}
```

### Рекомендации по расширению:
1. Добавить кэширование результатов
2. Реализовать пагинацию
3. Добавить фильтрацию по источникам/категориям
4. Ввести механизм повторных попыток для неудачных запросов
5. Добавить метрики производительности

Сервис обеспечивает:
- Высокую производительность за счет параллелизма
- Отказоустойчивость при проблемах с отдельными источниками
- Гибкость конфигурации
- Простоту интеграции с другими компонентами
```