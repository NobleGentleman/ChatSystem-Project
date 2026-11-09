package fr.insa.chatsystem.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insa.chatsystem.DiscoveryMessage;

public class UdpListener implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(UdpListener.class);
    private static final int BUFFER_SIZE = 1024;

    private final DatagramSocket socket;
    private final MessageHandler handler;
    private final Set<InetAddress> localAddresses; // pour ignorer mes propres broadcasts

    public UdpListener(int port, MessageHandler handler, Set<InetAddress> localAddresses)
            throws SocketException {
        this.socket = new DatagramSocket(port);
        this.handler = handler;
        this.localAddresses = localAddresses;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            try {
                socket.receive(packet);

                if (localAddresses.contains(packet.getAddress())) {
                    continue;
                }

                String text = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                DiscoveryMessage message = DiscoveryMessage.parse(text);
                handler.handle(message, packet.getAddress());

            } catch (IllegalArgumentException e) {
                LOG.warn("Ignoring malformed packet from {}: {}", packet.getAddress(), e.getMessage());
            } catch (IOException e) {
                if (socket.isClosed()) {
                    LOG.info("UDP listener stopped");
                    return;
                }
                LOG.error("Error while receiving UDP packet", e);
            }
        }
    }

    public void stop() {
        socket.close();
    }
}