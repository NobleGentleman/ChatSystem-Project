package fr.insa.chatsystem.network;

import java.io.IOException;
import java.net.InetAddress;

import fr.insa.chatsystem.discovery.DiscoveryMessage;

public interface MessageSender {
    void send(DiscoveryMessage message, InetAddress destination) throws IOException;
    void broadcast(DiscoveryMessage message) throws IOException;
}