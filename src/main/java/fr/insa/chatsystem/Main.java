package fr.insa.chatsystem;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insa.chatsystem.udp.UdpListener;
import fr.insa.chatsystem.udp.UdpSender;

/** Entry point of the ChatSystem agent. */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final int DISCOVERY_PORT = 4445;
    private static final long DISCOVERY_WINDOW_MILLIS = 2000; // < 3 s (CdC-Bs-24)

    private Main() { }

    public static void main(String[] args) {
        LOG.info("ChatSystem started");
        try {
            run();
        } catch (IOException e) {
            LOG.error("Network error", e);
            System.err.println("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void run() throws IOException, InterruptedException {
        // Création des objets
        InetAddress myAddress = localAddress();
        ContactList contacts = new ContactList();
        UdpSender sender = new UdpSender(DISCOVERY_PORT);
        DiscoveryService discovery = new DiscoveryService(contacts, sender, DISCOVERY_WINDOW_MILLIS);
        UdpListener listener = new UdpListener(DISCOVERY_PORT, discovery, Set.of(myAddress));

        // Thread d'écoute UDP
        Thread listenerThread = new Thread(listener, "udp-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();

        // Arrêt propre (quit, Ctrl+C, fermeture du terminal)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                discovery.disconnect();
            } catch (IOException e) {
                LOG.warn("Unable to send USER_LEFT", e);
            }
            listener.stop();
            sender.close();
        }, "shutdown"));

        // Découverte puis choix du pseudo
        CommandLineInterface cli = new CommandLineInterface(System.in, System.out);
        System.out.println("Searching for users on the network...");
        discovery.discover();

        String username = cli.askUsername(discovery::isUsernameAvailable);
        User me = new User(myAddress);
        me.setUsername(username);
        discovery.announce(me.getUsername());
        System.out.println("Welcome, " + me.getUsername() + " (" + me.getIpAddress().getHostAddress() + ")!");

        // Affichage en direct des changements
        contacts.addListener(new ContactListener() {
            @Override
            public void onContactAdded(User user) {
                System.out.println("\n+ " + user.getUsername() + " joined");
            }

            @Override
            public void onContactRemoved(User user) {
                System.out.println("\n- " + user.getUsername() + " left");
            }

            @Override
            public void onUsernameChanged(User user, String oldUsername) {
                System.out.println("\n~ " + oldUsername + " is now " + user.getUsername());
            }
        });
        printContacts(contacts);

        // Boucle de commandes
        System.out.println("Commands: list, quit");
        String command;
        while ((command = cli.readCommand()) != null && !command.equals("quit")) {
            if (command.equals("list")) {
                printContacts(contacts);
            } else if (!command.isEmpty()) {
                System.out.println("Unknown command.");
            }
        }
        // fin de main -> la JVM s'arrête -> le shutdown hook envoie USER_LEFT
    }

    private static void printContacts(ContactList contacts) {
        if (contacts.getAll().isEmpty()) {
            System.out.println("No other user online.");
            return;
        }
        System.out.println("Online users:");
        for (User user : contacts.getAll()) {
            System.out.println("  " + user.getUsername() + " (" + user.getIpAddress().getHostAddress() + ")");
        }
    }

    // L'IP de cette machine sur le réseau local.
    private static InetAddress localAddress() throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 80);
            return socket.getLocalAddress();
        }
    }
}