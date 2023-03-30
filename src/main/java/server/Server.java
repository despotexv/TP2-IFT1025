package main.java.server;

import javafx.util.Pair;
import main.java.server.models.Course;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/** Cette classe représente un serveur.
 * Un serveur qui va attendre les instructions de potentiels clients pour
 * les exécuter.
 * */

public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /** Crée un nouveau serveur
     * Le serveur créé sera initialisé sur le serveur mis en arguments.
     *
     * @param port Le port où on souhaite initialisé notre serveur
     * @throws IOException */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /** Ajoute un évenement à notre liste d'évènements à gérer
     *
     * @param h évenement à ajouter  */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }
    /** */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }
    /** Cette méthode démarre notre serveur */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /** Cette méthode démarre la boucle d'évènement.
     *
     * Entre autre, en démarrant la boucle des évènements, cette méthode
     * écoute et exécute les instructions qui lui sont nourries.
     * @throws IOException
     * @throws ClassNotFoundException  */

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }
    /** Retourne une paire de string composé de la commande et de ses arguments
     * Cette méthode décortique une string et en ressort la commande et les
     * arguments qui s'y sont encoder.
     *
     * @param line La ligne à decrypter  */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }
    /** Déconnecte le serveur
     *
     * @throws IOException si l'un des inputs ou des outputs sont érronés  */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }
    /** Cette function gère les evénèments désirés.
     * Exécute les instructions reçu en arguments si elle correspondent aux
     * commandes d'inscription et de chargement des cours.
     *
     * @param cmd La commande rentrée
     * @param arg Les arguments associés à la commande rentrés */

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les
     transformer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        // TODO: implémenter cette méthode
        ArrayList<Course> listeDeCours = null;
        File docDeCours = new File("/data/cours.text") ;
        Scanner scan = null;
        try {
            scan = new Scanner(docDeCours);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(scan.hasNextLine()) {
            String [] parts = scan.nextLine().split("\t");
            if (arg == parts[2]) {
                listeDeCours.add(new Course(parts[0],parts[1],parts[2] ));
            }
            System.out.print(listeDeCours);
            try {
                objectOutputStream.writeObject(listeDeCours);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        objectInputStream.readObject()
        // TODO: implémenter cette méthode
    }
}

