package util;

import cache.QueryCache;
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

import java.util.logging.Logger;

public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());

    private static final int DEFAULT_CACHE_SIZE = 100;

    private static volatile ApplicationContext instance;

    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final ProductRepository productRepository;
    private final QueryCache queryCache;

    private final UserService userService;
    private final AuditService auditService;
    private final ProductService productService;
    private final MetricsService metricsService;

    private ApplicationContext() {
        logger.info("Initializing ApplicationContext...");

        this.userRepository = new UserRepositoryImpl();
        this.auditRepository = new AuditRepositoryImpl();
        this.productRepository = new ProductRepositoryImpl();
        this.queryCache = new QueryCache(DEFAULT_CACHE_SIZE);

        this.userService = new UserServiceImpl(userRepository);
        this.auditService = new AuditServiceImpl(auditRepository);
        this.productService = new ProductServiceImpl(productRepository, queryCache);
        this.metricsService = new MetricsServiceImpl();

        logger.info("ApplicationContext initialized successfully");
    }

    public static void initialize() {
        if (instance == null) {
            synchronized (ApplicationContext.class) {
                if (instance == null) {
                    instance = new ApplicationContext();
                }
            }
        }
    }

    private static ApplicationContext getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }

    public static UserService getUserService() {
        return getInstance().userService;
    }

    public static AuditService getAuditService() {
        return getInstance().auditService;
    }

    public static ProductService getProductService() {
        return getInstance().productService;
    }

    public static MetricsService getMetricsService() {
        return getInstance().metricsService;
    }

    public static UserRepository getUserRepository() {
        return getInstance().userRepository;
    }

    public static AuditRepository getAuditRepository() {
        return getInstance().auditRepository;
    }

    public static ProductRepository getProductRepository() {
        return getInstance().productRepository;
    }

    public static void shutdown() {
        logger.info("Shutting down ApplicationContext...");
        instance = null;
    }
}
