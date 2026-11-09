package fr.insa.chatsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactListTest {

    /** Faux listener : il note chaque notification reçue sous forme de texte. */
    private static class RecordingListener implements ContactListener {
        final List<String> events = new ArrayList<>();

        @Override
        public void onContactAdded(User user) {
            events.add("added:" + user.getUsername());
        }

        @Override
        public void onContactRemoved(User user) {
            events.add("removed:" + user.getUsername());
        }

        @Override
        public void onUsernameChanged(User user, String oldUsername) {
            events.add("changed:" + oldUsername + "->" + user.getUsername());
        }
    }

    private static InetAddress ip(String address) {
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final InetAddress ALICE_IP = ip("192.168.1.20");
    private static final InetAddress BOB_IP = ip("192.168.1.21");

    private ContactList contacts;
    private RecordingListener listener;

    @BeforeEach
    void setUp() {
        contacts = new ContactList();
        listener = new RecordingListener();
        contacts.addListener(listener);
    }

    // --- addOrUpdate ---

    @Test
    void newContactIsAddedAndNotified() {
        contacts.addOrUpdate(ALICE_IP, "Alice");

        assertEquals(1, contacts.getAll().size());
        assertEquals(List.of("added:Alice"), listener.events);
    }

    @Test
    void sameIpSameUsernameDoesNotNotify() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        contacts.addOrUpdate(ALICE_IP, "Alice");

        assertEquals(1, contacts.getAll().size());
        assertEquals(List.of("added:Alice"), listener.events);
    }

    @Test
    void usernameChangeNotifiesWithOldUsername() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        contacts.addOrUpdate(ALICE_IP, "Bob");

        assertEquals(1, contacts.getAll().size());
        assertEquals(List.of("added:Alice", "changed:Alice->Bob"), listener.events);
    }

    @Test
    void twoDifferentIpsGiveTwoContacts() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        contacts.addOrUpdate(BOB_IP, "Bob");

        assertEquals(2, contacts.getAll().size());
    }

    @Test
    void nullUsernameIsRejected() {
        assertThrows(NullPointerException.class, () -> contacts.addOrUpdate(ALICE_IP, null));
    }

    // --- remove ---

    @Test
    void knownContactIsRemovedAndNotified() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        contacts.remove(ALICE_IP);

        assertTrue(contacts.getAll().isEmpty());
        assertEquals(List.of("added:Alice", "removed:Alice"), listener.events);
    }

    @Test
    void removingUnknownContactDoesNotNotify() {
        contacts.remove(ALICE_IP);

        assertTrue(listener.events.isEmpty());
    }

    // --- isUsernameTaken ---

    @Test
    void usernameOfActiveContactIsTaken() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        assertTrue(contacts.isUsernameTaken("Alice"));
    }

    @Test
    void usernameIsTakenIgnoringCase() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        assertTrue(contacts.isUsernameTaken("aLiCe"));
    }

    @Test
    void unknownUsernameIsNotTaken() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        assertFalse(contacts.isUsernameTaken("Bob"));
    }

    @Test
    void oldUsernameIsFreedAfterChange() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        contacts.addOrUpdate(ALICE_IP, "Bob");
        assertFalse(contacts.isUsernameTaken("Alice"));
    }

    // --- getAll ---

    @Test
    void getAllReturnsACopy() {
        contacts.addOrUpdate(ALICE_IP, "Alice");
        contacts.getAll().clear();

        assertEquals(1, contacts.getAll().size());
    }
}