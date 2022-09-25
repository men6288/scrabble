import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class RackTile extends JFrame{

   //ON SERVER SIDE, MAKE A STACK OF ALL POSSIBLE LETTERS THAT WILL BE RECIEVED
   //INTO THE RACKTILE CLASS
   
   public String rackTileLetter = "";
   public static String sendValue;
   public JButton rackTileButton;
   public static JButton undoneButton;
   public static Deque<JButton> rackStack = new ArrayDeque<JButton>();
   public static int count = -1;
   public static int tileID;
   
   public static String PATH = "../Assets/Image/Letters/rack/blankTileResized.jpg"; //FOR TESTING ONLY!!!!!
   public static String aa   = "../Assets/Image/Letters/rack/A.jpg";
   public static String bb   = "../Assets/Image/Letters/rack/B.jpg";
   public static String cc   = "../Assets/Image/Letters/rack/C.jpg";
   public static String dd   = "../Assets/Image/Letters/rack/D.jpg";
   public static String ee   = "../Assets/Image/Letters/rack/E.jpg";
   public static String ff   = "../Assets/Image/Letters/rack/F.jpg";
   public static String gg   = "../Assets/Image/Letters/rack/G.jpg";
   public static String hh   = "../Assets/Image/Letters/rack/H.jpg";
   public static String ii   = "../Assets/Image/Letters/rack/I.jpg";
   public static String jj   = "../Assets/Image/Letters/rack/J.jpg";
   public static String kk   = "../Assets/Image/Letters/rack/K.jpg";
   public static String ll   = "../Assets/Image/Letters/rack/L.jpg";
   public static String mm   = "../Assets/Image/Letters/rack/M.jpg";
   public static String nn   = "../Assets/Image/Letters/rack/N.jpg";
   public static String oo   = "../Assets/Image/Letters/rack/O.jpg";
   public static String pp   = "../Assets/Image/Letters/rack/P.jpg";
   public static String qq   = "../Assets/Image/Letters/rack/Q.jpg";
   public static String rr   = "../Assets/Image/Letters/rack/R.jpg";
   public static String ss   = "../Assets/Image/Letters/rack/S.jpg";
   public static String tt   = "../Assets/Image/Letters/rack/T.jpg";
   public static String uu   = "../Assets/Image/Letters/rack/U.jpg";
   public static String vv   = "../Assets/Image/Letters/rack/V.jpg";
   public static String ww   = "../Assets/Image/Letters/rack/W.jpg";
   public static String xx   = "../Assets/Image/Letters/rack/X.jpg";
   public static String yy   = "../Assets/Image/Letters/rack/Y.jpg";
   public static String zz   = "../Assets/Image/Letters/rack/Z.jpg";

   public ImageIcon blank   = new ImageIcon(PATH);
   public ImageIcon letterA = new ImageIcon(aa);
   public ImageIcon letterB = new ImageIcon(bb);
   public ImageIcon letterC = new ImageIcon(cc);
   public ImageIcon letterD = new ImageIcon(dd);
   public ImageIcon letterE = new ImageIcon(ee);
   public ImageIcon letterF = new ImageIcon(ff);
   public ImageIcon letterG = new ImageIcon(gg);
   public ImageIcon letterH = new ImageIcon(hh);
   public ImageIcon letterI = new ImageIcon(ii);
   public ImageIcon letterJ = new ImageIcon(jj);
   public ImageIcon letterK = new ImageIcon(kk);
   public ImageIcon letterL = new ImageIcon(ll);
   public ImageIcon letterM = new ImageIcon(mm);
   public ImageIcon letterN = new ImageIcon(nn);
   public ImageIcon letterO = new ImageIcon(oo);
   public ImageIcon letterP = new ImageIcon(pp);
   public ImageIcon letterQ = new ImageIcon(qq);
   public ImageIcon letterR = new ImageIcon(rr);
   public ImageIcon letterS = new ImageIcon(ss);
   public ImageIcon letterT = new ImageIcon(tt);
   public ImageIcon letterU = new ImageIcon(uu);
   public ImageIcon letterV = new ImageIcon(vv);
   public ImageIcon letterW = new ImageIcon(ww);
   public ImageIcon letterX = new ImageIcon(xx);
   public ImageIcon letterY = new ImageIcon(yy);
   public ImageIcon letterZ = new ImageIcon(zz);
       

   ImageIcon tileBack = new ImageIcon("tile.jpg");
   ImageIcon a = new ImageIcon("Assets/Images/Letters/A.png");

   
   
   public RackTile(String rackTileLetter){ //rackTileLetter is pulled from array of letters from SERVER 
      
      
      
      tileID = ++count;
      
      this.rackTileLetter = rackTileLetter;
      //tileValue = getTileValue();
      //replaced by tileNumber =>>> //_2dValue = "";// Need to be able to reference master array that's adding to board.
      rackTileButton = new JButton(letterToIcon(rackTileLetter)); //originally going to be set to stack of images that will be the board game background tiles
      rackTileButton.setContentAreaFilled(false);
      rackTileButton.setOpaque(true);
      rackTileButton.setBorderPainted(false);
      rackTileButton.setDisabledIcon(letterToIcon(rackTileLetter));
      
      
      rackTileButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent ae){
           if(ae.getSource()==rackTileButton){
               
               tileID = ++count;
               //System.out.println("RT ID: " + tileID);
               
               sendValue = rackTileLetter;
               
               undoneButton = rackTileButton;
               rackStack.push(undoneButton);
               
               
               //add a thing to keep track of 
               rackTileButton.setEnabled(false); //RETURN TO FALSE AFTER TESTING
               System.out.println("Rack value hold: " + sendValue);
               
            }  
         }
       });
      
    }

      
   
  
  
   
   public String fetchSendValue(){
      return sendValue;
   } 
   public void setSendValue(String value){
      this.sendValue = value;
   }
         
   public JButton fetchButton(){
      return rackTileButton;
   }
   
   public void disableButton(){
      //makes button selectable again after it's been set
      rackTileButton.setEnabled(false);
   }
   public ImageIcon letterToIcon(String lettername){
     ImageIcon imgicon = new ImageIcon();
     switch(lettername.toLowerCase() ){
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
//       RackTile rt = new RackTile("a");
//       //System.out.println("The value of bt: " + bt.getTileValue());
//       rt.setSize(50, 70);
//       rt.setResizable(false);
//       rt.setLocationRelativeTo(null);
//       JPanel jp = new JPanel(new FlowLayout());
//       //JButton jb = new JButton("test");
//       
//       rt.add(jp, BorderLayout.CENTER);
//       jp.add(rt.fetchButton());
//       rt.setDefaultCloseOperation(EXIT_ON_CLOSE);
//       rt.setVisible(true);
//       
//       
//    }
   

}