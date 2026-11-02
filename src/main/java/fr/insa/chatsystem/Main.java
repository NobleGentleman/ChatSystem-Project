package fr.insa.chatsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Entry point of the ChatSystem agent. */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() { }

    public static void main(String[] args) {
        LOG.info("ChatSystem started");
    }
}
