package gdd.powerup;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PowerUp {
    private int x, y;
    private int level;
    private boolean collected = false;
    private BufferedImage image;

    public PowerUp(int x, int y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("./src/assets/sprites/shotSkill" + level + ".png"));
            System.out.println("✅ Loaded shotSkill" + level + ".png");
        } catch (Exception e) {
            System.err.println("❌ Cannot load shotSkill image: " + e.getMessage());
        }
    }

    public void update() {
        x -= 2; // 💨 move left at constant speed
    }

    public void draw(Graphics g, Component c) {
        if (!collected && image != null) {
            g.drawImage(image, x, y, 48, 48, c);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 48, 48);
    }

    public int getLevel() {
        return level;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}