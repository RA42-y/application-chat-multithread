package com.ra.ClientPackage;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Classe héritée de la classe Thread qui intercepte les messages venant du serveur
 *
 * @author Jieni YU, Zhuzexuan SHI
 */
public class MessageReceptorClient extends Thread {

    /**
     * Le nom d'utilisateur
     */
    private final String username;

    /**
     * Le socket de client
     */
    private final Socket client;

    /**
     * Construire un objet MessageReceptorClient
     *
     * @param username le nom d'utilisateur
     * @param client   le socket de client
     */
    public MessageReceptorClient(String username, Socket client) {
        this.username = username;
        this.client = client;
    }

    /**
     * Réécrire la fonction run() de la classe mère Thread
     * Obtenir les messages venant du serveur et les afficher dans la console de client
     */
    @Override
    public void run() {
        try {
            DataInputStream insData = new DataInputStream(client.getInputStream());
            while (true) {
                String str = insData.readUTF();
                System.out.println(str);

                if (str.equals("    " + username + " a quitté la conversation")) {
                    break;
                }
            }
            client.close();
            this.join();
        } catch (IOException ex) {
//            Logger.getLogger(MessageReceptorClient.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Le server ne répond pas, quittez la conversation!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
