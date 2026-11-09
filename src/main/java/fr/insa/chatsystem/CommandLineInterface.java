package fr.insa.chatsystem;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.function.Predicate;

public class CommandLineInterface {
    private final Scanner scanner;
    private final PrintStream out;

    public CommandLineInterface(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public User askUsername() throws UnknownHostException {
        return askUsername(name -> true);
    }

    public User askUsername(Predicate<String> isAvailable) throws UnknownHostException {
        InetAddress ipAddress = InetAddress.getByName("192.168.1.10"); // arbitrary for now
        User user = new User(ipAddress);
        String username;

        while (true) {
            out.print("Please enter your username: ");
            username = scanner.nextLine().trim();

            UsernameStatus status = UsernameValidator.validate(username);
            if (!status.isValid()) {
                out.println(status.getMessage());
            } else if (!isAvailable.test(username)) {
                out.println(UsernameStatus.ALREADY_TAKEN.getMessage());
            } else {
                break;
            }
        }

        user.setUsername(username);
        return user;
    }

    /** Lit une commande tapée par l'utilisateur, ou null si l'entrée est fermée. */
    public String readCommand() {
        out.print("> ");
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
}
