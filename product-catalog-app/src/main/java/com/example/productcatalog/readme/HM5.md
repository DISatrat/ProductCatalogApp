# ДЗ 5

## Основные изменения

### 1. Multi-Module Maven архитектура

Проект переструктурирован из монолитной структуры в многомодульный Maven проект:

```
ProductCatalogApp/ (parent POM)
├── audit-spring-boot-starter       # Модуль 1: Аудит
├── logging-spring-boot-starter     # Модуль 2: Логирование
└── product-catalog-app             # Модуль 3: Основное приложение
```

### 2. Spring Boot Starters

#### audit-spring-boot-starter

Автоматический стартер для логирования операций:
- `@Audited` - аннотация для маркирования методов
- `AuditAspect` - AOP аспект перехватывает методы
- `AuditService` - интерфейс для записи операций в БД
- `AuditAutoConfiguration` - автоматическая регистрация компонентов

**Подключение**: Автоматическое (через файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`)

#### logging-spring-boot-starter

Стартер логирования производительности методов:
- `@EnablePerformanceLogging` - аннотация для явного включения
- `PerformanceLoggingAspect` - замер времени выполнения всех методов
- `PerformanceLoggingConfiguration` - конфигурация AOP

**Подключение**: Явное через `@EnablePerformanceLogging` на главном классе приложения

### 3. Обновление основного приложения

**product-catalog-app**:
- Удалены классы `AuditAspect` и `PerformanceLoggingAspect` (вынесены в стартеры)
- Добавлена аннотация `@EnablePerformanceLogging` в `ProductCatalogApp.java`
- `AuditServiceImpl` реализует интерфейс из `audit-spring-boot-starter`
- Все остальные компоненты остались без изменений

### 4. Spring Boot 3.2.1

Обновлена версия Spring Boot до 3.2.1 с использованием:
- Spring Web MVC
- Spring JDBC
- Spring AOP / AspectJ
- Spring Boot AutoConfiguration

### 5. SpringDoc OpenAPI

**springdoc-openapi-starter-webmvc-ui** версии 2.3.0:
- Автоматическая генерация OpenAPI 3.0 спецификации
- Swagger UI интерфейс: http://localhost:8080/api/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

### 6. Обновленные тесты

Все тесты остались без изменений:
- JUnit 5 (Jupiter)
- Mockito 5.x
- MockMvc для WebMVC тестирования
- TestContainers для БД

## Технологический стек


### API Documentation
- **SpringDoc OpenAPI 2.3.0** - OpenAPI 3.0 спецификация
- **Swagger UI** - интерактивная документация

### Testing
- **JUnit 5** - unit тесты
- **Mockito** - мокирование
- **TestContainers** - контейнеризированные БД для тестов

## Запуск приложения

### Требования
- Java 17+
- Maven 3.8+
- PostgreSQL 12+

### Сборка
```bash
mvn clean install
```

### Запуск
```bash
cd product-catalog-app
docker-compose up -d
mvn spring-boot:run
```

## Использование стартеров

### Использование @Audited

```java
@Service
public class UserService {
    @Audited(action = "USER_LOGIN", details = "Вход пользователя")
    public void login(String username) {
        // Операция автоматически будет залогирована
    }
}
```

### Использование @EnablePerformanceLogging

```java
@SpringBootApplication
@EnablePerformanceLogging  // Включить логирование производительности
public class ProductCatalogApp {
    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApp.class, args);
    }
}
```
