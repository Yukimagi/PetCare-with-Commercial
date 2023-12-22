package PetCare_Store;
import java.net.*;
import java.io.*;
import java.util.*;

public class PetCare_Store {
  private static ServerSocket SSocket;
  private static int port;
  private Hashtable ht = new Hashtable();
  Socket socket;

  public PetCare_Store() throws IOException {
     try {
          SSocket = new ServerSocket(port);
          System.out.println("Server created.");
          System.out.println("waiting for client to connect...");
	
          while (true) {
              socket = SSocket.accept();
              System.out.println("connected from Client " + socket.getInetAddress().getHostAddress());
				
              DataOutputStream outstream = new DataOutputStream(socket.getOutputStream());
              ht.put(socket, outstream);
              Thread thread = new Thread(new ServerThread(socket, ht));
              thread.start();
           }
      }
      catch (IOException ex) {
           ex.printStackTrace();
      }
   }

  public static void main(String[] args) throws Exception {
      port=8888;
      PetCare_Store ServerStart=new PetCare_Store();
  }
}

class ServerThread extends Thread implements Runnable {
  private Socket socket;
  private Hashtable ht;

  public ServerThread(Socket socket, Hashtable ht) {
     this.socket = socket;
     this.ht = ht;
  }



  public void writeTXT(String message){
     String FilePath = "ad.txt";
     try{
          BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath, false));
          writer.write(message);
          writer.newLine();
          writer.close();
     } catch (IOException e) {
          e.printStackTrace();
     }
  }
  static String ad="";
  public static void readTXT(String filePath) {
      try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
          String line;
          while ((line = reader.readLine()) != null) {
              // 在這裡處理每一行的內容，例如印出到控制台
              System.out.println("txt:"+line);
              ad=ad+line;
              
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public void run() {
     DataInputStream instream;
     String returnMessage = "";
     try {
          instream = new DataInputStream(socket.getInputStream());
			
          while (true) {
               String message = instream.readUTF();
               System.out.println("Message: " + message);

               try{
                    if(message!=""){
                    	// 指定文本檔案的路徑
                    	String filePath = "ad.txt";
                    
                    	if(message.equals("client")) {
                    		// 調用讀取檔案的方法
                      	  	readTXT(filePath);
                            returnMessage = ad;
                    	}
                    	else {	
                    	  writeTXT(message);
                      	  returnMessage = "I got the server message:"+message;
                        	  
                    	}
                     }else{
                              returnMessage = "FAIL";
                         }
                    
               }catch(Exception exc){
                    returnMessage = "ERROR";
                    exc.printStackTrace();
               }

               System.out.println("returnMessage: " + returnMessage);
               synchronized (ht) {
                    DataOutputStream outstream = (DataOutputStream)ht.get(this.socket);
                    try {
                        outstream.writeUTF(returnMessage);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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

