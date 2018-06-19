/*  Scott Xu
    Frogger.java
    This program creates the Frogger class, which allows the user to play a game of Frogger.
    The user starts at a menu screen, plays through the levels, and is then shown a game over screen.
    The player has three lives in total. There are three levels to pass, each in increasing difficulty, in order to beat the game.
    In each level, there are five homes which the player must cross the screen in order to reach. The player gets 50 points
    for each home they make it to, and 1000 points for making all five and completing the level. They also receive 1 point for each
    1/2 second remaining in the time bar, which refills with every home they make. There are many ways of dying:
        -getting hit by a vehicle
        -jumping in water/staying on a turtle while it is diving
        -being taken off the screen (by a log)
        -colliding with the bush
        -jumping into an already made home
        -running out of time
    The game is over when the player runs out of lives (and loses) or passes all three levels (and wins).
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



public class Frogger extends JFrame implements ActionListener, KeyListener{
    private static int MENU = 0, GAME = 1, GAME_OVER_SCREEN = 2;                                // represent which screen the user is on
    private JLayeredPane layeredPane;                                                           // pane where all of the panels will go
    private String[][] levelStrings;                                                            // Strings containing data for each level
    private javax.swing.Timer myTimer;                                                          // timer for game
    private boolean[] keys;                                                                     // state of keys; true if pressed
    private int screen;                                                                         // which screen the user is on

    private GamePanel level;                                                                    // panel for current level
    private int levelNum, lives, score;                                                         // current level; lives left; cumulative score

    private MenuPanel menu;                                                                     // menu panel
    private GameOverPanel gameOverScreen;                                                       // game over panel

    public Frogger(){                                                                           // creates the game
        super("Frogger");
        setSize(750, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // create panels and add them to the pane
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(750, 700));

        menu = new MenuPanel();
        menu.setBounds(0, 0, 750, 700);
        gameOverScreen = new GameOverPanel();
        gameOverScreen.setBounds(0, 0, 750, 700);

        layeredPane.add(menu, new Integer(1));
        layeredPane.add(gameOverScreen, new Integer(0));
        add(layeredPane);


        keys = new boolean[KeyEvent.KEY_LAST + 1];
        for (int i = 0; i < keys.length; i++){
            keys[i] = false;
        }
        addKeyListener(this);


        // data for levels
        levelStrings = new String[0][0];
        try{
            Scanner inFile = new Scanner(new BufferedReader(new FileReader("Levels/levels.txt")));         // levels file
            int n = Integer.parseInt(inFile.nextLine());                                        // number of levels
            levelStrings = new String[n][10];
            for (int i = 0; i < n; i++){
                String s = inFile.nextLine();                                                   // ignore empty line
                for (int j = 0; j < 10; j++){
                    levelStrings[i][j] = inFile.nextLine();
                }
            }
        }
        catch (IOException ex){
            System.out.println("Could not find Levels/levels.txt");
        }
        /* levels.txt
        levels
        [*empty line*
        vx,min spacing,max spacing,log/turtle,pic/count,0/diving
        "
        "
        "
        "
        vx,min spacing,max spacing,pic
        "
        "
        "
        "]
        */


        // game settings
        screen = MENU;
        levelNum = 1;
        lives = 3;
        score = 0;

        level = new GamePanel(lives, levelStrings[levelNum-1], score, levelNum);
        level.setBounds(0, 0, 750, 700);

        myTimer = new javax.swing.Timer(10, this);


        setResizable(false);
        setVisible(true);
    }



    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();                                                          // what triggered the action

        if (screen == GAME){
            if (source == myTimer){
                level.movetheStuff(keys);

                if (level.won()){
                    lives = level.getLives();
                    score = level.getScore();
                    levelNum++;

                    if (levelNum > levelStrings.length){                                        // show game over screen if no more levels
                        gameOverScreen.setStatus(true);
                        gameOverScreen.setScore(score);
                        layeredPane.setLayer(gameOverScreen, layeredPane.highestLayer()+1);
                        gameOverScreen.repaint();
                        screen = GAME_OVER_SCREEN;
                    }
                    else{                                                                       // advance to the next level
                        layeredPane.remove(layeredPane.getIndexOf(level));
                        level = new GamePanel(lives, levelStrings[levelNum-1], score, levelNum);
                        level.setBounds(0, 0, 750, 700);
                        layeredPane.add(level, new Integer(layeredPane.highestLayer()+1));
                    }
                }

                else if (level.lost()){                                                         // show game over screen
                    score = level.getScore();
                    layeredPane.remove(layeredPane.getIndexOf(level));

                    gameOverScreen.setStatus(false);
                    gameOverScreen.setScore(score);
                    layeredPane.setLayer(gameOverScreen, layeredPane.highestLayer()+1);
                    gameOverScreen.repaint();
                    screen = GAME_OVER_SCREEN;
                }
            }

            level.repaint();
        }
    }



    public void keyTyped(KeyEvent e){}

    public void keyPressed(KeyEvent e){
        keys[e.getKeyCode()] = true;
        if (screen == MENU && e.getKeyCode() == KeyEvent.VK_ENTER){
            startGame();
        }
        else if (screen == GAME_OVER_SCREEN){
            if (e.getKeyCode() == KeyEvent.VK_ENTER){
                levelNum = 1;
                lives = 3;
                score = 0;
                myTimer = new javax.swing.Timer(10, this);
                level = new GamePanel(lives, levelStrings[levelNum-1], score, levelNum);
                level.setBounds(0, 0, 750, 700);

                layeredPane.setLayer(menu, layeredPane.highestLayer()+1);
                screen = MENU;
            }
        }
    }

    public void keyReleased(KeyEvent e){
        keys[e.getKeyCode()] = false;
    }



    public void startGame(){                                                                    // sets screen to game
        layeredPane.add(level, new Integer(layeredPane.highestLayer()+1));
        myTimer.start();

        screen = GAME;
    }



    public static void main(String[] args){
        new Frogger();
    }
}