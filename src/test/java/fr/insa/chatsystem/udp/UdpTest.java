package fr.insa.chatsystem.udp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.insa.chatsystem.DiscoveryMessage;

class UdpTest {

    private static final int TEST_PORT = 45678;
    private static final InetAddress LOCALHOST = InetAddress.getLoopbackAddress();

    /** Ce que le faux handler a reçu : le message et l'adresse de l'expéditeur. */
    private static class Received {
        final DiscoveryMessage message;
        final InetAddress sender;

        Received(DiscoveryMessage message, InetAddress sender) {
            this.message = message;
            this.sender = sender;
        }
    }

    private final BlockingQueue<Received> received = new LinkedBlockingQueue<>();
    private UdpListener listener;
    private Thread listenerThread;
    private UdpSender sender;

    private void startListener(Set<InetAddress> localAddresses) throws Exception {
        listener = new UdpListener(TEST_PORT,
                (message, from) -> received.add(new Received(message, from)),
                localAddresses);
        listenerThread = new Thread(listener, "test-udp-listener");
        listenerThread.start();
        sender = new UdpSender(TEST_PORT);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (sender != null) sender.close();
        if (listener != null) listener.stop();
        if (listenerThread != null) listenerThread.join(1000);
    }

    @Test
    void sentMessageIsReceivedWithSenderAddress() throws Exception {
        startListener(Set.of());

        sender.send(new DiscoveryMessage(DiscoveryMessage.Type.ANNOUNCE, "Mathéo"), LOCALHOST);

        Received r = received.poll(2, TimeUnit.SECONDS);
        assertNotNull(r, "no message received");
        assertEquals(DiscoveryMessage.Type.ANNOUNCE, r.message.getType());
        assertEquals("Mathéo", r.message.getUsername()); // vérifie aussi l'UTF-8
        assertEquals(LOCALHOST, r.sender);
    }

    @Test
    void listenerSurvivesMalformedPacket() throws Exception {
        startListener(Set.of());

        // paquet pourri envoyé "à la main", sans passer par DiscoveryMessage
        try (DatagramSocket raw = new DatagramSocket()) {
            byte[] data = "garbage".getBytes(StandardCharsets.UTF_8);
            raw.send(new DatagramPacket(data, data.length, LOCALHOST, TEST_PORT));
        }
        sender.send(new DiscoveryMessage(DiscoveryMessage.Type.USER_LEFT, ""), LOCALHOST);

        Received r = received.poll(2, TimeUnit.SECONDS);
        assertNotNull(r, "listener died after malformed packet");
        assertEquals(DiscoveryMessage.Type.USER_LEFT, r.message.getType());
    }

    @Test
    void packetsFromLocalAddressAreIgnored() throws Exception {
        startListener(Set.of(LOCALHOST));

        sender.send(new DiscoveryMessage(DiscoveryMessage.Type.ANNOUNCE, "Me"), LOCALHOST);

        assertNull(received.poll(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void stopTerminatesListenerThread() throws Exception {
        startListener(Set.of());

        listener.stop();
        listenerThread.join(1000);

        assertFalse(listenerThread.isAlive());
    }
}