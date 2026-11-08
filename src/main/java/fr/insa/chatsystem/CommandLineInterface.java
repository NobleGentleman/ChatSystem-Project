package fr.insa.chatsystem;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CommandLineInterface {
    private final Scanner scanner;
    private final PrintStream out;

    public CommandLineInterface(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public User askUsername() throws UnknownHostException {
        InetAddress ipAddress = InetAddress.getByName("192.168.1.10"); // arbitrary for now
        User user = new User(ipAddress);
        String username;

        while(true) {
            out.print("Please enter your username: ");
            username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                out.println("Username cannot be empty.");
            } /*else if (!isUsernameAvailable(username)) {
                out.println("This username is already taken.");
            } */ else {
                break;
            }
        }

        user.setUsername(username);
        return user;
    }
}
