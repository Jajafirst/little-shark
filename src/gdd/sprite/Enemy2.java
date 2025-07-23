package gdd.sprite;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Enemy2 {
    private int x, y;
    private final int speed = 10;

    private final int drawWidth = 128;
    private final int drawHeight = 64;

    private BufferedImage[] frames;
    private int frameIndex = 0;
    private int animationCounter = 0;
    private final int animationDelay = 6; // Slightly faster for smoothness

    // ✅ Shared across all instances
    private static BufferedImage[] sharedFrames = null;

    public Enemy2(int panelWidth, int panelHeight) {
        this.x = panelWidth + new Random().nextInt(200); // Start further out
        this.y = new Random().nextInt(panelHeight - drawHeight - 50);
        loadSpriteSheet("./src/assets/sprites/enemy2.png");

        System.out.println("🦈 Spawned Enemy2 at random Y = " + y);
    }

    private void loadSpriteSheet(String path) {
        try {
            if (sharedFrames != null) {
                frames = sharedFrames;
                return;
            }

            BufferedImage sheet = ImageIO.read(new File(path));

            int frameWidth = 320; 
            int frameHeight = 160;
            int frameCount = 4;

            frames = new BufferedImage[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }

            sharedFrames = frames;
            this.frames = sharedFrames;

            System.out.println("✅ Enemy2 sprite sheet loaded. Frame size: " + frameWidth + "x" + frameHeight);

        } catch (IOException e) {
            System.err.println("❌ Failed to load Enemy2 sprite: " + e.getMessage());
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
    }

    public void draw(Graphics g, Component c) {
        if (frames != null && frames[frameIndex] != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(frames[frameIndex], x + drawWidth, y, -drawWidth, drawHeight, c);
        }
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