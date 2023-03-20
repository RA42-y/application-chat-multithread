package com.ra.ClientPackage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Classe héritée de la classe Thread qui récupère les messages saisis par l’utilisateur et les transmet au serveur
 *
 * @author Jieni YU, Zhuzexuan SHI
 */
public class MessageEmitterClient extends Thread {
    /**
     * Le nom d'utilisateur
     */
    private final String username;

    /**
     * Le socket de client
     */
    private final Socket client;

    /**
     * Construire un objet MessageEmitterClient
     *
     * @param username le nom d'utilisateur
     * @param client   le socket de client
     */
    public MessageEmitterClient(String username, Socket client) {
        this.username = username;
        this.client = client;
    }

    /**
     * Réécrire la fonction run() de la classe mère Thread
     * Récupère les messages saisis par l’utilisateur et les transmet au serveur
     */
    @Override
    public void run() {
        try {
            DataOutputStream outsData = new DataOutputStream(client.getOutputStream());
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String inputString = scanner.nextLine();
                if (!inputString.equals("")) {
                    outsData.writeUTF(inputString);
                    if (inputString.equals("exit")) {
                        break;
                    }
                } else {
                    System.out.println("Impossible d'envoyer un message vide");
                }
            }
            this.join();
        } catch (IOException ex) {
            Logger.getLogger(MessageReceptorClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
