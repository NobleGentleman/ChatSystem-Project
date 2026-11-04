package fr.insa.chatsystem;

import java.net.InetAddress;
import java.util.Objects;

public class User {
    private String username;
    private final InetAddress ipAddress;

    /**
     * @param ipAddress the user's IP address, must not be null
     * @throws NullPointerException if ipAddress is null
     */
    public User(InetAddress ipAddress) {
        this.ipAddress = Objects.requireNonNull(ipAddress, "the user's IP address, must not be null");
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
