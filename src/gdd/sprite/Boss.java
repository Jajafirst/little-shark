package gdd.sprite;

import javax.swing.*;

import static gdd.Global.IMG_BOSS;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class Boss extends Sprite {
    private static final int SCREEN_HEIGHT = 720;
    
    // Boss actions
    private String action = "idle"; // Default action
    private static final String IDLE = "idle";
    private static final String ATTACK = "attack";
    private static final String HURT = "hurt";
    
    // Laser attack
    private boolean laserActive = false;
    private float laserOpacity = 0.25f;
    private long chargeStartTime;
    private final int PHASE1 = 500;
    private final int PHASE2 = 1000;
    private final int PHASE3 = 1500;
    
    // Animation
    public int frame = 0;
    private int animationDelay = 0;
    private final int ANIMATION_SPEED = 8;
    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[] {
            new Rectangle(0, 0, 100, 96), // Frame 0
            new Rectangle(103, 0, 100, 96), // Frame 1
            new Rectangle(212, 0, 100, 96), // Frame 2
            new Rectangle(309, 0, 100, 96), // Frame 3
            new Rectangle(420, 0, 100, 96), // Frame 4
            new Rectangle(515, 0, 100, 96), // Frame 5
            new Rectangle(628, 0, 100, 96), // Frame 6
            new Rectangle(740, 0, 60, 96), // Frame 7

            // Attack frames (if different size)
            new Rectangle(0, 96, 150, 96), // Attack Frame 0
            new Rectangle(150, 96, 150, 96), // Attack Frame 1

            // Hurt frame
            new Rectangle(300, 96, 103, 96) // Hurt Frame
    };
    
    private Random random = new Random();
    private double laserAngle = 0;
    private final int MAX_ANGLE = 15;
    private int health = 1000; // Boss has more health

    public Boss(int x, int y) {
        this.x = x;
        this.y = y;
        initBoss();
    }

    private void initBoss() {
        var icon = new ImageIcon(IMG_BOSS);
        if (icon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
            System.err.println("Error: Boss image not loaded properly");
        }

        int scale = 3; // Make boss 3x bigger
        Image scaledImage = icon.getImage().getScaledInstance(
                icon.getIconWidth() * scale,
                icon.getIconHeight() * scale,
                Image.SCALE_SMOOTH);

        setImage(scaledImage);

        for (int i = 0; i < clips.length; i++) {
            Rectangle old = clips[i];
            clips[i] = new Rectangle(
                    old.x * scale,
                    old.y * scale,
                    old.width * scale,
                    old.height * scale);
        }
    }


    public void update() {
        // Animation
        animationDelay++;
        if (animationDelay >= ANIMATION_SPEED) {
            animationDelay = 0;
            frame++;
            
            switch (action) {
                case IDLE:
                    clipNo = frame % 8; // Cycle through idle frames
                    break;
                    
                case ATTACK:
                    if (frame < 2) { // Only 2 attack frames
                        clipNo = 3 + frame;
                    } else {
                        action = IDLE;
                        frame = 0;
                    }
                    break;
                    
                case HURT:
                    if (frame < 1) { // Only 1 hurt frame
                        clipNo = 5;
                    } else {
                        action = IDLE;
                        frame = 0;
                    }
                    break;
            }
            if (random.nextInt(200) < 1 && !laserActive) { // 0.5% chance per frame
                activateLaser();
            }

        }
        
        // Laser logic
        if (laserActive) {
            long elapsed = System.currentTimeMillis() - chargeStartTime;
            
            if (elapsed < PHASE1) {
                laserOpacity = 0.25f;
            } else if (elapsed < PHASE2) {
                laserOpacity = 0.5f;
                action = ATTACK;
                frame = 0;
            } else if (elapsed < PHASE3) {
                laserOpacity = 1.0f;
            } else {
                laserActive = false;
                laserOpacity = 0f;
                randomizeLaserAngle();
                action = IDLE;
                frame = 0;
            }
        }
    }
    
    private void randomizeLaserAngle() {
        laserAngle = Math.toRadians(-MAX_ANGLE + random.nextInt(MAX_ANGLE * 2));
    }
    
    public void drawLaser(Graphics g) {
        if (!laserActive || laserOpacity <= 0f) return;
        
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, laserOpacity));
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(8));
        
        int startX = x + (int) (getWidth() * 0.9); // 90% from left
        int startY = y + (int) (getHeight() * 0.3); // 30% from top

        
        double dx = Math.cos(laserAngle);
        double dy = Math.sin(laserAngle);
        double t = startX / dx;
        int endX = 0;
        int endY = (int)(startY - dy * t);
        
        g2d.drawLine(startX, startY, endX, endY);
        g2d.dispose();
    }
    
    public void activateLaser() {
        if (!laserActive) {
            this.chargeStartTime = System.currentTimeMillis();
            this.laserOpacity = 0.25f;
            randomizeLaserAngle();
        }
        this.laserActive = true;
    }
    
    // Getters and setters matching Player class structure
    @Override
    public int getWidth() {
        return clips[clipNo].width;
    }
    
    @Override
    public int getHeight() {
        return clips[clipNo].height;
    }
    
    @Override
    public Image getImage() {
        Rectangle bound = clips[clipNo];
        BufferedImage bImage = toBufferedImage(image);
        return bImage.getSubimage(bound.x, bound.y, bound.width, bound.height);
    }
    
    public Rectangle getLaserBounds() {
        if (!laserActive) return new Rectangle();
        int startX = x + getWidth() - 40;
        int startY = y + getHeight() / 4;
        return new Rectangle(0, startY - 12, startX, 24);
    }
    
    public boolean isLaserActive() {
        return laserActive;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
        if (health < this.health) { // If took damage
            this.action = HURT;
            this.frame = 0;
        }
        this.health = Math.max(0, health);
    }
    
    public int getBulletX() {
        return x + (int) (getWidth() * 0.9);
    }

    public int getBulletY() {
        return y + (int) (getHeight() * 0.4);
    }

    @Override
    public void act() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }

    private boolean laserHitPlayer = false;

    public boolean hasHitPlayerWithLaser() {
        return laserHitPlayer;
    }

    public void setHasHitPlayerWithLaser(boolean hit) {
        this.laserHitPlayer = hit;
    }

    /* public Rectangle getLaserBounds() {
        if (!laserActive)
            return new Rectangle();

        int startX = x + getWidth() - 40;
        int startY = y + getHeight() / 4;
        int endX = 0;
        int endY = (int) (startY - Math.tan(laserAngle) * startX);

        // Create a wider rectangle for the laser's hitbox
        int laserWidth = 20; // Wider than visual laser for better gameplay
        return new Rectangle(0, Math.min(startY, endY) - laserWidth / 2,
                startX, Math.abs(endY - startY) + laserWidth);
    } */
}