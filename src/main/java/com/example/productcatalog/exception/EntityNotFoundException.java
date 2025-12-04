package com.example.productcatalog.exception;

/**
 * Исключение, выбрасываемое, когда запрашиваемая сущность не найдена.
 *
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Создает новое исключение сущности не найдена с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение сущности не найдена с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   причина исключения
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
