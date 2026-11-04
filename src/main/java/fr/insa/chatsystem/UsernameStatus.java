package fr.insa.chatsystem;

public enum UsernameStatus {
    VALID("Valid username."),
    EMPTY("Empty username."),
    TOO_SHORT("Username is too short."),
    TOO_LONG("Username is too long."),
    INVALID_CHARACTERS("Invalid characters."),
    ALREADY_TAKEN("Username already taken."),
    UNCHANGED("Username already yours.");

    private final String message;

    UsernameStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid() {
        return this == VALID;
    }
}
