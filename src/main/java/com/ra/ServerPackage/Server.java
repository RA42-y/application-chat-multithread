package com.ra.ServerPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;


/**
 * Programme côté serveur
 *
 * @author Jieni YU, Zhuzexuan SHI
 */
public class Server {

    /**
     * L'objet de la classe Server instancié au moment de la création de classe
     */
    private static final Server instance = new Server();

    /**
     * Le tableau qui stocke les sockets de client et les noms d'utilisateur correspondant
     */
    private final Map<String, Socket> clientsTable;

    /**
     * Construire un object Serveur, instancier le tableau des clients
     */
    private Server() {
        clientsTable = new Hashtable<>();
    }

    /**
     * Obtenir la seule instance Serveur
     *
     * @return la seule instance Serveur
     */
    public static Server getInstance() {
        return instance;
    }

    /**
     * Obtenir le tableau des clients
     *
     * @return le tableau des clients
     */
    public Map<String, Socket> getClientsTable() {
        return instance.clientsTable;
    }

    /**
     * Ajouter un socket de client nouvellement connecté au tableau avec le nom d'utilisateur valide
     *
     * @param username le nom d'utilisateur saisi
     * @param client   le socket de client connecté au serveur
     * @return validité du nom d'utilisateur
     */
    public boolean addClient(String username, Socket client) {
        Server server = Server.getInstance();
        // Vérifier si le nom d'utilisateur saisi est non null et n'existe pas dans le tableau
        if (!username.equals("") && !server.getClientsTable().containsKey(username)) {
            server.getClientsTable().put(username, client);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Supprimer le client quitté
     *
     * @param username le nom d'utilisateur
     */
    public void deleteClient(String username) {
        Server server = Server.getInstance();
        server.getClientsTable().remove(username);
    }

    /**
     * Diffuser un message à tous les sockets de client connecté au serveur
     *
     * @param msg message envoyé par un client
     * @throws IOException
     */
    public void broadcast(String msg) throws IOException {
        Server server = Server.getInstance();
        for (Map.Entry<String, Socket> entry : server.getClientsTable().entrySet()) {
            DataOutputStream outsData = new DataOutputStream(entry.getValue().getOutputStream());
            outsData.writeUTF(msg);
        }
    }

    /**
     * Exécuter le programme côté serveur
     *
     * @param args les arguments du programme
     */
    public static void main(String[] args) {

        try {
            Server server = Server.getInstance();
            ServerSocket conn = new ServerSocket(10080);

            while (true) {
                try {
                    // Accepter les demandes entrantes
                    Socket client = conn.accept();
                    System.out.println("New client connected");

                    DataInputStream insData = new DataInputStream(client.getInputStream());
                    DataOutputStream outsData = new DataOutputStream(client.getOutputStream());
                    String username;

                    // Vérifier la validité du nom d'utilisateur et ajouter le client
                    while (true) {
                        username = insData.readUTF();
                        System.out.println("Client input username: " + username);
                        if (server.addClient(username, client)) {  // L'ajout de client réussi
                            outsData.writeUTF("true");
                            server.broadcast("    " + username + " a rejoint la conversation");
                            System.out.println("Username valid, client joined: " + username);
                            break;
                        } else { // Le nom d'utilisateur est invalide
                            outsData.writeUTF("false");
                            System.out.println("Username invalid");
                        }
                    }

                    // Lancer le thread qui intercepte tout message envoyé dans cet objet socket récemment stocké
                    MessageReceptorServer msgReceptorServer = new MessageReceptorServer(username, client);
                    msgReceptorServer.start();
                } catch (IOException e) {
                    System.out.println("New client disconnected");
                }
            }
        } catch (IOException ex) {
//            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
