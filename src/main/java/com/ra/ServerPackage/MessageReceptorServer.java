package com.ra.ServerPackage;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Classe héritée de la classe Thread qui intercepte tout message envoyé par un client
 *
 * @author Jieni YU, Zhuzexuan SHI
 */
public class MessageReceptorServer extends Thread {
    /**
     * Le socket de client
     */
    private Socket client;

    /**
     * Le nom d'utilisateur
     */
    private String username;

    /**
     * Construire un objet MessageReceptorServer
     *
     * @param username le nom d'utilisateur
     * @param client   le socket de client
     */
    public MessageReceptorServer(String username, Socket client) {
        this.username = username;
        this.client = client;
    }

    /**
     * Obtenir le socket de client
     *
     * @return le socket de client
     */
    public Socket getClient() {
        return client;
    }

    /**
     * Modifier le socket de client
     *
     * @param client le socket de client
     */
    public void setClient(Socket client) {
        this.client = client;
    }

    /**
     * Obtenir le nom d'utilisateur
     *
     * @return le nom d'utilisateur
     */
    public String getUsername() {
        return username;
    }

    /**
     * Modifier le nom d'utilisateur
     *
     * @param username le nom d'utilisateur
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Réécrire la fonction run() de la classe mère Thread
     * Recevoir les messages envoyés par le client et les diffuser à tous les clients connectés au serveur
     * Fermer le socket de client et le supprimer de tableau des clients en cas de quitter avec "exit" ou IOException
     */
    @Override
    public void run() {
        Server server = Server.getInstance();

        try {
            DataInputStream insData = new DataInputStream(getClient().getInputStream());
            while (true) {
                String msg = insData.readUTF();
                if (msg.equals("exit")) {
                    server.broadcast("    " + getUsername() + " a quitté la conversation");
                    break;
                } else {
                    String str = "    " + getUsername() + "a dit : " + msg;
                    server.broadcast(str);
                }
            }
            insData.close();
            getClient().close();
            server.deleteClient(getUsername());
            System.out.println("Client quit with \"exit\": " + getUsername() + ", client removed!");
            this.join();
        } catch (IOException ex) {
//            Logger.getLogger(MessageReceptorClient.class.getName()).log(Level.SEVERE, null, ex);
            try {
                server.broadcast("    " + getUsername() + " a quitté la conversation");
                server.deleteClient(getUsername());
                System.out.println("Client disconnected: " + getUsername() + ", client removed!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
