package fr.insa.chatsystem;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insa.chatsystem.udp.MessageHandler;
import fr.insa.chatsystem.udp.MessageSender;

public class DiscoveryService implements MessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryService.class);

    private final ContactList contacts;
    private final MessageSender sender;
    private final long discoveryWindowMillis;

    /** null tant que l'utilisateur n'a pas choisi de pseudo. */
    private volatile String myUsername;

    public DiscoveryService(ContactList contacts, MessageSender sender, long discoveryWindowMillis) {
        this.contacts = contacts;
        this.sender = sender;
        this.discoveryWindowMillis = discoveryWindowMillis;
    }

    // --- Réception (appelé par le thread UDP) ---

    @Override
    public void handle(DiscoveryMessage message, InetAddress from) {
        switch (message.getType()) {
            
            case DISCOVERY_REQUEST:
                String me = myUsername;
                if (me != null) {
                    try {
                        sender.send(new DiscoveryMessage(DiscoveryMessage.Type.DISCOVERY_RESPONSE, me), from);
                    } catch (IOException e) {
                        LOG.error("Unable to answer discovery request from {}", from, e);
                    }
                }
                break;
            
            case DISCOVERY_RESPONSE:
            
            case ANNOUNCE:
                if (!UsernameValidator.validate(message.getUsername()).isValid()) {
                    LOG.warn("Ignoring invalid username '{}' from {}", message.getUsername(), from);
                    break;
                }
                contacts.addOrUpdate(from, message.getUsername());
                break;

            case USER_LEFT:
                contacts.remove(from);
                break;

            default:
                LOG.warn("Unhandled message type {} from {}", message.getType(), from);
        }
    }

    // --- Actions locales (appelées par la CLI / Main) ---

    /** Demande aux agents présents de se signaler, puis attend leurs réponses. */
    public void discover() throws IOException, InterruptedException {
        sender.broadcast(new DiscoveryMessage(DiscoveryMessage.Type.DISCOVERY_REQUEST, ""));
        Thread.sleep(discoveryWindowMillis);
    }

    public boolean isUsernameAvailable(String username) {
        return !contacts.isUsernameTaken(username);
    }

    public void announce(String username) throws IOException {
        myUsername = username;
        sender.broadcast(new DiscoveryMessage(DiscoveryMessage.Type.ANNOUNCE, username));
    }

    public void disconnect() throws IOException {
        if (myUsername != null) {
            myUsername = null;
            sender.broadcast(new DiscoveryMessage(DiscoveryMessage.Type.USER_LEFT,""));
        }
    }

    public String getMyUsername() {
        return myUsername;
    }
}