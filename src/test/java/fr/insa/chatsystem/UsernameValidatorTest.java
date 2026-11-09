package fr.insa.chatsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UsernameValidatorTest {

    // --- EMPTY ---

    @Test
    void nullUsernameIsEmpty() {
        assertEquals(UsernameStatus.EMPTY, UsernameValidator.validate(null));
    }

    @Test
    void emptyUsernameIsEmpty() {
        assertEquals(UsernameStatus.EMPTY, UsernameValidator.validate(""));
    }

    @Test
    void blankUsernameIsEmpty() {
        assertEquals(UsernameStatus.EMPTY, UsernameValidator.validate("   "));
    }

    // --- VALID ---

    @Test
    void usernameWithAccentDigitsAndUnderscoreIsValid() {
        assertEquals(UsernameStatus.VALID, UsernameValidator.validate("Mathéo_42"));
    }

    @Test
    void usernameWithHyphenIsValid() {
        assertEquals(UsernameStatus.VALID, UsernameValidator.validate("jean-luc"));
    }

    @Test
    void singleCharacterUsernameIsValid() {
        assertEquals(UsernameStatus.VALID, UsernameValidator.validate("a"));
    }

    // --- Length limit ---

    @Test
    void usernameAtMaxLengthIsValid() {
        assertEquals(UsernameStatus.VALID, UsernameValidator.validate("abcdefghijkl")); // 12
    }

    @Test
    void usernameAboveMaxLengthIsTooLong() {
        assertEquals(UsernameStatus.TOO_LONG, UsernameValidator.validate("abcdefghijklm")); // 13
    }

    // --- INVALID_CHARACTERS ---

    @Test
    void usernameWithSpaceIsInvalid() {
        assertEquals(UsernameStatus.INVALID_CHARACTERS, UsernameValidator.validate(" Mathis"));
    }

    @Test
    void usernameWithSpecialCharacterIsInvalid() {
        assertEquals(UsernameStatus.INVALID_CHARACTERS, UsernameValidator.validate("a;b"));
    }

    @Test
    void usernameWithNewlineIsInvalid() {
        assertEquals(UsernameStatus.INVALID_CHARACTERS, UsernameValidator.validate("Mat\nhis"));
    }
}