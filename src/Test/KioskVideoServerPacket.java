/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kiosk01
 */
public class KioskVideoServerPacket {
    public static void main(String[] argv)
    {
        ServerSocket ss=null;
        Socket scc=null;
        Socket scc1=null;
        OutputStream os1=null,os2=null;
        InputStream is1=null,is2=null;
        try {
            ss=new ServerSocket(7777);
            System.out.println("Waiting for first client...");
            scc=ss.accept();
            System.out.println("client socket : " + scc);
            System.out.println("Client 1 is just connected! waiting for client 2...");
            scc1=ss.accept();
            System.out.println("Client 2 is just connected! Streaming...");
            os1=scc.getOutputStream();
            os2=scc1.getOutputStream();
            is1=scc.getInputStream();
            is2=scc1.getInputStream();
            os1.write(100);os1.flush();
            os2.write(100);os2.flush();
            System.out.println(scc1);
            
            System.out.println("done");
            
            VideoForwardPacket v1=new VideoForwardPacket(os2, is1, 1);  //pair two client
            //VideoForwardPacket v2=new VideoForwardPacket(os1, is2, 1);
            Thread t1=new Thread(v1);
            //Thread t2=new Thread(v2);
            t1.start();
            //t2.start();
        } catch (IOException ex) {
            Logger.getLogger(VideoServer133.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
