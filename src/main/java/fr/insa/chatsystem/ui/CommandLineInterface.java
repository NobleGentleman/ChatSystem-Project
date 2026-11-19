package fr.insa.chatsystem.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Predicate;

public class CommandLineInterface {
    private final Scanner scanner;
    private final PrintStream out;

    public CommandLineInterface(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public String askUsername() {
        return askUsername(name -> true);
    }

    public String askUsername(Predicate<String> isAvailable) {
        while (true) {
            out.print("Please enter your username: ");

            // Check if the input is still available to handle Ctrl+D (EOF).
            if (!scanner.hasNextLine()) {
                return null;
            }

            String username = scanner.nextLine().trim();

            UsernameStatus status = UsernameValidator.validate(username);
            if (!status.isValid()) {
                out.println(status.getMessage());
            } else if (!isAvailable.test(username)) {
                out.println(UsernameStatus.ALREADY_TAKEN.getMessage());
            } else {
                return username;
            }
        }
    }

    /** Reads a command entered by the user, or null if the input is closed. */
    public String readCommand() {
        out.print("> ");
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
}