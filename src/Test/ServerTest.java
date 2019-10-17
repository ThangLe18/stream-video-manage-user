/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author ThayLe
 */
public class ServerTest {
    public static ServerSocket ss;
    public static ServerSocket sscontroll;
    public static ArrayList<Socket> ssc_ctrl = new ArrayList<>();
    public static ArrayList<User> userList = new ArrayList<>();
    
    public static void getUserlist(){
        try {
                    MongoClient mongoClient = new MongoClient("localhost",27017);
                    DB db = mongoClient.getDB("res");
                    System.out.println("Connected to database");
                    DBCollection coll = db.getCollection("lib");
                    DBCursor cursor = coll.find();
                    while(cursor.hasNext()){
                        String _id = cursor.next().toString().substring(18, 42);
                        User u = new User(_id, false, 1111);
                        userList.add(u);
                    }
                    for(User m:userList) System.out.println("userID : " + m._id + "---" + m.active+ "----" + m.portClient);
                } catch (Exception e) {
                    System.out.println("can not connect to database");
                }
    }
    
    //enable - disable Active
    public static void checkActive(String id){
        int i = -1;
        for(User m : userList){
            i++;
            if(m._id.toString().equals(id)) {
                userList.get(i).active = !userList.get(i).active;
            }
        }
        for(User m:userList) System.out.println("userID : " + m._id + "---" + m.active + "----" + m.portClient);
    }
    
    
    public static void sendMessageToClient(String Mess){
        
    }
    
    public static void main(String[] args) {
        getUserlist();
        //checkActive("5da83b4a759bc816704d09cc");
        
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                Socket scc=null;
                Socket scc1=null;
                OutputStream os1=null,os2=null;
                InputStream is1=null,is2=null;
//                try {
//                    ss=new ServerSocket(7777);
//                    System.out.println("Waiting for first client...");
//                    scc=ss.accept();
//                    System.out.println("Client 1 is just connected! waiting for client 2...");
//                    scc1=ss.accept();
//                    System.out.println("Client 2 is just connected! Streaming...");
//                    os1=scc.getOutputStream();
//                    os2=scc1.getOutputStream();
//                    is1=scc.getInputStream();
//                    is2=scc1.getInputStream();
//                    os1.write(100);os1.flush();
//                    os2.write(100);os2.flush();
//                    System.out.println(scc1);
//
//                    System.out.println("done");
//
//                    VideoForwardPacket v1=new VideoForwardPacket(os2, is1, 1);  //pair two client
//                    //VideoForwardPacket v2=new VideoForwardPacket(os1, is2, 1);
//                    Thread t1=new Thread(v1);
//                    //Thread t2=new Thread(v2);
//                    t1.start();
//                    //t2.start();
//                } catch (IOException ex) {
//                    Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
//                }
                      }
                
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    sscontroll=new ServerSocket(6666);
                    System.out.println("ServerSocket controll created");
                    while(true){
                        Socket p = null;
                        p = sscontroll.accept();
                        ssc_ctrl.add(p);
                        System.out.println("Active clients nomber : " + ssc_ctrl.size());
                        
                        System.out.println("");
                        InputStream inputStream = p.getInputStream();
                        DataInputStream dataInputStream = new DataInputStream(inputStream);
                        String message = dataInputStream.readUTF();
                        System.out.println("Client Actions : " + message);
                       
                        if(message.toString().substring(0, 3).equals("con")){
                            checkActive(message.toString().substring(3, message.length()));  
                        }
                        if(message.toString().substring(0, 3).equals("rig")){
                            OutputStream outputStream = p.getOutputStream();
                            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                            System.out.println("Sending string to the Client");
                            dataOutputStream.writeUTF(message + " from server");
                            dataOutputStream.flush(); // send the message
                        }
                            

                        
                        
                        
                        OutputStream outputStream = p.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        System.out.println("Sending string to the Client");
                        dataOutputStream.writeUTF("Hello from Server");
                        dataOutputStream.flush(); // send the message
//                        
//                        Scanner sc = new Scanner(System.in);
//                        String i = sc.nextLine();
//                        
//                        dataOutputStream.writeUTF(i);
//                        dataOutputStream.flush();
                    }
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
               
               
            }
        }).start();
        
        
        
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                //getUserlist();
            }
        }).start();
    }
}
