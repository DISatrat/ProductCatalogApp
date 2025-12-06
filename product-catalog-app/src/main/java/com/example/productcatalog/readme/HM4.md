# ДЗ 4

### 1. Java-конфигурация приложения (application.yml)

Все кастомные конфигурационные файлы заменены на единый **application.yml**:
-  Централизованная конфигурация всех параметров приложения
- Поддержка профилей (dev, prod, test)
- Конфигурация базы данных (PostgreSQL)
- Конфигурация логирования
- Конфигурация Swagger/OpenAPI

### 2. REST-контроллеры (Spring MVC)

Удалены все сервлеты из ДЗ 3. Реализованы 5 REST-контроллеров:

#### AuthController (/auth)
- POST /auth/register - регистрация нового пользователя
- POST /auth/login - аутентификация пользователя
- POST /auth/logout - выход из системы

#### ProductController (/products)
- GET /products - получение всех товаров
- GET /products/{id} - получение товара по ID
- GET /products/count - количество товаров
- POST /products - создание нового товара
- PUT /products/{id} - обновление товара
- DELETE /products/{id} - удаление товара
- POST /products/search - поиск товаров по критериям

#### UserController (/users)
- GET /users - получение списка пользователей (требует роли ADMIN)

#### AuditController (/audit-logs)
- GET /audit-logs - получение журнала аудита (требует роли ADMIN)

#### MetricsController (/metrics)
- GET /metrics - все метрики приложения
- GET /metrics/search-count - количество поисковых запросов
- GET /metrics/average-time - среднее время поиска

### 3. Swagger + Swagger UI

Интегрирована библиотека **springdoc-openapi** для документирования REST API:
- OpenAPI 3.0 спецификация
- Swagger UI интерфейс (http://localhost:8080/swagger-ui.html)
- Все контроллеры задокументированы аннотациями:
    - @Tag - описание контроллера
    - @Operation - описание операции
    - @ApiResponse - описание возможных ответов
    - @Parameter - описание параметров
    - @Schema - описание структуры данных

### 4. Spring AOP

Аспекты полностью переписаны на **Spring AOP** (@Aspect):
- **AuditAspect** - аудит операций методов, помеченных @Audited
- **PerformanceLoggingAspect** - логирование времени выполнения методов
- Использование @Around advice для замера времени выполнения
- Pointcut-ы для перехвата методов сервисов и контроллеров
- Интеграция с AuditService для логирования операций

### 5. Внедрение зависимостей через конструктор

Все компоненты приложения используют **конструкторное внедрение зависимостей**:
- Удалены все @Autowired на полях
- Использована аннотация @RequiredArgsConstructor от Lombok
- Spring полностью управляет созданием и конфигурацией бинов
- Нет ручного создания объектов сервисов, репозиториев и т.д.


### 6. Тесты на контроллеры (WebMVC)

Реализованы компрехензивные **WebMVC тесты** для всех REST контроллеров:

Использованы:
- JUnit 5 (Jupiter)
- Mockito для мокирования сервисов
- Spring Test

### Компоненты

- **Controllers** - REST эндпоинты, обработка HTTP запросов
- **Services** - бизнес-логика приложения
- **Repositories** - работа с базой данных через JDBC
- **DTOs** - объекты для передачи данных
- **Mappers** - преобразование между сущностями и DTO (MapStruct)
- **Aspects** - кросс-слойная функциональность (аудит, логирование)
- **Exceptions** - кастомные исключения и GlobalExceptionHandler
- **Models** - сущности данных
- **Config** - конфигурация приложения (OpenAPI/Swagger)

## Используемые технологии

### Spring Framework
- **Spring MVC** - REST контроллеры
- **Spring AOP** - аспектное программирование
- **Spring Data JDBC** - работа с БД

### API документирование
- **springdoc-openapi** (Swagger UI)
- **OpenAPI 3.0** спецификация

### Маппирование данных
- **MapStruct 1.5+** - DTO ↔ сущности
- **Lombok** - @RequiredArgsConstructor, @Getter, @Setter

### Валидация
- **Jakarta Bean Validation**
- **Hibernate Validator**

### Тестирование
- **JUnit 5** (Jupiter)
- **Mockito 5.x**
- **MockMvc**

### База данных
- **PostgreSQL**
- **JDBC Template**
- **Liquibase** - миграции БД

### Логирование
- **SLF4J** с **Logback**

## Запуск приложения

### Требования
- Java 17+
- Maven 3.8+
- PostgreSQL 12+

### Конфигурация
1. Создать базу данных PostgreSQL
2. Настроить подключение в `application.yml`
3. Миграции автоматически запустятся при старте

### Сборка
```bash
# Сборка
mvn clean install
```

### Доступ к приложению
- REST API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI docs: http://localhost:8080/v3/api-docs

## Примеры использования API

### Регистрация пользователя
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123"}'
```

### Вход в систему
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123"}'
```

### Создание товара
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "name":"iPhone 15 Pro",
    "category":"Электроника",
    "brand":"Apple",
    "price":999.99
  }'
```

### Получение всех товаров
```bash
curl http://localhost:8080/products
```

### Поиск товаров
```bash
curl -X POST http://localhost:8080/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "nameSubstr":"iPhone",
    "priceMin":500,
    "priceMax":1500
  }'
```

### Запуск тестов
```bash
mvn test
```