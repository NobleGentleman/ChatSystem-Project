package fr.insa.chatsystem.network;

import java.net.InetAddress;

import fr.insa.chatsystem.discovery.DiscoveryMessage;

public interface MessageHandler {
    void handle(DiscoveryMessage message, InetAddress sender);
}