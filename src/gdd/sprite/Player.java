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
    private static int speedY = 3; // Added default speed

    // private static final int speedY = 5; // Added default speed

    // private Image image;
    private static final int START_X = 60;
    private static final int START_Y = 250;
    private static final int SCREEN_HEIGHT = 500; // Example value, adjust as needed

    private boolean upPressed, downPressed;

    // Player actions
    private String action = WALK; // Default action

    private static final String WALK = "walk";
    private static final String SHOOT = "shoot";
    private static final String HURT = "hurt";
    private static final String DIE = "die";

    private boolean isShooting = false;
    private boolean isHurt = false;

    // Player Shots timing
    private long lastShotTime = 0;
    private static final long SHOT_DELAY = 1000; // 1000 milliseconds (1 second) between player shots

    // Player Health
    private int health; // Example health value, adjust as needed

    // Animation
    public int frame = 0;
    private int animationDelay = 0;
    private final int ANIMATION_SPEED = 6; // Higher = slower animation
    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[] {

            new Rectangle(0, 0, 120, 64), // Frame 0
            new Rectangle(120, 0, 120, 64), // Frame 1
            new Rectangle(240, 0, 120, 64), // Frame 2
            new Rectangle(360, 0, 120, 64), // Frame 3
            new Rectangle(480, 0, 120, 64), // Frame 4

            // Walking
            new Rectangle(0, 504, 120, 63), // Frame 0
            new Rectangle(120, 504, 120, 63), // Frame 1
            new Rectangle(240, 504, 120, 63), // Frame 2
            new Rectangle(360, 504, 120, 63), // Frame 3
            new Rectangle(480, 504, 120, 63), // Frame 4

            // Shooting
            new Rectangle(0, 61, 120, 61), // Frame 5
            new Rectangle(120, 61, 120, 61), // Frame 6
            new Rectangle(240, 61, 120, 61), // Frame 7
            new Rectangle(360, 61, 120, 61), // Frame 8

            // hurting
            new Rectangle(0, 322, 120, 62), // Frame 9
            new Rectangle(120, 322, 120, 62), // Frame 10

            // dying
            new Rectangle(0, 241, 120, 81), // Frame 11
            new Rectangle(120, 241, 120, 81), // Frame 12
            new Rectangle(240, 241, 120, 81), // Frame 13
            new Rectangle(360, 241, 120, 81), // Frame 14
            new Rectangle(480, 241, 120, 81) // Frame 15

    };

    private boolean reverseControls;

    // ____________________________________
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
        // _______Movement
        if (!reverseControls) {
            if (upPressed) {
                y = Math.max(0, y - speedY);
            }
            if (downPressed) {
                y = Math.min(SCREEN_HEIGHT - getHeight(), y + speedY);
            }
        } else {
            // 🔄 Reversed: Up goes down, Down goes up
            if (upPressed) {
                y = Math.min(SCREEN_HEIGHT - getHeight(), y + speedY);
            }
            if (downPressed) {
                y = Math.max(0, y - speedY);
            }
        }

        // _______Animation
        animationDelay++;
        if (animationDelay >= ANIMATION_SPEED) {
            animationDelay = 0;
            frame++;

            switch (action) {
                case WALK:
                    // Handle walk animation cycling
                    clipNo = frame % 5; // Cycle through walk frames 0-4

                    // Check for immediate actions that should interrupt walk animation
                    if (isShooting) {
                        action = SHOOT;
                        frame = 0; // Reset frame counter for shoot animation
                        clipNo = 5; // First shoot frame
                        isShooting = false;
                    } else if (isHurt) {
                        action = HURT;
                        frame = 0;
                        clipNo = 9; // First hurt frame
                        isHurt = false;
                    }
                    break;

                case SHOOT:
                    if (frame < 4) {
                        clipNo = 5 + frame; // Play shoot frames 5-8
                    } else {
                        action = WALK; // Return to walking
                        frame = 0;
                    }
                    break;

                case HURT:
                    if (frame < 2) {
                        clipNo = 9 + frame; // Play hurt frames 9-10
                    } else {
                        action = WALK; // Return to walking
                        frame = 0;
                    }
                    break;
            }
        }
    }

    public boolean shootingDelay() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOT_DELAY) {
            return true;
        } else {
            return false;
        }
    }

    public void setLastShotTime(long lastShotTime) {
        this.lastShotTime = lastShotTime;
    }

    // Input handling
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_SPACE:
                if (shootingDelay()) {
                    isShooting = true;
                } else {
                    isShooting = false;
                }
                break;

            case KeyEvent.VK_1:
                isHurt = true;
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
            /*
             * case KeyEvent.VK_UP:
             * case KeyEvent.VK_DOWN:
             * action = WALK; // Reset action to WALK when UP or DOWN is released
             * break;
             */

            case KeyEvent.VK_SPACE:
                break;

            default:
                break;
        }
    }

    // _____________________________________
    public void updatePlayer(boolean phase) {
        String imagePath = phase ? IMG_PLAYER_PHASE2 : IMG_PLAYER;
        setImage(new ImageIcon(imagePath).getImage());
    }

    // Getters and setters
    public int getFrame() {
        return frame;
    }

    @Override
    public int getHeight() {
        return clips[clipNo].height;
    }

    @Override
    public int getWidth() {
        return clips[clipNo].width;
    }

    @Override
    public Image getImage() {
        Rectangle bound = clips[clipNo];
        BufferedImage bImage = toBufferedImage(image);
        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }

    // Either implement or remove this

    @Override
    // Either implement or remove this
    public void act() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void applySpeedLevel(int level) {
        int[] SPEED_VALUES = { 5, 10, 15, 20 }; // LV1-LV4 speeds
        if (level < 1)
            level = 1;
        if (level > 4)
            level = 4;
        speedY = SPEED_VALUES[level - 1];
        System.out.println("🏎️ Player speed set to LV" + level + " (speedY=" + speedY + ")");
    }

    public void setReverseControls(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setReverseControls'");
    }

}