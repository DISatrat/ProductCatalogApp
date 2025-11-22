package exception;

/**
 * Исключение для операций с аудитом
 */
public class AuditRepositoryException extends RepositoryException {
    public AuditRepositoryException(String message) {
        super(message);
    }

    public AuditRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}