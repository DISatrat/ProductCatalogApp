package com.example.productcatalog.exception;

import com.example.productcatalog.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * <p>
 * Предоставляет централизованную обработку исключений во всех контроллерах,
 * преобразуя исключения в соответствующие HTTP ответы.
 * </p>
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает EntityNotFoundException.
     *
     * @param ex исключение
     * @return ответ 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Сущность не найдена: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Обрабатывает IllegalArgumentException.
     *
     * @param ex исключение
     * @return ответ 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Неверный аргумент: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Обрабатывает исключения валидации из аннотаций @Valid.
     *
     * @param ex исключение
     * @return ответ 400 Bad Request с ошибками валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Неверное значение",
                        (first, second) -> first
                ));

        log.warn("Ошибка валидации: {}", errors);

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .error("Ошибка валидации")
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Обрабатывает SecurityException для ошибок аутентификации/авторизации.
     *
     * @param ex исключение
     * @return ответ 401 Unauthorized
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleSecurityException(SecurityException ex) {
        log.warn("Ошибка безопасности: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Обрабатывает RepositoryException для ошибок базы данных.
     *
     * @param ex исключение
     * @return ответ 500 Internal Server Error
     */
    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<ApiResponse<Void>> handleRepositoryException(RepositoryException ex) {
        log.error("Ошибка репозитория: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Произошла ошибка базы данных"));
    }

    /**
     * Обрабатывает все остальные необработанные исключения.
     *
     * @param ex исключение
     * @return ответ 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Непредвиденная ошибка: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Произошла непредвиденная ошибка"));
    }
}
