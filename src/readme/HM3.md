# ДЗ 3

## Реализованный функционал

### Архитектура и API
- JSON для всех входящих/исходящих данных
- Понятные эндпоинты с REST-конвенциями именования

### Сервлеты и эндпоинты

#### AuthServlet (/api/auth)
- POST /api/auth/login - аутентификация пользователя
- POST /api/auth/register - регистрация нового пользователя
- POST /api/auth/logout - выход из системы

#### ProductServlet (/api/products)
- GET /api/products - получение всех товаров
- GET /api/products/{id} - получение товара по ID
- GET /api/products/search - поиск товаров
- GET /api/products/count - количество товаров
- POST /api/products - создание нового товара
- PUT /api/products/{id} - обновление товара

#### UserServlet (/api/users)
- GET /api/users - получение списка пользователей (требует аутентификации)

#### AuditServlet (/api/audit-logs)
- GET /api/audit-logs - получение журнала аудита

#### MetricsServlet (/api/metrics)
- GET /api/metrics - все метрики
- GET /api/metrics/search-count - количество поисковых запросов
- GET /api/metrics/average-time - среднее время поиска

### Техническая реализация

#### DTO и маппинг
- Использование DTO для передачи данных
- MapStruct для маппинга между сущностями и DTO
- Jackson для сериализации/десериализации JSON

#### Валидация
- Валидация входящих DTO
- Проверка обязательных полей
- Валидация бизнес-правил

#### Аспекты
- Реализована основа для аспектного программирования
- Созданы аспекты для аудита и логирования производительности
- Настроена конфигурация AspectJ (aop.xml)
- но не получилось написать правильный pom чтобы в рантайме логировало

### Тестирование
- Покрытие сервлетов unit-тестами
- Тестирование различных сценариев
- Проверка статус-кодов и ответов

## Запуск приложения

### Требования
- Java 11+
- Apache Tomcat 9+
- PostgreSQL

### Конфигурация
1. Настройка базы данных в config/dev.yaml
2. Инициализация миграций Liquibase
3. Запуск embedded Tomcat сервера

### Сборка и запуск
```bash
mvn clean compile
java -jar target/product-catalog-app.jar