/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transferapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transferapp.Multithread.ip;

/**
 *
 * @author Salman
 */

    public class threading extends Thread {
     private Thread t;
   private String threadName;
   private byte[] data;
   private int id;
   private int port;
  
  
  
        
   threading( String name,byte[] dt,int port,int id)  {
      threadName = name;
      data=dt;
   this.port=port;
      this.id=id;
      
      
       
     // System.out.println("Creating " +  threadName );
      
   }

    
   
     @Override
     public void run() {
    
    // Path path = Paths.get("D:/demo/"+threadName+".txt");
         try {
            
      Socket s=new Socket(ip,port);  
        DataOutputStream dout= new DataOutputStream(s.getOutputStream());
        dout.writeInt(id);
             System.out.println("port"+port+"  length:"+data.length);
         dout.writeInt(data.length);
          dout.write(data);
          dout.close();
          s.close();

           
        } catch (IOException ex) {
             Logger.getLogger(threading.class.getName()).log(Level.SEVERE, null, ex);
         } 
           
 
   }
   
     @Override
   public void start () {
    //  System.out.println("Starting " +  threadName );
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
   
}


