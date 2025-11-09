import Repository.ProductRepository;
import Repository.UserRepository;
import UI.ConsoleUI;
import cache.QueryCache;
import model.User;
import service.AuditService;
import service.CatalogService;
import util.PersistenceUtil;

public class ProductCatalogApp {
    public static void main(String[] args) {
        ProductRepository productRepo = PersistenceUtil.loadObject("products.dat", ProductRepository.class);
        if (productRepo == null) {
            productRepo = new ProductRepository();
        } else {
            productRepo.restoreIdGenerator();
        }

        UserRepository userRepo = PersistenceUtil.loadObject("users.dat", UserRepository.class);
        if (userRepo == null) {
            userRepo = new UserRepository();
            userRepo.addUser(new User("admin", Integer.toString("admin".hashCode())));
        }

        final ProductRepository finalProductRepo = productRepo;
        final UserRepository finalUserRepo = userRepo;


        AuditService audit = new AuditService();
        QueryCache cache = new QueryCache(100);
        CatalogService service = new CatalogService(productRepo, userRepo, cache, audit);
        ConsoleUI ui = new ConsoleUI(service, productRepo, userRepo, audit);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving data...");
            PersistenceUtil.saveObject(finalProductRepo, "products.dat");
            PersistenceUtil.saveObject(finalUserRepo, "users.dat");
        }));

        ui.start();
    }
}