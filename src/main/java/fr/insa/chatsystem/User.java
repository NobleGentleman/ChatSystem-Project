package fr.insa.chatsystem;

import java.net.InetAddress;

public class User {
    private String username;
    private final InetAddress ipAddress;

    public User(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
