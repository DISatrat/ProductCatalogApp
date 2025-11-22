package exception;

/**
 * Исключение для операций с пользователями
 */
public class UserRepositoryException extends RepositoryException {
    public UserRepositoryException(String message) {
        super(message);
    }

    public UserRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}