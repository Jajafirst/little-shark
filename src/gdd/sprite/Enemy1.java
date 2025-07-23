package gdd.sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy1 {
    private int x, y;
    private final int speed = 3;

    private final int drawWidth = 128;
    private final int drawHeight = 128;

    private BufferedImage[] frames;
    private int frameIndex = 0;
    private int animationCounter = 0;
    private final int animationDelay = 8;

    private List<EnemyBullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private static final int SHOT_INTERVAL = 2000; // 2 seconds

    private static BufferedImage[] sharedFramesEnemy1 = null;

    public Enemy1(int panelWidth, int panelHeight) {
        this.x = panelWidth + new Random().nextInt(300); // appear offscreen
        this.y = new Random().nextInt(panelHeight - drawHeight - 50);
        loadSpriteSheet("./src/assets/sprites/enemy1.png");

        System.out.println("🧠 Spawned Enemy1 at Y = " + y);
    }

    private void loadSpriteSheet(String path) {
        try {
            if (sharedFramesEnemy1 != null) {
                frames = sharedFramesEnemy1;
                return;
            }

            BufferedImage sheet = ImageIO.read(new File(path));
            int frameCount = 4;
            int frameWidth = 320;
            int frameHeight = 320;

            frames = new BufferedImage[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }

            sharedFramesEnemy1 = frames;
            this.frames = sharedFramesEnemy1;

            System.out.println("✅ Enemy1 sprite sheet loaded: " + frameCount + " frames");

        } catch (IOException e) {
            System.err.println("❌ Failed to load Enemy1 sprite: " + e.getMessage());
        }
    }

    public void update() {
        x -= speed;

        // Animate
        animationCounter++;
        if (animationCounter >= animationDelay) {
            animationCounter = 0;
            frameIndex = (frameIndex + 1) % frames.length;
        }

        // Fire two bullets
        long now = System.currentTimeMillis();
        if (now - lastShotTime > SHOT_INTERVAL) {
            bullets.add(new EnemyBullet(x, y + drawHeight / 3)); // upper bullet
            bullets.add(new EnemyBullet(x, y + 2 * drawHeight / 3)); // lower bullet
            lastShotTime = now;
        }

        // Update bullets
        bullets.removeIf(b -> {
            b.update();
            return b.getX() < -20;
        });
    }   

    public void draw(Graphics g, Component c) {
        if (frames != null && frames[frameIndex] != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(frames[frameIndex], x + drawWidth, y, -drawWidth, drawHeight, c);
        }

        for (EnemyBullet bullet : bullets) {
            bullet.draw(g, c);
        }
    }

    public List<EnemyBullet> getBullets() {
        return bullets;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, drawWidth, drawHeight);
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return drawWidth;
    }

    public int getY() {
        return y;
    }
}