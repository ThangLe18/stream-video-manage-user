
package Test;

import static Test.ClientController.outputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Client1 {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 7777);
        System.out.println("Connected!");
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Hello from the other side!"));
        messages.add(new Message("How are you doing?"));
        messages.add(new Message("What time is it?"));
        messages.add(new Message("Hi hi hi hi."));

        System.out.println("Sending messages to the ServerSocket");
        objectOutputStream.writeObject(new Message("Hello from Client - 1234"));
        objectOutputStream.writeObject(new Message("Hello from Client - 5678"));
        Thread.sleep(2000);
        objectOutputStream.writeObject(new Message("Hello from Client - abcd"));
        objectOutputStream.writeObject(new Message("Hello from Client - xywt"));
        System.out.println("Closing socket and terminating program.");
        socket.close();
    }
}

