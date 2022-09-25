import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;

public class BoardTile{

   public String tileLetter = "";
   public int tileValue = 0;
   public int totalTileValue = 0;
   public String _2dValue = "";
   public int arrayValue = 0;
   public static int tileNumber = 0;
   public int boardTileID;
   public JButton tileButton;
   public boolean undo = false;
   public boolean valueDecider = true;
   
   public ImageIcon finalIcon;
   
   public static Deque<JButton> boardStack = new ArrayDeque<JButton>();  
   //public String PATH = "../Assets/Image/Letters/rack/blankTileResized.jpg"; //FOR TESTING ONLY!!!!!
   
   public ImageIcon board   = new ImageIcon("../Assets/Image/Letters/board/board.jpg");
   public ImageIcon center  = new ImageIcon("../Assets/Image/Letters/board/center.jpg");
   public ImageIcon blank   = new ImageIcon("../Assets/Image/Letters/board/blankTileResized.jpg");
   public ImageIcon letterA = new ImageIcon("../Assets/Image/Letters/board/A.jpg");
   public ImageIcon letterB = new ImageIcon("../Assets/Image/Letters/board/B.jpg");
   public ImageIcon letterC = new ImageIcon("../Assets/Image/Letters/board/C.jpg");
   public ImageIcon letterD = new ImageIcon("../Assets/Image/Letters/board/D.jpg");
   public ImageIcon letterE = new ImageIcon("../Assets/Image/Letters/board/E.jpg");
   public ImageIcon letterF = new ImageIcon("../Assets/Image/Letters/board/F.jpg");
   public ImageIcon letterG = new ImageIcon("../Assets/Image/Letters/board/G.jpg");
   public ImageIcon letterH = new ImageIcon("../Assets/Image/Letters/board/H.jpg");
   public ImageIcon letterI = new ImageIcon("../Assets/Image/Letters/board/I.jpg");
   public ImageIcon letterJ = new ImageIcon("../Assets/Image/Letters/board/J.jpg");
   public ImageIcon letterK = new ImageIcon("../Assets/Image/Letters/board/K.jpg");
   public ImageIcon letterL = new ImageIcon("../Assets/Image/Letters/board/L.jpg");
   public ImageIcon letterM = new ImageIcon("../Assets/Image/Letters/board/M.jpg");
   public ImageIcon letterN = new ImageIcon("../Assets/Image/Letters/board/N.jpg");
   public ImageIcon letterO = new ImageIcon("../Assets/Image/Letters/board/O.jpg");
   public ImageIcon letterP = new ImageIcon("../Assets/Image/Letters/board/P.jpg");
   public ImageIcon letterQ = new ImageIcon("../Assets/Image/Letters/board/Q.jpg");
   public ImageIcon letterR = new ImageIcon("../Assets/Image/Letters/board/R.jpg");
   public ImageIcon letterS = new ImageIcon("../Assets/Image/Letters/board/S.jpg");
   public ImageIcon letterT = new ImageIcon("../Assets/Image/Letters/board/T.jpg");
   public ImageIcon letterU = new ImageIcon("../Assets/Image/Letters/board/U.jpg");
   public ImageIcon letterV = new ImageIcon("../Assets/Image/Letters/board/V.jpg");
   public ImageIcon letterW = new ImageIcon("../Assets/Image/Letters/board/W.jpg");
   public ImageIcon letterX = new ImageIcon("../Assets/Image/Letters/board/X.jpg");
   public ImageIcon letterY = new ImageIcon("../Assets/Image/Letters/board/Y.jpg");
   public ImageIcon letterZ = new ImageIcon("../Assets/Image/Letters/board/Z.jpg");

   
   public static Deque<String> moves2Server = new ArrayDeque<String>(); // MOVE TO GAMEBOARD
   public ArrayList<JButton> tilesDisplayed = new ArrayList<JButton>();

   ImageIcon tileBack = new ImageIcon("tile.jpg");
  


   public BoardTile(String tileLetter){
      
      RackTile retriever = new RackTile("");
      
      this.finalIcon = letterToIcon(tileLetter);
      this.tileLetter = tileLetter;
      
      boardTileID = tileNumber;
      //System.out.println("Board Tile ID: " + boardTileID );
      //System.out.println("incremented boardTile ID: " + boardTileID);
          
      
      if(tileLetter != "board" ||  tileLetter != "center"){
           // tileNumber++;
//            System.out.println("incremented tileNumber: " + tileNumber);
           tileValue = getTileValue();
           tileButton = new JButton(finalIcon ); //originally going to be set to stack of images that will be the board game background tiles
           //tileButton.setDisabledIcon(letterToIcon(tileLetter) );

      }
      else{
         
         tileButton = new JButton(letterToIcon(tileLetter));
         
      }
      tileButton.setContentAreaFilled(true);
      tileButton.setOpaque(true);
      tileButton.setBorderPainted(false);
      tileButton.setBackground(Color.BLACK);
      
 
      
      
      tileButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent ae){
           System.out.println("I was clicked!");
           if(ae.getSource()==tileButton){
         
           //System.out.println("2d value: " + getBoardTileID() + " value: " + tileLetter );

         
               if(retriever.fetchSendValue() != null){ //HYPO: RackTile.sendValue = variable saved when RackTile object is clicked
                      valueDecider = false;
                      
                      System.out.println("2d value: " + getBoardTileID() + " value: " + tileLetter );
//                      System.out.println("ID: " + get2dValue() );
                     System.out.println("fetchSendValue: " + retriever.fetchSendValue() );
                     tileButton.setIcon(letterToIcon( retriever.fetchSendValue() ) );//RackTile.sendValue) ); //HYPO: setIcon would be set to the value thats sent from RackTile class    
                     // RackTile.sendValue = null;
                     boardStack.push(tileButton);
                 
                  
                  //System.out.println("tile number: " + tileNumber);
                  saveMove(); //SAVES MOVE AS A STRING as x,y,LETTER to moves2Server Array.
                  System.out.println("2d value: " + get2dValue() );
                  
                  retriever.setSendValue(null);
                  //RackTile.sendValue = null;
                  
                  System.out.println("Button was pressed!");
                  
                  valueDecider = true;
               }
               else{
                  valueDecider = true;
                  System.out.println("VALUE OF VALUEDECIDER WAS SET TO TRUE!!!!!!");
               }
            }
         }
       });
      
    }

  //  public void undo(){
//    
//       RackTile.rackQueue.peek().setEnabled(true);
//       RackTile.rackQueue.pop();
//       
//       BoardTile.boardStack.peek().setIcon(BoardTile.letterToIcon("board"));
//       BoardTile.boardQueue.pop();
//       //DELETE MOVE FROM moves2Server  
//    }  
   
   public void addMoves2Server(String move){
      moves2Server.add(move);
   }
   public int getLengthMoves2Server(){
      return moves2Server.size();
   }
   
   public String popMoves2Server(){
      return moves2Server.pop();
   }
   
   public void clearMoves2Server(){
      moves2Server.clear();
   }
      
   public int getBoardTileID(){
      return boardTileID;
   }
   
   public void replaceTileLetter(String letter){
      
      RackTile retriever = new RackTile("");
      fetchButton().setIcon(null);
      
      this.finalIcon = letterToIcon(letter);
      fetchButton().setIcon(finalIcon);
      
      this.tileLetter = letter;
      //disableButton();
      //retriever.setSendValue(letter);//RackTile.sendValue = letter;
      //System.out.println("stored letter: " + tileLetter ) ;
      //System.out.println("Added letter: " + letter );
      
      fetchButton().addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent ae){
           //System.out.println("I was clicked!");
           if(ae.getSource()==tileButton){
         
               if (retriever.fetchSendValue() == null ){//.sendValue == null){
                  if(tileLetter != "board" || tileLetter != "center"){
                     
                     saveMove();
                     System.out.println("2d value: " + get2dValue() );
                     System.out.println("Button was pressed! && went to the else if!" );
                     retriever.setSendValue(null);//RackTile.sendValue = null;
                  }
            }     
         }
      }
    });
      
   }
   
   public void setTileLetter(String letter){
      this.tileLetter = letter;
   }
   
   public String fetchTileLetter(){
      return this.tileLetter;
   }
   
   public JButton fetchButton(){
      return this.tileButton;
   }
   
   
   public void saveMove(){
      //grabs current position in array                //tileNumber
      //converts it to 2d array position that is 15x15 //get2dValue()
      //converts it to string "10,10,W"                //get2dValue()
      //adds string to moves2server array
      
      addMoves2Server(get2dValue() );
      
      //Ultimately, all the moves will be sent to server with loop
   }
   
   //MOVED TO GAMEBOARD
   // public void sendMove(){
//       for(int i = 0; i < moves2Server.size(); i++){
//          moves2Server.get(i); // FINISH THIS WITH THE ACTUAL SENDING STUFF 
//       }
//    }

   
   public String get2dValue(){
      RackTile retriever = new RackTile("");
      
      String chosen = "";
      String LetterExists = this.tileLetter;
      String letterPlaced = retriever.fetchSendValue();
      
      System.out.println("value Decider: " + valueDecider );
      
      if(valueDecider == true){
         chosen = this.tileLetter;
      }
      else{
         chosen = letterPlaced;
      }
      
      System.out.println("chosen: " + chosen);
      
      if( this.boardTileID >= 0   && this.boardTileID <= 14  ){ _2dValue = "0,"  + (this.boardTileID - ( 15 * 0  )) + "," + chosen;} //tileLetter
      if( this.boardTileID >= 15  && this.boardTileID <= 29  ){ _2dValue = "1,"  + (this.boardTileID - ( 15 * 1  )) + "," + chosen;}
      if( this.boardTileID >= 30  && this.boardTileID <= 44  ){ _2dValue = "2,"  + (this.boardTileID - ( 15 * 2  )) + "," + chosen;}
      if( this.boardTileID >= 45  && this.boardTileID <= 59  ){ _2dValue = "3,"  + (this.boardTileID - ( 15 * 3  )) + "," + chosen;}
      if( this.boardTileID >= 60  && this.boardTileID <= 74  ){ _2dValue = "4,"  + (this.boardTileID - ( 15 * 4  )) + "," + chosen;}
      if( this.boardTileID >= 75  && this.boardTileID <= 89  ){ _2dValue = "5,"  + (this.boardTileID - ( 15 * 5  )) + "," + chosen;}
      if( this.boardTileID >= 90  && this.boardTileID <= 104 ){ _2dValue = "6,"  + (this.boardTileID - ( 15 * 6  )) + "," + chosen;}
      if( this.boardTileID >= 105 && this.boardTileID <= 119 ){ _2dValue = "7,"  + (this.boardTileID - ( 15 * 7  )) + "," + chosen;}
      if( this.boardTileID >= 120 && this.boardTileID <= 134 ){ _2dValue = "8,"  + (this.boardTileID - ( 15 * 8  )) + "," + chosen;}
      if( this.boardTileID >= 135 && this.boardTileID <= 149 ){ _2dValue = "9,"  + (this.boardTileID - ( 15 * 9  )) + "," + chosen;}
      if( this.boardTileID >= 150 && this.boardTileID <= 164 ){ _2dValue = "10," + (this.boardTileID - ( 15 * 10 )) + "," + chosen;}
      if( this.boardTileID >= 165 && this.boardTileID <= 179 ){ _2dValue = "11," + (this.boardTileID - ( 15 * 11 )) + "," + chosen;}
      if( this.boardTileID >= 180 && this.boardTileID <= 194 ){ _2dValue = "12," + (this.boardTileID - ( 15 * 12 )) + "," + chosen;}
      if( this.boardTileID >= 195 && this.boardTileID <= 209 ){ _2dValue = "13," + (this.boardTileID - ( 15 * 13 )) + "," + chosen;}
      if( this.boardTileID >= 210 && this.boardTileID <= 224 ){ _2dValue = "14," + (this.boardTileID - ( 15 * 14 )) + "," + chosen;}
    
      return _2dValue;
   }
   //MAYBE MOVE TO GAMEBOARD
   public int getPlayerScore(){
      return totalTileValue;
   }
   
   public void disableButton(){
   
      tileButton.setEnabled(false);
   
   }
   
   public int getTileValue(){
     int value = 0;
     switch(tileLetter){
         case "board" : value = 0;
         case "center": value = 0;
         
         case "_": value = 0;
                   break;
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
     
     totalTileValue += value;
     return value;
   
   }
   
    public ImageIcon letterToIcon(String lettername){
     ImageIcon imgicon = new ImageIcon();
     switch(lettername.toLowerCase() ){
         case "board": imgicon = board;
                       break;
         case "center": imgicon = center;
                        break;
                        
                        
         case "_": imgicon = blank;
                   break;
         case "a": imgicon = letterA;
                   break;
         case "b": imgicon = letterB;
                   break;
         case "c": imgicon = letterC;
                   break;
         case "d": imgicon = letterD;
                   break;
         case "e": imgicon = letterE;
                   break;    
         case "f": imgicon = letterF;
                   break;    
         case "g": imgicon = letterG;
                   break;    
         case "h": imgicon = letterH;
                   break;    
         case "i": imgicon = letterI;
                   break;    
         case "j": imgicon = letterJ;
                   break;    
         case "k": imgicon = letterK;
                   break;    
         case "l": imgicon = letterL;
                   break;    
         case "m": imgicon = letterM;
                   break;    
         case "n": imgicon = letterN;
                   break;    
         case "o": imgicon = letterO;
                   break;    
         case "p": imgicon = letterP;
                   break;    
         case "q": imgicon = letterQ;
                   break;    
         case "r": imgicon = letterR;
                   break;    
         case "s": imgicon = letterS;
                   break;    
         case "t": imgicon = letterT;
                   break;    
         case "u": imgicon = letterU;
                   break;    
         case "v": imgicon = letterV;
                   break;    
         case "w": imgicon = letterW;
                   break;    
         case "x": imgicon = letterX;
                   break;    
         case "y": imgicon = letterY;
                   break;    
         case "z": imgicon = letterZ;
                   break;
     } 
     
     return imgicon;
   
   } 
   
   
   // public static void main(String [] args){
//       BoardTile bt = new BoardTile("a");
//       bt.addMoves2Server("0,0,r");
//       bt.addMoves2Server("    dfs");
//       
//       bt.getLengthMoves2Server();
//       System.out.println("length: " + bt.getLengthMoves2Server());
//       
//    }
   
}