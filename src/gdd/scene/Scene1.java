// TODO 
// - add sound effects
// FIXME
// - load Scene2 when end the round intead of pressing space 

// - fix timer, it makes player speed different in Scene1 and Scene2

package gdd.scene;

import gdd.Game;
import gdd.sprite.Player;

// import gdd.powerup.SpeedUp;
import java.util.Random;

import javax.imageio.ImageIO;
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

public class Scene1 extends JPanel {

    private Game game;
    private Player player;
    private Timer timer = null;

    private Image staticBg; // background1.png
    private Image parallaxBg; // parallax1.png
    private int parallaxX;

    private int currentSpeedLevel = 0;


    // private SpeedUp speedUp;
    private Random rand = new Random();

    public Scene1(Game game) {
        this.game = game;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(DELAY, new GameCycle());
        timer.start();

        gameInit();
        System.out.println("✅ Scene1 started");

        // 🔁 Game loop
        Timer timer = new Timer(16, e -> {
            update(); // move background
            repaint(); // redraw screen
        });
        timer.start();
    }

    private void gameInit() {
        // Load parallax background
        ImageIcon parallaxIcon = new ImageIcon("./src/assets/background/final-scene1.png");
        parallaxBg = parallaxIcon.getImage();

        // speedUp = new SpeedUp();

        player = new Player();
    }

    public void update() {
        // 🌊 Parallax background scroll
        parallaxX -= 1;
        if (parallaxBg != null && parallaxX <= -parallaxBg.getWidth(null)) {
            parallaxX = 0;
        }

        /* // ✅ Random spawn if not maxed level and no item exists
        if (speedUp == null && currentSpeedLevel < 4 && rand.nextInt(200) == 0) {
            speedUp = new SpeedUp(currentSpeedLevel + 1, BOARD_WIDTH, BOARD_HEIGHT);
            System.out.println("🟢 Spawned SpeedUp LV" + (currentSpeedLevel + 1));
        }

        // ✅ Update speedUp if it's active
        if (speedUp != null) {
            speedUp.update();

            Rectangle skillBox = speedUp.getBounds();

            // 💡 Use estimated player box (128x128 or your sprite size)
            Rectangle playerBox = new Rectangle(player.getX(), player.getY(), 128, 128);

            if (skillBox.intersects(playerBox)) {
                currentSpeedLevel++;
                speedUp = null;
                System.out.println("🎯 Collected LV" + currentSpeedLevel);
            }

            if (speedUp.getX() + speedUp.getWidth() < 0) {
                speedUp = null;
            }
        }
 */
        player.update();

    }

    public void draw(Graphics g) {
        // 1. Draw static background (ocean) first
        if (staticBg != null) {
            g.drawImage(staticBg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

        // 2. Draw scrolling parallax background (sand)
        if (parallaxBg != null) {
            int width = parallaxBg.getWidth(null);
            int height = parallaxBg.getHeight(null);
            int y = (BOARD_HEIGHT - height) / 2; // center vertically

            // Draw two tiles for seamless looping
            g.drawImage(parallaxBg, parallaxX, y, null);
            g.drawImage(parallaxBg, parallaxX + width - 4, y, null);
        }
        // Do about items
        /* if (speedUp != null) {
            speedUp.draw(g, this);
        } */

        // 3. Draw player on top
        drawPlayer(g);
    }

    public void drawPlayer(Graphics g) {
        if (player != null) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                System.out.println("🔁 Switching to Scene2...");
                game.loadScene2();
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }

    //_____________________________________________

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
        }
    }

}
