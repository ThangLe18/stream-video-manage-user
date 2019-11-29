package manageUser;


import Lib.UserData;
import Lib.UserState;
import Lib.UserStateDataSend;
import Lib.MessagePackage;
import Lib.TypeProtocol;
import ServerVideo.*;
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
    public static ServerSocket ss_video;
    public static Socket[] socket,socketVideo;
    public static int socketnumber = -1;
    public static int socketnumberVideo = -1;
    public OutputStream[] outputStreamList;
    public ObjectOutputStream[] objectOutputStreamList;
    public OutputStream outputStream;
    public ObjectOutputStream objectOutputStream;
    public static ArrayList<UserData> userData = new ArrayList<>();
    public ArrayList<UserState> listUserState = new ArrayList<>();
    public ArrayList<UserStateDataSend> listUserState2 = new ArrayList<>();
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        AccessDatabase accessDatabase = new AccessDatabase();
        accessDatabase.getListUserFromDB();
        //login information
        userData.add(new UserData("12347162", "Mickey Jr","a", "12345678"));
        userData.add(new UserData("12341527", "Rancix Sr","s", "12345678"));
        userData.add(new UserData("66211343", "Aslhycole","d", "12345678"));
        userData.add(new UserData("42211455", "Micl Owen","q", "12345678"));
        userData.add(new UserData("55223114", "FrLampard","w", "12345678"));
        
        new Thread(new Runnable() {
                @Override
                public void run(){
                    try {
                        startVideoServer();
                    } catch (IOException ex) {}
                }
            }).start();  
        
        Server server = new Server();
        server.createServerSocket();
        server.listenFromClient();
    }
    
    
    public void createServerSocket() throws IOException{
        ss = new ServerSocket(7777);
        System.out.println("Created Controller socket!");
    }
    
    
    public void listenFromClient() throws IOException{
        outputStreamList = new OutputStream[20];
        objectOutputStreamList = new ObjectOutputStream[20];
        socket = new Socket[20];
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
                             
                             
                             if(dataClient.getHeader()== TypeProtocol.REQUEST_CONNECT){
                                 System.out.println(dataClient.getHeader());
                                 System.out.println("username-password : "+ dataClient.getUsername()+"-"+dataClient.getPassword());
                                 for(UserData ud : userData){
                                     if(ud.userName.equals(dataClient.getUsername()) && ud.userPassword.equals(dataClient.getPassword())){
                                         System.out.println("Name auth : "+ud.name);
                                         dataClient.setSrcUid(ud.userID);
                                     }
                                 }
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
                                         findUsernameByID(dataClient.getSrcUid()),"free",null,dataClient.getUsername()
                                 ));
                                 System.out.println("listuserstate2 : " + listUserState2.size());
                                 sendStateToClient();
                             }
                             
                             
                             if(dataClient.getHeader()== TypeProtocol.REQUEST_LOGOUT){
                                 int a = findIndexOfUserByUserID(dataClient.getSrcUid());
                                 listUserState.remove(a);
                                 listUserState2.remove(a);
                                 sendStateToClient();
                                 System.out.println("logout : "+a);
                             }
                             
                             
                             if(dataClient.getHeader()== TypeProtocol.REQUEST_CALL_VIDEO){
                                 int a = findIndexOfUserByUserID(dataClient.getSrcUid());
                                 int b = findIndexOfUserByUserID(dataClient.getDestUid());
                                 //listUserState.get(a)
                                 listUserState2.get(a).setState("iscalling");
                                 listUserState2.get(a).setDesID(dataClient.getDestUid());
                                 listUserState2.get(b).setState("iscalled");
                                 listUserState2.get(b).setDesID("null");
                                 listUserState.get(a).setState("iscalling");
                                 listUserState.get(a).setDesID(dataClient.getDestUid());
                                 listUserState.get(b).setState("iscalled");
                                 listUserState.get(b).setDesID("null");
                                 sendStateToClient();
                             }
                             
                             
                             if(dataClient.getHeader()== TypeProtocol.REJECT_CALL_VIDEO){
                                 int a = findIndexOfUserByUserID(dataClient.getSrcUid());
                                 int b = findIndexOfUserByUserID(dataClient.getDestUid());
                                 //listUserState.get(a)
                                 listUserState2.get(a).setState("free");
                                 listUserState2.get(a).setDesID("null");
                                 listUserState2.get(b).setState("free");
                                 listUserState2.get(b).setDesID("null");
                                 listUserState.get(a).setState("free");
                                 listUserState.get(a).setDesID("null");
                                 listUserState.get(b).setState("free");
                                 listUserState.get(b).setDesID("null");
                                 sendStateToClient();
                             }
                             
                             
                             if(dataClient.getHeader()== TypeProtocol.END_CALL_VIDEO){
                                 int a = findIndexOfUserByUserID(dataClient.getSrcUid());
                                 int b = findIndexOfUserByUserID(dataClient.getDestUid());
                                 //listUserState.get(a)
                                 listUserState2.get(a).setState("free");
                                 listUserState2.get(a).setDesID("null");
                                 listUserState2.get(b).setState("free");
                                 listUserState2.get(b).setDesID("null");
                                 listUserState.get(a).setState("free");
                                 listUserState.get(a).setDesID("null");
                                 listUserState.get(b).setState("free");
                                 listUserState.get(b).setDesID("null");
                                 sendStateToClient();
                             }
                             
                             
                             if(dataClient.getHeader()== TypeProtocol.ACCEPT_CALL_VIDEO){
                                 int a = findIndexOfUserByUserID(dataClient.getSrcUid());
                                 int b = findIndexOfUserByUserID(dataClient.getDestUid());
                                 //listUserState.get(a)
                                 listUserState2.get(a).setState("incalling");
                                 listUserState2.get(a).setDesID(dataClient.getDestUid());
                                 listUserState2.get(b).setState("incalling");
                                 listUserState2.get(b).setDesID(dataClient.getSrcUid());
                                 listUserState.get(a).setState("incalling");
                                 listUserState.get(a).setDesID(dataClient.getDestUid());
                                 listUserState.get(b).setState("incalling");
                                 listUserState.get(b).setDesID(dataClient.getSrcUid());
                                 sendStateToClient();
                                 
                                 System.out.println("a="+a);
                                 System.out.println("b="+b);
                                 //pair client
                                 VideoForwardPacket v1=new VideoForwardPacket(
                                        socketVideo[a].getOutputStream(), 
                                        socketVideo[b].getInputStream(), 
                                        1);  
//                                 VideoForwardPacket v2=new VideoForwardPacket(
//                                        socketVideo[b].getOutputStream(), 
//                                        socketVideo[a].getInputStream(), 
//                                        1);
                                 Thread t1=new Thread(v1);
                                 //Thread t2=new Thread(v2);
                                 t1.start();
                                 //t2.start();
                             }
                             
                             
                             if(dataClient.getHeader()== TypeProtocol.SAVE_SOCKET_VIDEO){
                                 int a = findIndexOfSocketVideo(dataClient.getPort());
                                 System.out.println("findIndexOfSocketVideo : "+a);
                                 int m = findIndexOfUserByUserID(dataClient.getSrcUid());
                                 listUserState.get(m).setVideo_is(socketVideo[a].getInputStream());
                                 listUserState.get(m).setVideo_os(socketVideo[a].getOutputStream());
                                 
                                 for(UserState us : listUserState){
                                     System.out.println("this port : "+ us.getVideo_os());
                                 }
                                 
                                
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
    
    public int findIndexOfUserByUserID(String id){
        for(int i=0;i<listUserState2.size();i++){
            if(listUserState2.get(i).userID.equals(id))
                return i;
        }
        return -1;
    }
    
    public int findIndexOfSocket(int port){
        for(int i=0;i<socketnumber;i++){
            if(socket[i].getPort() == port)
                return i;
        }
        return -1;
    }
    
    public int findIndexOfSocketVideo(int port){
        for(int i=0;i<socketnumberVideo;i++){
            if(socketVideo[i].getPort() == port)
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
    
    public static void startVideoServer() throws IOException{

        ss_video=new ServerSocket(5555);
        socketVideo = new Socket[20];
        ServerSocket ss=null;
        socketnumberVideo = -1;
        try {
            while(true){
                
                
                socketnumberVideo++;
                System.out.println("Waiting for a client...");
                socketVideo[socketnumberVideo] = ss_video.accept();
                System.out.println("New client socket : " + socketVideo[socketnumberVideo]);
    
            }
        } catch (IOException ex) {}
    }
}



