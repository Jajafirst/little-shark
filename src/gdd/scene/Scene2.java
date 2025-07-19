package gdd.scene;

import gdd.Game;
import gdd.sprite.Player;

import javax.swing.*;

import static gdd.Global.BOARD_HEIGHT;
import static gdd.Global.BOARD_WIDTH;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.IOException;

public class Scene2 extends JPanel {

    private Game game;
    private Player player;

    private Image staticBg2;     // background2.png
    private Image parallaxBg2;   // parallax2.png
    private int parallaxX;

    public Scene2() {   

        // try {
        //     staticBg = ImageIO.read(getClass().getResource("/src/assets/background/background1.png"));
        //     parallaxBg = ImageIO.read(getClass().getResource("/src/assets/background/parallax1.png"));
        // } catch (IOException e) {
        //     System.err.println("Error loading background images");
        //     e.printStackTrace();
        // }

    }

    public void start() {
        // requestFocusInWindow();

        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        gameInit();
        System.out.println("✅ Scene2 started");
    }


    private void gameInit() {
        // Load static background
        ImageIcon titleIcon = new ImageIcon("./src/background/background2.png");
        staticBg2 = titleIcon.getImage();
        // Load parallax background
        ImageIcon parallaxIcon = new ImageIcon("./src/background/parallax1.png");
        parallaxBg2 = parallaxIcon.getImage();

        // Todo Auto-generated method stub
        player = new Player(100, 300);
    }

    public void update() {
        // parallaxX -= 1; // scroll speed
        // if (parallaxBg != null && parallaxX <= -parallaxBg.getWidth(null)) {
        //     parallaxX = 0;
        // }
        // player.update();
    }

    public void draw(Graphics g) {
        // Draw static background first
        if (staticBg2 != null) {
            g.drawImage(staticBg2, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

        // Then draw scrolling parallax layer
        // if (parallaxBg != null) {
        //     int width = parallaxBg.getWidth(null);
        //     g.drawImage(parallaxBg, parallaxX, 0, null);
        //     g.drawImage(parallaxBg, parallaxX + width, 0, null);
        // }
        if (parallaxBg2 != null) {
            int width = parallaxBg2.getWidth(null);
            g.drawImage(parallaxBg2, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e.getKeyCode());
        }
    }

}