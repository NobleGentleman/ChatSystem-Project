package fr.insa.chatsystem.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import fr.insa.chatsystem.DiscoveryMessage;

public class UdpSender implements AutoCloseable {

    private static final InetAddress BROADCAST = broadcastAddress();

    private final DatagramSocket socket;
    private final int port; // port d'écoute des autres agents

    public UdpSender(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket();   // port local choisi par le système
        this.socket.setBroadcast(true);       // obligatoire pour envoyer en broadcast
    }

    public void send(DiscoveryMessage message, InetAddress destination) throws IOException {
        byte[] data = message.serialize().getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, destination, port);
        socket.send(packet);
    }

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