
package manageUser;


import Lib.UserStateDataSend;
import Lib.MessagePackage;
import Lib.TypeProtocol;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Serializable{
    public static Config config = new Config();
    public Socket socket;
    public OutputStream outputStream;
    public InputStream inputStream;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    public static String currentActivity;
    public ArrayList<UserStateDataSend> listUserStateDataSend = new ArrayList<>();
    public boolean updateState = false;
    public static String inform_from_server = new String("p");

    public boolean isUpdateState() {
        return updateState;
    }

    public void setUpdateState(boolean updateState) {
        this.updateState = updateState;
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        //client.connectSocket();
        client.listenFromServer();
        client.sendMessage(new MessagePackage(TypeProtocol.REQUEST_CALL_VIDEO,"saddfsfgf","621873978"));
        client.sendMessage(new MessagePackage(TypeProtocol.ACCEPT_CALL_VIDEO,"forward","forward"));
    }
    public void connectSocket(String srcID,String username,String pw) throws IOException, InterruptedException{
        socket = new Socket(config.urlServer, config.clientControllerSocket);
        System.out.println("Connected!" + socket);
        outputStream = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);
        sendMessage(new MessagePackage(TypeProtocol.REQUEST_CONNECT,"null",srcID,socket.getLocalPort(),username,getMd5(pw)));
    }
    public void signup(String username,String pw) throws IOException, InterruptedException{
        socket = new Socket(config.urlServer, config.clientControllerSocket);
        System.out.println("Connected!" + socket);
        outputStream = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);
        String pw_hash = getMd5(pw);
        sendMessage(new MessagePackage(TypeProtocol.REQUEST_SIGNUP,"null","null",socket.getLocalPort(),username,pw_hash));
    }
    public void sendMessage(MessagePackage m) throws IOException, InterruptedException{
        System.out.println("Sending messages to the ServerSocket");
        objectOutputStream.writeObject(m);
        Thread.sleep(200);
    }
    public void listenFromServer_inform() throws IOException{
        currentActivity = new String();
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        new Thread(new Runnable() {
                @Override
                public void run()
                {
                     while(true){
                         try {
                            inform_from_server = new String(dataInputStream.readUTF());
                            System.out.println("The message sent from the socket was: " + inform_from_server);
                         } 
                         catch (IOException ex) {System.out.println(ex);}
                     }
                }
            }).start();   
    }
    public void listenFromServer() throws IOException{
        currentActivity = new String();
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        new Thread(new Runnable() {
                @Override
                public void run()
                {
                     while(true){
                         try {
                             listUserStateDataSend = (ArrayList<UserStateDataSend>) (List<UserStateDataSend>) objectInputStream.readObject();
                             System.out.println("messages from Server:" + listUserStateDataSend.size());
                             setUpdateState(true);
                             for(UserStateDataSend pp : listUserStateDataSend){
                                 System.out.println(pp.userID+"----"+pp.userName+"----"+pp.state+"----"+pp.desID);
                             }
                         } 
                         catch (IOException ex) {System.out.println(ex);} 
                         catch (ClassNotFoundException ex) {System.out.println(ex);}
                     }
                }
            }).start();   
    }
    public static String getMd5(String input) 
    { 
        try { 
            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5"); 
            // digest() method is called to calculate message digest 
            //  of an input digest() return array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
            return hashtext; 
        }  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    } 
}

