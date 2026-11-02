package fr.insa.chatsystem;

public class User {
    private String username;
    private final String ipAddress;

    public User(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
