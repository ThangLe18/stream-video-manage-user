package Test;

import static Test.ServerTest.checkActive;
import static Test.ServerTest.ssc_ctrl;
import static Test.ServerTest.sscontroll;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server1 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(7777);
        Socket[] socket = new Socket[100];
        int socketnumber = -1;
        while(true){
            socketnumber++;
            System.out.println("ServerSocket awaiting connections...");
            socket[socketnumber] = ss.accept(); 
            System.out.println("\nConnection from " + socket + "!");

            InputStream inputStream = socket[socketnumber].getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            
                 
            
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                     while(true){
                         try {
                             Message listOfMessages = (Message) objectInputStream.readObject();
                             System.out.println("messages:" + listOfMessages.getText()); 
                         } catch (IOException ex) {
                             //Logger.getLogger(Server1.class.getName()).log(Level.SEVERE, null, ex);
                         } catch (ClassNotFoundException ex) {
                             //Logger.getLogger(Server1.class.getName()).log(Level.SEVERE, null, ex);
                         }
                     }
                }
            }).start();

    }}
}



