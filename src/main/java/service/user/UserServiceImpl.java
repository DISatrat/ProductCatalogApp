package service.user;

import model.User;
import repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса пользователей с использованием репозитория для хранения данных.
 * Использует простую хеш-функцию для паролей (только для демонстрационных целей).
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void addUser(User user) {
        userRepository.addUser(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public String hashPassword(String pass) {
        return Integer.toString(pass.hashCode());
    }

    @Override
    public boolean checkPassword(String pass, String hash) {
        return hashPassword(pass).equals(hash);
    }
}
