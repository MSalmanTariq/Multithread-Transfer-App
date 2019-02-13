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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import static transferapp.multireciever.count;
import static transferapp.multireciever.destPath;
import static transferapp.multireciever.extension;
import static transferapp.multireciever.fileSize;
import static transferapp.multireciever.filename;
import static transferapp.multireciever.myBytes;
import static transferapp.multireciever.obj1;
import static transferapp.multireciever.threads;
import static transferapp.multireciever.timer;

/**
 *
 * @author Salman
 */
public class multireciever {
     public static int Start_Port = 6000;
    static ArrayList<threadingRecv> thd = new ArrayList<threadingRecv>();
   

   static  Vector<byte[]>  myBytes = new Vector<byte[]>();
  
   static int count;
   static int threads;
   static Timer timer;
   static long fileSize;
   static String filename=null;
   static reciever obj1;
   static String destPath="";
    public ServerSocket ss;
    public Socket s;

   static String extension = null;
   public void connect(int port, reciever r1 , String path ) throws IOException, InterruptedException{
   ss = new ServerSocket(port);
            //    System.out.println("Waiting for client to connect...");
            obj1=r1;
            destPath=path;
                s=ss.accept();
                 InetAddress addr = s.getInetAddress();
               
                  obj1.getConnectionData(addr.getHostAddress(), addr.getHostName());
                System.out.println("Connected with client");
                 DataOutputStream d1= new DataOutputStream(s.getOutputStream());
        d1.writeInt(0);
                recieve();
   }
   public void recieve() throws IOException, InterruptedException{
       
                
                
          
       
        DataInputStream diss=new DataInputStream(s.getInputStream());
        int thread_num=diss.readInt();
        filename=diss.readUTF();
        extension=diss.readUTF();
        fileSize=diss.readLong();
        threads=thread_num;
        System.out.println("Creating "+thread_num+" threads");
        obj1.getFileData(filename, extension, fileSize, threads);
        createThread(thread_num);
        for(int i=0;i<thread_num;i++){
    byte[] arr = {1};
    myBytes.add(arr);
      }
//TimerTask tasknew = new MyTimerTask();
      // timer = new Timer();
      
    //  timer.scheduleAtFixedRate(tasknew,500,1000);      
   
   // this method performs the task
      
        System.out.println("Recieving "+filename+" ...");
      for(int i=0;i<thread_num;i++){
    thd.get(i).start();
      }
      for(int i=0;i<thread_num;i++)
            {
                thd.get(i).join(); 
            } 
  s.close();
  ss.close();
   }
   
     public static void createThread(int thread_num){
      
         for(int i=0;i<thread_num;i++){
      int port = Start_Port+i+1;
          
       threadingRecv plr = new  threadingRecv (port,"Thread-"+(i+1)); // assuming you have a default constructor
        
        thd.add(plr);
      
      
  }
       
        
    }
     public void combine(){
          obj1.log("Combining ...");
           byte[] file = new byte[(int)fileSize];
    ByteBuffer target = ByteBuffer.wrap(file);
    for(int i=0;i<threads;i++){
         byte[] bFile = readBytesFromFile(destPath+"\\"+(i+1)+"."+extension);
        target.put(bFile);  
        
      
        
    }
     
    
     Path path = Paths.get(destPath+"\\"+filename);
      System.out.println("100% transfer completed!\n"+filename+" transfered to your computer");
       obj1.updateBar(100);
        obj1.log("Done");
        obj1.done();
          try {
              Files.write(path, file);
              for(int i=0;i<threads;i++){
         File file1 = new File(destPath+"\\"+(i+1)+"."+extension);
         file1.delete();
       
      
        
    }
             timer.cancel();
          } catch (IOException ex) {
              Logger.getLogger(MyTimerTask.class.getName()).log(Level.SEVERE, null, ex);
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


  class threadingRecv extends Thread {
            
        public int port;
        Thread t;
        String threadName;
        threadingRecv(int port,String threadName){
             
            this.port=port;
            this.threadName=threadName;
            
            
        }
        
        @Override
        public void run(){
            try {
                ServerSocket ss;
           //     System.out.println("IN "+this.threadName);
                ss = new ServerSocket(port);
                Socket s=ss.accept();
          //      System.out.println(this.threadName +" connection accepted");
              //  System.out.println("Recieved Data from :"+threadName);
                DataInputStream diss=new DataInputStream(s.getInputStream());
                int dataID=diss.readInt();
                int dataLength=diss.readInt();
                System.out.println("length:"+dataLength);
                byte[] message = null;
                if(dataLength>0) {
                  message = new byte[dataLength];
                  diss.readFully(message, 0, message.length);
                }
                int pos = (int)dataID-1;
               
          
                
          
            
           //     System.out.println("Data ID: "+dataID+"  Message  "+Arrays.toString(message));
                    Path path = Paths.get(destPath+"\\"+dataID+"."+extension);
            Files.write(path, message);
                diss.close();
                s.close();
                ss.close();
                    float perc = (count*100)/threads;
                    obj1.updateBar((int) perc);
        System.out.println(perc+"% transfer completed!");
                 count++;
                 if(count==threads){
                 new multireciever().combine();
                 }
            } catch (IOException ex) {
               
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
    
    class MyTimerTask extends TimerTask {
  public void run() {
     
      if(count==threads){
       
          
          
      }
      else{
       
      }
    
  }
  
  
    }
     class AddData{
     
   
    AddData(){
        
    }
     public synchronized void add(int index, byte[] data){
        myBytes.add(index,data);
     }
  }
