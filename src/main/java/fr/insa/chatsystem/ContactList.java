package fr.insa.chatsystem;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ContactList {

    private final Map<InetAddress, User> contacts = new ConcurrentHashMap<>();
    private final List<ContactListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(ContactListener listener) {
        listeners.add(listener);
    }

    public Collection<User> getAll() {
        return new ArrayList<>(contacts.values());
    }

    public void addOrUpdate(InetAddress ip, String username) {
        Objects.requireNonNull(username, "username must not be null");
        
        User user = new User(ip);
        user.setUsername(username);
        User previous = contacts.put(ip, user);

        if (previous == null) {
            for (ContactListener contactListener : listeners) {
                contactListener.onContactAdded(user);
            }
        } else if (!previous.getUsername().equals(username)) {
            for (ContactListener contactListener : listeners) {
                contactListener.onUsernameChanged(user, previous.getUsername());
            }
        }
    }

    public void remove(InetAddress ip) {
        User removed = contacts.remove(ip);

        if (removed != null) {
            for (ContactListener contactListener : listeners) {
                contactListener.onContactRemoved(removed);
            }
        }
    }

    public boolean isUsernameTaken(String username) {
        for (User user : contacts.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
}