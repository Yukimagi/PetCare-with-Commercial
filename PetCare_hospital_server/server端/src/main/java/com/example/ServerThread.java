package com.example;

import java.net.*;
import java.io.*;
import java.util.*;

class ServerThread extends Thread implements Runnable {
  private Socket socket;
  private Hashtable ht;
  private ShowDbChanges showDbChanges;

  public ServerThread(Socket socket, Hashtable ht,ShowDbChanges showDbChanges) {
     this.socket = socket;
     this.ht = ht;
     this.showDbChanges = showDbChanges;
  }

  public void callout(String returnMessage){
     synchronized (ht) {
          DataOutputStream outstream = (DataOutputStream)ht.get(this.socket);
          try {
               outstream.writeUTF(returnMessage);
          } catch (IOException ex) {
               ex.printStackTrace();
          }
     }
  }

  public void run() {
     DataInputStream instream;
     try {
          instream = new DataInputStream(socket.getInputStream());
			
          while (true) {
               String message = instream.readUTF();
               System.out.println("Message: " + message);
               try{
                    if(message.split(":")[0].equals("rsvreq")){
                         final String info = message.split(":")[1];
                         showDbChanges.check("rsv",info.split(";")[1],info.split(";")[2],info.split(";")[0], new ShowDbChanges.ValueEventListenerCallback() {
                              @Override
                              public void onDataChange(String value) {
                                   if(value.equals("y")){
                                        callout("FAIL;duplicate");
                                   }else if(value.equals("yf")){
                                        callout("FAIL;full");
                                   }else{
                                        showDbChanges.setting("rsv",info.split(";")[1], info.split(";")[2] ,info.split(";")[0],"name",info.split(";")[3]);
                                        showDbChanges.setting("rsv",info.split(";")[1], info.split(";")[2] ,info.split(";")[0],"comment",info.split(";")[4]);
                                        callout("SUCCESS");
                                   }
                              }
                              @Override
                              public void onCancelled(String errorMessage) {
                                   System.out.println(errorMessage);
                              }
                         });
                    }else if(message.split(":")[0].equals("health")){//health:10001*1,231;*0,8;
                         String info = message.split(":")[1];//10001*1,231;1,231;*0,8;
                         String name = info.split("\\*")[0];
                         String meds = info.split("\\*")[1];
                         String weight = info.split("\\*")[2];
                         showDbChanges.setting("health",name,"");
                         String[] temp = meds.split(";");
                         for(String i : temp){
                              showDbChanges.setting("health",name,"medicine",i.split(",")[0],i.split(",")[1]);
                         }
                         temp = weight.split(";");
                         for(String i : temp){
                              showDbChanges.setting("health",name,"weight",i.split(",")[0],i.split(",")[1]);
                         }
                         callout("SUCCESS");
                         System.out.println("DONE");
                    }else if(message.split(":")[0].equals("rsvdel")){//health:10001*1,231;*0,8;
                         final String info = message.split(":")[1];
                         showDbChanges.check("rsv",info.split(";")[1],info.split(";")[2],info.split(";")[0], new ShowDbChanges.ValueEventListenerCallback() {
                              @Override
                              public void onDataChange(String value) {
                                   if(value.equals("y")){
                                        showDbChanges.deleteChild("rsv",info.split(";")[1], info.split(";")[2] ,info.split(";")[0]);
                                        callout("SUCCESS");
                                   }else{
                                        callout("FAIL");
                                   }
                              }
                              @Override
                              public void onCancelled(String errorMessage) {
                                   System.out.println(errorMessage);
                              }
                         });
                    
                    }else{
                         callout("ERROR");
                    }
               }catch(Exception exc){
                    callout("ERROR");
                    exc.printStackTrace();
                    System.out.println(exc);
               }
          //     synchronized(ht) {
          //          for (Enumeration e = ht.elements(); e.hasMoreElements(); ) {
          //             DataOutputStream outstream = (DataOutputStream)e.nextElement();
          //             try {
          //                  outstream.writeUTF(message);
          //             } 
          //             catch (IOException ex) {
          //                  ex.printStackTrace();
          //             }
          //          }
          //     }
          }
     } 
     catch (IOException ex) {
     }
     finally {
          synchronized(ht) {
               System.out.println("Remove connection: " + socket);
		
               ht.remove(socket);
		
               try {
                    socket.close();
               } 
               catch (IOException ex) {
               }
          }
     }
  }
}
