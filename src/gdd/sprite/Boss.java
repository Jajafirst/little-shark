package gdd.sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import gdd.sprite.BossBullet;
import java.util.Random;

public class Boss {
    private int x, y;
    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private final int FRAME_DELAY = 120;

    private boolean laserActive = false;
    private float laserOpacity = 0.25f;
    private long chargeStartTime;

    private int laserOffsetY;
    private double laserAngle = 0; // current laser angle in radians
    private final int MAX_ANGLE = 15; // maximum random angle in degrees

    private final int PHASE1 = 500;
    private final int PHASE2 = 1000;
    private final int PHASE3 = 1500;

    private final int FRAME_COUNT = 8;
    private final int FRAME_WIDTH = 320;
    private final int FRAME_HEIGHT = 320;
    private final int DISPLAY_WIDTH = 320;
    private final int DISPLAY_HEIGHT = 320;

    private final int SCREEN_HEIGHT = 720;
    private final int BOSS_BOTTOM_MARGIN = 0;
    private final int LASER_OFFSET_RANGE = 60;

    private int zIndex = 1000; // 🔥 Boss always top layer

    private Random random = new Random();
    private Component boss;

    private int clipNo = 0;
    private final Rectangle[] clips = new Rectangle[] {
        // Walking
        new Rectangle(0, 0, 55, 96),  // Frame 0
        new Rectangle(103, 0, 55, 96),  // Frame 1
        new Rectangle(103, 0, 55, 96),  // Frame 1

    };


    //________________________________________________
    /** ✅ Constructor 1: Auto bottom alignment (only X) */
    public Boss(int x) {
        this.x = x;
        this.y = SCREEN_HEIGHT - DISPLAY_HEIGHT - BOSS_BOTTOM_MARGIN;
        init();
    }

    /** ✅ Constructor 2: Manual X and Y */
    public Boss(int x, int y) {
        this.x = x;
        this.y = y;
        init();
    }

    /** Shared init code */
    private void init() {
        loadSpriteSheet();
        sliceFrames();
        pickNewLaserOffset();
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(new File("src/assets/sprites/boss.png"));
        } catch (IOException e) {
            System.out.println("❌ Failed to load boss sprite sheet");
            e.printStackTrace();
        }
    }

    private void sliceFrames() {
        if (spriteSheet == null)
            return;
        frames = new BufferedImage[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = spriteSheet.getSubimage(i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT);
        }
    }

    private void pickNewLaserOffset() {
        laserOffsetY = -LASER_OFFSET_RANGE + random.nextInt(LASER_OFFSET_RANGE * 2);
        laserAngle = Math.toRadians(-MAX_ANGLE + random.nextInt(MAX_ANGLE * 2));
    }

    public void update() {
        animate();

        if (laserActive) {
            long elapsed = System.currentTimeMillis() - chargeStartTime;

            if (elapsed < PHASE1) {
                laserOpacity = 0.25f;
            } else if (elapsed < PHASE2) {
                laserOpacity = 0.5f;
            } else if (elapsed < PHASE3) {
                laserOpacity = 1.0f;
            } else {
                // ✅ Turn laser off and pick new Y & angle
                laserActive = false;
                laserOpacity = 0f;
                pickNewLaserOffset();
                System.out.println("🔻 Laser OFF + New Y/Angle");
            }
        }
    }

    private void animate() {
        if (frames == null)
            return;
        if (System.currentTimeMillis() - lastFrameTime > FRAME_DELAY) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastFrameTime = System.currentTimeMillis();
        }
    }

    public void draw(Graphics g, Component observer) {
        if (frames != null && frames[currentFrame] != null) {
            g.drawImage(frames[currentFrame], x, y, DISPLAY_WIDTH, DISPLAY_HEIGHT, observer);
        }
    }

    public void drawLaser(Graphics g) {
        if (!laserActive || laserOpacity <= 0f)
            return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, laserOpacity));
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(8));

        // ✅ Fixed start point (eye/head position)
        int startX = x + DISPLAY_WIDTH - 40; // Adjust to eye X
        int startY = y + DISPLAY_HEIGHT / 4; // Adjust to eye Y

        // 🔥 Calculate angle and force beam to x=0 at left edge
        double dx = Math.cos(laserAngle);
        double dy = Math.sin(laserAngle);

        double t = startX / dx;
        int endX = 0;
        int endY = (int) (startY - dy * t);

        g2d.drawLine(startX, startY, endX, endY);
        g2d.dispose();
    }

    public void setLaserActive(boolean active) {
        if (active && !laserActive) {
            this.chargeStartTime = System.currentTimeMillis();
            this.laserOpacity = 0.25f;
        }
        this.laserActive = active;
    }

    public boolean isLaserActive() {
        return laserActive;
    }

    public Rectangle getLaserBounds() {
        if (!laserActive)
            return new Rectangle(0, 0, 0, 0);
        int startX = x + DISPLAY_WIDTH / 2;
        int startY = y + DISPLAY_HEIGHT / 2 + laserOffsetY;
        return new Rectangle(0, startY - 12, startX, 24);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int z) {
        this.zIndex = z;
    }

    public int getBulletX() {
        return x + DISPLAY_WIDTH - 40; // adjust to mouth/eye X
    }

    public int getBulletY() {
        return y + DISPLAY_HEIGHT / 3; // adjust to mouth/eye Y
    }
}