// FIXME
// - the switching animation is not smooth yet, maybe change to be switch case might help.
// - when shooting, the system shoots but the player doesn't change to shooting animation immediately.

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

    private String action = WALK; // Default action

    private static final String WALK = "walk";
    private static final String SHOOT = "shoot";
    private static final String HURT = "hurt";
    private static final String DIE = "die";

    private boolean isShooting = false;

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
        new Rectangle(0, 322, 120, 62), // Frame 9
        new Rectangle(120, 322, 120, 62), // Frame 10

        //dying
        new Rectangle(0, 241, 120, 81), // Frame 11
        new Rectangle(120, 241, 120, 81), // Frame 12
        new Rectangle(240, 241, 120, 81), // Frame 13
        new Rectangle(360, 241, 120, 81), // Frame 14
        new Rectangle(480, 241, 120, 81) // Frame 15

    };

    public Player() {
        initPlayer();
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


            /* if (shoot) {
                frame = (frame + 1) % 4 + 5; // Loop through frames 5-8 for shooting
                clipNo = frame; // Use frames 5-9 for shooting
                shoot = false; // Reset shoot after one frame
            } else {
                frame = (frame + 1) % 5; // Loop through frames 0-4 for walking
                clipNo = frame; // Use frames 0-4 for walking
            } */

            switch (action) {
                case WALK:
                    frame = (frame + 1) % 5; // Loop through frames 0-4 for walking
                    clipNo = frame; // Use frames 0-4 for walking
                    break;

                case SHOOT:
                    frame = (frame + 1) % 4 + 5; // Loop through frames 5-8 for shooting
                    clipNo = frame; // Use frames 5-8 for shooting
                    action = WALK; // Reset action to WALK after shooting
                    break;

                case HURT:
                    frame = (frame + 1) % 2 + 9; // Loop through frames 9-15 for hurting
                    clipNo = frame; // Use frames 9-15 for hurting
                    break;

                default:
                    break;
            }
        }
    }

    // Input handling
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_RIGHT:
                action = SHOOT;
                System.out.println("🏀Shooting action triggered");
                break;

            case KeyEvent.VK_1:
                action = HURT;
                break;
                
            default:
                break;
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

        switch (key) {
            /* case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                action = WALK; // Reset action to WALK when UP or DOWN is released
                break; */
            
            case KeyEvent.VK_RIGHT:
                action = WALK; // Reset action to WALK when RIGHT is released
                break;
        
            default:
                break;
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

    @Override
    public void act() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }
    
}