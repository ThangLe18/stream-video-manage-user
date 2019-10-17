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
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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
public class ClientController extends Application{
    public static Socket scc;
    public static String _id;
    public static String _id_call;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void connectServer(String id) throws IOException{
        scc=new Socket("localhost",6666);
        OutputStream outputStream = scc.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        System.out.println("Sending string to the ServerSocket");
        dataOutputStream.writeUTF("con"+id);
        dataOutputStream.flush(); // send the message
        //dataOutputStream.close(); 
        
    }
    
    public static void call(String id) throws IOException{
        scc=new Socket("localhost",6666);
        System.out.println("calling " + id);
        OutputStream outputStream = scc.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        //dataOutputStream.writeUTF("rig"+id+"-"+_id);
        dataOutputStream.writeUTF("rig"+id+"-"+_id);
        dataOutputStream.flush(); // send the message
    }
    
    public static void exit() throws IOException{
        scc=new Socket("localhost",6666);
        System.out.println("Exiting");
        OutputStream outputStream = scc.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        //dataOutputStream.writeUTF("rig"+id+"-"+_id);
        dataOutputStream.writeUTF("Ext");
        dataOutputStream.flush(); // send the message
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Thread playThread = new Thread(new Runnable() {
            public void run() {
                while(true){
                    System.out.println("1.Connect server    2.Call    3.Exit");
                    System.out.println("Your choice : ");
                    Scanner sc = new Scanner(System.in);
                    String command = sc.nextLine(); 

                    switch(command){
                        case "1" : {
                            try {
                                System.out.println("Your client ID : ");
                                Scanner sc2 = new Scanner(System.in);
                                _id = sc2.nextLine(); 
                                connectServer(_id);
                            } catch (IOException ex) {
                                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                        case "2" : {
                            try {
                                System.out.println("Your ID : " + _id);
                                System.out.println("Who do you want to call : ");
                                Scanner sc2 = new Scanner(System.in);
                                _id_call = sc2.nextLine(); 
                                call(_id_call);
                            } catch (IOException ex) {
                                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                        case "3" : {
                            try {
                                exit();
                            } catch (IOException ex) {
                                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                    }
                }
            }
        });
        playThread.start();
        Thread playThread2 = new Thread(new Runnable() {
            public void run() {
                while(true){
                while(true){
                    try {
                        InputStream inputStream = scc.getInputStream();
                        DataInputStream dataInputStream = new DataInputStream(inputStream);
                        String message = dataInputStream.readUTF();
                        System.out.println("The message : " + message);
                        dataInputStream.close();
                    } catch (Exception e) {
                    }
                }
            }
            }
        });
        playThread2.start();
        
        
        
        
        
//        scc=new Socket("localhost",6666)
//        OutputStream outputStream = scc.getOutputStream();
//        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//        System.out.println("Sending string to the ServerSocket");
//        dataOutputStream.writeUTF("5da866bc759bc816704d09cf");
//        dataOutputStream.flush(); // send the message
//        //dataOutputStream.close(); 
//        
//        
//        InputStream inputStream = scc.getInputStream();
//        DataInputStream dataInputStream = new DataInputStream(inputStream);
//        while(true){
//            String message = dataInputStream.readUTF();
//            System.out.println("The message : " + message);
//        }
        
        
        

    }

    
       
}
