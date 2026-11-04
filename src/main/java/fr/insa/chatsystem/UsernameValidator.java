package fr.insa.chatsystem;

public final class UsernameValidator {
    
    private static final int MAX_LENGTH = 12;

    private static final String ALLOWED_PATTERN = "[\\p{L}0-9_-]+";

    private UsernameValidator() {}

    public static UsernameStatus validate(String username) {
        if (username == null || username.isBlank()) {
            return UsernameStatus.EMPTY;
        }

        if (username.length() > MAX_LENGTH) {
            return UsernameStatus.TOO_LONG;
        }

        if (!username.matches(ALLOWED_PATTERN)) {
            return UsernameStatus.INVALID_CHARACTERS;
        }

        return UsernameStatus.VALID;
    }
}
