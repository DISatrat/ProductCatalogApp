package model;

public class User {
    private static final long serialVersionUID = 1L;
    private String username;
    private String passwordHash; // для примера — хранится plain или простая хеш-функция

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}
