/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transferapp;

/**
 *
 * @author Salman
 */
public class localip {
    
    localip(){
        
        
    }
    
    public void getLocalIp(){
        
           try{
         
           Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"ipconfig /all ");
 
        }
    
        catch (Exception e)
        {
            System.out.println("HEY Buddy ! U r Doing Something Wrong ");
            e.printStackTrace();
        }
       
     
    }
}
