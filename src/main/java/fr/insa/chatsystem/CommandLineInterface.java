package fr.insa.chatsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CommandLineInterface {

    private final Scanner scanner = new Scanner(System.in);

    public User askUsername() throws UnknownHostException {
        InetAddress ipAddress = InetAddress.getByName("192.168.1.10"); // arbitrary for now
        User user = new User(ipAddress);
        String username;

        while(true) {
            System.out.print("Please enter your username: ");
            username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
            } /*else if (!isUsernameAvailable(username)) {
                System.out.println("This username is already taken.");
            } */ else {
                break;
            }
        }

        user.setUsername(username);
        return user;
    }
}
