package fr.insa.chatsystem;

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

    /** Lit une commande tapée par l'utilisateur, ou null si l'entrée est fermée. */
    public String readCommand() {
        out.print("> ");
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
}