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

        CommandLineInterface cli = new CommandLineInterface(System.in, System.out);
        try {
            User user = cli.askUsername();
            System.out.println("Welcome, " + user.getUsername() + "!");
        } catch (UnknownHostException e) {
            LOG.error("Unable to resolve the IP address", e);
            System.err.println("Error: unable to resolve the IP address.");
        }
    }
}
