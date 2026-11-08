package fr.insa.chatsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/** Entry point of the ChatSystem agent. */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() { }

    public static void main(String[] args) {
        LOG.info("ChatSystem started");

        CommandLineInterface cli = new CommandLineInterface();
        try {
            User user = cli.askUsername();
            System.out.println("Welcome, " + user.getUsername() + "!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.println("Error: unable to resolve the IP address.");
        }
    }
}
