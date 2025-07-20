package gdd.sprite;

import static gdd.Global.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {
    private int x;
    private int y;
    private int speedY;

    private Image image;
    // private final int WIDTH;
    // private final int HEIGHT;

    private static final int START_X = 0; // Default starting X position
    private static final int START_Y = 250; // Default starting Y position

    private boolean upPressed, downPressed;

    ImageIcon playerpic = new ImageIcon(IMG_PLAYER);
    

    public Player() {
        // this.x = x;
        // this.y = y;
        // speedY = 4;

        // image = new ImageIcon("./src/assets/sprites/player.png").getImage(); // Make sure this path is correct
        // WIDTH = image.getWidth(null);
        // HEIGHT = image.getHeight(null);

        updatePlayer(false); // Default to phase 1
        initPlayer(playerpic);
    }
    
    public Player(boolean phase) {
        updatePlayer(phase);
        initPlayer(playerpic);
    }

    public void initPlayer(ImageIcon pic) {
        // var playerImage = new ImageIcon(IMG_PLAYER);
        var playerImage = pic;

        // Scale the image to use the global scaling factor
        var scaledImage = playerImage.getImage().getScaledInstance(playerImage.getIconWidth() * SCALE_FACTOR,
                playerImage.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);

        if (getImage() == null) {
            System.err.println("❌ Error: Player image not found at " + IMG_PLAYER);
        } else {
            System.out.println("✅ Player initialized at position: (" + START_X + ", " + START_Y + ")");
        }

    }

    // public void update() {
    //     if (upPressed) {
    //         y -= speedY;
    //     }
    //     if (downPressed) {
    //         y += speedY;
    //     }

    //     // Optional: limit movement to screen bounds
    //     if (y < 0)
    //         y = 0;
    //     if (y > 600 - HEIGHT)
    //         y = 600 - HEIGHT; // assuming screen height is 600
    // }

    // public void draw(Graphics g) {
    //     g.drawImage(image, x, y, null);
    // }

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

    public void act() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }
    
    public void updatePlayer(boolean phase) {
        if (phase) {
            playerpic = new ImageIcon(IMG_PLAYER_PHASE2);
        } else {
            playerpic = new ImageIcon(IMG_PLAYER);
        }
    }

    // Accessors if needed
    // public int getX() {
    //     return x;
    // }

    // public int getY() {
    //     return y;
    // }

    // public int getWidth() {
    //     return WIDTH;
    // }

    // public int getHeight() {
    //     return HEIGHT;
    // }

    // public Image getImage() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getImage'");
    // }
}