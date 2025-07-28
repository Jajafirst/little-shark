package gdd.sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EnemyBullet {
    private int x, y;
    private final int speed = 4;
    private final int width = 32;
    private final int height = 32;
    private BufferedImage image;
    private boolean visible;

    public EnemyBullet(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage("./src/assets/sprites/enemyBullet.png");
    }

    private void loadImage(String path) {
        try {
            image = ImageIO.read(new File(path));
            System.out.println("✅ Loaded bullet image");
        } catch (IOException e) {
            System.err.println("❌ Bullet image load error: " + e.getMessage());
        }
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g, Component c) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, c);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public boolean isVisible() {
        return visible; // You should have this flag
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    //_____________________
    private boolean hasHitPlayer = false;

    public boolean hasHitPlayer() {
        return hasHitPlayer;
    }

    public void setHasHitPlayer(boolean hasHitPlayer) {
        this.hasHitPlayer = hasHitPlayer;
    }


    public int getX() { return x; }
    public int getWidth() { return width; }
}