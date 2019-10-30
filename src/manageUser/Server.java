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
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server implements Serializable{
    
    public ServerSocket ss;
    public Socket[] socket;
    public int socketnumber = -1;
    public OutputStream[] outputStreamList;
    public ObjectOutputStream[] objectOutputStreamList;
    public OutputStream outputStream;
    public ObjectOutputStream objectOutputStream;
    public static ArrayList<UserData> userData = new ArrayList<>();
    public ArrayList<UserState> listUserState = new ArrayList<>();
    public ArrayList<UserStateDataSend> listUserState2 = new ArrayList<>();
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
                                 listUserState.add(new UserState(dataClient.getSrcUid(), 
                                         findUsernameByID(dataClient.getSrcUid()),
                                         socket[a].getInputStream(),
                                         socket[a].getOutputStream(),
                                         null,null,"free",null,
                                         new ObjectOutputStream(socket[a].getOutputStream())
                                 ));
                                 listUserState2.add(new UserStateDataSend(dataClient.getSrcUid(), 
                                         findUsernameByID(dataClient.getSrcUid()),"free",null
                                 ));
                                 System.out.println("listuserstate2 : " + listUserState2.size());
                                 sendStateToClient();
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
    
    public void sendStateToClient() throws IOException{
           for(UserState u : listUserState)
           {
               u.ctr_oos.flush();
               u.ctr_oos.reset();
               System.out.println("send state to client listUserState2 : "+listUserState2.size());
               u.ctr_oos.writeObject(listUserState2);
           }
        }
    
}



