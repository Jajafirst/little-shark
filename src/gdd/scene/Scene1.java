// TODO 
// - make player animation, 
// - background scrolling, 
// - add sound effects
// FIXME
// - load Scene2 when end the round intead of pressing space 

package gdd.scene;

import gdd.Game;
import gdd.sprite.Player;

import javax.imageio.ImageIO;
import javax.swing.*;

import static gdd.Global.BOARD_HEIGHT;
import static gdd.Global.BOARD_WIDTH;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.IOException;

public class Scene1 extends JPanel {

    private Game game;
    private Player player;

    private Image staticBg;     // background1.png
    private Image parallaxBg;   // parallax1.png
    private int parallaxX;

    public Scene1(Game game) {   
        this.game = game;
        
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
        System.out.println("✅ Scene1 started");
    }


    private void gameInit() {
        // Load static background
        ImageIcon backgroundIcon = new ImageIcon("./src/assets/background/background1.png");
        staticBg = backgroundIcon.getImage();
        // Load parallax background
        ImageIcon parallaxIcon = new ImageIcon("./src/assets/background/parallax1.png");
        parallaxBg = parallaxIcon.getImage();

        // TODO Auto-generated method stub
        player = new Player();
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
        if (staticBg != null) {
            g.drawImage(staticBg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

        // Then draw scrolling parallax layer
        // if (parallaxBg != null) {
        //     int width = parallaxBg.getWidth(null);
        //     g.drawImage(parallaxBg, parallaxX, 0, null);
        //     g.drawImage(parallaxBg, parallaxX + width, 0, null);
        // }
        if (parallaxBg != null) {
            int width = parallaxBg.getWidth(null);
            g.drawImage(parallaxBg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

        drawPlayer(g);

    }

    public void drawPlayer(Graphics g) {
        if (player != null) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
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
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                System.out.println("🔁 Switching to Scene2...");
                game.loadScene2();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e.getKeyCode());
        }
    }

}