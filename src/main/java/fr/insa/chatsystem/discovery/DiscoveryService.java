package fr.insa.chatsystem.discovery;

import java.io.IOException;
import java.net.InetAddress;

import fr.insa.chatsystem.contacts.ContactList;
import fr.insa.chatsystem.ui.UsernameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insa.chatsystem.network.MessageHandler;
import fr.insa.chatsystem.network.MessageSender;

public class DiscoveryService implements MessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryService.class);

    private final ContactList contacts;
    private final MessageSender sender;
    private final long discoveryWindowMillis;

    /** null until the user has chosen a username. */
    private volatile String myUsername;

    public DiscoveryService(ContactList contacts, MessageSender sender, long discoveryWindowMillis) {
        this.contacts = contacts;
        this.sender = sender;
        this.discoveryWindowMillis = discoveryWindowMillis;
    }

    /** Receive (called by the UDP thread). */

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
                if (message.getUsername().equalsIgnoreCase(myUsername)) {
                    // If anyone wants my username, I'll let them know
                    try {
                        sender.send(new DiscoveryMessage(DiscoveryMessage.Type.USERNAME_TAKEN, myUsername), from);
                    } catch (IOException e) {
                        LOG.error("Unable to send USERNAME_TAKEN to {}", from, e);
                    }
                    break;
                }
                contacts.addOrUpdate(from, message.getUsername());
                break;

            case USERNAME_TAKEN:
                LOG.warn("Username '{}' is already used, please restart with another one", message.getUsername());
                myUsername = null;
                break;

            case USER_LEFT:
                contacts.remove(from);
                break;

            default:
                LOG.warn("Unhandled message type {} from {}", message.getType(), from);
        }
    }

    /** Local actions (called by the CLI / Main). */

    /** Asks the present agents to signal themselves, then waits for their replies. */
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