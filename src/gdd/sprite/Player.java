// FIXME
// - the switching animation is not smooth yet, maybe change to be switch case might help.

package gdd.sprite;

import static gdd.Global.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;

import javax.swing.ImageIcon;

public class Player extends Sprite {
    private static final int speedY = 5; // Added default speed
    
    // private Image image;
    private static final int START_X = 60;
    private static final int START_Y = 250;
    private static final int SCREEN_HEIGHT = 500; // Example value, adjust as needed
    
    private boolean upPressed, downPressed;
    private boolean shoot;
    
    // Animation
    public int frame = 0;
    private int animationDelay = 0;
    private final int ANIMATION_SPEED = 8; // Higher = slower animation
    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[] {
        // Walking
        new Rectangle(0, 504, 120, 63),  // Frame 0
        new Rectangle(120, 504, 120, 63),  // Frame 1
        new Rectangle(240, 504, 120, 63),  // Frame 2
        new Rectangle(360, 504, 120, 63),  // Frame 3
        new Rectangle(480, 504, 120, 63),  // Frame 4

        // Shooting
        new Rectangle(0, 61, 120, 61),  // Frame 5
        new Rectangle(120, 61, 120, 61), // Frame 6
        new Rectangle(240, 61, 120, 61), // Frame 7
        new Rectangle(360, 61, 120, 61), // Frame 8

        // hurting
        new Rectangle(0, 321, 120, 62), // Frame 9
        new Rectangle(120, 321, 120, 62), // Frame 10

        //dying
        new Rectangle(0, 214, 120, 81), // Frame 11
        new Rectangle(120, 214, 120, 81), // Frame 12

    };

    public Player() {
        initPlayer();
        System.out.println("‼️ Player initialized");
    }

    private void initPlayer() {
        var icon = new ImageIcon(IMG_PLAYER);
        if (icon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
            System.err.println("Error: Player image not loaded properly");
        }
        setImage(icon.getImage());
        setX(START_X);
        setY(START_Y);
    }

    public void update() {
        if (upPressed) {
            y = Math.max(0, y - speedY);
        }
        if (downPressed) {
            y = Math.min(SCREEN_HEIGHT - getHeight(), y + speedY);
        }

        // Handle animation
        animationDelay++;
        if (animationDelay >= ANIMATION_SPEED) {
            animationDelay = 0;

            if (shoot) {
                frame = (frame + 1) % 4 + 5; // Loop through frames 5-8 for shooting
                clipNo = frame; // Use frames 5-9 for shooting
                shoot = false; // Reset shoot after one frame
            } else {
                frame = (frame + 1) % 5; // Loop through frames 0-4 for walking
                clipNo = frame; // Use frames 0-4 for walking
            }
        }
    }


    // Input handling
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_RIGHT) {
            shoot = true;
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            upPressed = true;   
            System.out.println("Key pressed: " + KeyEvent.getKeyText(e.getKeyCode())); 
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            downPressed = true;
            System.out.println("Key pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            downPressed = false;
        }
    }

    public void updatePlayer(boolean phase) {
        String imagePath = phase ? IMG_PLAYER_PHASE2 : IMG_PLAYER;
        setImage(new ImageIcon(imagePath).getImage());
    }

    // Getters and setters
    public int getFrame() { return frame; }
    
    @Override
    public int getHeight() { return clips[clipNo].height; }
    
    @Override
    public int getWidth() { return clips[clipNo].width; }
    
    @Override
    public Image getImage() {
        Rectangle bound = clips[clipNo];
        BufferedImage bImage = toBufferedImage(image);
        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }
    
    // Either implement or remove this
    public void act() {
        // Optional: Add behavior here if needed
    }
}