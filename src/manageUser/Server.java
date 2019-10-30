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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    
    public ServerSocket ss;
    public Socket[] socket;
    public int socketnumber = -1;
    public OutputStream[] outputStreamList;
    public ObjectOutputStream[] objectOutputStreamList;
    public OutputStream outputStream;
    public ObjectOutputStream objectOutputStream;
    public static ArrayList<UserData> userData = new ArrayList<>();
    public ArrayList<UserState> listUserState = new ArrayList<>();
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //login information
        userData.add(new UserData("12347162", "Mickey js","a", "1"));
        userData.add(new UserData("12341527", "Rancix ft","b", "1"));
        userData.add(new UserData("66211343", "Aslhycole","c", "1"));
        userData.add(new UserData("42211455", "Michel Owen","d", "1"));
        userData.add(new UserData("55223114", "Frank lampard","e", "1"));
        
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
                             MessagePackage dataClient = (MessagePackage) objectInputStream.readObject();
                             System.out.println("messages:" + dataClient.getDestUid()+"---" + dataClient.getSrcUid());
                             //System.out.println("socket test :   " + dataClient.getSocket());
                             if(dataClient.getHeader()== TypeProtocol.REQUEST_CONNECT)
                             {
                                 System.out.println(dataClient.getHeader());
                                 System.out.println("source : " + dataClient.getSrcUid());
                                 System.out.println("Port : "+dataClient.getPort());
                                 int a = findIndexOfSocket(dataClient.getPort());
                                 System.out.println("find port : "+a);
                                 listUserState.add(new UserState(dataClient.getSrcUid(), findUsernameByID(dataClient.getSrcUid()), "free"));
                                 System.out.println(listUserState.size()+findUsernameByID(dataClient.getSrcUid()));
                             }
                             
                         } 
                         catch (IOException ex) {} catch (ClassNotFoundException ex) { 
                             Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                         } 
                     }
                }
            }).start();   
        }
    }
    
    public String findUsernameByID(String id){
        for(UserData u : userData){
            if(u.userID.equals(id))
                return u.name;
        }
        return "unknow";
    }
    
    public int findIndexOfSocket(int port){
        for(int i=0;i<socketnumber;i++){
            if(socket[i].getPort() == port)
                return i;
        }
        return -1;
    }
    
    public void sendToClient(){

    }
}



