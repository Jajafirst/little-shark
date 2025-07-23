package gdd.powerup;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SpeedUp {
    private Image image;
    private int x, y;
    private int speed = 3;
    private int width, height;
    private int level;

    // ✅ Track the last collected level (shared across game)
    private static int lastCollectedLevel = 0;

    // ✅ Set level when collected
    public static void setCollectedLevel(int level) {
        lastCollectedLevel = level;
    }

    // ✅ Get level for display
    public static int getCollectedLevel() {
        return lastCollectedLevel;
    }

    // Constructor
    public SpeedUp(int level, int boardWidth, int boardHeight) {
        this.level = level;
        ImageIcon icon = new ImageIcon("./src/assets/sprites/speedSkill" + level + ".png");
        image = icon.getImage();

        width = image.getWidth(null);
        height = image.getHeight(null);

        x = boardWidth;
        Random rand = new Random();
        y = 50 + rand.nextInt(boardHeight - 100); // random Y position with padding

        System.out.println("📦 SpeedUp LV" + level + " created at Y=" + y);
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g, JPanel panel) {
        g.drawImage(image, x, y, panel);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getLevel() { return level; }
}