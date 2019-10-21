package manageUser;

import Test.*;
import static Test.ServerTest.checkActive;
import static Test.ServerTest.ssc_ctrl;
import static Test.ServerTest.sscontroll;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    public ServerSocket ss;
    public Socket[] socket;
    public OutputStream outputStream;
    public ObjectOutputStream objectOutputStream;
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Server server = new Server();
        server.createServerSocket();
        server.listenFromClient();
        server.sendToClient();
    }
    
    
    public void createServerSocket() throws IOException{
        ss = new ServerSocket(7777);
        System.out.println("Created socket!");
    }
    
    
    public void listenFromClient() throws IOException{
        socket = new Socket[100];
        int socketnumber = -1;
        while(true){
            socketnumber++;
            System.out.println("ServerSocket awaiting connections...");
            socket[socketnumber] = ss.accept(); 
            System.out.println("\nConnection from " + socket + "!");
            InputStream inputStream = socket[socketnumber].getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            OutputStream outputStream = socket[socketnumber].getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                     while(true){
                         try {
                             MessagePackage m = (MessagePackage) objectInputStream.readObject();
                             System.out.println("messages:" + m.getDestUid()+"---" + m.getSrcUid());
                             if(m.getSrcUid().equals("forward")) {
                                 for(int h=0;h<19;h++){
                                     System.out.println("Send again to client");
                                     objectOutputStream.writeObject(new MessagePackage(TypeProtocol.CALLING_VIDEO,"IamServer","IamServer"));
                                     Thread.sleep(2000);
                                 }
                                 }
                         } 
                         catch (IOException ex) {} 
                         catch (ClassNotFoundException ex) {} catch (InterruptedException ex) {
                         }
                     }
                }
            }).start();   
        }
    }
    
    
    public void sendToClient(){

    }
}



