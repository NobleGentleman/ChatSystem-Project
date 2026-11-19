package fr.insa.chatsystem.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import fr.insa.chatsystem.discovery.DiscoveryMessage;

public class UdpSender implements MessageSender, AutoCloseable {

    private static final InetAddress BROADCAST = broadcastAddress();

    private final DatagramSocket socket;
    private final int port; // Listening port for other agents

    public UdpSender(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket();   // Local port selected by the system
        this.socket.setBroadcast(true);       // Required to send a broadcast
    }

    @Override
    public void send(DiscoveryMessage message, InetAddress destination) throws IOException {
        byte[] data = message.serialize().getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, destination, port);
        socket.send(packet);
    }

    @Override
    public void broadcast(DiscoveryMessage message) throws IOException {
        send(message, BROADCAST);
    }

    @Override
    public void close() {
        socket.close();
    }

    private static InetAddress broadcastAddress() {
        try {
            return InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }
}