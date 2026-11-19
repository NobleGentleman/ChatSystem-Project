package fr.insa.chatsystem.discovery;

import java.util.Objects;

public final class DiscoveryMessage {

    /** USERNAME_TAKEN helps handle situations when someone announces a username on the network that is already in use */
    public enum Type {
        DISCOVERY_REQUEST,
        DISCOVERY_RESPONSE,
        ANNOUNCE,
        USERNAME_TAKEN,
        USER_LEFT
    }

    private static final String SEPARATOR = ";";

    private final Type type;
    private final String username; // empty for DISCOVERY_REQUEST and USER_LEFT

    public DiscoveryMessage(Type type, String username) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.username = (username == null) ? "" : username;
    }

    public Type getType() { return type; }
    public String getUsername() { return username; }

    /** Example: ANNOUNCE + "Bob" -> "ANNOUNCE;Bob" */
    public String serialize() {
        return type.name() + SEPARATOR + username;
    }

    /** Example: "ANNOUNCE;Bob" -> DiscoveryMessage(ANNOUNCE, "Bob") */
    public static DiscoveryMessage parse(String raw) {
        if (raw == null) {throw new IllegalArgumentException("Message must not be null.");}
        
        String[] parts = raw.split(SEPARATOR, -1);
        
        if (parts.length != 2) {
            throw new IllegalArgumentException("Malformed discovery message: " + raw);
        }

        Type type = Type.valueOf(parts[0]);
        String username = parts[1];

        return new DiscoveryMessage(type, username);
    }
}