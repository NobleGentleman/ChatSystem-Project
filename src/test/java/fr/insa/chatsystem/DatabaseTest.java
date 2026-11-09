package fr.insa.chatsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    private Database database;
    private InetAddress ip;

    @BeforeEach
    void setUp() throws Exception {
        this.database = new Database("jdbc:sqlite::memory:");
        this.ip = InetAddress.getByName("192.168.1.5");
    }

    @Test
    void loadLocalUserReturnsEmptyOnFirstLaunch() {
        Optional<User> loaded = database.loadLocalUser();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void saveThenLoadLocalUser() {
        User user = new User(ip);
        user.setUsername("Bob");
        database.saveLocalUser(user);

        Optional<User> loaded = database.loadLocalUser();
        assertTrue(loaded.isPresent());
        assertEquals("Bob", loaded.get().getUsername());
        assertEquals(ip, loaded.get().getIpAddress());
    }

    @Test
    void savingTwiceUpdatesTheUsername() {
        User user = new User(ip);
        user.setUsername("Bob");
        database.saveLocalUser(user);

        user.setUsername("Bobby");
        database.saveLocalUser(user);

        Optional<User> loaded = database.loadLocalUser();
        assertTrue(loaded.isPresent());
        assertEquals("Bobby", loaded.get().getUsername());
    }

    @Test
    void savingTwiceUpdatesTheIpAddress() throws UnknownHostException {
        InetAddress newIp = InetAddress.getByName("192.168.1.9");

        User user = new User(ip);
        user.setUsername("Bob");
        database.saveLocalUser(user);

        User moved = new User(newIp);
        moved.setUsername("Bob");
        database.saveLocalUser(moved);

        Optional<User> loaded = database.loadLocalUser();
        assertTrue(loaded.isPresent());
        assertEquals("Bob", loaded.get().getUsername());
        assertEquals(newIp, loaded.get().getIpAddress());
    }
}
