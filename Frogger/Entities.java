/*  Scott Xu
    Entities.java
    This program creates classes for all entities in the game.
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



// This class stores all data for the player, including x position, y position, and images. The user can get or change any of its stats.

class Frog{
    private int x, y;                                                                           // x & y coordinates
    private int direction, pic;                                                                 // direction the player is facing; image number
    private boolean jumping, dead;                                                              // whether or not the player is jumping/dead
    private int deathFrames;                                                                    // number of frames in which the death image was shown
    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;                              // represent direction the player is facing

    private BufferedImage[] pics;                                                               // images of frogs
    private BufferedImage deathImage;                                                           // image of skull and crossbones

    public Frog(){
        // default stats
        x = 375;
        y = 675;
        jumping = false;
        dead = false;
        deathFrames = 0;
        pic = 0;
        direction = UP;

        // load images
        pics = new BufferedImage[6];
        for (int i = 0; i < pics.length; i++){
            try{
                pics[i] = ImageIO.read(new File("Images/Frog/" + i + ".png"));
            }
            catch (IOException e){
                pics[i] = null;
                System.out.println("Could not find " + "Images/Frog/" + i + ".png");
            }
        }

        try{
            deathImage = ImageIO.read(new File("Images/Frog/death.png"));
        }
        catch (IOException e){
            deathImage = null;
            System.out.println("Could not find " + "Images/Frog/death.png");
        }
    }

    public void jump(int d){                                                                    // sets player state to jumping, and sets direction of the jump
        if (!jumping && !dead){
            if (d == UP && y > 25){                                                             // do not let player jump offscreen
                direction = UP;
                jumping = true;
            }
            else if (d == DOWN  && y < 675){
                direction = DOWN;
                jumping = true;
            }
            else if (d == LEFT && x > 25){
                direction = LEFT;
                jumping = true;
            }
            else if (d == RIGHT && x < 725){
                direction = RIGHT;
                jumping = true;
            }
        }
    }

    public void move(){                                                                          // moves player if they are in the middle of a jump
        if (!dead){
            if (jumping){
                if (pic < 5){
                    if (direction == UP){
                        y = Math.max(y-10, 25);
                    }
                    else if (direction == DOWN){
                        y = Math.min(y+10, 675);
                    }
                    else if (direction == LEFT){
                        x = Math.max(x-10, 25);
                    }
                    else if (direction == RIGHT){
                        x = Math.min(x+10, 725);
                    }

                    pic++;
                }
                else{
                    pic = 0;
                    jumping = false;
                }
            }
        }
        else{                                                                                       // shown death for another frame
            deathFrames++;
        }
    }

    public boolean jumping(){                                                                       // whether or not the player is in the middle of a jump
        return jumping;
    }

    public int getX(){                                                                              // x-coordinate of player
        return x;
    }

    public int getY(){                                                                              // y-coordinate of player
        return y;
    }

    public Rectangle getHitBox(){                                                                   // player's hitbox
        Rectangle hitBox = new Rectangle(x-18, y-18, 36, 36);
        return hitBox;
    }

    public void moveX(int dx){                                                                      // moves player in x direction for logs and turtles
        if (!dead){
            x += dx;
        }
    }

    public BufferedImage getPic() {                                                                 // image for current state of the player
        if (dead){
            return deathImage;
        }

        double ang = 0;                                                                             // angle the default image needs to be rotated
        if (direction == LEFT){
            ang = Math.toRadians(90);
        }
        else if (direction == UP){
            ang = Math.toRadians(180);
        }
        else if (direction == RIGHT){
            ang = Math.toRadians(270);
        }

        AffineTransform tx = new AffineTransform();
        tx.rotate(ang, pics[pic].getWidth() / 2, pics[pic].getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(pics[pic], null);
    }

    public void setDead(boolean d){                                                                   // sets player status to dead
        dead = d;
    }

    public boolean isDead(){                                                                          // whether or not the player is dead
        return dead;
    }

    public int getDeathFrames(){                                                                      // number of frames we showed death for
        return deathFrames;
    }
}




// This class stores all data for vehicles, including x position, y position, and images. The user can get or change any of its stats.

class Vehicle{
    private int x, y, w, h, vx;                                                                        // x-coordinate; y-coordinate; width; height; speed in x direction
    private BufferedImage image;                                                                       // image

    public Vehicle(int xx, int yy, String fileName, int vv){
        x = xx;
        y = yy;

        // load image
        try{
            image = ImageIO.read(new File(fileName));
        }
        catch (IOException e){
            System.out.println("Could not find " + fileName);
            image = null;
        }

        w = image.getWidth();
        h = image.getHeight();
        vx = vv;

        if (vx < 0){                                                                                    // flip image if moving to the left
            double ang = Math.toRadians(180);
            AffineTransform tx = new AffineTransform();
            tx.rotate(ang, w / 2, h / 2);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            image = op.filter(image, null);
        }
    }

    public void move(){                                                                                 // moves the vehicle
        x += vx;
    }

    public Rectangle getHitBox(){                                                                       // hitbox of vehicle
        Rectangle hitBox = new Rectangle(x-w/2, y-h/2, w, h);
        return hitBox;
    }

    public int getX(){                                                                                  // x-coordinate of vehicle
        return x;
    }

    public int getY(){                                                                                  // y-coordinate of vehicle
        return y;
    }

    public int getW(){                                                                                  // width of vehicle
        return w;
    }

    public int getH(){                                                                                  // height of vehicle
        return h;
    }

    public BufferedImage getImage() {                                                                    // image of vehicle
        return image;
    }
}



// This class stores all data for logs, including x position, y position, and images. The user can get or change any of its stats.

class Log{
    private int x, y, w, h, vx;                                                                        // x-coordinate; y-coordinate; width; height; speed in x direction
    private BufferedImage image;                                                                       // image

    public Log(int xx, int yy, String fileName, int vv){
        x = xx;
        y = yy;

        // load images
        try{
            image = ImageIO.read(new File(fileName));
        }
        catch (IOException e){
            System.out.println("Could not find " + fileName);
            image = null;
        }

        w = image.getWidth();
        h = image.getHeight();
        vx = vv;
    }

    public void move(){                                                                             // moves the log
        x += vx;
    }

    public Rectangle getHitBox(){                                                                   // log's hitbox
        Rectangle hitBox = new Rectangle(x-w/2, y-h/2, w, h);
        return hitBox;
    }

    public int getX(){                                                                              // log's x-coordinate
        return x;
    }

    public int getY(){                                                                              // log's y-coordinate
        return y;
    }

    public int getW(){                                                                              // log's width
        return w;
    }

    public int getH(){                                                                              // log's height
        return h;
    }

    public int getVX(){                                                                              // log's speed in x direction
        return vx;
    }

    public BufferedImage getImage(){                                                                 // log's image
        return image;
    }
}



// This class stores all data for each group of turtles, including x position, y position, and images. The user can get or change any of its stats.

class Turtles{
    private int x, y, w, h, vx;                                                                        // x-coordinate & y-coordinate of first turtle; width; height; speed in x direction
    private int count, pic, interval;                                                                  // number of turtles in group; image number; number of frames since last image change
    private boolean diving;                                                                            // whether of not the group of turtles dives
    private BufferedImage pics[];                                                                      // images of turtles

    public Turtles(int xx, int yy, int vv, int cc, boolean dd){
        x = xx;
        y = yy;

        // load images
        pics = new BufferedImage[6];
        for (int i = 0; i < pics.length; i++){
            try{
                pics[i] = ImageIO.read(new File("Images/Turtle/" + i + ".png"));
                if (vv < 0){                                                                            // flip image if moving to the left
                    double ang = Math.toRadians(180);
                    AffineTransform tx = new AffineTransform();
                    tx.rotate(ang, pics[i].getWidth() / 2, pics[i].getHeight() / 2);
                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                    pics[i] = op.filter(pics[i], null);
                }
            }
            catch (IOException e){
                pics[i] = null;
            }
        }

        w = 50*cc-10;
        h = 40;
        vx = vv;
        count = cc;
        diving = dd;

        pic = 0;
        interval = 0;
    }

    public void move(){                                                                                 // moves group of turtles
                                                                                                        // if they dive; there image changes evey 50 ticks
        x += vx;
        if (diving){
            interval++;
            if (interval >= 50){
                pic = (pic+1) % 6;
                interval = 0;
            }
        }
    }

    public Rectangle getHitBox(){                                                                       // hitbox of group of turtles
        if (vx > 0){                                                                                    // go left of first turtle if moving to the right
            Rectangle hitBox = new Rectangle(x+20-w, y-h/2, w, h);
            return hitBox;
        }
        else{                                                                                           // go right of first turtle if moving to the left
            Rectangle hitBox = new Rectangle(x-20, y-h/2, w, h);
            return hitBox;
        }
    }

    public int getX(){                                                                                  // x-coordinate of first turtle
        return x;
    }

    public int getY(){                                                                                  // y-coordinate of first turtle
        return y;
    }

    public int getVX(){                                                                                 // speed of turtles
        return vx;
    }

    public int getCount(){                                                                              // number of turtles in the group
        return count;
    }

    public boolean isSubmerged(){                                                                        // whether or not the turtles are underwater
        return pic==3;
    }

    public BufferedImage getImage(){                                                                      // current image of one of the turtles
        return pics[pic];
    }
}