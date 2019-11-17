
package manageUser;


import Lib.UserStateDataSend;
import Lib.MessagePackage;
import Lib.TypeProtocol;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Serializable{
    public Socket socket;
    public OutputStream outputStream;
    public InputStream inputStream;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    public static String currentActivity;
    public ArrayList<UserStateDataSend> listUserStateDataSend = new ArrayList<>();
    public boolean updateState = false;

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
    public void connectSocket(String srcID) throws IOException, InterruptedException{
        socket = new Socket("localhost", 7777);
        System.out.println("Connected!" + socket);
        outputStream = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);
        sendMessage(new MessagePackage(TypeProtocol.REQUEST_CONNECT,"null",srcID,socket.getLocalPort()));
    }
    public void sendMessage(MessagePackage m) throws IOException, InterruptedException{
        System.out.println("Sending messages to the ServerSocket");
        objectOutputStream.writeObject(m);
        Thread.sleep(200);
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
}

