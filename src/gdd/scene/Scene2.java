package gdd.scene;

import gdd.Game;
import gdd.sprite.Player;

import javax.swing.*;

import static gdd.Global.BOARD_HEIGHT;
import static gdd.Global.BOARD_WIDTH;
import static gdd.Global.DELAY;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.IOException;

public class Scene2 extends JPanel {
    
    private Game game;
    private Player player;

    private Image staticBg2; // background2.png
    private Image parallaxBg2; // parallax2.png
    private int parallaxX;

    private Timer timer;

    public Scene2(Game game) {
        this.game = game;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        gameInit();
        System.out.println("✅ Scene2 started");
        
        // 🔁 Game loop
        timer = new Timer(DELAY, new GameCycle());
        timer.start();
    }

    private void gameInit() {
        // Load parallax background
        ImageIcon parallaxIcon = new ImageIcon("./src/assets/background/final-scene2.png");
        parallaxBg2 = parallaxIcon.getImage();

        player = new Player();
        
    }

    public void update() {
        parallaxX -= 1; // adjust speed if needed

        if (parallaxBg2 != null) {
            int width = parallaxBg2.getWidth(null);
            if (parallaxX <= -width) {
                parallaxX = 0;
            }
        }

        System.out.println("Updating Scene2...");
        player.update();
    }

    public void draw(Graphics g) {
        // 1. Draw static background (ocean) first
        if (staticBg2 != null) {
            g.drawImage(staticBg2, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

        // 2. Draw scrolling parallax background (sand)
        if (parallaxBg2 != null) {
            int width = parallaxBg2.getWidth(null);
            int height = parallaxBg2.getHeight(null);
            int y = (BOARD_HEIGHT - height) / 2; // center vertically

            // Draw two tiles for seamless looping
            g.drawImage(parallaxBg2, parallaxX, y, null);
            g.drawImage(parallaxBg2, parallaxX + width - 4, y, null);
        }

        // 3. Draw player on top
        drawPlayer(g);
        player.updatePlayer(true);
    }

    public void drawPlayer(Graphics g) {
        if (player != null) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
    }

    // _____________________________________________

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
            System.out.println("Game cycle executed");
        }
    }

}