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
    private static final String HURT = "hurt";
    private static final String DIE = "die";

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
            // IDLE frames: row 0
            new Rectangle(0, 0, 300, 289), // Frame 0
            new Rectangle(300, 0, 300, 289), // Frame 1
            new Rectangle(600, 0, 300, 289), // Frame 2
            new Rectangle(900, 0, 300, 289), // Frame 3
            new Rectangle(1200, 0, 300, 289), // Frame 4
            new Rectangle(1500, 0, 300, 289), // Frame 5
            new Rectangle(1800, 0, 300, 289), // Frame 6
            new Rectangle(2100, 0, 300, 289), // Frame 7

            // HURT frames: row 1
            new Rectangle(0, 300, 300, 289), // Frame 8
            new Rectangle(300, 300, 300, 289), // Frame 9
            new Rectangle(600, 300, 300, 289), // Frame 10

            // DIE frames: row 2
            new Rectangle(0, 600, 300, 289), // Frame 11
            new Rectangle(300, 600, 300, 289), // Frame 12
            new Rectangle(600, 600, 300, 289), // Frame 13
    };

    private Random random = new Random();
    private double laserAngle = 0;
    private final int MAX_ANGLE = 15;
    private int health = 1000; // Boss has more health

    private boolean bossIsHurt = false;

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

        Image scaledImage = icon.getImage();
        setImage(scaledImage);
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

                case HURT:
                    if (frame < 3) { // Only 3 hurt frames
                        clipNo = 8 + frame;
                    } else {
                        action = IDLE;
                        frame = 0;
                    }
                    break;

                case DIE:
                    if (frame < 3) { // Only 3 die frames
                        clipNo = 11 + frame;
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
                laserState = LaserState.CHARGING;
            } else if (elapsed < PHASE2) {
                laserOpacity = 0.5f;
                laserState = LaserState.CHARGING;
            } else if (elapsed < PHASE3) {
                laserOpacity = 1.0f;
                laserState = LaserState.FULLY_CHARGED; // Only now is it fully charged
            } else {
                laserActive = false;
                laserOpacity = 0f;
                randomizeLaserAngle();
                laserState = LaserState.INACTIVE;
            }
        }
    }

    private boolean isDying = false;

    public void setDying(boolean dying) {
        this.isDying = dying;
        if (dying) {
            this.action = DIE;
            this.frame = 0; // Reset animation frame
        }
    }

    private void randomizeLaserAngle() {
        laserAngle = Math.toRadians(-MAX_ANGLE + random.nextInt(MAX_ANGLE * 2));
    }

    public void drawLaser(Graphics g) {
        if (!laserActive || laserOpacity <= 0f)
            return;

        Graphics2D g2d = (Graphics2D) g.create();
        try {
            // Different colors based on charge state
            Color laserColor;
            if (laserState == LaserState.FULLY_CHARGED) {
                laserColor = new Color(255, 50, 50, (int) (255 * laserOpacity)); // Red when fully charged
            } else {
                laserColor = new Color(255, 200, 50, (int) (200 * laserOpacity)); // Yellow when charging
            }

            g2d.setColor(laserColor);
            g2d.setStroke(new BasicStroke(8 + (8 * laserOpacity)));

            int startX = x + getWidth() - 40;
            int startY = y + getHeight() / 4;
            int endX = 0;
            int endY = (int) (startY - Math.tan(laserAngle) * startX);

            g2d.drawLine(startX, startY, endX, endY);

            // Add glow effect
            g2d.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, laserOpacity * 0.3f));
            g2d.setStroke(new BasicStroke(30));
            g2d.drawLine(startX, startY, endX, endY);
        } finally {
            g2d.dispose();
        }
    }

    public void activateLaser() {
        if (!laserActive) {
            this.chargeStartTime = System.currentTimeMillis();
            this.laserOpacity = 0.25f;
            randomizeLaserAngle();
            this.laserState = LaserState.CHARGING;
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
        if (!laserActive)
            return new Rectangle();

        int startX = x + getWidth() - 40;
        int startY = y + getHeight() / 4;
        int endX = 0;
        int endY = (int) (startY - Math.tan(laserAngle) * startX);

        // Create a polygon that better represents the laser's actual path
        int laserWidth = 15; // Width of the laser hitbox
        Polygon laserPolygon = new Polygon();

        // Add points to create a "thick line" polygon
        double angle = Math.atan2(endY - startY, endX - startX);
        double perpendicular = angle + Math.PI / 2;

        // Calculate offset points
        int xOffset = (int) (laserWidth / 2 * Math.cos(perpendicular));
        int yOffset = (int) (laserWidth / 2 * Math.sin(perpendicular));

        laserPolygon.addPoint(startX + xOffset, startY + yOffset);
        laserPolygon.addPoint(startX - xOffset, startY - yOffset);
        laserPolygon.addPoint(endX - xOffset, endY - yOffset);
        laserPolygon.addPoint(endX + xOffset, endY + yOffset);

        return laserPolygon.getBounds();
    }

    public boolean isLaserActive() {
        return laserActive;
    }

    public int getHealth() {
        return health;
    }

    /* public void setHealth(int health) {
        this.health = health;
        if (health < this.health) { // If took damage
            this.action = HURT;
            this.frame = 0;
        }
        this.health = Math.max(0, health);
    } */

    public void setHealth(int health) {
        if (health < this.health) { // Took damage
            this.action = HURT;
            this.frame = 0;
        }
        this.health = Math.max(0, health); // Apply new health after check
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

    public boolean isPlayerHitByLaser(Rectangle playerBox) {
        if (!isLaserFullyCharged()) return false;

        int startX = x + getWidth() - 40;
        int startY = y + getHeight() / 4;
        int endX = 0;
        int endY = (int) (startY - Math.tan(laserAngle) * startX);

        // Calculate the line equation: y = mx + b
        double m = (double) (endY - startY) / (endX - startX);
        double b = startY - m * startX;

        int steps = 10;
        int laserWidth = 15; // Width of the laser hitbox

        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            int checkX = (int) (startX + t * (endX - startX));
            int checkY = (int) (startY + t * (endY - startY));

            // Check a rectangle around this point of the laser
            Rectangle laserSegment = new Rectangle(
                    checkX - laserWidth / 2,
                    checkY - laserWidth / 2,
                    laserWidth,
                    laserWidth);

            if (laserSegment.intersects(playerBox)) {
                return true;
            }
        }

        return false;
    }

    public enum LaserState {
        CHARGING,
        FULLY_CHARGED,
        INACTIVE
    }

    private LaserState laserState = LaserState.INACTIVE;

    public boolean isLaserFullyCharged() {
        return laserState == LaserState.FULLY_CHARGED;
    }

    private long laserDamageCooldown = 0;
    private static final long LASER_DAMAGE_INTERVAL = 500; // 0.5 seconds between damage ticks

    public boolean canDamagePlayer() {
        return isLaserFullyCharged() &&
                System.currentTimeMillis() - laserDamageCooldown > LASER_DAMAGE_INTERVAL;
    }

    public void registerDamage() {
        laserDamageCooldown = System.currentTimeMillis();
    }

    public double getLaserAngle() {
        return laserAngle;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, getWidth(), getHeight());
    }

}