package Repository;

import model.User;

import java.io.Serializable;
import java.util.*;

public class UserRepository implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, User> users = new HashMap<>();

    public synchronized Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public synchronized boolean addUser(User u) {
        if (users.containsKey(u.getUsername())) return false;
        users.put(u.getUsername(), u);
        return true;
    }

    public synchronized List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
