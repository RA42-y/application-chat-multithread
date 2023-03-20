package com.ra.ClientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


/**
 * Programme côté client
 *
 * @author Jieni YU, Zhuzexuan SHI
 */
public class Client {
    /**
     * Exécuter le programme côté client
     *
     * @param args les arguments du programme
     */
    public static void main(String[] args) {
        try {
            // Effectuer une connexion auprès du serveur
            Socket client = new Socket("localhost", 10080);

            String username;
            String addClientState;

            DataInputStream insData = new DataInputStream(client.getInputStream());
            DataOutputStream outsData = new DataOutputStream(client.getOutputStream());

            // Créer un nom d'utilisateur
            while (true) {
                System.out.println("Entrez votre pseudo : ");
                Scanner scanner = new Scanner(System.in);

                username = scanner.next();
                outsData.writeUTF(username);

                // Vérifier la validité de nom d'utilisateur indiquée par le serveur
                addClientState = insData.readUTF();
                if (addClientState.equals("true")) {
                    System.out.println("    " + username + " a rejoint la conversation");
                    System.out.println("----------------------------");
                    break;
                } else {
                    System.out.println("Ce pseudo est déjà utilisé !");
                }
            }

            // Lancer le thread qui intercepte les messages saisis par l’utilisateur et les envoient au serveur
            MessageEmitterClient msgEmitter = new MessageEmitterClient(username, client);
            msgEmitter.start();
            // Lancer le thread qui récupère les messages saisis par l’utilisateur et les transmet au serveur
            MessageReceptorClient msgReceptor = new MessageReceptorClient(username, client);
            msgReceptor.start();

//            while (true) {
//                if (msgReceptor.isAlive()) {
//                    continue;
//                } else {
//                    msgEmitter.interrupt();
//                    return;
//                }
//            }
        } catch (SocketException ex) {
            System.out.println("Échec de la connexion");
        } catch (IOException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Le server ne répond pas, quittez la conversation!");
        }
    }
}
