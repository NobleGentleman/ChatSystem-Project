package fr.insa.chatsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private InetAddress ipAddress;
    private User user;

    @BeforeEach
    void setUp() throws UnknownHostException {
        ipAddress = InetAddress.getByName("192.168.1.10");
        user = new User(ipAddress);
    }

    @Test
    void getIpAddress_returnsConstructorIpAddress() {
        assertEquals(ipAddress, user.getIpAddress());
    }

    @Test
    void getUsername_isNullByDefault() {
        assertNull(user.getUsername());
    }

    @Test
    void setUsername_updatesUsername() {
        user.setUsername("Alice");
        assertEquals("Alice", user.getUsername());
    }

    @Test
    void setUsername_canBeChanged() {
        user.setUsername("Alice");
        user.setUsername("Bob");
        assertEquals("Bob", user.getUsername());
    }

    @Test
    void setUsername_acceptsNull() {
        user.setUsername("Alice");
        user.setUsername(null);
        assertNull(user.getUsername());
    }
}
