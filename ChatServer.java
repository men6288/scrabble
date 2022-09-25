import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer{
   int userNumber = 0;
   ArrayList<PrintWriter> alpw = new ArrayList<PrintWriter>();
   Socket controlSocket;
   public PrintWriter wri;
   boolean keepGoing = true;
   
   public static void main(String [] args) {
      
      ChatServer t1 = new ChatServer();  
   }
	
   
   public ChatServer()
   {
      ServerSocket socket = null;
   
      try {
         System.out.println("getLocalHost: "+InetAddress.getLocalHost() );
         System.out.println("getByName:    "+InetAddress.getByName("localhost") );
      
         socket = new ServerSocket(15000);
         controlSocket = null;
        
         while(true){ 		// run forever once up
         
            controlSocket = socket.accept(); 				// wait for connection
            ThServer createThread = new ThServer( controlSocket );
            createThread.start();
         } 
      }
      catch( BindException be ) {
         System.out.println("Server already running on this computer, stopping.");
      }
      catch( IOException ioe ) {
         System.out.println("IO Error");
         ioe.printStackTrace();
      }
   }
   
   // public void setFlag()
   // {
      // if(keepGoing == true)
      // {
         // keepGoing = false;
      // }
      // else if(keepGoing == false)
      // {
         // keepGoing = true;
      // }
   //    
   // 
   // }
   
   public boolean getFlag()
   {
      return keepGoing;
   }
   
      	
   class ThServer extends Thread {
      Socket cs;
      String name;
      int count = 0;
      String username;
      BufferedReader reader;
      PrintWriter writer;
      String exit = "The Server is offline";
      boolean Going;
      boolean nameSet = false;
   
      public ThServer( Socket cs ) {
         this.cs = cs;
      }
      
      
   
   	
      public void run() {
      
         Runtime.getRuntime().addShutdownHook(
               new Thread(
            new Runnable() {
               @Override
                  public void run()
               {
                  try
                  {
                     writer.println(exit);
                     writer.flush();
                  } 
                  catch(Exception sda){}
               }}));
               
         String message = "";
      
         try {
            
            reader = new BufferedReader(
                  new InputStreamReader( 
                  	cs.getInputStream()));
            writer = new PrintWriter(
                  new OutputStreamWriter(
                  	cs.getOutputStream()));
            alpw.add(writer);   
         }      
         catch(Exception e ) { 
            System.out.println("Error in Thread"); 
            e.printStackTrace();
         }
      
         Going = getFlag();
         while(Going == true){
         
            
               		
            if(message != null){
               try{
                  message = reader.readLine();					// from client 
                  
                  if(message.length()>0)
                  {        
                     if(count == 0){
                        username = message;
                        String join = username + " joined the chat!";
                        System.out.println(join);
                        nameSet = true;
                        for(PrintWriter pw : alpw){
                        
                           pw.println(join);
                           pw.flush();
                        }
                        count ++;
                        userNumber++;
                     }
                     else if(count == 1){   
                        String sendMess = username + ": " + message;
                        System.out.println(sendMess);
                     
                        for(PrintWriter pw : alpw){
                        
                           pw.println(sendMess);
                           pw.flush();
                        }
                     }
                  }
                  
               } 
               catch(Exception e){
                  String left = username + " left the chat";
                  System.out.println(left); 
                  for(PrintWriter pw : alpw){
                     
                     pw.println(left);
                     pw.flush();
                  }
                  try{
                     cs.close();
                     break;
                  }
                  catch(Exception ex)
                  {
                     System.out.println("Error closing error.");
                  }
               }  
            }                              
         }
         
      }
   }
}
