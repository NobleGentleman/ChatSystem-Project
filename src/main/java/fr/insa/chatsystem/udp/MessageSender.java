package fr.insa.chatsystem.udp;

import java.io.IOException;
import java.net.InetAddress;

import fr.insa.chatsystem.DiscoveryMessage;

public interface MessageSender {
    void send(DiscoveryMessage message, InetAddress destination) throws IOException;
    void broadcast(DiscoveryMessage message) throws IOException;
}