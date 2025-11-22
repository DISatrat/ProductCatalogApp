package exception;

/**
 * Исключение для операций с товарами
 */
public class ProductRepositoryException extends RepositoryException {
    public ProductRepositoryException(String message) {
        super(message);
    }

    public ProductRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}