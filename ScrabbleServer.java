import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Color;

public class ScrabbleServer extends JFrame{

   //*********************************************************
   public JPanel portNumPanel = new JPanel(new FlowLayout());
   public JPanel numClientsConnPanel = new JPanel(new GridLayout(20,1));
   public JPanel serverMessagesPanel = new JPanel(new GridLayout(1,1));
   
   
   public JTextArea serverMessagesArea = new JTextArea();
   public JScrollPane serverMessagesScroll = new JScrollPane(serverMessagesArea);
   
   
   public JLabel playerConn1 = new JLabel("");
   public JLabel playerConn2 = new JLabel("");
   public JLabel playerConn3 = new JLabel("");
   public JLabel playerConn4 = new JLabel("");
   public JLabel connectedLabel = new JLabel("Clients Connected:");
   public JLabel portLabel = new JLabel("Port Number: ");
   public JLabel portNumLabel = new JLabel("");
   //**********GUI*************************************************
   
   public ArrayList<String> preStackArray = new ArrayList<String>(); //Alphabelt scrabble letters before shuffle
   public Stack<String> scrabbleLetterStack = new Stack<String>(); //Alphabelt scrabble letters after shuffle
   
   public Vector<String> sendBackClient = new Vector<String>(); //ArrayList that sends letters and coordinates back to other clients after move is validated
   public int[] gamePoints = new int[4];
   
   public ArrayList<String> dictionaryWords = new ArrayList<String>(); //ArrayList of all the words in the dictionary
   
   public Queue<Integer> rows = new LinkedList<Integer>(); //list of x coordinates of letters sent from client to server
   public Queue<Integer> columns = new LinkedList<Integer>(); //list of y coordinates of letters sent from client to server
   public Queue<String> wordQGame = new LinkedList<String>(); //list of letters sent from client to server that is used to place on the board if wordQ array validates
   public ArrayList<String> wordQ = new ArrayList<String>(); //list of letters sent from client to server that is used to check the dictionary
   
   int outerLengthOfword = 0; //Length of thedictionaryWordComparepassed from thread to the outer class for dictionary validation
   public String dictionaryWordCompare = ""; //Word concat thatis used to compare against the dictionary
      
   public Stack<PrintWriter> globalWriters = new Stack<PrintWriter>(); //Vector of PrintWriter that will print to all clients
   public Stack<BufferedReader> globalReaders = new Stack<BufferedReader>(); //Vector of BufferedReaders that will print to all clients
   public Stack<String> readerWriterNumber = new Stack<String>();
 
   public String [][] logicBoard = new String [15][15]; //Logical scrabble board on the server
   
   Socket controlSocket; //Server socket 
   
   public int playerTurnNumber = 1; //Client player who's turn it is
   public int playerConnectionNumber = 0; //Number of users connected count
   
   Object globalONE = new Object();
   Object addGlobalWR = new Object();
   Object startObject = new Object();
   Object addWriterSync = new Object();
   Object winSync = new Object();
   Object skipSync = new Object();
   Object sendBackSync = new Object();
   
   
   public int socketWaitCount = 0; //The amount of clients at the wait
   
   public int sentWordValue = 0; //The value of the incoming word
   
   public int skipTurnWinCount = 0; //The amount of skips for a win condition
   
   public int winSkipCounter = 0; //The skip turn counter that increments for a win condition
  
   public boolean wordCheck; //boolean value to see if word validated
   
   public boolean winFirstSkipCounter = false; //
   
   public boolean winFlag = false; //boolean value to see if word validated
   
   
   

    //Parameterized constructor
   public ScrabbleServer(int portPass){
      
       //************************GUI********************** 
      setSize(830, 680);
      setResizable(true);
      setLocationRelativeTo(null);
      setTitle("Werds with Nerds GUI");
      
      add(portNumPanel, BorderLayout.NORTH);
      add(serverMessagesPanel, BorderLayout.CENTER);
      add(numClientsConnPanel, BorderLayout.EAST);
      
      serverMessagesPanel.add(serverMessagesScroll);
      numClientsConnPanel.add(connectedLabel);
      numClientsConnPanel.add(playerConn1);
      numClientsConnPanel.add(playerConn2);
      numClientsConnPanel.add(playerConn3);
      numClientsConnPanel.add(playerConn4);
      portNumPanel.add(portLabel);
      portNumPanel.add(portNumLabel);
      portNumLabel.setText(Integer.toString(portPass));
      //portField.setEditable(false);
      
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
      //************************GUI**********************
      
      ServerSocket socket = null;
   
      try {
         //Output Server data to screen
         System.out.println("getLocalHost: "+InetAddress.getLocalHost() );
         serverMessagesArea.append("getLocalHost: "+InetAddress.getLocalHost()+"\n");
         serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
         System.out.println("getByName:    "+InetAddress.getByName("localhost") );
         serverMessagesArea.append("getByName:    "+InetAddress.getByName("localhost") +"\n");
         serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
         
         //Pass the port into the socket and instantiate controlSocket
         socket = new ServerSocket(portPass);
         controlSocket = null;
         
         //Call newgame method which populates the board and dictionary
         newGame();
      
         //Keep the program running and accept connections
         while(true){  
         
            //Allow only four connections in each game
            if(playerConnectionNumber<=4){
               controlSocket = socket.accept(); 				// wait for connection
               
               ScrabbleServer.this.playerConnectionNumber++;
               System.out.println("Player connection number " + playerConnectionNumber);
               serverMessagesArea.append("Player connection number " + playerConnectionNumber +"\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                                 
               if(playerConnectionNumber == 1)
               {
                  createLetters(); 
                  randomizeLetters();
                  System.out.println("Created letters for two players");
                  serverMessagesArea.append("Created letters for two players\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               }
               else if(playerConnectionNumber == 4)
               {
                  createLetters(); 
                  randomizeLetters(); 
                  System.out.println("Created letters for four players");
                  serverMessagesArea.append("Created letters for four players\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());   
               }
               
               
               System.out.println("STACKS STACKS STACKS SIZEEEEE " + ScrabbleServer.this.scrabbleLetterStack.size());
               serverMessagesArea.append("STACKS STACKS STACKS SIZEEEEE " + ScrabbleServer.this.scrabbleLetterStack.size() + "\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               //Create object of thread class and start the thread
               clientThread connectThread = new clientThread(controlSocket, playerConnectionNumber);
               
               connectThread.start();
               
            }
            //Else if there are already four clients in the game
            else{
               controlSocket = socket.accept();
               
               //Reader and writer for denied connections
               BufferedReader deniedRead = new BufferedReader(
                  new InputStreamReader( 
                  	controlSocket.getInputStream()));
               PrintWriter deniedWrite = new PrintWriter(
                  new OutputStreamWriter(
                  	controlSocket.getOutputStream()));
                     
               //Increment the number of clients trying to connect 
               playerConnectionNumber++;
               deniedWrite.println(playerConnectionNumber); //Write out client playerConnectionNumber
               deniedWrite.flush();
               
               //Close socket and reader/writer
               deniedWrite.close();
               deniedRead.close();   
               controlSocket.close();
               
               System.out.println("Client connection denied");
               serverMessagesArea.append("Client connection denied\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());      
            }
            
         }
      }
      catch( BindException be ){
         System.out.println("Server already running on this computer.");
      }
      catch( IOException ioe ){
         System.out.println("IO Error");
         ioe.printStackTrace();
      }
   }


   //This method reads in dictionary.txt file and stores all the words into an arrayList
   public void buildDictionary()
   {
      try{
         File file = new File("dictionary.txt");
         BufferedReader read = new BufferedReader(new FileReader(file));
         ArrayList<String> untrimmed = new ArrayList<String>();
         //178691
         //Grab all the words from the txt file and store them into an arraylist
         for(int i = 0;i<178691;i++)
         {
            String stn = read.readLine();
            untrimmed.add(stn);
            dictionaryWords.add(untrimmed.get(i).trim());
         }  
      }
      catch(Exception e)
      {
         System.out.println("Error reading dictionary");
      }
   }
   
   //This method builds the word from the letters the client sent and searches the dictionary arraylist for the word
   public boolean checkWord(ArrayList<String> stringer, int length)
   {
      ArrayList<String> wordBreak = new ArrayList<String>(); //ArrayList for letters sent from the client
      
      
      wordBreak = stringer; //ArrayList passed in from inside the client thread Turn method
      outerLengthOfword = length; //The length of the word that the client sent to the server, this is passed from the Turn method inside the client thread 
      dictionaryWordCompare =""; //This is the word builder that concatinates each letter from the array
      
      //Grabs the letters from the array and builds the string
      for(int i = 0;i<outerLengthOfword;i++)
      {
      
         dictionaryWordCompare+= wordBreak.get(i);
      
      }
      
      System.out.println(dictionaryWordCompare);
      serverMessagesArea.append(dictionaryWordCompare + "\n");
      serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
      
      
      //Checks to see if word is in array
      boolean checky = false;
      checky = this.dictionaryWords.contains(dictionaryWordCompare);
      
      //Returns true or false
      if(checky==true)
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   //This method creates the board and initializes it with all zeros
   public void newGame()
   {
   
      //Set default values of board to 0
      for(int i = 0;i<15;i++)
      {
         for(int j = 0;j<15;j++)
         {
            logicBoard[i][j] = "0";
            //System.out.print(logicBoard[i][j]+" ");
         }
         //System.out.print("row "+i);
         //System.out.println();
      }
      
      System.out.println("Array Loaded");
      serverMessagesArea.append("Array Loaded\n");
      serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
      //createLetters(); //Method to create the letter tiles in the game
      //randomizeLetters(); //Method to randomize the letter tiles and puts them into a stack
      buildDictionary(); //Method to load the dictionary into arraylist
      
   }
   //Method to randomize the letter tiles and puts them into a stack
   public void randomizeLetters()
   {
      Collections.shuffle(preStackArray); //Shuffle pre-stack array
      
      //Populate stack from array
      for(String let : preStackArray){
         scrabbleLetterStack.push(let);
      }
      
      preStackArray.clear();
   
   }
   //Method to create the letter tiles in the game
   public void createLetters()
   {
      
      
      for(int a = 0;a<9;a++)
      {
         preStackArray.add("a");
      }
      
      for(int b = 0;b<2;b++)
      {
         preStackArray.add("b");
      }
      
      for(int c = 0;c<2;c++)
      {
         preStackArray.add("c");
      }
      
      for(int d = 0;d<4;d++)
      {
         preStackArray.add("d");
      }
      
      for(int e = 0;e<12;e++)
      {
         preStackArray.add("e");
      }
      
      for(int f = 0;f<2;f++)
      {
         preStackArray.add("f");
      }
      
      for(int g = 0;g<3;g++)
      {
         preStackArray.add("g");
      }
      
      for(int h = 0;h<2;h++)
      {
         preStackArray.add("h");
      }
      
      for(int i = 0;i<9;i++)
      {
         preStackArray.add("i");
      }
      
      for(int j = 0;j<1;j++)
      {
         preStackArray.add("j");
      }
      
      for(int k = 0;k<1;k++)
      {
         preStackArray.add("k");
      }
      
      for(int l = 0;l<4;l++)
      {
         preStackArray.add("l");
      }
      
      for(int m = 0;m<2;m++)
      {
         preStackArray.add("m");
      }
      
      for(int n = 0;n<6;n++)
      {
         preStackArray.add("n");
      }
      
      for(int o = 0;o<8;o++)
      {
         preStackArray.add("o");
      }
      
      for(int p = 0;p<2;p++)
      {
         preStackArray.add("p");
      }
      
      for(int q = 0;q<1;q++)
      {
         preStackArray.add("q");
      }
      
      for(int r = 0;r<6;r++)
      {
         preStackArray.add("r");
      }
      
      for(int s = 0;s<4;s++)
      {
         preStackArray.add("s");
      }
      
      for(int t = 0;t<6;t++)
      {
         preStackArray.add("t");
      }
      
      for(int u = 0;u<2;u++)
      {
         preStackArray.add("u");
      }
      
      for(int v = 0;v<2;v++)
      {
         preStackArray.add("v");
      }
      
      for(int w = 0;w<2;w++)
      {
         preStackArray.add("w");
      }
      
      for(int x = 0;x<1;x++)
      {
         preStackArray.add("x");
      }
      
      for(int y = 0;y<2;y++)
      {
         preStackArray.add("y");
      }
      
      for(int z = 0;z<1;z++)
      {
         preStackArray.add("z");
      }
         
   }
   
   public void checkTheBoardForPoints(int xaxis,int yaxis,String letter)
   {
      for(int row = yaxis;row<=yaxis;row++)
      {
         for(int col = 0;col<14;col++)
         {
            
         
         }
      }   
   
   
   }








   //Method to get the player turn number for write
   public synchronized String getTurn()
   {
      String num = Integer.toString(playerTurnNumber);
      return num;
   }
    //Method to get the player turn number in int form
   public synchronized int getTurnInt()
   {
      return this.playerTurnNumber;
   }
   
   
   
   
   
   //Method to add printWriter to vector in order to global write
   public synchronized void setGlobalWriters(PrintWriter value)
   {
      globalWriters.add(value);
   }
   
   //Method to add BufferedReader to vector in order to global write
   public synchronized void setGlobalReaders(BufferedReader value)
   {
      globalReaders.add(value);
   }
   
    //Method to add BufferedReader to vector in order to global write
   public synchronized void setRWNumber(String value)
   {
      readerWriterNumber.add(value);
   }
   
   
   //Set the wait count for the clients waiting
   public synchronized void setWaitCount()
   {
      this.socketWaitCount++;
   }
   //Get the count of clients waiting
   public int getWaitCount()
   {
      return socketWaitCount;
   }

   //Method to change the turn after each valid move
   public synchronized void changeTurn(String client)
   {
      int clientNum = 0;
      clientNum = Integer.parseInt(client);
   
      if(this.playerConnectionNumber == 4)
      {
         if(clientNum == 1)
         {
            this.playerTurnNumber = 2;
         }
         else if(clientNum == 2)
         {
            this.playerTurnNumber = 3;
         }
         else if(clientNum == 3)
         {
            this.playerTurnNumber = 4;
         }
         else if(clientNum == 4)
         {
            this.playerTurnNumber = 1;
         }
      }
      else if(this.playerConnectionNumber == 3)
      {
         if(clientNum == 1)
         {
            this.playerTurnNumber = 2;
         }
         else if(clientNum == 2)
         {
            this.playerTurnNumber = 3;
         }
         else if(clientNum == 3)
         {
            this.playerTurnNumber = 1;
         }   
      }
      else if(this.playerConnectionNumber == 2)
      {
         if(clientNum == 1)
         {
            this.playerTurnNumber = 2;
         }
         else if(clientNum == 2)
         {
            this.playerTurnNumber = 1;
         }
      } 
   }
  
  //Get the value of letters
   public int getLetterValue(String in)
   {
      int value = 0;
      switch(in){
         case "a": value = 1;
            break;
         case "b": value = 3;
            break;
         case "c": value = 3;
            break;
         case "d": value = 2;
            break;
         case "e": value = 1;
            break;
         case "f": value = 4;
            break;
         case "g": value = 2;
            break;
         case "h": value = 4;
            break;
         case "i": value = 1;
            break;
         case "j": value = 8;
            break;
         case "k": value = 5;
            break;
         case "l": value = 1;
            break;
         case "m": value = 3;
            break;
         case "n": value = 1;
            break;
         case "o": value = 1;
            break;
         case "p": value = 3;
            break;
         case "q": value = 10;
            break;
         case "r": value = 1;
            break;
         case "s": value = 1;
            break;
         case "t": value = 1;
            break;
         case "u": value = 1;
            break;
         case "v": value = 4;
            break;
         case "w": value = 4;
            break;
         case "x": value = 8;
            break;
         case "y": value = 4;
            break;
         case "z": value = 10;
            break;
      }
      
      return value;
   
   
   }
   
   //Set points for each player
   public void setPoints(int number)
   {  
      int cNumber = number;
      if(cNumber == 1)
      {
         gamePoints[0] += this.sentWordValue;
      }
      else if(cNumber == 2)
      {
         gamePoints[1] += this.sentWordValue;
      }
      else if(cNumber == 3)
      {
         gamePoints[2] += this.sentWordValue;
      }
      else if(cNumber == 4)
      {
         gamePoints[3] += this.sentWordValue;
      }
   
   }
   //Set wincondition based on the number of players
   public void winConditionSet()
   {  
      if(this.playerConnectionNumber == 2)
      {
         this.skipTurnWinCount = 4;
      }
      else if(this.playerConnectionNumber == 3)
      {
         this.skipTurnWinCount = 6;
      }
      else if(this.playerConnectionNumber == 4)
      {
         this.skipTurnWinCount = 8;
      }
   
   }
   //Check for the number of skips to declare winner
   public boolean winCheck()
   {  
      if(this.skipTurnWinCount == this.winSkipCounter)
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
    //Inner class that is used to run the threads  	
   class clientThread extends Thread{
   
      Socket cs; //Accepted socket passed in from the outer class
      
      BufferedReader reader; //Thread reader
      PrintWriter writer; //Thread writer
      PrintWriter gWriter; //Global writer
      BufferedReader gReader; //Global Reader
      
      
      int clientNumber; //client number passed in from the outer class
      int intwordLength = 0; //Length of the word recieved from the client's move
      
      int wordValue = 0;
      
      String stringWordLength = ""; //Length of the word recieved from the client's move in string
      String listenForEnd = ""; //String for used when endturn is pressed on the client
      
      int turn;
      
      
      
      
      //Parameterized constructor, takes in socket and client number. Creates readers and writers
      public clientThread( Socket _cs, int num){
         this.cs = _cs;
         clientNumber = num;
         
         try{
         
            reader = new BufferedReader(
                  new InputStreamReader( 
                  	cs.getInputStream()));
            writer = new PrintWriter(
                  new OutputStreamWriter(
                  	cs.getOutputStream()));
                     
         }
         catch(Exception ert)
         {
         }
      }
   
      //Thread run method
      public void run(){
       
         try{
            setName(Integer.toString(clientNumber));
            if(clientNumber == 1){
            
               playerConn1.setText("Player 1 connected");
               repaint();
            }
            else if(clientNumber == 2){
            
               playerConn2.setText("Player 2 connected");
               repaint();
            }
            else if(clientNumber == 3){
            
               playerConn3.setText("Player 3 connected");
               repaint();
            }
            else if(clientNumber == 4){
            
               playerConn4.setText("Player 4 connected");
               repaint();
            }
            
            reader.readLine(); //initial communication from client  
            
            
             
            // String cNum = "";
            // cNum = Integer.toString(clientNumber); //Parse client number to string
            writer.println(getName()); //Send the client number to client
            writer.flush();
            
            
            System.out.println("Player "+getName()+" connected!");
            serverMessagesArea.append("Player "+getName()+" connected!\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            reader.readLine(); //client number recieved ack
            
              
            //Pass starting letters to the clients
            for(int let = 0;let<7;let++){
               String sendLetter = "";
               sendLetter = (String) ScrabbleServer.this.scrabbleLetterStack.pop(); //pull random letter from stack
               writer.println(sendLetter); //Send letter to client
               writer.flush();
               
               
               reader.readLine(); //Read letter ack from client
            }
            
            
            if(Integer.parseInt(getName()) < 4) 
            {
               synchronized(ScrabbleServer.this.startObject)
               {
                  try{
                     ScrabbleServer.this.startObject.wait();
                  }
                  catch(Exception erry)
                  {}   
               }    
            }
            else if(Integer.parseInt(getName()) == 4)
            {
               synchronized(ScrabbleServer.this.startObject)
               {
                  ScrabbleServer.this.startObject.notifyAll();
                       
               }    
            }
           
           
            writer.println(getTurn()); //Send the turn number to the clients
            writer.flush();
            reader.readLine(); //Read turn ack from client
            
            
            writer.println("sync"); //Send a Sync to the client
            writer.flush();
         
         
            //Loop to continue game  
            while(true){ 
               //If the client number equals the turn number then that particular thread fires
                  
               turn = getTurnInt();
               System.out.println(getName() +" Is about to read the turn " +turn);
               serverMessagesArea.append(getName() +" Is about to read the turn " +turn + "\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               System.out.println();
               serverMessagesArea.append("\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
            
               if(turn == Integer.parseInt(getName())){
                 
                  boolean stoppper = Turn();  //Call the turn method to proceed with move
                  
                  //If returns false client exited game
                  if(stoppper == false)
                  {
                     break;
                  } 
                        
               }//Else other clients wait to hear from the first thread if the move is valid and then prints it out
               else if(turn != Integer.parseInt(getName())){
                  
                  System.out.println();
                  serverMessagesArea.append("\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  
                  
                  synchronized(ScrabbleServer.this.addWriterSync)
                  {
                     setGlobalWriters(writer);
                     setGlobalReaders(reader);
                     System.out.println("Server thread "+getName()+" added global READER and WRITER");
                     serverMessagesArea.append("Server thread "+getName()+" added global READER and WRITER\n");
                     serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                     setRWNumber(getName());
                     setWaitCount();
                     System.out.println("Server thread "+getName()+" incremented Socket COUNT");
                     serverMessagesArea.append("Server thread "+getName()+" incremented Socket COUNT\n");
                     serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  
                  }
                  
                  synchronized(ScrabbleServer.this.globalONE)
                  {
                     try{
                        ScrabbleServer.this.globalONE.wait();
                     }
                     catch(Exception erry)
                     {}   
                  } 
                  
                  System.out.println("Client "+getName()+" DONE WITH LISTEN LOOP");
                  serverMessagesArea.append("Client "+getName()+" DONE WITH LISTEN LOOP\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  
                  System.out.println();
                  serverMessagesArea.append("\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
               } 
               
            }           
              
         }      
         catch(Exception e ){ 
            System.out.println("Error"); 
            e.printStackTrace();
         }          
      }
      
      
      
      //Method used to listen for the endturn after move is placed
      public boolean Turn()
      {
         try{  
         
            ////////////////////////////////////////LISTEN FOR END TURN/////////////////////////////////////////////////
            listenForEnd = "";
            this.listenForEnd = reader.readLine(); //Read end turn from the client
            System.out.println("Server thread "+getName()+" should recieve end: "+this.listenForEnd);
            serverMessagesArea.append("Server thread "+getName()+" should recieve end: "+this.listenForEnd + "\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(ScrabbleServer.this.scrabbleLetterStack.size()< 7)
            {
               System.out.println("Server thread "+getName()+" sent OUT OF WORDS ACK");
               serverMessagesArea.append("Server thread "+getName()+" sent OUT OF WORDS ACK\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
               
               Winner();
            }  
            else if(this.listenForEnd.equals("SKIP")){
            
               ////////////////////////////////////////END TURN ACK///////////////////////////////////////////////////////
               System.out.println("Server thread "+getName()+" sent SKIP ACK");
               serverMessagesArea.append("Server thread "+getName()+" sent SKIP ACK\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
               writer.println("Server recieved SKIP before check"); //Send ack if server got the END
               writer.flush();
            
               ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
               /////////////////SET THE AMOUNT OF SKIPS FOR WIN CONDITION THE FRIST TIME GOING THROUGH//////////////////
               if(ScrabbleServer.this.winFirstSkipCounter == false)
               {
                  winConditionSet();
                  ScrabbleServer.this.winFirstSkipCounter = true;
               }
               ////////////////////////////////////////////////////////////////////////////////////////////////////////////
               
               
               
               ////////////////////////////////////////INCRIMENT THE SKIP COUNTER//////////////////////////////////////////
               ScrabbleServer.this.winSkipCounter += 1;
               ////////////////////////////////////////////////////////////////////////////////////////////////////////////
               
               
               ///////////////////////////////////////////CHECK FOR WIN////////////////////////////////////////////////////
               ScrabbleServer.this.winFlag = winCheck();
               ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
               //////////////////////////////////////////IF WIN FLAG IS TRIPPED////////////////////////////////////////////
               if(ScrabbleServer.this.winFlag == true)
               {
                  Winner();
               }
               ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
               
               /////////////////////////////////////////////ELSE IF THE WIN FLAG ISNT TRIPPED/////////////////////////////////////////
               else{
               
                  /////////////////////////////////////////////////SEND SKIP TO LOCAL CLIENT//////////////////////////////////////////
                  writer.println("SKIP");
                  System.out.println("intWordLength == 0");
                  serverMessagesArea.append("intWordLength == 0\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  writer.flush();
                  System.out.println(reader.readLine());
                  /////////////////////////////////////////////////SEND SKIP TO LOCAL CLIENT//////////////////////////////////////////
               
                  /////////////////////////////////////////////////CHANGE TURN////////////////////////////////////////////////////////
                  changeTurn(getName()); 
                  /////////////////////////////////////////////////CHANGE TURN////////////////////////////////////////////////////////
                  
                  while(true){
                     ////////////////////////////////////////PCHECK TO SEE THAT ALL CLIENTS ARE WAITING///////////////////////////////////////////////// 
                     if(getWaitCount() == ScrabbleServer.this.playerConnectionNumber-1){
                        System.out.println("//////////////////////////////////////////////////////SOCKET COUNT: "+getWaitCount());
                        System.out.println("//////////////////////////////////////////////////////READER COUNT: "+ScrabbleServer.this.globalReaders.size());
                        System.out.println("//////////////////////////////////////////////////////WRITER COUNT: "+ScrabbleServer.this.globalWriters.size());
                        
                        serverMessagesArea.append("//////////////////////////////////////////////////////SOCKET COUNT: "+getWaitCount() + "\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                        
                        serverMessagesArea.append("//////////////////////////////////////////////////////READER COUNT: "+ScrabbleServer.this.globalReaders.size() + "\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                        
                        serverMessagesArea.append("//////////////////////////////////////////////////////WRITER COUNT: "+ScrabbleServer.this.globalWriters.size() + "\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                     
                        
                        synchronized(ScrabbleServer.this.skipSync)
                        {
                        ////////////////////////////////////////SEND BACK FOR THE NUMBER OF CLIENTS CONNECTED///////////////////////////////////////////////// 
                           for(int go = 0;go<getWaitCount();go++){
                              gReader = ScrabbleServer.this.globalReaders.pop();
                              gWriter = ScrabbleServer.this.globalWriters.pop();
                           
                           
                              System.out.println("CREATED READER AND WRITER");
                              serverMessagesArea.append("CREATED READER AND WRITER\n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                           
                           ///////////////////////////////////////SEND SKIP FOR MOVE PASS/////////////////////////////////////////////////////////////
                              System.out.println("SERVER SENT SKIP"); 
                              serverMessagesArea.append("SERVER SENT SKIP\n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length()); 
                              gWriter.println("SKIP");
                              gWriter.flush();
                           
                           
                              System.out.println("SERVER SKIP ACK "+gReader.readLine()); 
                              serverMessagesArea.append("SERVER SKIP ACK \n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length()); 
                           
                           ///////////////////////////////////////SEND SKIP FOR MOVE PASS/////////////////////////////////////////////////////////////
                           
                                
                           ///////////////////////////////////////PRINT TURN TO OTHER CLIENTS//////////////////////////////////////////
                              System.out.println("SERVER SENT "+getTurn());
                              serverMessagesArea.append("SERVER SENT "+getTurn() + "\n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length()); 
                              gWriter.println(getTurn());
                              gWriter.flush();
                           
                           
                              System.out.println("SERVER READ TURN NUMBER"+gReader.readLine()); 
                              serverMessagesArea.append("SERVER READ TURN NUMBER\n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length()); 
                           ///////////////////////////////////////PRINT TURN TO OTHER CLIENTS//////////////////////////////////////////
                           
                           }
                        ////////////////////////////////////////PCHECK TO SEE THAT ALL CLIENTS ARE WAITING/////////////////////////////////////////////////
                        }
                        break;
                     
                     }
                    
                  
                  }
                  
                  /////////////////////////////////////////////////SEND TURN TO LOCAL CLIENT//////////////////////////////////////////
                  writer.println(getTurn());
                  writer.flush();
                  System.out.println(reader.readLine());
                  /////////////////////////////////////////////////SEND TURN TO LOCAL CLIENT//////////////////////////////////////////
               
               
               
                  
               ////////////////////////////////////////RESET///////////////////////////////////////////////// 
                  ScrabbleServer.this.globalWriters.clear(); //The writers for the waiting clients
                  ScrabbleServer.this.globalReaders.clear(); //The readers for the waiting clients
                  ScrabbleServer.this.sendBackClient.clear(); //The letters that will be sent back to the other clients
                  ScrabbleServer.this.socketWaitCount = 0; //The amount of clients at the wait
                  ScrabbleServer.this.sentWordValue = 0; //Value of the word being sent to the server
                  ScrabbleServer.this.rows.clear(); //pull the x index and store it in a queue
                  ScrabbleServer.this.columns.clear(); //pull the y index and store it in a queue
                  ScrabbleServer.this.wordQGame.clear(); //pull the letter and store it in a queue in order to place it on the board
                  ScrabbleServer.this.wordQ.clear(); //pull the letter and store it in a arraylist in order to validate word 
                  ScrabbleServer.this.wordCheck = false;
                  ////////////////////////////////////////RESET///////////////////////////////////////////////// 
               
               
               ////////////////////////////////////////NOTIFY WAITING CLIENTS/////////////////////////////////////////////////
                  synchronized(ScrabbleServer.this.globalONE)
                  {
                  
                     System.out.println("Client "+getName()+" GLOBALTWO NOTIFY");
                     serverMessagesArea.append("Client "+getName()+" GLOBALTWO NOTIFY\n");
                     serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                     ScrabbleServer.this.globalONE.notifyAll();
                  
                  }
               ////////////////////////////////////////NOTIFY WAITING CLIENTS/////////////////////////////////////////////////
               
                  ScrabbleServer.this.globalONE = null; //SYNC OBJECT FOR THE WAIT AND NOTIFY
                  ScrabbleServer.this.globalONE = new Object();
               
               }
            } 
            
            ////////////////////////////////////////ELSE IF THE TURN IS VALID//////////////////////////////////////////
            else{
            ////////////////////////////////////////END TURN ACK///////////////////////////////////////////////////////
               System.out.println("Server thread "+getName()+" sent END ACK");
               serverMessagesArea.append("Server thread "+getName()+" sent END ACK\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               writer.println("Server recieved END before check"); //Send ack if server got the END
               writer.flush();
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
               /////////////////SET THE AMOUNT OF SKIPS FOR WIN CONDITION THE FRIST TIME GOING THROUGH//////////////////
               if(ScrabbleServer.this.winFirstSkipCounter == false)
               {
                  winConditionSet();
                  ScrabbleServer.this.winFirstSkipCounter = true;
               }
               ////////////////////////////////////////////////////////////////////////////////////////////////////////////
               
               ////////////////////////////////////////ZERO THE SKIP COUNTER//////////////////////////////////////////
               ScrabbleServer.this.winSkipCounter = 0;
               ////////////////////////////////////////ZERO THE SKIP COUNTER//////////////////////////////////////////
               
            
               ////////////////////////////////////////LISTEN FOR WORD LENGTH//////////////////////////////////////////////
               stringWordLength = "";   
               stringWordLength = reader.readLine(); //Read stringWordLength from the client
               
               System.out.println("Server thread "+getName()+" should receive the length of the incoming word: "+stringWordLength);
               serverMessagesArea.append("Server thread "+getName()+" should receive the length of the incoming word: "+stringWordLength + "\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               intwordLength = 0;
               intwordLength = Integer.parseInt(stringWordLength);
               ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
               ////////////////////////////////////////WORD LENGTH ACK/////////////////////////////////////////////////
               System.out.println("Server thread "+getName()+" sent length of word ACK");
               serverMessagesArea.append("Server thread "+getName()+" sent length of word ACK\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               writer.println("Server recieved legnth of word"); //Send ack to client saying it got thedictionaryWordComparelength
               writer.flush();
               ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
               //////////////////////////////////////////////Recieve Letters//////////////////////////////////////////////////////  
               for(int j = 0;j<intwordLength;j++)
               {
                  String letterIndex = reader.readLine();
                  System.out.println("Server thread "+getName()+" should receive sent letter index: "+letterIndex); //Read each letter and index
                  serverMessagesArea.append("Server thread "+getName()+" should receive sent letter index: "+letterIndex + "\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  
                  String[] splitter = letterIndex.split(",");
                         
                  ScrabbleServer.this.rows.add(Integer.parseInt(splitter[0])); //pull the x index and store it in a queue
                  ScrabbleServer.this.columns.add(Integer.parseInt(splitter[1])); //pull the y index and store it in a queue
                  ScrabbleServer.this.wordQGame.add(splitter[2]); //pull the letter and store it in a queue in order to place it on the board
                  ScrabbleServer.this.wordQ.add(splitter[2]); //pull the letter and store it in a arraylist in order to validate word
               
                  ScrabbleServer.this.sentWordValue += getLetterValue(splitter[2]); 
                  
                  System.out.println("Server thread "+getName()+" sent letter index# ACK!!");      
                  serverMessagesArea.append("Server thread "+getName()+" sent letter index# ACK!!\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());      
                  writer.println("Server sent ACK for letter and index #"); //send ack after evey letter recieved
                  writer.flush();
                  
                  Arrays.fill(splitter, null);
               }
            //////////////////////////////////////////////Recieve Letters////////////////////////////////////////////////////// 
            
            //////////////////////////////////////////////ReSYNC////////////////////////////////////////////////////// 
               System.out.println(ScrabbleServer.this.sentWordValue+ "POINTTTTTSSSSS");
               serverMessagesArea.append(ScrabbleServer.this.sentWordValue+ "POINTTTTTSSSSS\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                   
               reader.readLine();   
            //////////////////////////////////////////////ReSYNC//////////////////////////////////////////////////////
               
               
            ////////////////////////////////////////CHECK DICTIONARY FOR WORD/////////////////////////////////////////////////    
               ScrabbleServer.this.wordCheck = checkWord(wordQ, intwordLength); //Send the wordQ arraylist and length of word to checkword method. returns true or false
               System.out.println("Server thread  "+getName()+" tripped check word flag " + ScrabbleServer.this.wordCheck);
               serverMessagesArea.append("Server thread  "+getName()+" tripped check word flag " + ScrabbleServer.this.wordCheck + "\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            ////////////////////////////////////////CHECK DICTIONARY FOR WORD////////////////////////////////////////////////
            
            
               ////////////////////////////////////////IF WORD RETURNS TRUE////////////////////////////////////////////////
               if(ScrabbleServer.this.wordCheck == true)
               {
               ////////////////////////////////////////WORD IS VALID/////////////////////////////////////////////////
                  System.out.println("Server thread  "+getName()+" sent YES to client!");
                  serverMessagesArea.append("Server thread  "+getName()+" sent YES to client!\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
                  writer.println("YES"); //If accepted I send a response to client
                  writer.flush();
               ////////////////////////////////////////WORD IS VALID/////////////////////////////////////////////////
               
               ////////////////////////////////////////VALID WORD ACK/////////////////////////////////////////////////
                  System.out.println("Server thread  "+getName()+" recieved valid send ACK: "+reader.readLine());
                  serverMessagesArea.append("Server thread  "+getName()+" recieved valid send ACK: \n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               ////////////////////////////////////////VALID WORD ACK/////////////////////////////////////////////////
               
               ////////////////////////////////////////SET POINTS FOR WORD/////////////////////////////////////////////////
                  setPoints(Integer.parseInt(getName()));
               ////////////////////////////////////////SET POINTS FOR WORD/////////////////////////////////////////////////
               
                  placeBoard(); //Method to place the letters on the board
                  validityPassed(); //Method to change the turn and give latters back to client
               }
               ////////////////////////////////////////IF WORD RETURNS TRUE////////////////////////////////////////////////\
               
               ////////////////////////////////////////IF WORD RETURNS FALSE////////////////////////////////////////////////
               else
               {  
               ////////////////////////////////////////WORD IS INVALID/////////////////////////////////////////////////
                  System.out.println("Server thread  "+getName()+" Sent valid NO");
                  serverMessagesArea.append("Server thread  "+getName()+" Sent valid NO\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
                  writer.println("NO"); //If accepted I send a response to client
                  writer.flush();
               ////////////////////////////////////////WORD IS INVALID///////////////////////////////////////////////// 
               
               
               ////////////////////////////////////////WORD INVALID ACK/////////////////////////////////////////////////
                  System.out.println(reader.readLine());
               ////////////////////////////////////////WORD INVALID ACK/////////////////////////////////////////////////
               
               //Clear queues and lists
                  ScrabbleServer.this.sendBackClient.clear(); //The letters that will be sent back to the other clients
                  ScrabbleServer.this.sentWordValue = 0; //Value of the word being sent to the server
                  ScrabbleServer.this.rows.clear(); //pull the x index and store it in a queue
                  ScrabbleServer.this.columns.clear(); //pull the y index and store it in a queue
                  ScrabbleServer.this.wordQGame.clear(); //pull the letter and store it in a queue in order to place it on the board
                  ScrabbleServer.this.wordQ.clear(); //pull the letter and store it in a arraylist in order to validate word 
                  ScrabbleServer.this.wordCheck = false;
               
                  Turn(); //Call turn method again
               }
            }
         }
         
         catch(Exception rot)
         {
            rot.printStackTrace();
            return false;
         }
         
         
         return true;
      }
   
   
      public void Winner()
      {
         try
         {
            this.writer.println("WIN");
            System.out.println("intWordLength == 0");
            serverMessagesArea.append("intWordLength == 0\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  
            this.writer.flush();
            System.out.println(this.reader.readLine());
                  
            while(true){
                  ////////////////////////////////////////PCHECK TO SEE THAT ALL CLIENTS ARE WAITING///////////////////////////////////////////////// 
               if(getWaitCount() == ScrabbleServer.this.playerConnectionNumber-1){
                  System.out.println("//////////////////////////////////////////////////////SOCKET COUNT: "+getWaitCount());
                  System.out.println("//////////////////////////////////////////////////////READER COUNT: "+ScrabbleServer.this.globalReaders.size());
                  System.out.println("//////////////////////////////////////////////////////WRITER COUNT: "+ScrabbleServer.this.globalWriters.size());
                        
                  serverMessagesArea.append("//////////////////////////////////////////////////////SOCKET COUNT: "+getWaitCount() + "\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                        
                  serverMessagesArea.append("//////////////////////////////////////////////////////READER COUNT: "+ScrabbleServer.this.globalReaders.size() + "\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                        
                  serverMessagesArea.append("//////////////////////////////////////////////////////WRITER COUNT: "+ScrabbleServer.this.globalWriters.size() + "\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                     
                     
                     
                  synchronized(ScrabbleServer.this.winSync)
                  {
                        ////////////////////////////////////////SEND BACK WIN CONDITION TO OTHER CLIENTS///////////////////////////////////////////////// 
                     for(int go = 0;go<getWaitCount();go++){
                        gReader = ScrabbleServer.this.globalReaders.pop();
                        gWriter = ScrabbleServer.this.globalWriters.pop();
                           
                        gWriter.println("WIN");
                        System.out.println("intWordLength == 0");
                        serverMessagesArea.append("intWordLength == 0\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                        gWriter.flush();
                        System.out.println(gReader.readLine());
                     }
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                  }  
                  break;
                     
               }
                     ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
         }
         catch(Exception billy)
         {
         
         }
               
      }
      
       //Method to place the letters on the board
      public void placeBoard()
      {
         
         
         ////////////////////////////////////////PLACE WORDS ON BOARD/////////////////////////////////////////////////
         for(int j = 0;j<intwordLength;j++)
         {
            //Removing x, y, and letters to place on the board
            int xC = ScrabbleServer.this.rows.remove();
            int yC = ScrabbleServer.this.columns.remove();
            String letC =ScrabbleServer.this.wordQGame.remove();
            ScrabbleServer.this.logicBoard [xC][yC] = letC;
            
            //Building move to send back to each client
            String xBack = Integer.toString(xC);
            String yBack = Integer.toString(yC);
            String letBack = letC;
            String builderSendBack = xBack+","+yBack+","+letC;
            
            
            ScrabbleServer.this.sendBackClient.add(builderSendBack); //Calls method to populate an arraylist to send back to other clients
            
            //Reset
            xC=0;
            yC=0;
            letC="";
         
         }
         ////////////////////////////////////////PLACE WORDS ON BOARD/////////////////////////////////////////////////
         
      }
      
       //Method to give new letters after valid move and change the turn
      public void validityPassed()
      {
         try{
            String refreshLetter;
           ////////////////////////////////////////SEND RACK LETTERS BACK TO CLIENT/////////////////////////////////////////////////
            for(int let = 0;let<intwordLength;let++){
               refreshLetter = "";
               refreshLetter = (String) ScrabbleServer.this.scrabbleLetterStack.pop();
               
               System.out.println("STACKS STACKS STACKS SIZEEEEE " + ScrabbleServer.this.scrabbleLetterStack.size());
               serverMessagesArea.append("STACKS STACKS STACKS SIZEEEEE " + ScrabbleServer.this.scrabbleLetterStack.size() + "\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
               System.out.println("Server thread  "+getName()+" sent letters to the client for board: "+refreshLetter);
               serverMessagesArea.append("Server thread  "+getName()+" sent letters to the client for board: "+refreshLetter + "\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
               writer.println(refreshLetter);
               writer.flush();  
               
                 
               System.out.println("Server thread  "+getName()+" recieved ACK for letters: "+reader.readLine());
               serverMessagesArea.append("Server thread  "+getName()+" recieved ACK for letters \n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            
            }
            ////////////////////////////////////////SEND RACK LETTERS BACK TO CLIENT/////////////////////////////////////////////////
            
               
            ////////////////////////////////////////CHANGE TURN///////////////////////////////////////////////// 
            changeTurn(getName()); 
            ////////////////////////////////////////CHANGE TURN///////////////////////////////////////////////// 
            
            
            
            
            
            ////////////////////////////////////////PRINT OUT TO OTHER CLIENTS///////////////////////////////////////////////// 
            System.out.println("///////////ENTERING GLOBAL WRITE PHASEE////////////////////"); 
            serverMessagesArea.append("///////////ENTERING GLOBAL WRITE PHASEE////////////////////\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            
            
            while(true)
            {
               ////////////////////////////////////////PCHECK TO SEE THAT ALL CLIENTS ARE WAITING///////////////////////////////////////////////// 
               if(getWaitCount() == ScrabbleServer.this.playerConnectionNumber-1){
                  System.out.println("WORKING////////////////////////////////////"); 
                  System.out.println("//////////////////////////////////////////////////////SOCKET COUNT: "+getWaitCount());
                  System.out.println("//////////////////////////////////////////////////////READER COUNT: "+ScrabbleServer.this.globalReaders.size());
                  System.out.println("//////////////////////////////////////////////////////WRITER COUNT: "+ScrabbleServer.this.globalWriters.size());
                  
                  serverMessagesArea.append("WORKING////////////////////////////////////\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  serverMessagesArea.append("//////////////////////////////////////////////////////SOCKET COUNT: "+getWaitCount() + "\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  serverMessagesArea.append("//////////////////////////////////////////////////////READER COUNT: "+ScrabbleServer.this.globalReaders.size() + "\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  serverMessagesArea.append("//////////////////////////////////////////////////////WRITER COUNT: "+ScrabbleServer.this.globalWriters.size() + "\n");
                  serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  
                  
                  synchronized(ScrabbleServer.this.sendBackSync)
                  {
                  ////////////////////////////////////////SEND BACK FOR THE NUMBER OF CLIENTS CONNECTED///////////////////////////////////////////////// 
                     for(int go = 0;go<getWaitCount();go++){
                        gReader = ScrabbleServer.this.globalReaders.pop();
                        gWriter = ScrabbleServer.this.globalWriters.pop();
                        String numberClient = ScrabbleServer.this.readerWriterNumber.pop();
                     
                        System.out.println("CREATED READER AND WRITER");
                        serverMessagesArea.append("CREATED READER AND WRITER\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                     
                        
                     ///////////////////////////////////////SEND GO FOR MOVE ACCEPTED/////////////////////////////////////////////////////////////
                        gWriter.println("GO");
                        gWriter.flush();
                     
                        System.out.println("SERVER READ GO ACK"+gReader.readLine()); 
                        serverMessagesArea.append("SERVER READ GO ACK\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                     
                     ///////////////////////////////////////SEND GO FOR MOVE ACCEPTED/////////////////////////////////////////////////////////////   
                        
                     ////////////////////////////////////////PRINT WORD LENGTH TO OTHER CLIENTS/////////////////////////////////////////////////    
                        System.out.println("SERVER SENT GLOBAL WORD LENGTH"+ScrabbleServer.this.sendBackClient.size());    
                        serverMessagesArea.append("SERVER SENT GLOBAL WORD LENGTH"+ScrabbleServer.this.sendBackClient.size() + "\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                      
                        
                        gWriter.println(Integer.toString(ScrabbleServer.this.sendBackClient.size()));
                        gWriter.flush();
                     
                        System.out.println("SERVER READ WORD LENGTH "+gReader.readLine()); 
                        serverMessagesArea.append("SERVER READ WORD LENGTH \n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length()); 
                     
                     ////////////////////////////////////////PRINT WORD LENGTH TO OTHER CLIENTS///////////////////////////////////////////////// 
                     
                     
                     ////////////////////////////////////////PRINT WORDS TO OTHER CLIENTS/////////////////////////////////////////////////   
                        for(String stringySend :ScrabbleServer.this.sendBackClient)
                        {
                           System.out.println("SERVER SENT GLOBAL MOVE BACK"+stringySend); 
                           serverMessagesArea.append("SERVER SENT GLOBAL MOVE BACK"+stringySend + "\n");
                           serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                        
                           gWriter.println(stringySend);
                           gWriter.flush();
                        
                        
                           System.out.println("SERVER READ GLOBAL MOVE"+gReader.readLine()); 
                           serverMessagesArea.append("SERVER READ GLOBAL MOVE\n");
                           serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                        
                        }
                     ////////////////////////////////////////PRINT WORDS TO OTHER CLIENTS//////////////////////////////////////////
                     
                     
                     ///////////////////////////////////////PRINT POINTS TO OTHER CLIENTS//////////////////////////////////////////
                        int[] inPoints =  Arrays.copyOf(ScrabbleServer.this.gamePoints, ScrabbleServer.this.gamePoints.length);
                     
                        for(int k =1;k<=inPoints.length;k++)
                        {
                           if(numberClient.equals(Integer.toString(k)))
                           {
                              System.out.println("SKIPPEDD");
                              serverMessagesArea.append("SKIPPEDD\n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                           
                           }
                           else
                           {
                              gWriter.println(Integer.toString(k));
                           
                              gWriter.flush();
                              System.out.println("SENT GLOBAL P POINTS "+Integer.toString(k));
                              System.out.println("recieve GLOBAL P POINTS "+gReader.readLine());
                              serverMessagesArea.append("SENT GLOBAL P POINTS\n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                              serverMessagesArea.append("recieve GLOBAL P POINTS \n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                           
                           
                              gWriter.println(Integer.toString(inPoints[k-1]));
                              gWriter.flush();
                           
                           
                              System.out.println("SENT GLOBAL POINTS "+inPoints[k-1]);
                              serverMessagesArea.append("SENT GLOBAL POINTS\n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                              System.out.println("recieve GLOBAL POINTS "+gReader.readLine());
                              serverMessagesArea.append("recieve GLOBAL POINTS \n");
                              serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                           
                           } 
                        }
                     ///////////////////////////////////////PRINT POINTS TO OTHER CLIENTS//////////////////////////////////////////
                        
                     
                     ///////////////////////////////////////PRINT TURN TO OTHER CLIENTS//////////////////////////////////////////
                        System.out.println("SERVER SENT "+getTurn()); 
                        serverMessagesArea.append("SERVER SENT "+getTurn() + "\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length()); 
                     
                        gWriter.println(getTurn());
                        gWriter.flush();
                        
                        
                        System.out.println("SERVER READ TURN NUMBER"+gReader.readLine()); 
                        serverMessagesArea.append("SERVER READ TURN NUMBER\n");
                        serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length()); 
                     
                     ///////////////////////////////////////PRINT TURN TO OTHER CLIENTS//////////////////////////////////////////
                     
                     }
                  }
                  
                  
                  
                  
                  
                  break;
                  
               }
               
            } 
            ////////////////////////////////////////PRINT OUT TO OTHER CLIENTS///////////////////////////////////////////////// 
            
            
            
            System.out.println("//////////////////////////////////////////////////ENDING GLOBAL WRITE PHASEE///////////////////////////////////////////////");  
            serverMessagesArea.append("//////////////////////////////////////////////////ENDING GLOBAL WRITE PHASEE///////////////////////////////////////////////\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            
            ///////////////////////////////////////PRINT POINTS TO LOCAL CLIENT//////////////////////////////////////////
            String pointy ="";
            pointy = Integer.toString(ScrabbleServer.this.gamePoints[this.clientNumber-1]);
            System.out.println(pointy+"SENDDDDE POINTTT");
            serverMessagesArea.append("SENT POINTS\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            
            writer.println(pointy);
            writer.flush();
            reader.readLine();
            ///////////////////////////////////////PRINT POINTS TO LOCAL CLIENT//////////////////////////////////////////
         
            
            ////////////////////////////////////////PRINT OUT TURN/////////////////////////////////////////////////
            System.out.println("Server thread  "+getName()+" wrote out the turnNumber "+getTurn());
            serverMessagesArea.append("Server thread  "+getName()+" wrote out the turnNumber "+getTurn() + "\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            
            writer.println(getTurn());
            writer.flush();
            ////////////////////////////////////////PRINT OUT TURN/////////////////////////////////////////////////
            
            
            ////////////////////////////////////////TURN NUMBER ACK/////////////////////////////////////////////////
            System.out.println("Server thread  "+getName()+" recived ACK for turnNumber: "+reader.readLine());
            serverMessagesArea.append("Server thread  "+getName()+" recived ACK for turnNumber\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            ////////////////////////////////////////TURN NUMBER ACK/////////////////////////////////////////////////
            
            ////////////////////////////////////////NOTIFY OTHER CLIENTS/////////////////////////////////////////////////
            synchronized(ScrabbleServer.this.globalONE)
            {
               
               System.out.println("Client "+getName()+" GLOBALTWO NOTIFY");
               serverMessagesArea.append("Client "+getName()+" GLOBALTWO NOTIFY\n");
               serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
               
               ScrabbleServer.this.globalONE.notifyAll();
               
            }
            ////////////////////////////////////////NOTIFY OTHER CLIENTS/////////////////////////////////////////////////
            
            ////////////////////////////////////////RESET///////////////////////////////////////////////// 
            ScrabbleServer.this.globalONE = null;
            ScrabbleServer.this.globalONE = new Object();
            ScrabbleServer.this.globalWriters.clear(); //The writers for the waiting clients
            ScrabbleServer.this.globalReaders.clear(); //The readers for the waiting clients
            ScrabbleServer.this.sendBackClient.clear(); //The letters that will be sent back to the other clients
            ScrabbleServer.this.socketWaitCount = 0; //The amount of clients at the wait
            ScrabbleServer.this.sentWordValue = 0; //Value of the word being sent to the server
            ScrabbleServer.this.rows.clear(); //pull the x index and store it in a queue
            ScrabbleServer.this.columns.clear(); //pull the y index and store it in a queue
            ScrabbleServer.this.wordQGame.clear(); //pull the letter and store it in a queue in order to place it on the board
            ScrabbleServer.this.wordQ.clear(); //pull the letter and store it in a arraylist in order to validate word 
            ScrabbleServer.this.wordCheck = false;
            ////////////////////////////////////////RESET///////////////////////////////////////////////// 
            
             
             
            System.out.println("Server thread  "+getName()+" Done with MOVE THREAD");
            serverMessagesArea.append("Server thread  "+getName()+" Done with MOVE THREAD\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
            System.out.println();
            serverMessagesArea.append("\n");
            serverMessagesArea.setCaretPosition(serverMessagesArea.getText().length());
                  
         }
         catch(Exception ery)
         {
            ery.printStackTrace();
         }
      }
         
   }
  
  
   public static void main(String [] args){
      
      int port = Integer.parseInt(args[0]);
      
      ScrabbleServer run = new ScrabbleServer(port);
      
   }
}

