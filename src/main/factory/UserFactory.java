package main.factory;

import main.model.User;
import main.repository.UserRepository;
import main.util.PersistenceUtil;

import java.util.List;

/**
 * Фабрика для создания и управления репозиториями пользователей.
 * Обеспечивает загрузку пользователей из постоянного хранилища и сохранение обратно.
 * Создает пользователя-администратора по умолчанию если данных нет.
 */
public class UserFactory {

    /**
     * Создает и инициализирует репозиторий пользователей.
     * Загружает существующих пользователей из файла "users.dat" если он существует.
     * Если файл не существует или пуст, создается пользователь-администратор по умолчанию
     * с логином "admin" и паролем "1".
     *
     * @return инициализированный репозиторий пользователей
     */
    public static UserRepository createUserRepository() {
        UserRepository repo = new UserRepository();

        List<User> users = PersistenceUtil.loadObject("users.dat", List.class);
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                repo.addUser(user);
            }
        } else {
            User admin = new User("admin", Integer.toString("1".hashCode()));
            admin.makeUserAdmin();
            repo.addUser(admin);
        }

        return repo;
    }

    /**
     * Сохраняет всех пользователей из репозитория в файл "users.dat".
     * Используется для сохранения состояния данных между запусками приложения.
     *
     * @param userRepo репозиторий пользователей для сохранения
     */
    public static void saveData(UserRepository userRepo) {
        PersistenceUtil.saveObject(userRepo.findAll(), "users.dat");
    }
}