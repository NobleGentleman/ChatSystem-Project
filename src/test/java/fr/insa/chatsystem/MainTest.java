package fr.insa.chatsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void mainDoesNotThrow() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
