/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transferapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Salman
 */
public class Multithread {
      static String  ip = "";       // enter public ip or localhost
       public  int Start_Port ;
       public static Socket s;
   public  ObjectOutputStream dout;
   int thread_count;
   int temp=1;
   int pointer=0;

    String filename = "";          //enter file name only (without ext)
    String extension = "";             //enter file extension only (without file name)
    String filepath = "";
    static byte[] bFile ;
   
    public void setParams(int threads,String filename, String extension, String filepath){
        
        
        this.thread_count=threads;
        this.filename=filename;
        this.extension=extension;
        this.filepath=filepath;
        
    }
    
    public int connet(String ip,int port) {
          try { 
              Multithread.ip = ip;
            this.Start_Port=port;
              this.s=new Socket(Multithread.ip,this.Start_Port);
              DataInputStream d2=new DataInputStream(s.getInputStream());
        int codenum=d2.readInt();
        if(codenum==0){
            return 0;
        }
        else{
              return 1;
          }
              
          } catch (IOException ex) {
              
              Logger.getLogger(Multithread.class.getName()).log(Level.SEVERE, null, ex);
              return 1;
          }
    }
    public boolean sendFile() throws IOException{
        System.out.println("filepath:"+filepath);
        bFile = readBytesFromFile(filepath);
        
        DataOutputStream dout= new DataOutputStream(s.getOutputStream());
     ArrayList<threading> thd = new ArrayList<threading>();
       // int num;
    //    System.out.println("Sending to Server: "+filename+"."+extension);
    //    System.out.println("Number of thread to transfer file \n* Use 1 - 7 threads on public ip due to limit port forward\n* Use 1 - "+bFile.length+" threads on local host");
        
 // num = sc.nextInt();
 //  Multithread m1 = new  Multithread();
 // m1.thread_count=num;
  dout.writeInt(thread_count);
  dout.writeUTF(filename);
    dout.writeUTF(extension);
  dout.writeLong(bFile.length);
  
 
      
  for(int i=0;i<thread_count;i++){
       int port = Start_Port+i+1;
       threading plr = new  threading ("Thread-"+(i+1),distributeLoad(), port,(i+1)); // assuming you have a default constructor
        
        thd.add(plr);
      
  }
//  thd.forEach((a)->{
//      
//      
//      a.start();
//      a->a.join();
//              
//              });
//  }

      //  System.out.println("Sending....");
        
for(int i=0;i<thread_count;i++){
    thd.get(i).start();
  
    
}

 for(int i=0;i<thread_count;i++){
        {
            try {
                thd.get(i).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Multithread.class.getName()).log(Level.SEVERE, null, ex);
                s.close();
                return false; }   
        }
 
 } 
  s.close();
 return true;

    }
    
    
     public  byte[] distributeLoad(){
        
        int partition = bFile.length/thread_count;
                
        if(temp!=thread_count){
           //    System.out.println("pointer at "+temp+":"+pointer);
            byte[] bt = new byte[partition];
            int y=0;
            int pt=0;
            int range = pointer+partition;
            for(int x=pointer;x<range;x++){
                bt[y]=bFile[x];
                y++;
                pt=x;
            }
        //      System.out.println(y);
            pointer=pt+1;
           //   System.out.println("pointer at end"+pointer);
            temp++;
            return bt;
            
        }
        else{
        //    System.out.println("pointer:"+pointer);
             int range = pointer+partition;
            byte[] bt = new byte[bFile.length-pointer];
            int y=0;
            for(int x=pointer;x<bFile.length;x++){
                bt[y]=bFile[x];
                y++;
            }
        //    System.out.println(y);
            return bt; 
        
        }
        
    }
    
     private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }
}
