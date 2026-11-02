package fr.insa.chatsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Point d'entrée de l'agent ChatSystem. */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() { }

    public static void main(String[] args) {
        LOG.info("ChatSystem démarré");
    }
}
