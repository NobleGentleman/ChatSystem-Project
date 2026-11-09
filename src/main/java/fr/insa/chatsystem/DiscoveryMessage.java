package fr.insa.chatsystem;

import java.util.Objects;

public final class DiscoveryMessage {

    public enum Type {
        DISCOVERY_REQUEST,
        DISCOVERY_RESPONSE,
        ANNOUNCE,
        USER_LEFT
    }

    private static final String SEPARATOR = ";";

    private final Type type;
    private final String username; // vide pour DISCOVERY_REQUEST et USER_LEFT

    public DiscoveryMessage(Type type, String username) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.username = (username == null) ? "" : username;
    }

    public Type getType() { return type; }
    public String getUsername() { return username; }

    /** Ex : ANNOUNCE + "Bob" -> "ANNOUNCE;Bob" */
    public String serialize() {
        return type.name() + SEPARATOR + username;
    }

    /** Ex : "ANNOUNCE;Bob" -> DiscoveryMessage(ANNOUNCE, "Bob") */
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