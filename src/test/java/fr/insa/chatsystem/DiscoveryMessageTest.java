package fr.insa.chatsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class DiscoveryMessageTest {

    // --- serialize ---

    @Test
    void announceIsSerializedWithTypeAndUsername() {
        DiscoveryMessage message = new DiscoveryMessage(DiscoveryMessage.Type.ANNOUNCE, "Bob");
        assertEquals("ANNOUNCE;Bob", message.serialize());
    }

    @Test
    void nullUsernameIsSerializedAsEmpty() {
        DiscoveryMessage message = new DiscoveryMessage(DiscoveryMessage.Type.USER_LEFT, null);
        assertEquals("USER_LEFT;", message.serialize());
    }

    // --- constructor ---

    @Test
    void nullTypeIsRejected() {
        assertThrows(NullPointerException.class, () -> new DiscoveryMessage(null, "Bob"));
    }

    // --- parse: round trip ---

    @ParameterizedTest
    @EnumSource(DiscoveryMessage.Type.class)
    void parseOfSerializeGivesSameMessage(DiscoveryMessage.Type type) {
        DiscoveryMessage original = new DiscoveryMessage(type, "Bob");

        DiscoveryMessage parsed = DiscoveryMessage.parse(original.serialize());

        assertEquals(type, parsed.getType());
        assertEquals("Bob", parsed.getUsername());
    }

    @Test
    void messageWithoutUsernameIsParsedWithEmptyUsername() {
        DiscoveryMessage parsed = DiscoveryMessage.parse("USER_LEFT;");

        assertEquals(DiscoveryMessage.Type.USER_LEFT, parsed.getType());
        assertEquals("", parsed.getUsername());
    }

    // --- parse: malformed messages ---

    @Test
    void nullMessageIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiscoveryMessage.parse(null));
    }

    @Test
    void emptyMessageIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiscoveryMessage.parse(""));
    }

    @Test
    void messageWithoutSeparatorIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiscoveryMessage.parse("ANNOUNCE"));
    }

    @Test
    void messageWithTooManyPartsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiscoveryMessage.parse("ANNOUNCE;Bob;extra"));
    }

    @Test
    void unknownTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiscoveryMessage.parse("HELLO;Bob"));
    }

    @Test
    void lowercaseTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiscoveryMessage.parse("announce;Bob"));
    }
}