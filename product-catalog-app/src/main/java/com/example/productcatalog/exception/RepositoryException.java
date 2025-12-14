package com.example.productcatalog.exception;

/**
 * Исключение, выбрасываемое, когда операция репозитория не удается.
 *
 */
public class RepositoryException extends RuntimeException {

    /**
     * Создает новое исключение репозитория с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение репозитория с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   причина исключения
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
