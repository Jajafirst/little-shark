package gdd.sprite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player {
    private int x;
    private int y;
    private int speedY;

    private Image image;
    private final int WIDTH;
    private final int HEIGHT;

    private boolean upPressed, downPressed;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        speedY = 4;

        image = new ImageIcon("src/assets/sprites/player.png").getImage(); // Make sure this path is correct
        WIDTH = image.getWidth(null);
        HEIGHT = image.getHeight(null);
    }

    public void update() {
        if (upPressed) {
            y -= speedY;
        }
        if (downPressed) {
            y += speedY;
        }

        // Optional: limit movement to screen bounds
        if (y < 0)
            y = 0;
        if (y > 600 - HEIGHT)
            y = 600 - HEIGHT; // assuming screen height is 600
    }

    public void draw(Graphics2D g) {
        g.drawImage(image, x, y, null);
    }

    public void keyPressed(int key) {
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            downPressed = true;
        }
    }

    public void keyReleased(int key) {
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            downPressed = false;
        }
    }

    // Accessors if needed
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}