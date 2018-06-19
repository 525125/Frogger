/*  Scott Xu
    Panels.java
    This program creates classes for the menu panel, the game over panel, and the level panels.
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;



// Panel for each level; stores all data for the level.
// The user can change the state of the game using the state of the keys, find out if the player won or lost,
// get the number of remaining lives, or get the cumulative score.

class GamePanel extends JPanel{
    private Frog player;				                                                        // player
    private int lives;					                                                        // lives left
    private int score;                                                                          		// cumulative score
    private boolean[] homes;			                                                        	// whether or not player made it to each home
    private Rectangle[] homeRects;		                                                        	// rectangles for each home
    private Polygon bush;				                                                        // bush
    private Rectangle waterPool;		                                                        	// water between homes and middle lane
    private ArrayList<Vehicle>[] vehicles;		                                                	// vehicles in each road lane
    private ArrayList[] logs;			                                                        	// logs/turtles in each water  lane

    private String[][] vehiclesData;                                                            		// data for road lanes in level
    private String[][] logsData;                                                                		// data for water lanes in level

    private BufferedImage background, lilypadPic;                                               		// image of background and lilypad

    private int ticksLeft, levelNum;                                                            		// time left; current level

    public GamePanel(int l, String[] levelString, int s, int levelN){                           		// creates panel for level
        // read data for level
        logsData = new String[5][6];
        for (int i = 0; i < 5; i++){
            logsData[i] = levelString[i].split(",");
        }

        vehiclesData = new String[5][4];
        for (int i = 0; i < 5; i++){
            vehiclesData[i] = levelString[i+5].split(",");
        }


        // set homes, bush, and water; initiate vehicles and logs/turtles
        homes = new boolean[5];
        homeRects = new Rectangle[5];
        for (int i = 0; i < 5; i++){
            homes[i] = false;
            homeRects[i] = new Rectangle(40+150*i, 40, 70, 60);
        }

        int[] bushx = {0, 750, 750, 710, 710, 640, 640, 560, 560, 490, 490, 410, 410, 340, 340, 260, 260, 190, 190, 110, 110, 40, 40, 0};           // x-coordinates of vertices of  bush
        int[] bushy = {0, 0, 100, 100, 40, 40, 100, 100, 40, 40, 100, 100, 40, 40, 100, 100, 40, 40, 100, 100, 40, 40, 100, 100};                   // y-coordinates of vertices of  bush
        bush = new Polygon(bushx, bushy, bushx.length);

        waterPool = new Rectangle(0, 100, 750, 250);

        vehicles = new ArrayList[5];				                                            	// 5 lanes
        for (int i = 0; i < vehicles.length; i++){
            vehicles[i] = new ArrayList<Vehicle>();
            for (int j = 0; j < 5; j++){			                                            	// generate 5 vehicles per lane
                addVehicle(vehicles[i], vehiclesData[i], 425+50*i);
            }
        }

        logs = new ArrayList[5];					                                    	// 5 lanes
        for (int i = 0; i < logs.length; i++){
            logs[i] = new ArrayList<>();
            for (int j = 0; j < 5; j++){			                                            	// generate 5 logs/turtles per lane
                addLog(logs[i], logsData[i], 125+50*i);
            }
        }


        // load images for background and lilypad
        try{
            lilypadPic = ImageIO.read(new File("Images/lilypad.png"));
        }
        catch(IOException e){
            lilypadPic = null;
            System.out.println("Could not find Images/lilypad.png");
        }

        try{
            background = ImageIO.read(new File("Images/background.png"));
        }
        catch(IOException e){
            background = null;
            System.out.println("Could not find Images/background.png");
        }

        // create player and set default level settings
        player = new Frog();
        lives = l;
        ticksLeft = 6000;
        score = s;
        levelNum = levelN;

        requestFocus();
    }

    private void addVehicle(ArrayList<Vehicle> lane, String[] laneData, int y){                 // adds a vehicle to the given lane
        int nextX = 0;                                                                          // x position of the new vehicle
        int vx = Integer.parseInt(laneData[0]);                                                 // speed of new vehicle
        int minGap = Integer.parseInt(laneData[1]);                                             // minimum space between vehicles in the lane
        int maxGap = Integer.parseInt(laneData[2]);                                             // maximum space between vehicles in the lane

        if (lane.size() == 0) {                                                                 // if no vehicles in the lane, then pick random x
            nextX = (int)(Math.random()*810-60);
        }
        else{                                                                                   // if there are, add it behind the last vehicle in the lane
            nextX = (lane.get(lane.size()-1)).getX();

            if (vx < 0){                                                                        // travelling left; add vehicle to the right
                nextX += (int)(Math.random()*(maxGap-minGap)+minGap);
            }
            else{                                                                               // travelling right; add vehicle to the left
                nextX -= (int)(Math.random()*(maxGap-minGap)+minGap);
            }
        }

        // add vehicle
        lane.add(new Vehicle(nextX, y, "Images/Vehicle/" + laneData[3] + ".png", vx));
    }

    private void addLog(ArrayList lane, String[] laneData, int y){                              // adds a log or turtle to the given lane
        int nextX = 0;                                                                          // x position of the new log/turtle
        int vx = Integer.parseInt(laneData[0]);                                                 // speed of new log/turtle
        int minGap = Integer.parseInt(laneData[1]);                                             // minimum space between logs/turtles in the lane
        int maxGap = Integer.parseInt(laneData[2]);                                             // maximum space between logs/turtles in the lane

        if (lane.size() == 0) {                                                                 // if nothing in the lane, then pick random x
            nextX = (int)(Math.random()*810-60);
        }
        else{                                                                                   // if there is, then add behind last object
            if (lane.get(lane.size()-1) instanceof Log){
                nextX = ((Log)(lane.get(lane.size()-1))).getX();
            }
            else if (lane.get(lane.size()-1) instanceof Turtles){
                nextX = ((Turtles)(lane.get(lane.size()-1))).getX();
            }

            if (vx < 0){                                                                        // travelling left; add log/turtle to the right
                nextX += (int)(Math.random()*(maxGap-minGap)+minGap);
            }
            else{                                                                               // travelling right; add log/turtle to the left
                nextX -= (int)(Math.random()*(maxGap-minGap)+minGap);
            }
        }

        // add whatever belongs in the lane
        if (laneData[3].equals("log")){
            lane.add(new Log(nextX, y, "Images/Log/" + laneData[4] + ".png", vx));
        }
        else if(laneData[3].equals("turtle")){
            int count = Integer.parseInt(laneData[4]);                                          // number of turtles in group
            boolean isDiving = Math.random() < (double)Integer.parseInt(laneData[5])/100.0;     // chance of diving
            lane.add(new Turtles(nextX, y, vx, count, isDiving));
        }
    }

    @Override
    public void paintComponent(Graphics g){
        // draw background
        g.drawImage(background, 0, -20, null);


        // show progress in game
        g.setColor(Color.white);
        g.setFont(new Font("Lucida Grande", Font.PLAIN, 15));

        g.drawString(String.format("LEVEL %d", levelNum), 30,20);                       	// current level
        g.drawString(String.format("SCORE: %05d", score), 210,20);                      	// cumulative score
        g.drawString(String.format("LIVES: %d", lives), 420,20);                        	// lives left
        g.drawString("Time", 610,20);                                               		// time remaining

        // time bar
        g.setColor(Color.green);
        if (ticksLeft < 1000){
            g.setColor(Color.red);
        }
        g.fillRect(660, 10, Math.max(0, ticksLeft/100), 10);                     		// time left


        // draw vehicles
        for (ArrayList<Vehicle> lane: vehicles){
            for (Vehicle v: lane){
                g.drawImage(v.getImage(), v.getX()-v.getW()/2, v.getY()-v.getH()/2, null);
            }
        }

        // draw logs & turtles
        for (ArrayList lane: logs){
            for (Object o: lane){
                if (o instanceof Log){
                    int x = ((Log)o).getX();                                                    // x position
                    int y = ((Log)o).getY();                                                    // y position
                    int w = ((Log)o).getW();                                                    // width
                    int h = ((Log)o).getH();                                                    // height
                    BufferedImage img = ((Log)o).getImage();                                    // image
                    g.drawImage(img, x-w/2, y-h/2, null);
                }
                else if (o instanceof Turtles){
                    int x = ((Turtles)o).getX();                                                // x position
                    int y = ((Turtles)o).getY();                                                // y position
                    int c = ((Turtles)o).getCount();                                            // number of turtles in group
                    BufferedImage img = ((Turtles)o).getImage();                                // image of one turtle

                    for (int i = 0; i < c; i++){
                        if (((Turtles)o).getVX() > 0){                                          // travelling right; turtles trail to the left
                            g.drawImage(img, x-img.getWidth()/2-50*i, y-img.getHeight()/2, null);
                        }
                        else{                                                                   // travelling left; turtles trail to the right
                            g.drawImage(img, x-img.getWidth()/2+50*i, y-img.getHeight()/2, null);
                        }
                    }
                }
            }
        }

        // draw lilypads in the made homes
        for (int i = 0; i < homes.length; i++){
            if (homes[i]){
                g.drawImage(lilypadPic, 75+150*i-lilypadPic.getWidth()/2, 80-lilypadPic.getHeight()/2, null);
            }
        }

        // draw the player
        BufferedImage img = player.getPic();                                                    // image of frog
        g.drawImage(img, player.getX()-img.getWidth()/2, player.getY()-img.getHeight()/2, null);
    }

    public void movetheStuff(boolean[] keys){
        // make player jump
        if (keys[KeyEvent.VK_UP]){
            player.jump(Frog.UP);
            keys[KeyEvent.VK_UP] = false;
        }
        if (keys[KeyEvent.VK_RIGHT]){
            player.jump(Frog.RIGHT);
            keys[KeyEvent.VK_RIGHT] = false;
        }
        if (keys[KeyEvent.VK_DOWN]){
            player.jump(Frog.DOWN);
            keys[KeyEvent.VK_DOWN] = false;
        }
        if (keys[KeyEvent.VK_LEFT]){
            player.jump(Frog.LEFT);
            keys[KeyEvent.VK_LEFT] = false;
        }

        ticksLeft--;

        // move player
        player.move();

        // move vehicles
        for (int i = 0; i < vehicles.length; i++){
            ArrayList<Vehicle> lane = vehicles[i];                                              // vehicles in lane

            for (Vehicle v: lane){
                v.move();
            }

            // if first vehicle goes offscreen, delete and add new vehicle behind last vehicle
            if (lane.get(0).getX() < -50 || lane.get(0).getX() > 800){
                lane.remove(0);
                addVehicle(vehicles[i], vehiclesData[i], 425+50*i);
            }
        }

        // move logs/turtles
        for (int i = 0; i < logs.length; i++){
            ArrayList lane = logs[i];                                                           // logs/turtles in lane

            for (Object o: lane){
                if (o instanceof Log){
                    ((Log)o).move();
                }
                else if (o instanceof Turtles){
                    ((Turtles)o).move();
                }
            }

            // if first log/turtle goes offscreen, delete and add new one behind last one
            Object first = lane.get(0);
            if (first instanceof Log){
                if (((Log)first).getX() < -120 || ((Log)first).getX() > 870){
                    lane.remove(0);
                    addLog(logs[i], logsData[i], 125+50*i);
                }
            }
            else if (first instanceof Turtles){
                if (((Turtles)first).getX() < -100 || ((Turtles)first).getX() > 850){
                    lane.remove(0);
                    addLog(logs[i], logsData[i], 125+50*i);
                }
            }
        }


        if (!player.isDead()) {
            // check for player death or making it to a home
            for (ArrayList<Vehicle> lane : vehicles) {
                for (Vehicle v : lane) {
                    if (v.getHitBox().intersects(player.getHitBox())) {                     	// hit by vehicle
                        player.setDead(true);
                    }
                }
            }

            if (bush.intersects(player.getHitBox())) {                                      	// jumped into bush
                player.setDead(true);
            }
            else if (player.getY() == 75) {
                for (int i = 0; i < homeRects.length; i++) {
                    if (homeRects[i].contains(player.getHitBox())) {                        	// jumped into a made home
                        if (homes[i]) {
                            player.setDead(true);
                        }
                        else {                                                              	// add points for making it to a home
                            score += 50;
                            score += ticksLeft / 50;
                            if (won()) {
                                score += 1000;
                            }

                            homes[i] = true;
                            player = new Frog();
                            ticksLeft = 6000;
                        }
                    }
                }
            }
            else if (waterPool.y < player.getY() && player.getY() < waterPool.y + waterPool.height) {
                if (!player.jumping()) {
                    boolean onLog = false;                                                  	// if player is on a log/turtle
                    for (ArrayList lane : logs) {
                        for (Object o : lane) {
                            if (o instanceof Log) {
                                if (((Log) o).getHitBox().intersects(player.getHitBox())) {
                                    player.moveX(((Log) o).getVX());                        	// move with the log
                                    onLog = true;
                                    break;
                                }
                            }
                            else if (o instanceof Turtles) {
                                if (!((Turtles) o).isSubmerged()){
                                    if (((Turtles) o).getHitBox().intersects(player.getHitBox())) {
                                        player.moveX(((Turtles) o).getVX());                	// move with the turtles
                                        onLog = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (!onLog) {                                                           	// player fell in water
                        player.setDead(true);
                    }
                }
            }
            if (player.getX() < -20 || player.getX() > 770) {                               	// player went offscreen
                player.setDead(true);
            }

            if (ticksLeft <= 0) {                                                           	// ran out of time
                player.setDead(true);
            }
        }
        else if (player.getDeathFrames() > 200){                                            	// shown death for 200 ticks; reset player
            lives--;
            player = new Frog();
            ticksLeft = 6000;
        }
    }

    public boolean won(){                                                                    	// whether or not the player has beaten the level
        for (boolean h: homes){
            if (!h){
                return false;
            }
        }
        return true;
    }

    public boolean lost(){                                                                    	// whether or not the player ran out of lives
        return lives<0;
    }

    public int getLives(){                                                                      // number of lives left
        return lives;
    }

    public int getScore(){                                                                      // cumulative score
        return score;
    }
}



// Panel for main menu; shows logo and a prompt for the user to start the game.

class MenuPanel extends JPanel{
    private BufferedImage logo;                                                                 // image of logo

    public MenuPanel(){
        try{
            logo = ImageIO.read(new File("Images/logo.png"));
        }
        catch (IOException e){
            logo = null;
            System.out.println("Could not find Images/logo.png");
        }

        requestFocus();
    }

    @Override
    public void paintComponent(Graphics g){
        //g.drawImage(this, 0, 0, null);
        g.setColor(Color.black);
        g.fillRect(0, 0, 750, 700);

        g.drawImage(logo, 86, 250, null);

        g.setColor(Color.white);
        g.setFont(new Font("Lucida Grande", Font.PLAIN, 32));
        g.drawString("Press <Enter> to Start", 200,500);
    }
}



// Panel for game over menu; says "You Win" or "You Lose", shows score, and prompts user to return to main menu.

class GameOverPanel extends JPanel{
    private boolean won;						                    	// whether or not the player won
    private int score;                                                                  	// final score

    public GameOverPanel(){
        won = true;
        score = 0;
        requestFocus();
    }

    @Override
    public void paintComponent(Graphics g){
        //g.drawImage(this, 0, 0, null);
        g.setColor(Color.black);
        g.fillRect(0, 0, 750, 700);
        g.setColor(Color.white);
        g.setFont(new Font("Lucida Grande", Font.PLAIN, 32));
        if (won){
            g.drawString("You win!", 310,250);
        }
        else{
            g.drawString("You lose!", 300,250);
        }
        g.drawString(String.format("Score: %05d", score), 270,350);
        g.drawString("Press <Enter> to play again", 150,450);
    }

    public void setStatus(boolean w){								// set win or lose state
        won = w;
    }

    public void setScore(int s){								// set final score
        score = s;
    }
}