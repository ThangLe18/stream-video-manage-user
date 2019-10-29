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
    public OutputStream[] outputStreamList;
    public ObjectOutputStream[] objectOutputStreamList;
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
        outputStreamList = new OutputStream[100];
        objectOutputStreamList = new ObjectOutputStream[100];
        socket = new Socket[100];
        int socketnumber = -1;
        Socket p = null;
        while(true){
            socketnumber++;
            System.out.println("ServerSocket awaiting connections...");
            socket[socketnumber] = ss.accept();
            System.out.println("\nConnection from " + socket[socketnumber] + "!");
            InputStream inputStream = socket[socketnumber].getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);


            if(socketnumber > -1){
                System.out.println("sending to client");
                outputStreamList[socketnumber] = socket[socketnumber].getOutputStream();
                objectOutputStreamList[socketnumber] = new ObjectOutputStream(outputStreamList[socketnumber]);
                objectOutputStreamList[socketnumber].writeObject(new MessagePackage(TypeProtocol.CALLING_VIDEO,"=0","IamServer"));
            }
          
            objectOutputStreamList[0].writeObject(new MessagePackage(TypeProtocol.CALLING_VIDEO,"test","IamServer"));
            

            
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                     while(true){
                         try {
                             MessagePackage m = (MessagePackage) objectInputStream.readObject();
                             System.out.println("messages:" + m.getDestUid()+"---" + m.getSrcUid());
//                             if(m.getSrcUid().equals("forward")) {
//                                 for(int h=0;h<5;h++){
//                                     System.out.println("Send again to client");
//                                     objectOutputStream.writeObject(new MessagePackage(TypeProtocol.CALLING_VIDEO,"IamServer","IamServer"));
//                                     Thread.sleep(2000);
//                                 }
//                                 }
                         } 
                         catch (IOException ex) {} catch (ClassNotFoundException ex) { 
                             Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                         } 
                     }
                }
            }).start();   
        }
    }
    
    
    public void sendToClient(){

    }
}



