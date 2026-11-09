package fr.insa.chatsystem;

import java.net.InetAddress;

public interface MessageHandler {
    void handle(DiscoveryMessage message, InetAddress sender);
}