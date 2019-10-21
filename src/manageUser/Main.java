/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manageUser;

import java.io.IOException;

/**
 *
 * @author ThayLe
 */
public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Server server = new Server();
        server.createServerSocket();
        server.listenFromClient();
        
        Client client = new Client();
        client.connectSocket();
        client.sendMessage(new Message("621873978"));
        client.sendMessage(new Message("asfdsfadf"));
    }
}
