package main.java.server;

/** Une interface fonctionnelle qui permet de créé des nouveaux évènement à
 * gérer. */
@FunctionalInterface
public interface EventHandler {
    void handle(String cmd, String arg);
}
