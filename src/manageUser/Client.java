
package manageUser;

import static Test.ClientController.outputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    public Socket socket;
    public OutputStream outputStream;
    public InputStream inputStream;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.connectSocket();
        client.listenFromServer();
        client.sendMessage(new MessagePackage(TypeProtocol.REQUEST_CALL_VIDEO,"saddfsfgf","621873978"));
        client.sendMessage(new MessagePackage(TypeProtocol.ACCEPT_CALL_VIDEO,"forward","forward"));
    }
    public void connectSocket() throws IOException, InterruptedException{
        socket = new Socket("localhost", 7777);
        System.out.println("Connected!");
        outputStream = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);
        
    }
    public void sendMessage(MessagePackage m) throws IOException, InterruptedException{
        System.out.println("Sending messages to the ServerSocket");
        objectOutputStream.writeObject(m);
        Thread.sleep(200);
    }
    public void listenFromServer() throws IOException{
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        new Thread(new Runnable() {
                @Override
                public void run()
                {
                     while(true){
                         try {
                             System.out.println(".");
                             MessagePackage m = (MessagePackage) objectInputStream.readObject();
                             System.out.println("messages from Server:" + m.getDestUid()+"---" + m.getSrcUid());
                         } 
                         catch (IOException ex) {System.out.println(ex);} 
                         catch (ClassNotFoundException ex) {System.out.println(ex);}
                     }
                }
            }).start();   
    }
}

