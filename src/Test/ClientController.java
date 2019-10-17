/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 *
 * @author ThayLe
 */
public class ClientController {
    public static void main(String[] args) throws IOException {
        Socket scc=new Socket("localhost",6666);
        
        
        OutputStream outputStream = scc.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        System.out.println("Sending string to the ServerSocket");
        dataOutputStream.writeUTF("Hello from the other side!");
        dataOutputStream.flush(); // send the message
        //dataOutputStream.close(); 
        
        
        InputStream inputStream = scc.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        while(true){
            String message = dataInputStream.readUTF();
            System.out.println("The message : " + message);
        }
        

    }
}
