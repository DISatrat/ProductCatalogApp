package util;

import cache.QueryCache;
import lombok.Getter;
import repository.audit.AuditRepository;
import repository.audit.AuditRepositoryImpl;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import repository.user.UserRepository;
import repository.user.UserRepositoryImpl;
import service.audit.AuditService;
import service.audit.AuditServiceImpl;
import service.metrics.MetricsService;
import service.metrics.MetricsServiceImpl;
import service.product.ProductService;
import service.product.ProductServiceImpl;
import service.user.UserService;
import service.user.UserServiceImpl;

public class ServiceLocator {
    private static final UserService userService;
    private static final AuditService auditService;
    private static final ProductService productService;
    private static final MetricsService metricsService;


    static {
        UserRepository userRepository = new UserRepositoryImpl();
        AuditRepository auditRepository = new AuditRepositoryImpl();
        ProductRepository productRepository = new ProductRepositoryImpl();
        QueryCache queryCache = new QueryCache(100);

        userService = new UserServiceImpl(userRepository);
        auditService = new AuditServiceImpl(auditRepository);
        productService = new ProductServiceImpl(productRepository, queryCache);
        metricsService = new MetricsServiceImpl();
    }

    public static UserService getUserService() {
        return userService;
    }

    public static AuditService getAuditService() {
        return auditService;
    }

    public static ProductService getProductService() {
        return productService;
    }
    public static MetricsService getMetricsService() {
        return metricsService;
    }
}