package gdd.scene;

import gdd.Game;
import gdd.sprite.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Scene1 extends JPanel implements Scene {

    private Game game;
    private Player player;

    private BufferedImage staticBg;     // background1.png
    private BufferedImage parallaxBg;   // parallax1.png
    private int parallaxX;

    public Scene1(Game game) {
        this.game = game;
        setFocusable(true);
        setPreferredSize(new Dimension(1300, 800)); // Match your Global settings
        setBackground(Color.BLACK);

        try {
            staticBg = ImageIO.read(getClass().getResource("/assets/background/background1.png"));
            parallaxBg = ImageIO.read(getClass().getResource("/assets/background/parallax1.png"));
        } catch (IOException e) {
            System.err.println("Error loading background images");
            e.printStackTrace();
        }

        player = new Player(100, 300);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e.getKeyCode());
            }
        });
    }

    public void start() {
        requestFocusInWindow();
    }

    @Override
    public void update() {
        parallaxX -= 1; // scroll speed
        if (parallaxBg != null && parallaxX <= -parallaxBg.getWidth()) {
            parallaxX = 0;
        }
        player.update();
    }

    @Override
    public void draw(Graphics2D g) {
        // Draw static background first
        if (staticBg != null) {
            g.drawImage(staticBg, 0, 0, null);
        }

        // Then draw scrolling parallax layer
        if (parallaxBg != null) {
            int width = parallaxBg.getWidth();
            g.drawImage(parallaxBg, parallaxX, 0, null);
            g.drawImage(parallaxBg, parallaxX + width, 0, null);
        }

        // Then draw player
        player.draw(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
}