package gdd.sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BossBullet {
    private int x, y;
    private final int width = 32;     // bullet display width
    private final int height = 32;    // bullet display height
    private int speed = 8;
    private boolean active = true;

    private static BufferedImage bulletImage; // shared image to avoid reloading every time

    public BossBullet(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        if (bulletImage != null) return; // already loaded

        try {
            bulletImage = ImageIO.read(new File("src/assets/sprites/bossBullet.png"));
            System.out.println("✅ Boss bullet image loaded");
        } catch (IOException e) {
            System.out.println("⚠️ Boss bullet image not found, using default oval");
            bulletImage = null;
        }
    }

    public void update() {
        x -= speed; // move left
        if (x + width < 0) {
            active = false; // remove when off-screen
        }
    }

    public void draw(Graphics g, Component c) {
        if (bulletImage != null) {
            g.drawImage(bulletImage, x, y, width, height, c);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillOval(x, y, width, height);
        }
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}