package fr.insa.chatsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.insa.chatsystem.DiscoveryMessage.Type;
import fr.insa.chatsystem.udp.MessageSender;

class DiscoveryServiceTest {

    /** Faux sender : n'envoie rien sur le réseau, note juste les appels. */
    private static class FakeSender implements MessageSender {
        final List<DiscoveryMessage> broadcasts = new ArrayList<>();
        final List<DiscoveryMessage> sent = new ArrayList<>();
        final List<InetAddress> destinations = new ArrayList<>();

        @Override
        public void send(DiscoveryMessage message, InetAddress destination) {
            sent.add(message);
            destinations.add(destination);
        }

        @Override
        public void broadcast(DiscoveryMessage message) {
            broadcasts.add(message);
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

    private ContactList contacts;
    private FakeSender sender;
    private DiscoveryService service;

    @BeforeEach
    void setUp() {
        contacts = new ContactList();
        sender = new FakeSender();
        service = new DiscoveryService(contacts, sender, 0); // pas d'attente dans les tests
    }

    // --- DISCOVERY_REQUEST ---

    @Test
    void requestIsIgnoredWhileIHaveNoUsername() {
        service.handle(new DiscoveryMessage(Type.DISCOVERY_REQUEST, ""), ALICE_IP);

        assertTrue(sender.sent.isEmpty());
    }

    @Test
    void requestIsAnsweredToSenderOnlyOnceIHaveAUsername() throws Exception {
        service.announce("Mathis");

        service.handle(new DiscoveryMessage(Type.DISCOVERY_REQUEST, ""), ALICE_IP);

        assertEquals(1, sender.sent.size());
        assertEquals(Type.DISCOVERY_RESPONSE, sender.sent.get(0).getType());
        assertEquals("Mathis", sender.sent.get(0).getUsername());
        assertEquals(ALICE_IP, sender.destinations.get(0));
    }

    // --- DISCOVERY_RESPONSE / ANNOUNCE ---

    @Test
    void responseAddsContact() {
        service.handle(new DiscoveryMessage(Type.DISCOVERY_RESPONSE, "Alice"), ALICE_IP);

        assertTrue(contacts.isUsernameTaken("Alice"));
    }

    @Test
    void announceFromKnownIpChangesUsername() {
        service.handle(new DiscoveryMessage(Type.ANNOUNCE, "Alice"), ALICE_IP);
        service.handle(new DiscoveryMessage(Type.ANNOUNCE, "Alicia"), ALICE_IP);

        assertEquals(1, contacts.getAll().size());
        assertTrue(contacts.isUsernameTaken("Alicia"));
        assertFalse(contacts.isUsernameTaken("Alice"));
    }

    @Test
    void invalidUsernameIsIgnored() {
        service.handle(new DiscoveryMessage(Type.ANNOUNCE, ""), ALICE_IP);

        assertTrue(contacts.getAll().isEmpty());
    }

    // --- USER_LEFT ---

    @Test
    void userLeftRemovesContact() {
        service.handle(new DiscoveryMessage(Type.ANNOUNCE, "Alice"), ALICE_IP);
        service.handle(new DiscoveryMessage(Type.USER_LEFT, ""), ALICE_IP);

        assertTrue(contacts.getAll().isEmpty());
    }

    // --- Actions locales ---

    @Test
    void discoverBroadcastsRequest() throws Exception {
        service.discover();

        assertEquals(1, sender.broadcasts.size());
        assertEquals(Type.DISCOVERY_REQUEST, sender.broadcasts.get(0).getType());
    }

    @Test
    void announceBroadcastsAndStoresMyUsername() throws Exception {
        service.announce("Mathis");

        assertEquals("Mathis", service.getMyUsername());
        assertEquals(Type.ANNOUNCE, sender.broadcasts.get(0).getType());
        assertEquals("Mathis", sender.broadcasts.get(0).getUsername());
    }

    @Test
    void usernameAvailabilityFollowsContactList() {
        assertTrue(service.isUsernameAvailable("Alice"));

        service.handle(new DiscoveryMessage(Type.ANNOUNCE, "Alice"), ALICE_IP);

        assertFalse(service.isUsernameAvailable("Alice"));
    }

    @Test
    void disconnectBroadcastsUserLeftAndForgetsMyUsername() throws Exception {
        service.announce("Mathis");
        service.disconnect();

        assertEquals(Type.USER_LEFT, sender.broadcasts.get(1).getType());
        assertNull(service.getMyUsername());
    }

    @Test
    void noAnswerToRequestsAfterDisconnect() throws Exception {
        service.announce("Mathis");
        service.disconnect();

        service.handle(new DiscoveryMessage(Type.DISCOVERY_REQUEST, ""), ALICE_IP);

        assertTrue(sender.sent.isEmpty());
    }

    @Test
    void disconnectWithoutUsernameSendsNothing() throws Exception {
        service.disconnect();

        assertTrue(sender.broadcasts.isEmpty());
    }
}