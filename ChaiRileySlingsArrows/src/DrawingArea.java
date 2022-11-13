import java.awt.*;
import java.awt.event.ActionEvent;//Event management
import java.awt.event.ActionListener;//Action listeners
import javax.swing.Timer;//Timer module
import java.awt.Graphics;//Graphics library
import java.awt.Image;//Rendering images
import java.awt.Toolkit;//Rendering images
import java.awt.event.KeyAdapter;//Key mapping
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;//Mouse events to reset focus in the game
import java.awt.AWTEvent;
import java.lang.*;//for millis() system time methods for keyboard interaction

public class DrawingArea extends javax.swing.JPanel {

    //Variables
    Timer time;
    long thresh = 40;
    long lastPress = System.currentTimeMillis();
    static int player1X = 0;//Static to allow the variable to be reset in MainFrame
    static int player2X = 700;//Player starting posistions
    static int player1XHurt = -150;//Places the hurt models off screen until needed
    static int player2XHurt = -150;
    static int rock1X = -50;//Places the weapons off screen until fired
    int rock1Y = 0;
    static int rock2X = -50;
    int rock2Y = 600;
    static int arrow1X = -50;
    int arrow1Y = 0;
    static int arrow2X = -50;
    int arrow2Y = 0;
    static int hitsP1 = 0;//Number of hits scored
    static int hitsP2 = 0;
    static int missesP1 = 0;//Number of misses
    static int missesP2 = 0;
    static int ammoP1 = 20;//Amount of ammo for each player
    static int ammoP2 = 20;
    int arrow1YChange = 1;//Vertical change of the arrow
    int arrow2YChange = 1;
    int currentStep = 1;//Current step of the arrow
    int currentStep2 = 1;
    int lastDirectionP1 = 1;//Gets the last direction moved
    int lastDirectionP2 = -1;//Will still curve if fired before any movement
    int lastDirection1;//Is set to lastDirectionP1 when the arrow is fired to prevent the direction from changing while the player moves
    int lastDirection2;
    int curKey;//Current key pressed
    long curTimeMillis1;//Used for the timer that changes the player model when a player is hit
    long curTimeMillis2;
    boolean[] theKeys = new boolean[8];//Movement and weapon keys
    static boolean isFlyingP1 = false;//Stops the player from firing multiple weapons at once
    static boolean isFlyingP2 = false;
    boolean P1Hit = false;//Used to change the player model when a player gets a hit
    boolean P2Hit = false;
    boolean showD = false;//Dialog at the end of the game
    static boolean instructions = true;//Shows instructions when the game is first launched
    Image bkg,player1,player2,rockP1,arrowP1,rockP2,arrowP2,helpImage,player1Hurt,player2Hurt;//Images
    String hitSound1 = "hit1.wav";//Hit sounds
    String hitSound2 = "hit3.wav";
    String missSound1 = "miss1.wav";//Miss sounds
    String missSound2 = "miss2.wav";
    SoundPlayer theSound = new SoundPlayer();//For hit and miss sounds
    
    public DrawingArea() {
        initComponents();
        setFocusable(true);//Make the panel the focus
        addKeyListener(new DrawingArea.AL());//Add key press listener
        time = new Timer(50,new DrawingArea.TimerListener());
        time.start();//Starts the timer
        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK
                | AWTEvent.FOCUS_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK
                | AWTEvent.WINDOW_EVENT_MASK);
    }
    
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);       
        //Background and player images
        Image bkg = Toolkit.getDefaultToolkit().getImage("Field.JPEG");   
        g.drawImage(bkg,0,0,800,600,this);
        Image player1 = Toolkit.getDefaultToolkit().getImage("Link1.PNG");   
        g.drawImage(player1,player1X,470,this);
        Image player2 = Toolkit.getDefaultToolkit().getImage("Ghirahim1.PNG");   
        g.drawImage(player2,player2X,0,this);
        Image player1Hurt = Toolkit.getDefaultToolkit().getImage("LinkHurt.PNG");   
        g.drawImage(player1Hurt,player1XHurt,470,this);
        Image player2Hurt = Toolkit.getDefaultToolkit().getImage("GhirahimHurt.PNG");   
        g.drawImage(player2Hurt,player2XHurt,0,this);
        
        //Images for the rock and arrow and places them off screen
        Image arrowP1 = Toolkit.getDefaultToolkit().getImage("Arrow1.PNG");   
        g.drawImage(arrowP1,arrow1X,arrow1Y,this);
        Image rockP1 = Toolkit.getDefaultToolkit().getImage("Rock1.PNG");   
        g.drawImage(rockP1,rock1X,rock1Y,this);
        Image arrowP2 = Toolkit.getDefaultToolkit().getImage("Arrow2.PNG");   
        g.drawImage(arrowP2,arrow2X,arrow2Y,this);
        Image rockP2 = Toolkit.getDefaultToolkit().getImage("Rock2.PNG");   
        g.drawImage(rockP2,rock2X,rock2Y,this);
        
        //Image for the instructions is loaded in dialog2
               
        //When a weapon is fired
        if(isFlyingP1 == true && rock1Y > 0){//When P1 shoots the rock
            rock1Y = rock1Y - 20;//Moves the rock up the screen   
            if(rock1Y < 40 && rock1X >= player2X-20 && rock1X <= player2X+70){//Detects if the rock hit player 2              
                curTimeMillis1 = System.currentTimeMillis();//Used for the timer that changes the player model
                P1Hit = true;//Allows the timer to start
                theSound.play(hitSound2);//Plays the hit sound
                hitsP1++;//Add one to hit counter for player 1
                MainFrame.jLabel1.setText("Player 1 Hits: " + hitsP1);//Updates the label with the current number of hits 
                rock1X = -50;//Reset the X and Y for the rock off screen
                rock1Y = 0;
                ammoP1--;//Subtract one from P1 ammo (Subtract here to allow the last shot to be fired)
                isFlyingP1 = false;//Allows another weapon to be fired
            }
            else if(rock1Y == 0){//If the rock misses
                theSound.play(missSound1);//Plays the miss sound
                missesP1++;//Add one to the miss counter
                MainFrame.jLabel8.setText("Player 1 Misses: " + missesP1);//Update the label with the current number of misses
                rock1X = -50;//Reset the X and Y for the rock off screen
                rock1Y = 0;
                ammoP1--;//Subtract one from P1 ammo
                isFlyingP1 = false;//Allows another weapon to be fired
            }             
        }
        if(isFlyingP2 == true && rock2Y < 600){//When P2 shoots the rock
            rock2Y = rock2Y + 20;//Moves the rock up the screen  
            if(rock2Y > 500 && rock2X >= player1X-20 && rock2X <= player1X+70){//Detects if the rock hit player 1
                curTimeMillis2 = System.currentTimeMillis();//Used for the timer that changes the player model
                P2Hit = true;//Allows the timer to start
                theSound.play(hitSound1);//Plays the hit sound
                hitsP2++;//Add one to hit counter for player 2
                MainFrame.jLabel2.setText("Player 2 Hits: " + hitsP2);//Updates the label with the current number of hits 
                rock2X = -50;//Reset the X and Y for the rock off screen
                rock2Y = 600;
                ammoP2--;//Subtract one from P2 ammo
                isFlyingP2 = false;//Allows another weapon to be fired
            }
            else if(rock2Y == 600){//If the rock misses
                theSound.play(missSound2);//Plays the miss sound
                missesP2++;//Add one to the miss counter
                MainFrame.jLabel9.setText("Player 2 Misses: " + missesP2);//Update the label with the current number of misses
                rock2X = -50;//Reset the X and Y for the rock off screen
                rock2Y = 600;
                ammoP2--;//Subtract one from P2 ammo
                isFlyingP2 = false;//Allows another weapon to be fired
            }
        }                
        if(isFlyingP1 == true && arrow1Y > 0){//When P1 shoots the arrow       
            arrow1Y = arrow1Y - arrow1YChange*currentStep;//Subtract the arrow change multiplied by the current step
            arrow1X = arrow1X + 10 * lastDirection1;//Curves the arrow to the left or right depending on the last direction
            currentStep = currentStep + 2;//Add two to the current step
            if(arrow1Y < 40 && arrow1X >= player2X+10 && arrow1X <= player2X+70){//Detects is the arrow hit player 2
                curTimeMillis1 = System.currentTimeMillis();//Used for the timer that changes the player model
                P1Hit = true;//Allows the timer to start
                theSound.play(hitSound2);//Plays the hit sound
                hitsP1++;//Add one to hit counter for player 1
                MainFrame.jLabel1.setText("Player 1 Hits: " + hitsP1);//Updates the label with the current number of hits 
                arrow1X = -50;//Reset the X and Y for the arrow off screen
                arrow1Y = 0;
                ammoP1--;//Subtract one from P1 ammo
                isFlyingP1 = false;//Allows another weapon to be fired
            }      
            else if(arrow1Y < 0){//If the arrow misses
                theSound.play(missSound1);//Plays the miss sound
                missesP1++;//Add one to the miss counter
                MainFrame.jLabel8.setText("Player 1 Misses: " + missesP1);//Updates the label with the current number of misses
                arrow1X = -50;//Reset the X and Y for the arrow off screen
                arrow1Y = 0;
                ammoP1--;//Subtract one from P1 ammo
                isFlyingP1 = false;//Allows another weapon to be fired
            }   
        }
        if(isFlyingP2 == true && arrow2Y > 0){//When P2 shoots the arrow       
            arrow2Y = arrow2Y + arrow2YChange*currentStep2;//Subtract the arrow change multiplied by the current step
            arrow2X = arrow2X + 10 * lastDirection2;//Curves the arrow to the left or right depending on the last direction
            currentStep2 = currentStep2 + 2;//Add two to the current step
            if(arrow2Y > 550 && arrow2X >= player1X-10 && arrow2X <= player1X+80){//Detects is the arrow hit player 1
                curTimeMillis2 = System.currentTimeMillis();//Used for the timer that changes the player model
                P2Hit = true;//Allows the timer to start
                theSound.play(hitSound1);//Plays the hit sound
                hitsP2++;//Add one to the hit counter
                MainFrame.jLabel2.setText("Player 2 Hits: " + hitsP2);//Updates the label with the current number of hits 
                arrow2X = -50;//Reset the X and Y for the arrow off screen
                arrow2Y = 0;
                ammoP2--;//Subtract one from P2 ammo
                isFlyingP2 = false;//Allows another weapon to be fired
            }      
            else if(arrow2Y > 550){//If the arrow misses
                theSound.play(missSound2);
                missesP2++;//Add one to the miss counter
                MainFrame.jLabel9.setText("Player 2 Misses: " + missesP2);//Updates the label with the current number of misses
                arrow2X = -50;//Reset the X and Y for the arrow off screen
                arrow2Y = 0;
                ammoP2--;//Subtract one from P2 ammo
                isFlyingP2 = false;//Allows another weapon to be fired
            }   
        }
        //*If the player hits with their last shot they will win
        if(hitsP1 == 5 || ammoP2 == 0){//If player 1 gets 5 hits or if player 2 runs out of ammo
            MainFrame.jLabel3.setText("Player 1 Wins!");//Change the text in the dialog
            showD = true;//Shows the play again dialog
        }
        else if(hitsP2 == 5 || ammoP1 == 0){//If player 2 gets 5 hits or if player 1 runs out of ammo
            MainFrame.jLabel3.setText("Player 2 Wins!");
            showD = true;
        }
    }
    private class TimerListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae){
            if(instructions == true){//When the game is first launched or the help button is pressed
                MainFrame.jDialog2.setVisible(true);//Show the help dialog
                instructions = false;//Stops the dialog from reopening
            }
            if(showD == true){//When a player wins
                MainFrame.jDialog1.setVisible(true);//Bring up the play again dialog
                showD = false;//Stops the dialog from reopening
            }
            else{                
            //When a movement key is pressed
            //Player 1
            if((theKeys[0] == true) && (player1X > 0)) {//Stops the player from going off screen when moving
                player1X = player1X - 10;//Moves the player along the X axis left or right
                lastDirectionP1 = -1;//Determines if the arrow will go left or right when fired
            }
            if((theKeys[1] == true) && (player1X < 680)) {
                player1X = player1X + 10;
                lastDirectionP1 = 1;
            }
            //Player 2
            if((theKeys[4] == true) && (player2X > 0)) {
                player2X = player2X - 10;
                lastDirectionP2 = -1;
            }
            if((theKeys[5] == true) && (player2X < 700)) {
                player2X = player2X + 10;
                lastDirectionP2 = 1;
            }
            
            //When a weapon key is pressed
            if(theKeys[2] == true && isFlyingP1 == false){//P1 rock
                rock1X = player1X+30;//Moves the rock on screen infront of the player
                rock1Y = 440;
                MainFrame.jLabel6.setText("Player 1 Ammo: " + (ammoP1-1));//Update the ammo label on screen
                isFlyingP1 = true;//Prevents another weapon from being fired until it hits or misses
            }
            if(theKeys[7] == true && isFlyingP2 == false){//P2 rock
                rock2X = player2X+30;
                rock2Y = 100;
                MainFrame.jLabel7.setText("Player 2 Ammo: " + (ammoP2-1));
                isFlyingP2 = true;
            } 
            if(theKeys[3] == true && isFlyingP1 == false){//P1 arrow
                arrow1YChange = 1;//Reset the vertical change 
                currentStep = 1;//Reset the currentStep 
                lastDirection1 = lastDirectionP1;//Stores the last direction in another variable to prevent the direction from changing with the player movement
                arrow1X = player1X+50;//Moves the arrow on screen infront of the player
                arrow1Y = 440;
                MainFrame.jLabel6.setText("Player 1 Ammo: " + (ammoP1-1));
                isFlyingP1 = true;
            }
            if(theKeys[6] == true && isFlyingP2 == false){//P2 arrow
                arrow2YChange = 1;
                currentStep2 = 1;
                lastDirection2 = lastDirectionP2;
                arrow2X = player2X+50;
                arrow2Y = 20;
                MainFrame.jLabel7.setText("Player 2 Ammo: " + (ammoP2-1));
                isFlyingP2 = true;
            }
            
            //Extra feature, Change player model when hit
            if(curTimeMillis1 > System.currentTimeMillis()-1000 && P1Hit == true){//A 1000 ms timer when P2 is hit
                    player2XHurt = player2X;//Places the hurt player model over the existing one
                }
                else{//When the timer is up
                    P1Hit = false;//Stops the timer
                    player2XHurt = -100;//Move the hurt player model off screen
                }
            if(curTimeMillis2 > System.currentTimeMillis()-1000 && P2Hit == true){
                    player1XHurt = player1X;
                }
                else{
                    P2Hit = false;
                    player1XHurt = -100;
                }
            repaint();
            }
        }
    }
    public class AL extends KeyAdapter{
    @Override
    public void keyPressed(KeyEvent e){
        if(System.currentTimeMillis() - lastPress > thresh){//Attempt to reduce key jamming
        curKey = e.getKeyCode();//Detects what key is currently pressed and does the corresponding action
        if(curKey == e.VK_LEFT){
            theKeys[0] = true;
        }
        if(curKey == e.VK_RIGHT){
            theKeys[1] = true;
        }
        if(curKey == e.VK_DOWN){
            theKeys[2] = true;//Fire rock P1     
        }
        if(curKey == e.VK_UP){
            theKeys[3] = true;//Fire arrow P1
        }
        if(curKey == e.VK_A){
            theKeys[4] = true;
        }
        if(curKey == e.VK_D){
            theKeys[5] = true;
        }
        if(curKey == e.VK_W){
            theKeys[6] = true;//Fire arrow P2
        }
        if(curKey == e.VK_S){
            theKeys[7] = true;//Fire rock P2
        }
        lastPress = System.currentTimeMillis();
        }
    }   
    @Override
    public void keyReleased(KeyEvent e){
       if(curKey == e.VK_LEFT){
            theKeys[0] = false;
            theKeys[1] = false;//Prevents errors when both keys are pressed simultaneously
        }
        if(curKey == e.VK_RIGHT){
            theKeys[1] = false;
            theKeys[0] = false;
        }
        if(curKey == e.VK_DOWN){
            theKeys[2] = false;
        }
        if(curKey == e.VK_UP){
            theKeys[3] = false;
        }
        if(curKey == e.VK_A){
            theKeys[4] = false;
            theKeys[5] = false;
        }
        if(curKey == e.VK_D){
            theKeys[5] = false;
            theKeys[4] = false;
        }
        if(curKey == e.VK_W){
            theKeys[6] = false;
        }
        if(curKey == e.VK_S){
            theKeys[7] = false;
        }        
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}