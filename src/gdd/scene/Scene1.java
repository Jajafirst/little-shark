// TODO 
// - make player animation, 
// - background scrolling, 
// - add sound effects
// FIXME
// - load Scene2 when end the round intead of pressing space 

package gdd.scene;

import gdd.Game;
import gdd.sprite.Player;

import gdd.powerup.SpeedUp;
import java.util.Random;

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
    // private Timer timer;
    private Image staticBg; // background1.png
    private Image parallaxBg; // parallax1.png
    private int parallaxX;
    private int currentSpeedLevel = 0;
    private boolean firstSpawned = false;
    private long lastSpawnTime = 0;
    private static final long SPAWN_INTERVAL = 6_000; // 1 minute in milliseconds

    private SpeedUp speedUp;
    private Image speedIcon;

    private static int lastCollectedLevel = 0; // default LV0, nothing collected

    public static void setCollectedLevel(int level) {
        lastCollectedLevel = level;
    }

    public static int getCollectedLevel() {
        return lastCollectedLevel;
    }

    public Scene1(Game game) {
        this.game = game;

        // try {
        // staticBg =
        // ImageIO.read(getClass().getResource("/src/assets/background/background1.png"));
        // parallaxBg =
        // ImageIO.read(getClass().getResource("/src/assets/background/parallax1.png"));
        // } catch (IOException e) {
        // System.err.println("Error loading background images");
        // e.printStackTrace();
        // }

    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        gameInit();
        System.out.println("✅ Scene1 started");

        // Game loop
        Timer timer = new Timer(16, e -> {
            update(); // move background
            repaint(); // redraw screen
        });
        timer.start();
    }

    private void gameInit() {
        // Load static background
        ImageIcon backgroundIcon = new ImageIcon("./src/assets/background/background1.png");
        staticBg = backgroundIcon.getImage();
        // Load parallax background
        ImageIcon parallaxIcon = new ImageIcon("./src/assets/background/final-scene1.png");
        parallaxBg = parallaxIcon.getImage();

        // speedUp = new SpeedUp();
        ImageIcon speedIconImg = new ImageIcon("./src/assets/sprites/speedSkill1.png");
        speedIcon = speedIconImg.getImage();

        player = new Player();
    }

    public void update() {
        // 🌊 Background scroll
        parallaxX -= 1;
        if (parallaxBg != null && parallaxX <= -parallaxBg.getWidth(null)) {
            parallaxX = 0;
        }

        long currentTime = System.currentTimeMillis();
 
        // Spawn LV1 at start
        if (!firstSpawned) {
            speedUp = new SpeedUp(1, BOARD_WIDTH, BOARD_HEIGHT);
            lastSpawnTime = currentTime;
            firstSpawned = true;
            System.out.println("🟢 SpeedUp LV1 created at Y=" + speedUp.getY());
        }

        // ⏱ Spawn next level (after interval) if none on screen
        if (speedUp == null && currentSpeedLevel < 4 && (currentTime - lastSpawnTime >= SPAWN_INTERVAL)) {
            int nextLevel = currentSpeedLevel + 1;
            speedUp = new SpeedUp(nextLevel, BOARD_WIDTH, BOARD_HEIGHT);
            lastSpawnTime = currentTime;
            System.out.println("🟢 SpeedUp LV" + nextLevel + " created at Y=" + speedUp.getY());
        }

        // Update and detect collision
        if (speedUp != null) {
            speedUp.update();

            Rectangle skillBox = speedUp.getBounds();
            Rectangle playerBox = new Rectangle(player.getX(), player.getY(), 128, 128);

            // COLLECTED
            if (skillBox.intersects(playerBox)) {
                currentSpeedLevel = speedUp.getLevel(); // match exact level
                updateSpeedIcon(currentSpeedLevel);
                SpeedUp.setCollectedLevel(currentSpeedLevel);
                speedUp = null;
                lastSpawnTime = System.currentTimeMillis();
                System.out.println("🎯 Collected SpeedUp LV" + currentSpeedLevel);
            }

            // MISSED
            else if (speedUp.getX() + speedUp.getWidth() < 0) {
                System.out.println("🗑️ Missed SpeedUp LV" + speedUp.getLevel() + " — retrying same LV...");
                speedUp = null;
                lastSpawnTime = System.currentTimeMillis(); // retry after interval
            }
        }

        // Optional: end game if max level reached
        if (currentSpeedLevel >= 4 && speedUp == null) {
            System.out.println("All SpeedUps collected! Switch to Scene2...");
            game.loadScene2(); // or trigger boss, next wave, etc.
        }
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
            int y = (BOARD_HEIGHT - height) / 4; // center vertically

            // Draw two tiles for seamless looping
            g.drawImage(parallaxBg, parallaxX, y, null);
            g.drawImage(parallaxBg, parallaxX + width, y, null);
        }

        if (speedUp != null) {
            speedUp.draw(g, this);
        }

        // 3. Draw player on top
        drawPlayer(g);

        // 4. Speed Icon
        drawPowerUpUI(g);
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

    private void drawPowerUpUI(Graphics g) {
        if (currentSpeedLevel > 0 && speedIcon != null) {
            int iconSize = 48;
            int padding = 10;
            int x = BOARD_WIDTH - iconSize - padding;
            int y = padding;

            g.drawImage(speedIcon, x, y, iconSize, iconSize, this);

            // Draw Roman level (I, II, III, IV)
            String roman = switch (currentSpeedLevel) {
                case 1 -> "I";
                case 2 -> "II";
                case 3 -> "III";
                case 4 -> "IV";
                default -> "";
            };

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString(roman, x + iconSize + 5, y + iconSize - 10);
        }
    }

    private void updateSpeedIcon(int level) {
        String path = switch (level) {
            case 1 -> "./src/assets/sprites/speedSkill1.png";
            case 2 -> "./src/assets/sprites/speedSkill2.png";
            case 3 -> "./src/assets/sprites/speedSkill3.png";
            case 4 -> "./src/assets/sprites/speedSkill4.png";
            default -> null;
        };

        if (path != null) {
            speedIcon = new ImageIcon(path).getImage();
        }
    }
}
