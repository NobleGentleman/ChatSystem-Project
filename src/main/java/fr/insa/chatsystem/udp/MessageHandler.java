package fr.insa.chatsystem.udp;

import java.net.InetAddress;

import fr.insa.chatsystem.DiscoveryMessage;

public interface MessageHandler {
    void handle(DiscoveryMessage message, InetAddress sender);
}