package fr.insa.chatsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineInterfaceTest {

    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() {
        outBuf = new ByteArrayOutputStream();
    }

    private CommandLineInterface cliWithInput(String input) {
        return new CommandLineInterface(new ByteArrayInputStream(input.getBytes()), new PrintStream(outBuf));
    }

    @Test
    void askUsername_acceptsValid() throws UnknownHostException {
        User user = cliWithInput("Alice\n").askUsername();

        assertFalse(outBuf.toString().contains(UsernameStatus.EMPTY.getMessage()));
        assertEquals("Alice", user.getUsername());
    }

    @Test
    void askUsername_trimsWhitespace() throws UnknownHostException {
        User user = cliWithInput("   Alice   \n").askUsername();

        assertEquals("Alice", user.getUsername());
    }

    @Test
    void askUsername_rejectsEmptyThenAcceptsValid() throws UnknownHostException {
        User user = cliWithInput("\nAlice\n").askUsername();

        assertTrue(outBuf.toString().contains(UsernameStatus.EMPTY.getMessage()));
        assertEquals("Alice", user.getUsername());
    }

    @Test
    void askUsername_rejectsTakenThenAcceptsFree() throws UnknownHostException {
        User user = cliWithInput("Alice\nBob\n").askUsername(name -> !name.equals("Alice"));

        assertTrue(outBuf.toString().contains(UsernameStatus.ALREADY_TAKEN.getMessage()));
        assertEquals("Bob", user.getUsername());
    }

    @Test
    void askUsername_rejectsInvalidCharactersThenAcceptsValid() throws UnknownHostException {
        User user = cliWithInput("a;b\nAlice\n").askUsername();

        assertTrue(outBuf.toString().contains(UsernameStatus.INVALID_CHARACTERS.getMessage()));
        assertEquals("Alice", user.getUsername());
    }

    @Test
    void askUsername_rejectsTooLongThenAcceptsValid() throws UnknownHostException {
        User user = cliWithInput("abcdefghijklm\nAlice\n").askUsername();

        assertTrue(outBuf.toString().contains(UsernameStatus.TOO_LONG.getMessage()));
        assertEquals("Alice", user.getUsername());
    }
}