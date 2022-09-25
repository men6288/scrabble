import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Color;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import java.net.Socket.*;
import java.net.*;

public class GameBoard extends JFrame implements ActionListener{
   
   String chatIp = "localhost";
   int chatPort = 15000;
   
   String IPADDRESS1 = "10.190.100.5";
   String IPADDRESS2 = "10.190.100.6";
   String IPADDRESS3 = "10.190.100.7";
   String IPADDRESS4 = "10.190.100.8";
   String IPADDRESS5 = "10.190.100.5";
   String IPADDRESS6 = "10.190.100.6";
   String IPADDRESS7 = "10.190.100.7";
   String IPADDRESS8 = "localhost";
   
   int PORT1 = 15001;
   int PORT2 = 15002;
   int PORT3 = 15003;
   int PORT4 = 15004;
   int PORT5 = 15005;
   int PORT6 = 15006;
   int PORT7 = 15007;
   int PORT8 = 15008;
   
   
   int port;
   String message;
   int turnNumber;
   String userName;
   String ipAddress;
   String player1Name;
   String player2Name;
   String player3Name;
   String player4Name;
   
   boolean wait = false;
   boolean go = true;
   int clientNumber = 0;
   int lettersNeeded = 0;
   boolean usernameSet = false;
   int boardPoints = 0;
   

   //Socket and in/out streams for the CHAT
   Socket chatSocket = null;InputStream chatIn = null;OutputStream chatOut = null;
   BufferedReader chatBin = null;PrintStream chatPout = null;
   
   //Socket and in/out streams for the GAME
   Socket gameSocket = null;InputStream gameIn = null;OutputStream gameOut = null;
   BufferedReader gameBin;PrintStream gamePout;
   
   //Icon object for the game board.
   ImageIcon undoImage = new ImageIcon("../Assets/Image/undoButton.png");
   ImageIcon shuffleImage = new ImageIcon("../Assets/Image/shuffleButton.png");
   ImageIcon endTurnImage = new ImageIcon("../Assets/Image/sendMove.png");
   ImageIcon tileBack = new ImageIcon("tile.jpg");
   ImageIcon rackTile = new ImageIcon("blankTileResized.jpg");
   ImageIcon icon = new ImageIcon("../Assets/Image/Letters/rack/W.jpg");
   
   //Array to hold all the background tiles
   ArrayList<BoardTile> backgroundTileArray = new ArrayList<BoardTile>();
   ArrayList<JButton> rackTileArray = new ArrayList<JButton>();
   ArrayList<String> lettersFromServer = new ArrayList<String>();
   //ArrayList<String> playerMoves = new ArrayList<String>();
 
   //method that creates actual gameboard buttons. Stored in backgroundTileArray
  
   public void createBoard(){
      for(int i = 0; i<225;i++){
      
         if( i != 112){
            BoardTile bt = new BoardTile("board");
            backgroundTileArray.add(bt);
            
         }
         else{
            BoardTile bt1 = new BoardTile("center");
            backgroundTileArray.add(bt1);
         }
      
         BoardTile.tileNumber++;
      }
      
      for(int j = 0; j < 225; j++){
         //System.out.println("Size of Array: " + backgroundTileArray.size() );
         gameBoardPanel.add( (backgroundTileArray.get(j)).fetchButton() );
      
      }
   
   }
  
   //JButton jb = new JButton("test");
   
   
   JButton shuffleButton = new JButton(shuffleImage);
   JButton endTurnButton = new JButton(endTurnImage);
   JButton test = new JButton("bloop");
      
   //all the panels for the UI. Will need to do some work here
   JPanel gameBoardPanel = new JPanel(new GridLayout(15,15));JPanel playerPanel = new JPanel(new FlowLayout());
   JPanel chatPanel = new JPanel(new BorderLayout());JPanel chatWindowPanel = new JPanel(new GridLayout(1,1));
   JPanel chatPane = new JPanel(new GridBagLayout());GridBagConstraints gbc = new GridBagConstraints();
   JPanel bottomPanel = new JPanel(new BorderLayout());JPanel rackPanel = new JPanel(new GridLayout(1,7));
   JPanel separatorPanel = new JPanel(new GridLayout(1,1));JPanel playButtonPanel = new JPanel(new GridLayout(1,3));
   JPanel serverListPanel = new JPanel();
   
   //Top Row Player names panel
   JLabel player1Label = new JLabel("Player 1");JTextField player1ScoreField = new JTextField(2);
   JLabel player2Label = new JLabel("Player 2");JTextField player2ScoreField = new JTextField(2);
   JLabel player3Label = new JLabel("Player 3");JTextField player3ScoreField = new JTextField(2);
   JLabel player4Label = new JLabel("Player 4");JTextField player4ScoreField = new JTextField(2);
   
   //individual parts for the chat, add the textArea for the list of names in the chat
   JTextArea chatArea = new JTextArea(10, 10);JScrollPane chatScroll = new JScrollPane(chatArea);
   JTextField chatField = new JTextField(10);JButton enterButton = new JButton("Enter");
   
   //JMenuBar
   JMenuBar jmb = new JMenuBar();JMenu menu = new JMenu("Menu");
   JMenuItem newGameItem = new JMenuItem("New Game");JMenuItem quitItem = new JMenuItem("Quit");
   JButton undoButton = new JButton(undoImage);
   
   
   public GameBoard(){
      
      
      //size is set, can be changed, "relativeToNull" puts the window in the middle of screen
      setSize(830, 680);
      setResizable(false);
      setLocationRelativeTo(null);
      setTitle("Werds with Nerds");
      
      //serverlist pop up, disable serverSelect() to disable popup
      serverListPanel.setLayout(new GridLayout(8,1));
      ButtonGroup bg = new ButtonGroup();
      JRadioButton server1 = new JRadioButton("Winter is Coming");
      JRadioButton server2 = new JRadioButton("Billy's Server");
      JRadioButton server3 = new JRadioButton("Kennedy's Backet of Deplorables");
      JRadioButton server4 = new JRadioButton("La casa de tu madre");
      JRadioButton server5 = new JRadioButton("Bush did Harambe");
      JRadioButton server6 = new JRadioButton("Trump's America");
      JRadioButton server7 = new JRadioButton("More Cowbell");
      JRadioButton server8 = new JRadioButton("localhost(launch server on port 15008)");
      bg.add(server1);bg.add(server2);bg.add(server3);bg.add(server4);bg.add(server5);
      bg.add(server6);bg.add(server7);bg.add(server8); 
   
      serverListPanel.add(server1);serverListPanel.add(server2);serverListPanel.add(server3);
      serverListPanel.add(server4);serverListPanel.add(server5);serverListPanel.add(server6);
      serverListPanel.add(server7);serverListPanel.add(server8);
      
      serverSelect();//disable this to disable pop up
      if(server1.isSelected()){ipAddress = IPADDRESS1;port = PORT1;}
      else if(server2.isSelected()){ipAddress = IPADDRESS2;port = PORT2;}
      else if(server3.isSelected()){ipAddress = IPADDRESS3;port = PORT3;}
      else if(server4.isSelected()){ipAddress = IPADDRESS4;port = PORT4;}
      else if(server5.isSelected()){ipAddress = IPADDRESS5;port = PORT5;}
      else if(server6.isSelected()){ipAddress = IPADDRESS6;port = PORT6;}
      else if(server7.isSelected()){ipAddress = IPADDRESS7;port = PORT7;}
      else if(server8.isSelected()){ipAddress = IPADDRESS8;port = PORT8;}
      
      player1Label.setBackground(Color.YELLOW);player2Label.setBackground(Color.YELLOW);
      player3Label.setBackground(Color.YELLOW);player4Label.setBackground(Color.YELLOW);
      
      //adding gameBoardPanel to background panel
      add(gameBoardPanel, BorderLayout.CENTER);
      //add(leftSidePanel, BorderLayout.WEST);
      add(bottomPanel, BorderLayout.SOUTH);
      add(playerPanel, BorderLayout.NORTH);
      add(chatPanel, BorderLayout.EAST);
      setJMenuBar(jmb);
      
      gameBoardPanel.setBackground(Color.BLACK);
      gameBoardPanel.setOpaque(true);
      
      //left side panel stuff   
      bottomPanel.add(rackPanel, BorderLayout.CENTER);
      bottomPanel.add(separatorPanel, BorderLayout.WEST);
      bottomPanel.add(playButtonPanel, BorderLayout.EAST);
   
      createRack();
      undoButton.setOpaque(true);
      //undoButton.setContentAreaFilled(false);
      //undoButton.setBorderPainted(false);
      shuffleButton.setOpaque(true);
      //shuffleButton.setContentAreaFilled(false);
      //shuffleButton.setBorderPainted(false);
      endTurnButton.setOpaque(true);
      //endTurnButton.setContentAreaFilled(false);
      //endTurnButton.setBorderPainted(false);
      JButton blankButton = new JButton("");
      blankButton.setOpaque(false);
      blankButton.setBorderPainted(false);
      separatorPanel.add(undoButton);
      playButtonPanel.add(blankButton);
      playButtonPanel.add(shuffleButton);
      playButtonPanel.add(endTurnButton);
            
      createBoard(); //This method creates and add tiles to panel
      //rewriteBoard();
           
      //populating top row player panel
      playerPanel.add(player1Label);playerPanel.add(player1ScoreField);playerPanel.add(player2Label);
      playerPanel.add(player2ScoreField);playerPanel.add(player3Label);playerPanel.add(player3ScoreField);
      playerPanel.add(player4Label);playerPanel.add(player4ScoreField);
      player1ScoreField.setHorizontalAlignment(JTextField.CENTER);player2ScoreField.setHorizontalAlignment(JTextField.CENTER);
      player3ScoreField.setHorizontalAlignment(JTextField.CENTER);player4ScoreField.setHorizontalAlignment(JTextField.CENTER);
      player1ScoreField.setText("0");player2ScoreField.setText("0");
      player3ScoreField.setText("0");player4ScoreField.setText("0");
            
      //putting the chat panel stuff together
      chatPanel.add(chatWindowPanel, BorderLayout.CENTER);
      chatPanel.add(chatPane, BorderLayout.SOUTH);
      chatField.setText("Enter Username!");
      
      //code for the textField and enter button
      gbc.gridx = 0;gbc.gridy = 0;gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.WEST;chatPane.add(chatField, gbc);
      gbc.gridx++;gbc.insets = new Insets(0, 0, 0, 0);chatPane.add(enterButton, gbc);
   
      chatWindowPanel.add(chatScroll);
      chatArea.setEditable(false);
      chatArea.setLineWrap(true);
      chatArea.setWrapStyleWord(true);
      
      //**menu items
      jmb.add(menu);
      menu.add(newGameItem);
      menu.add(quitItem); 
      
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);  
      chatField.requestFocusInWindow(); //this has to stay AFTER the setVisible command
      
      //ActionListeners *********
      undoButton.addActionListener(this);
      enterButton.addActionListener(this);
      shuffleButton.addActionListener(this);
      endTurnButton.addActionListener(this);
      chatField.addActionListener(this);
      quitItem.addActionListener(this);
      newGameItem.addActionListener(this);
      enterButton.setActionCommand("keyboarded");chatField.setActionCommand("clicked");
      
      //***********************Socket Stuff**********
      try{
      
         chatSocket = new Socket(chatIp, chatPort);
         gameSocket = new Socket(ipAddress, port);
         
         chatIn = chatSocket.getInputStream();
         chatOut = chatSocket.getOutputStream();
         chatPout = new PrintStream(chatOut);
         chatBin = new BufferedReader(new InputStreamReader(chatIn));
         
         gameIn = gameSocket.getInputStream();
         gameOut = gameSocket.getOutputStream();
         gamePout = new PrintStream(gameOut);
         gameBin = new BufferedReader(new InputStreamReader(gameIn));
         
         ChatInnerClass chatThread = new ChatInnerClass(chatArea,chatBin);
         GameInnerClass gameThread = new GameInnerClass(gameBin, gamePout);
         //ListenInnerClass listenThread = new ListenInnerClass();
         
         Thread game = new Thread(gameThread);
         Thread chat = new Thread(chatThread);
         //Thread listen = new Thread(listenThread);
         
         game.start();
         chat.start();   
      }
      
      catch(UnknownHostException uhe) {
         System.out.println("no host");
         uhe.printStackTrace();
      }
      catch(IOException ioe)
      {
         System.out.println("IO error");
          //ioe.printStackTrace();
         JOptionPane.showMessageDialog(null, "¡Server Not Found!", "ERROR", JOptionPane.ERROR_MESSAGE);
         System.exit(0);
      }
      //*********************************End of chat socket stuff********************************************
      //createRack();//maybe change
   }
   
   public void createRack(){
      for(int j = 0; j <= 6; j++){
         //RackTile rt = new RackTile(lettersFromServer.get(j) );// lettersFromServer.get(j) ); //lettersFromServers is the ArrayList
         RackTile rt = new RackTile("_");
         rackTileArray.add(rt.fetchButton() );
         rt.setEnabled(true);
         rackPanel.add(rackTileArray.get(j));
      }
   }
   
   public void populateRack(){
   
      rackPanel.removeAll();
      //getContentPane().removeAll();
   
      rackPanel.revalidate();
      rackPanel.repaint();
      for(int y = 0; y <= 6; y++){
          
         RackTile rt   =  new RackTile(lettersFromServer.get(y) );// lettersFromServer.get(j) ); //lettersFromServers is the ArrayList
         rackTileArray.add(rt.fetchButton());
         rackPanel.add(rackTileArray.get(y));
         rackPanel.revalidate();
         rackPanel.repaint();
      }
   }

   
   public void actionPerformed(ActionEvent ae){
      String actionCommand = ae.getActionCommand();
      
      if(actionCommand.equals("keyboarded") || actionCommand.equals("clicked")) 
      {
         try
         {
            if(usernameSet == false){
               userName = chatField.getText();
               chatPout.println(userName);
               chatField.setText("");
               //setPlayerNumber();
               chatPout.flush();
               usernameSet = true;
            }
            else{
               message = chatField.getText();
               chatPout.println(message);
               chatField.setText("");
               chatPout.flush();
            } 
         }
         catch(Exception ioe)
         {}
      }
      else if(actionCommand.equals("Quit") ){
      
         try{
            chatSocket.close();
            System.exit(0);  
         }
         catch(Exception e){}
         
      }
      else if(/*actionCommand.equals("End Turn") */endTurnButton.equals(ae.getSource())){
      
         doMove();            
      }
      else if(/*actionCommand.equals("Shuffle")*/shuffleButton.equals(ae.getSource())){
      
         shuffleRackTiles(); 
      }
      else if(/*actionCommand.equals("Undo")*/undoButton.equals(ae.getSource())){
         
         undo();
      }
   }
   
   public boolean waitForMoves(){
      // String skipGoWin;
   //       
   //       System.out.println("waitForMoves() started");
   //       try{
   //          
   //          System.out.println("Reading in GO, WIN, or SKIP");
   //          int numLettersFromServer = 0;
   //          skipGoWin = gameBin.readLine();
   //          System.out.println("From server - SkipGoWin = " + skipGoWin);
   //          gamePout.println("ACKskipGoWin");
   //          System.out.println("ACKskipGoWin sent to server");
   //          
   //          if(skipGoWin.equals("WIN")){
   //             int player1Score = Integer.parseInt(player1ScoreField.getText());
   //             int player2Score = Integer.parseInt(player2ScoreField.getText());
   //             int player3Score = Integer.parseInt(player3ScoreField.getText());
   //             int player4Score = Integer.parseInt(player1ScoreField.getText());
   //             System.out.println("Player 1 score = " + player1Score);
   //             System.out.println("Player 2 score = " + player2Score);
   //             System.out.println("Player 3 score = " + player3Score);
   //             System.out.println("Player 4 score = " + player4Score);
   //             
   //             if(player1Score > player2Score && player1Score > player3Score 
   //                && player1Score > player4Score){
   //                
   //                JOptionPane.showMessageDialog(null,"Player 1 WINS!!");
   //                disableButtons();
   //             }
   //             else if(player2Score > player1Score && player2Score > player3Score 
   //                && player2Score > player4Score){
   //                
   //                JOptionPane.showMessageDialog(null,"Player 2 WINS!!");
   //                disableButtons();
   //             }
   //             else if(player3Score > player1Score && player3Score > player2Score 
   //                && player3Score > player4Score){
   //                
   //                JOptionPane.showMessageDialog(null,"Player 3 WINS!!");
   //                disableButtons();
   //             }
   //             else if(player4Score > player1Score && player4Score > player2Score 
   //                && player4Score > player3Score){
   //                
   //                JOptionPane.showMessageDialog(null,"Player 4 WINS!!");
   //                disableButtons();
   //             }
   //             else if(player4Score == player1Score && player4Score == player2Score 
   //                && player4Score == player3Score){
   //                
   //                JOptionPane.showMessageDialog(null,"TIE GAME, YOU'RE ALL WINNERS!!");
   //                disableButtons();
   //             }  
   //          }
   //          else if(skipGoWin.equals("SKIP")){
   //             System.out.println("SKIP triggered, reading turnNumber");
   //             this.turnNumber = Integer.parseInt(gameBin.readLine());
   //             System.out.println("Turn Number = " + turnNumber);
   //             gamePout.println("ACK After turnNumber received");
   //             System.out.println("ACK After turnNumber, sent to server");
   //             //JOptionPane.showMessageDialog(null,"Move Skipped!");
   //             whosTurn();
   //             disableButtons();
   //             System.out.println("whosTurn() triggered");
   //             
   //             if(turnNumber != clientNumber){
   //                
   //                System.out.println("clientNumber != turnNumber, waitForMoves() triggered again");
   //                waitForMoves();
   //             }
   //             else if(this.turnNumber == this.clientNumber){
   //                //JOptionPane.showMessageDialog(null, "It's your turn again!");
   //                enablePlayTiles();
   //                endTurnButton.setEnabled(true);
   //                return true;
   //                //break;
   //             }
   //          }
   //          else if(skipGoWin.equals("GO")){
   //             numLettersFromServer = Integer.parseInt(gameBin.readLine());
   //             System.out.println("Length recieved from SERVER =" + numLettersFromServer);
   //             gamePout.println("ACK(received number of letters that will be sent from server)");
   //             System.out.println("Sent ACK for numLettersFromServer");
   //             //System.out.println("number of letters to be sent from server: " + numLettersFromServer + "Client:" + clientNumber);
   //             String reep;         
   //             for(int h = 0; h < numLettersFromServer; h++){
   //                     
   //                playerMoves.add(gameBin.readLine());
   //               
   //                gamePout.println("ACK(Received player moves)Client:"+clientNumber);
   //                System.out.println("sending ack after receiving a player's moves");
   //                //System.out.println(playerMoves.get(h));
   //             }
   //             rewriteBoard();
   //             revalidate();
   //             repaint();
   //             for(int h = 0; h < 3; h++){
   //                
   //                 
   //                String pl = gameBin.readLine(); 
   //                System.out.println("Player Number points recieved:::::::::::::::::::::::::: "+pl);
   //                gamePout.println("ACK");
   //               
   //                String pt = gameBin.readLine();
   //                System.out.println("Player points recieved:::::::::::::::::::::::::: "+pt); 
   //                gamePout.println("ACK");
   //                
   //                setPoints(pl,pt);
   //             }
   //             
   //                
   //             //System.out.println("\nListening for turn number Client:"+ clientNumber);
   //             this.turnNumber = Integer.parseInt(gameBin.readLine());
   //             whosTurn();
   //             System.out.println(this.turnNumber+" Turn recieved");
   //             //System.out.println("Client Number: " + clientNumber);
   //             //System.out.println("Turn Number: " + turnNumber);
   //             String ackTurnNumRec = "ACKturnNumberClient" + clientNumber;
   //             
   //             //if(go = true){
   //             
   //             gamePout.println(ackTurnNumRec);
   //                //go = false;
   //                //System.out.println("Go set to false");
   //             //}   
   //             System.out.println("sending ack after receiving turn number in waitForMoves()");
   //             whosTurn();//Line 454ish
   //             System.out.println("Turn number: " + turnNumber);
   //             // if(this.turnNumber == this.clientNumber){
   //          //                JOptionPane.showMessageDialog(null, "It's your turn again!");
   //          //                enablePlayTiles();
   //          //                endTurnButton.setEnabled(true);
   //          //                return true;
   //          //                //break;
   //          //             }
   //          
   //             if(this.turnNumber == this.clientNumber){
   //                //JOptionPane.showMessageDialog(null, "It's your turn again!");
   //                enablePlayTiles();
   //                endTurnButton.setEnabled(true);
   //                return true;
   //                //break;
   //             }
   //          }
   //          
   //          // if(this.turnNumber == this.clientNumber){
   //       //             JOptionPane.showMessageDialog(null, "It's your turn again!");
   //       //             enablePlayTiles();
   //       //             endTurnButton.setEnabled(true);
   //       //             return true;
   //       //                //break;
   //       //          }
   //          
   //       }  
   //       catch(Exception e){
   //                   //System.out.println("Catch triggered while listening for a 1");
   //       }
      return false;
   }
   
   //////////////////////////////////////////////////////////////////////////////////////////////////////////
   public synchronized void setPoints(String _player, String value)
   {  
      int player = Integer.parseInt(_player);
      if(player == 1)
      {
         this.player1ScoreField.setText(value);
      }
      else if(player == 2)
      {
         this.player2ScoreField.setText(value);
      }
      else if(player == 3)
      {
         this.player3ScoreField.setText(value);
      }
      else if(player == 4)
      {
         this.player4ScoreField.setText(value);
      }
   
   }
   
   public void repaintLabels(){
   
      if(turnNumber == 1){
      
         turnNumber = 2;
         whosTurn();
      }
      else if(turnNumber == 2){
      
         turnNumber = 3;
         whosTurn();
      }
      else if(turnNumber == 3){
      
         turnNumber = 4;
         whosTurn();
      }
      else if(turnNumber == 4){
      
         turnNumber = 1;
         whosTurn();
      } 
   }
  
   public void doMove(){
      repaintLabels();
      BoardTile retriever = new BoardTile("");
      lettersNeeded = retriever.getLengthMoves2Server();
      //lettersNeeded = 0;
      try{
         
         if(lettersNeeded == 0){
         
            gamePout.println("SKIP");
            System.out.println("Ack for lettersNeeded size = 0 - " + gameBin.readLine());
                  
            disableButtons();
            endTurnButton.setEnabled(false);
            //waitForMoves();
            System.out.println("skip triggered, listen thread started");
            ListenInnerClass listenThread = new ListenInnerClass();
            Thread listen = new Thread(listenThread);
            listen.start();
         
         }
         else{
            enablePlayTiles();
            endTurnButton.setEnabled(true);
            
            gamePout.println("END");
            System.out.println("Sending END to SERVER");
            
            System.out.println(gameBin.readLine());
            
            gamePout.println(lettersNeeded);
            System.out.println(gameBin.readLine());
         //*********************************************************************
            for(int i = 0; i < lettersNeeded; i++){
                     
               System.out.println("lettersNeeded = " + lettersNeeded);
                        //System.out.println(retriever.popMoves2Server() + " sent to server");
               String sent = retriever.popMoves2Server();
               gamePout.println(sent);
                        
               System.out.println("sent = " + sent);
               System.out.println(gameBin.readLine()); 
            }
         //**********************************************************************
            // gamePout.println("0,0,a");
         //             System.out.println("sending 00a");
         //             System.out.println(gameBin.readLine());
         //             gamePout.println("0,1,s");
         //             System.out.println("sending 01s");
         //             System.out.println(gameBin.readLine());
         //             gamePout.println("0,2,s");
         //             System.out.println("sending 02s");
         //             System.out.println(gameBin.readLine());
         //**********************************************************************
         //System.out.println(gameBin.readLine() + "getting ack from server for letters sentClient:"+clientNumber);
            gamePout.println("SYNCafterSendingLettersClient:"+clientNumber);
            System.out.println("SYNC after sending letters(move) to server");
         //**************************************      
         //System.out.println("reading whether move is accepted or notClient:"+clientNumber);
            String moveAccepted = gameBin.readLine();
            String ackMoveAccepted = "ackMovAccptClient" + clientNumber;
            gamePout.println(ackMoveAccepted);
            System.out.println("sending ack for move accepted");
            System.out.println("Move accepted = " + moveAccepted);
         
         ////////////////////////////////////////Recieve POINTS TO CLIENT/////////////////////////////////////////////////
         // String points = "";
         // points = gameBin.readLine();
         // System.out.println(points+" GOT ME SOME POINTS");
         // gamePout.println("ACK BABY");
         // setPoints(points);
         ////////////////////////////////////////Recieve POINTS TO CLIENT/////////////////////////////////////////////////
                   
            if(moveAccepted.equals("YES")){
               disableButtons();
               endTurnButton.setEnabled(false);
               //JOptionPane.showMessageDialog(null,"Move Accepted!");
            
               for(int t = 0; t < lettersNeeded; t++){              
               
                  lettersFromServer.add(gameBin.readLine());
               //System.out.println(gameBin.readLine());
                  String ackRecLetters = "ackRecLettersClient:" + clientNumber;
                  gamePout.println(ackRecLetters);
                  System.out.println("sending ack that letters were reveived");
               //System.out.println("ack(received replacement lettersClient:"+clientNumber);
               }
            
               String point = gameBin.readLine();
               gamePout.println("Recieved points");
            
               setPoints(Integer.toString(this.clientNumber),point);
            
               turnNumber = Integer.parseInt(gameBin.readLine());
               whosTurn();
               System.out.println("Turn number: " + turnNumber);
               gamePout.println("ACK(turnNumber after replacement lettersClient:" + clientNumber);
               System.out.println("sending ack for turn number after getting replacement letter");
            //System.out.println("transitioning from receiving letters to waiting for movesClient:"+clientNumber);
            
               ListenInnerClass listenThread = new ListenInnerClass();
               Thread listen = new Thread(listenThread);
               listen.start();
            //******************************************************
              
              
              
               // while(true){
            //                //System.out.println("in doMove() waiting for other players");
            //                
            //                //if( wait != true){
            //                   if(clientNumber != turnNumber){
            //                      System.out.println("wait = false, keep waiting");
            //                   //wait = waitForMoves();
            //                   //break;
            //                      //waitForMoves();
            //                      System.out.println
            //                      Thread listen = new Thread();
            //                      listen.start();
            //                   }
            //                   //else if(wait == true){
            //                   else if(clientNumber == turnNumber){
            //                      System.out.println("wait = true, stop waiting");
            //                      enablePlayTiles();
            //                      endTurnButton.setEnabled(true);
            //                      break;
            //                   }
            //                }    
            }
            
            else if(moveAccepted.equals("NO")){
            
               JOptionPane.showMessageDialog(null,"Move declined, try again!");
            }
            else if(moveAccepted.equals("WIN")){
            
               System.out.println("Received a WIN");
               int player1Score = Integer.parseInt(player1ScoreField.getText());
               int player2Score = Integer.parseInt(player2ScoreField.getText());
               int player3Score = Integer.parseInt(player3ScoreField.getText());
               int player4Score = Integer.parseInt(player1ScoreField.getText());
               System.out.println("Player 1 score = " + player1Score);
               System.out.println("Player 2 score = " + player2Score);
               System.out.println("Player 3 score = " + player3Score);
               System.out.println("Player 4 score = " + player4Score);
            
               if(player1Score > player2Score && player1Score > player3Score 
               && player1Score > player4Score){
               
                  JOptionPane.showMessageDialog(null,"Player 1 WINS!!");
                  disableButtons();
               }
               else if(player2Score > player1Score && player2Score > player3Score 
               && player2Score > player4Score){
               
                  JOptionPane.showMessageDialog(null,"Player 2 WINS!!");
                  disableButtons();
               }
               else if(player3Score > player1Score && player3Score > player2Score 
               && player3Score > player4Score){
               
                  JOptionPane.showMessageDialog(null,"Player 3 WINS!!");
                  disableButtons();
               }
               else if(player4Score > player1Score && player4Score > player2Score 
               && player4Score > player3Score){
               
                  JOptionPane.showMessageDialog(null,"Player 4 WINS!!");
                  disableButtons();
               }
               else if(player4Score == player1Score && player4Score == player2Score 
               && player4Score == player3Score){
               
                  JOptionPane.showMessageDialog(null,"TIE GAME, YOU'RE ALL WINNERS!!");
                  disableButtons();
               }   
            }
         }                
      }
      catch(NullPointerException npe){
         npe.printStackTrace();
         //System.out.println("NPE Error in doMove()");
      }
      catch(IOException ioe){
      
         System.out.println("IOException in doMove()");
      } 
    
        
   }
   
   public void serverSelect(){
      
      if(clientNumber < 4){
         JOptionPane.showMessageDialog(null,  serverListPanel, "Werds with Nerds! \nSelect A Server", JOptionPane.INFORMATION_MESSAGE, icon);
      }
      else{
         JOptionPane.showMessageDialog(null,  serverListPanel, "Server FULL! Select A Different Server", JOptionPane.WARNING_MESSAGE/*, icon*/);
      }   
   }
   
   public void disableButtons(){
   
      for(int x = 0; x < backgroundTileArray.size(); x++){
         
         ((backgroundTileArray.get(x))).disableButton();
      }
      for(int y = 0; y < rackTileArray.size(); y++){
      
         rackTileArray.get(y).setEnabled(false);
      }
      
      endTurnButton.setEnabled(false);
   }
   
   // public void setPlayerNumber(){
//    
//       if(clientNumber == 1){
//          player1Label.setText(userName);
//       }
//       else if(clientNumber == 2){player2Label.setText(userName);}
//       else if(clientNumber == 3){player3Label.setText(userName);}
//       else if(clientNumber == 4){player4Label.setText(userName);}
//    }
   
   public void enablePlayTiles(){
   
      for(int k = 0; k < backgroundTileArray.size(); k++){
                  
         backgroundTileArray.get(k).fetchButton().setEnabled(true);
                     
      }  
      for(int x = 0; x <rackTileArray.size(); x++){
         rackTileArray.get(x).setEnabled(true);
      }
   }
   public void whosTurn(){
   
      if(turnNumber == 1){
         player1Label.setOpaque(true);
         player2Label.setOpaque(false);
         player3Label.setOpaque(false);
         player4Label.setOpaque(false);
         revalidate();
         repaint();
      }
      else if(turnNumber == 2){
         player1Label.setOpaque(false);
         player2Label.setOpaque(true);
         player3Label.setOpaque(false);
         player4Label.setOpaque(false);
         revalidate();
         repaint();
      }
      else if(turnNumber == 3){
         player1Label.setOpaque(false);
         player2Label.setOpaque(false);
         player3Label.setOpaque(true);
         player4Label.setOpaque(false);
         revalidate();
         repaint();
      }
      else if(turnNumber == 4){
         player1Label.setOpaque(false);
         player2Label.setOpaque(false);
         player3Label.setOpaque(false);
         player4Label.setOpaque(true);
         revalidate();
         repaint();
      }   
   }
   
   public static void main(String [] args){
     
      GameBoard gb = new GameBoard();     
   }
   
     
   class GameInnerClass implements Runnable{
      BufferedReader br;
      PrintStream pw;
   
      public GameInnerClass(BufferedReader _br, PrintStream _pw ){
         this.br = _br;
         this.pw = _pw;
      }
   
      public void run(){
         
         boolean keepGoing = true;
         disableButtons();//disable buttons by default
         try{
         
            pw.println("Initial Send");
            System.out.println("Initial Send");
            GameBoard.this.clientNumber = Integer.parseInt(br.readLine());System.out.println("I am client #: " + clientNumber);
            String initSendAck = "ACKInitialSendClient" + clientNumber;
            gamePout.println(initSendAck);
            System.out.println(initSendAck);
            
            rackTileArray.clear();
            //System.out.println("did it clear?: " + rackTileArray.size() );
            for(int i = 0; i < 7; i++){//gets 7 letters back from server
            
               lettersFromServer.add(br.readLine());
               //System.out.println(lettersFromServer.get(i));
               pw.println("receiving letters..." + clientNumber);
               System.out.println("ACK after receiving letters");
            } 
            populateRack();
            //rackPanel.remove(rt);
            JOptionPane.showMessageDialog(null, "You are player number " + clientNumber + "!");
            if(clientNumber <= 3){
               JOptionPane.showMessageDialog(null, "Waiting for others to join");
            }   
            GameBoard.this.turnNumber = Integer.parseInt(br.readLine());
            System.out.println("Turn Number: " + turnNumber);
            whosTurn();
            pw.println("ACKClient" + clientNumber);
            System.out.println("sending ack that turn number is received on initial run");
            //System.out.println(br.readLine());
            System.out.println(br.readLine());
            
            
            if(GameBoard.this.clientNumber == GameBoard.this.turnNumber){
               System.out.println("It's your turn");
                  //JOptionPane.showMessageDialog(null, "It's your turn!");
                  
               enablePlayTiles();
               endTurnButton.setEnabled(true);
               
                  //break; 
            }
            else if(GameBoard.this.clientNumber != GameBoard.this.turnNumber){
               System.out.println("not your turn");
                  //System.out.println("Waiting for moves Client" + clientNumber);
                  //waitForMoves();//351
               ListenInnerClass listenThread = new ListenInnerClass();
               Thread listen = new Thread(listenThread);
               listen.start();
                  //break;
            }
             
          
            //createRack();        
         } 
         catch(UnknownHostException uhe) {
            System.out.println("no host");
            uhe.printStackTrace();
         }
         catch(IOException ioe)
         {
            System.out.println("IO error");
          //ioe.printStackTrace();
            JOptionPane.showMessageDialog(null, "Game server Not Found!", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
         }    
      } 
   }
   
   class ListenInnerClass implements Runnable{
   
      public ListenInnerClass(){
      
      }  
      public void run(){
         
         String skipGoWin;
         System.out.println("Listen thread started");
         //System.out.println("waitForMoves() started");
         try{
         
            System.out.println("Reading in GO, WIN, or SKIP");
            int numLettersFromServer = 0;
            skipGoWin = gameBin.readLine();
            System.out.println("From server - SkipGoWin = " + skipGoWin);
            gamePout.println("ACKskipGoWin");
            System.out.println("ACKskipGoWin sent to server");
         
            if(skipGoWin.equals("WIN")){
               System.out.println("Received a WIN");
               int player1Score = Integer.parseInt(player1ScoreField.getText());
               int player2Score = Integer.parseInt(player2ScoreField.getText());
               int player3Score = Integer.parseInt(player3ScoreField.getText());
               int player4Score = Integer.parseInt(player4ScoreField.getText());
               System.out.println("Player 1 score = " + player1Score);
               System.out.println("Player 2 score = " + player2Score);
               System.out.println("Player 3 score = " + player3Score);
               System.out.println("Player 4 score = " + player4Score);
            
               if(player1Score > player2Score && player1Score > player3Score 
               && player1Score > player4Score){
               
                  JOptionPane.showMessageDialog(null,"Player 1 WINS!!");
                  disableButtons();
               }
               else if(player2Score > player1Score && player2Score > player3Score 
               && player2Score > player4Score){
               
                  JOptionPane.showMessageDialog(null,"Player 2 WINS!!");
                  disableButtons();
               }
               else if(player3Score > player1Score && player3Score > player2Score 
               && player3Score > player4Score){
               
                  JOptionPane.showMessageDialog(null,"Player 3 WINS!!");
                  disableButtons();
               }
               else if(player4Score > player1Score && player4Score > player2Score 
               && player4Score > player3Score){
               
                  JOptionPane.showMessageDialog(null,"Player 4 WINS!!");
                  disableButtons();
               }
               else if(player4Score == player1Score && player4Score == player2Score 
               && player4Score == player3Score){
               
                  JOptionPane.showMessageDialog(null,"TIE GAME, YOU'RE ALL WINNERS!!");
                  disableButtons();
               }  
            }
            else if(skipGoWin.equals("SKIP")){
               System.out.println("SKIP triggered, reading turnNumber");
               turnNumber = Integer.parseInt(gameBin.readLine());
               System.out.println("Turn Number = " + turnNumber);
               gamePout.println("ACK After turnNumber received");
               System.out.println("ACK After turnNumber, sent to server");
            //JOptionPane.showMessageDialog(null,"Move Skipped!");
               whosTurn();
               disableButtons();
               //System.out.println("whosTurn() triggered");
               
               if(turnNumber != clientNumber){
                  
                  System.out.println("clientNumber != turnNumber, keep waiting");
                  
                     //waitForMoves();
                  ListenInnerClass listenThread = new ListenInnerClass();
                  Thread listen = new Thread(listenThread);
                  listen.start();   
               }
               else if(turnNumber == clientNumber){
                  //JOptionPane.showMessageDialog(null, "It's your turn again!");
                  enablePlayTiles();
                  endTurnButton.setEnabled(true);
                  //return true;
                     //break;
               }
                
            }
            else if(skipGoWin.equals("GO")){
               numLettersFromServer = Integer.parseInt(gameBin.readLine());
               System.out.println("Length recieved from SERVER =" + numLettersFromServer);
               gamePout.println("ACK(received number of letters that will be sent from server)");
               System.out.println("Sent ACK for numLettersFromServer");
            //System.out.println("number of letters to be sent from server: " + numLettersFromServer + "Client:" + clientNumber);
               String reep;         
               for(int h = 0; h < numLettersFromServer; h++){
                    
                  movesFromServer.add(gameBin.readLine());
               
                  gamePout.println("ACK(Received player moves)Client:"+clientNumber);
                  System.out.println("sending ack after receiving a player's moves");
               //System.out.println(playerMoves.get(h));
               }
               rewriteBoard();
               revalidate();
               repaint();
               for(int h = 0; h < 3; h++){
               
                
                  String pl = gameBin.readLine(); 
                  System.out.println("Player Number points recieved:::::::::::::::::::::::::: "+pl);
                  gamePout.println("ACK");
               
                  String pt = gameBin.readLine();
                  System.out.println("Player points recieved:::::::::::::::::::::::::: "+pt); 
                  gamePout.println("ACK");
               
                  setPoints(pl,pt);
               }
            
               
            //System.out.println("\nListening for turn number Client:"+ clientNumber);
               turnNumber = Integer.parseInt(gameBin.readLine());
               whosTurn();
               System.out.println(turnNumber+" Turn recieved");
            //System.out.println("Client Number: " + clientNumber);
            //System.out.println("Turn Number: " + turnNumber);
               String ackTurnNumRec = "ACKturnNumberClient" + clientNumber;
            
            //if(go = true){
            
               gamePout.println(ackTurnNumRec);
               //go = false;
               //System.out.println("Go set to false");
            //}   
               System.out.println("sending ack after receiving turn number in listen thread");
               whosTurn();//Line 454ish
               // ListenInnerClass listenThread = new ListenInnerClass();
            //                Thread listen = new Thread(listenThread);
            //                listen.start();
               
               System.out.println("Turn number: " + turnNumber);
            // if(this.turnNumber == this.clientNumber){
            //                JOptionPane.showMessageDialog(null, "It's your turn again!");
            //                enablePlayTiles();
            //                endTurnButton.setEnabled(true);
            //                return true;
            //                //break;
            //             }
            
               if(turnNumber == clientNumber){
               //JOptionPane.showMessageDialog(null, "It's your turn again!");
                  System.out.println("It's your turn again");
                  enablePlayTiles();
                  endTurnButton.setEnabled(true);
               //return true;
               //break;
               }
               else if(turnNumber != clientNumber){
            
               ListenInnerClass listenThread = new ListenInnerClass();
               Thread listen = new Thread(listenThread);
               listen.start();
               }
            }
            
         
         // if(this.turnNumber == this.clientNumber){
         //             JOptionPane.showMessageDialog(null, "It's your turn again!");
         //             enablePlayTiles();
         //             endTurnButton.setEnabled(true);
         //             return true;
         //                //break;
         //          }
         
         }  
         catch(Exception e){
                  //System.out.println("Catch triggered while listening for a 1");
         }
      //return false;
      }       
    
   }
 
   
   //******************SHUFFLE BUTTON FUNCTIONALITY*********************
   public ArrayList<JButton> shuffledTilesArray = new ArrayList<JButton>();
   public boolean complete = false;
   public Random randomGenerator = new Random();
   public void shuffleRackTiles(){
   
      shuffledTilesArray.clear();
      rackPanel.removeAll();
        
      while( rackTileArray.size() != shuffledTilesArray.size() && complete == false){
      
         int index = randomGenerator.nextInt(rackTileArray.size() );
         JButton addedTile = rackTileArray.get( index );
         if (!shuffledTilesArray.contains(addedTile)){
            shuffledTilesArray.add( (addedTile) );
         }
         else if(shuffledTilesArray.size() == rackTileArray.size() ){
            complete = true;
         }
         
      }
      
      for (int i = 0; i < shuffledTilesArray.size(); i++){
         rackPanel.add( shuffledTilesArray.get(i) );
      }
      
      rackPanel.revalidate(); //THIS REFRESHES THE NEW RANDOMIZATION; THIS IS WHAT FIXED IT   
      rackPanel.repaint();
   }
      
         //********SEND MOVE
         //line 396
   public void sendMove(){
      BoardTile retriever = new BoardTile("");
      try{
         for(int i = 0; i < lettersNeeded; i++){
         
            //System.out.println("lettersNeeded = " + lettersNeeded);
            gamePout.println(retriever.popMoves2Server());
            System.out.println("sent letter");
            System.out.println(gameBin.readLine()); 
         }
      }   
      catch(IOException ioe){
         
         ioe.printStackTrace();
      }
         
      System.out.println("popMoves2Server = " + retriever.popMoves2Server());   
   }
   

   
   
      //**********REPOPULATE BOARD FROM SERVER
   public ArrayList<String> movesFromServer = new ArrayList<String>();
      //Arrays.asList("0,0,r", "10,0,z", "11,12,q", "5,5,l"));
   public void rewriteBoard(){
      
      for(int k = 0; k < movesFromServer.size(); k++){
      
         String[] tokens = movesFromServer.get(k).split(",");
         
         //gameBoardPanel = panel that holds the board tiles
         //backgroundTileArray = holds all the blue and one black board tiles
         
         // tokens[0] = x
         // tokens[1] = y
         // tokens[2] = letter
         int locationValue = 0;
         
         if( tokens[0].equals("0" )){  locationValue += (15 * 0 ); }
         if( tokens[0].equals("1" )){  locationValue += (15 * 1 ); }
         if( tokens[0].equals("2" )){  locationValue += (15 * 2 ); }
         if( tokens[0].equals("3" )){  locationValue += (15 * 3 ); }
         if( tokens[0].equals("4" )){  locationValue += (15 * 4 ); }
         if( tokens[0].equals("5" )){  locationValue += (15 * 5 ); }
         if( tokens[0].equals("6" )){  locationValue += (15 * 6 ); }
         if( tokens[0].equals("7" )){  locationValue += (15 * 7 ); }
         if( tokens[0].equals("8" )){  locationValue += (15 * 8 ); }
         if( tokens[0].equals("9" )){  locationValue += (15 * 9 ); }
         if( tokens[0].equals("10")){  locationValue += (15 * 10); }
         if( tokens[0].equals("11")){  locationValue += (15 * 11); }
         if( tokens[0].equals("12")){  locationValue += (15 * 12); }
         if( tokens[0].equals("13")){  locationValue += (15 * 13); }
         if( tokens[0].equals("14")){  locationValue += (15 * 14); }
         
         if( tokens[1].equals("0" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("1" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("2" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("3" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("4" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("5" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("6" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("7" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("8" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("9" )){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("10")){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("11")){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("12")){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("13")){  locationValue += Integer.parseInt(tokens[1]); }
         if( tokens[1].equals("14")){  locationValue += Integer.parseInt(tokens[1]); }
         
         //System.out.println("locationValue: " + locationValue);                                               
         for(int q = 0; q < backgroundTileArray.size(); q++){
            if(locationValue == backgroundTileArray.get(q).getBoardTileID() ){
                             
               System.out.println("letter rewrites from 'board': " +  backgroundTileArray.get(q).fetchTileLetter() );
            
               
               backgroundTileArray.get(q).setTileLetter(tokens[2]);
               backgroundTileArray.get(q).replaceTileLetter(tokens[2]);
            
               
               System.out.println("letter rewrites from 'board'?: " +  backgroundTileArray.get(q).fetchTileLetter() );
               //CLEAR THE ARRAY:
               // movesFromServer.clear();
               System.out.println("SIZE OF ARRAY: " + movesFromServer.size() );
            
               //backgroundTileArray.get(q).disableButton();
               
               // gameBoardPanel.revalidate();
            //                gameBoardPanel.repaint();
            }
         }
        
         locationValue = 0;
      
      }
      //movesFromServer.clear();
      
   }    
    
         //*******UNDO
         
   public void undo(){
      //if( RackTile.rackStack.peek() != null && BoardTile.boardStack.peek() != null){
      try{
         RackTile.rackStack.peek().setEnabled(true);
         RackTile.rackStack.remove();
            
         ImageIcon board  = new ImageIcon("../Assets/Image/Letters/board/board.jpg");
            //ImageIcon center = new ImageIcon("../Assets/Image/Letters/board/center.jpg");
      
            //if(BoardTile.boardStack.peek() == centerTile.toString()){
            //   BoardTile.boardStack.peek().setIcon(board);
            //}
            //else{
         BoardTile.boardStack.peek().setIcon(board); //used to be center
            //}
         BoardTile.boardStack.pop();
      }
      catch(NullPointerException npe){}
         //DELETE MOVE FROM moves2Server 
   
   }       
   
   
   //***********************************************************************
      
   
   
   class ChatInnerClass implements Runnable{
      BufferedReader reads;
      JTextArea chat;
      
      public ChatInnerClass(JTextArea text, BufferedReader read){
         chat = text;
         reads = read;
      }
      
      public void run(){
      
         while(true)
         {
            try
            {
               chatArea.append(reads.readLine()+"\n");
               chatArea.setCaretPosition(chat.getDocument().getLength());   
            }
            catch(Exception readd){}
         }
      }
   }
}
