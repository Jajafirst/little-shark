// TODO 
// - add sound effects
// - add hitbox for player
// FIXME
// - load Scene2 when end the round intead of pressing space 
// - delay spawn spawn player shots

package gdd.scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import gdd.sprite.Enemy1;
import gdd.sprite.Enemy2;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import gdd.powerup.SpeedUp;
import gdd.Game;

import static gdd.Global.BOARD_HEIGHT;
import static gdd.Global.BOARD_WIDTH;
import static gdd.Global.DELAY;
import static gdd.Global.PARALLAX_SCROLL_SPEED;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

public class Scene1 extends JPanel {

    private Game game;
    private Player player;
    private Timer timer;

    // Background and Parallax
    private Image staticBg;
    private Image parallaxBg;
    private int parallaxX;
    private boolean switchedToScene2 = false;

    // SpeedUp Icon
    private static int lastCollectedLevel = 0;
    private SpeedUp speedUp;
    private Image speedIcon;
    private int currentSpeedLevel = 0;
    private boolean firstSpawned = false;
    private long lastSpawnTime = 0;
    private static final long SPAWN_INTERVAL = 6_000; 

    // Enemies
    private List<Enemy1> enemy1List = new ArrayList<>();
    private List<Enemy2> enemy2List = new ArrayList<>();

    private List<Shot> shots = new ArrayList<>();

    public static void setCollectedLevel(int level) {
        lastCollectedLevel = level;
    }

    public static int getCollectedLevel() {
        return lastCollectedLevel;
    }

    private Random rand = new Random();

    //____________________________________________
    public Scene1(Game game) {
        this.game = game;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        gameInit();
        System.out.println("✅ Scene1 started");
        
        timer = new Timer(DELAY, new GameCycle());
        timer.start();
    }

    private void gameInit() {
        // Load parallax background
        ImageIcon parallaxIcon = new ImageIcon("./src/assets/background/final-scene1.png");
        parallaxBg = parallaxIcon.getImage();

        // speedUp 
        ImageIcon speedIconImg = new ImageIcon("./src/assets/sprites/speedSkill1.png");
        speedIcon = speedIconImg.getImage();

        player = new Player();
        player.setHealth(100);
    }

    public void update() {
        // Scroll parallax background
        parallaxX -= PARALLAX_SCROLL_SPEED;

        if (parallaxBg != null && parallaxX <= -parallaxBg.getWidth(null)) {
            parallaxX = 0;
        }

        long currentTime = System.currentTimeMillis();

        // Spawn first SpeedUp LV1
        if (!firstSpawned) {
            speedUp = new SpeedUp(1, BOARD_WIDTH, BOARD_HEIGHT);
            lastSpawnTime = currentTime;
            firstSpawned = true;
            System.out.println("🟢 SpeedUp LV1 created at Y=" + speedUp.getY());
        }

        // Spawn next SpeedUp after delay
        if (speedUp == null && currentSpeedLevel < 4 && (currentTime - lastSpawnTime >= SPAWN_INTERVAL)) {
            int nextLevel = currentSpeedLevel + 1;
            speedUp = new SpeedUp(nextLevel, BOARD_WIDTH, BOARD_HEIGHT);
            lastSpawnTime = currentTime;
            System.out.println("🟢 SpeedUp LV" + nextLevel + " created at Y=" + speedUp.getY());
        }

        // Update SpeedUp
        if (speedUp != null) {
            speedUp.update();

            Rectangle skillBox = speedUp.getBounds();
            Rectangle playerBox = new Rectangle(player.getX(), player.getY(), 128, 128);

            // Collected
            if (skillBox.intersects(playerBox)) {
                currentSpeedLevel = speedUp.getLevel();
                updateSpeedIcon(currentSpeedLevel);
                SpeedUp.setCollectedLevel(currentSpeedLevel);
                speedUp = null;
                lastSpawnTime = System.currentTimeMillis();
                System.out.println("🎯 Collected SpeedUp LV" + currentSpeedLevel);
            }

            // Missed
            else if (speedUp.getX() + speedUp.getWidth() < 0) {
                System.out.println("🗑️ Missed SpeedUp LV" + speedUp.getLevel());
                speedUp = null;
                lastSpawnTime = System.currentTimeMillis();
            }
        }

        // Enemy 1 : shot the bullet
        int MAX_ENEMY1 = 2;
        if (enemy1List.size() < MAX_ENEMY1 && rand.nextInt(100) < 2) {
            enemy1List.add(new Enemy1(BOARD_WIDTH, BOARD_HEIGHT));
            System.out.println("🐙 Spawned Enemy1 (total: " + enemy1List.size() + ")");
        }
        Iterator<Enemy1> it1 = enemy1List.iterator();
        while (it1.hasNext()) {
            Enemy1 e1 = it1.next();
            e1.update();
            if (e1.getX() + e1.getWidth() < 0) {
                it1.remove(); // remove off-screen
            }
        }

        // Enemy 2 : just dive
        int MAX_ENEMIES = 3;
        if (enemy2List.size() < MAX_ENEMIES && rand.nextInt(100) < 2) {
            enemy2List.add(new Enemy2(BOARD_WIDTH, BOARD_HEIGHT));
            System.out.println("🦈 Spawned Enemy2 (total: " + enemy2List.size() + ")");
        }

        Iterator<Enemy2> it = enemy2List.iterator();
        while (it.hasNext()) {
            Enemy2 e = it.next();
            e.update();
            if (e.getX() + e.getWidth() < 0) {
                it.remove();
            }
        }

        player.update();

        // Auto switch to Scene2 when done
        if (!switchedToScene2 && currentSpeedLevel >= 4 && speedUp == null) {
            switchedToScene2 = true;
            System.out.println("✅ All SpeedUps collected! Switching scene...");
            game.loadScene2();
        }

        // player shots
        List<Shot> toRemovesShots = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {

                // Kill enemy1 when shot hits
                for (Enemy1 enemy1 : enemy1List) {
                    if (enemy1.getBounds().intersects(shot.getBounds())) {
                        enemy1List.remove(enemy1);
                        toRemovesShots.add(shot);
                        break; // Exit loop after hit
                    }
                }
                // Kill enemy2 when shot hits
                for (Enemy2 enemy2 : enemy2List) {
                    if (enemy2.getBounds().intersects(shot.getBounds())) {
                        enemy2List.remove(enemy2);
                        toRemovesShots.add(shot);
                        break; // Exit loop after hit
                    }
                }

                // Speed up shot
                int x = shot.getX();
                x += 8;

                if (x > BOARD_WIDTH) {
                    shot.die();
                    System.out.println("🗑️ Shot removed (off-screen)");
                } else {
                    shot.setX(x);
                }

                if (!shot.isVisible()) {
                    toRemovesShots.add(shot);
                }
            }
        }
        shots.removeAll(toRemovesShots);

        // Player hitbox check - modified to only reduce health once per collision
        for (Enemy2 enemy2 : enemy2List) {
            if (enemy2.getBounds().intersects(new Rectangle(player.getX(), player.getY(), 128, 128))) {
                // Only reduce health if this enemy hasn't hit the player before
                if (!enemy2.hasHitPlayer()) {
                    player.setHealth(player.getHealth() - 10);
                    enemy2.setHasHitPlayer(true); // Mark this enemy as having hit the player
                    System.out.println("💥 Player hit! Health: " + player.getHealth());
                    if (player.getHealth() <= 0) {
                        System.out.println("💀 Player died!");
                        // Handle player death (e.g., game over)
                    }
                }
            } else {
                // Reset the flag when the enemy is no longer colliding
                enemy2.setHasHitPlayer(false);
            }
        }
        for (Enemy1 enemy1 : enemy1List) {
            if (enemy1.getBounds().intersects(new Rectangle(player.getX(), player.getY(), 128, 128))) {
                // Only reduce health if this enemy hasn't hit the player before
                if (!enemy1.hasHitPlayer()) {
                    player.setHealth(player.getHealth() - 10);
                    enemy1.setHasHitPlayer(true); // Mark this enemy as having hit the player
                    System.out.println("💥 Player hit! Health: " + player.getHealth());
                    if (player.getHealth() <= 0) {
                        System.out.println("💀 Player died!");
                        // Handle player death (e.g., game over)
                    }
                }
            } else {
                // Reset the flag when the enemy is no longer colliding
                enemy1.setHasHitPlayer(false);
            }
        }

    }

    public void draw(Graphics g) {
        // static background (ocean)
        if (staticBg != null) {
            g.drawImage(staticBg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

        // scrolling parallax background (sand)
        if (parallaxBg != null) {
            int width = parallaxBg.getWidth(null);
            int height = parallaxBg.getHeight(null);
            int y = (BOARD_HEIGHT - height) / 4; // center vertically

            // Draw two tiles for seamless looping
            g.drawImage(parallaxBg, parallaxX, y, null);
            g.drawImage(parallaxBg, parallaxX + width - 4, y, null);
        }
        // Do about items in SpeedUp
        if (speedUp != null) {
            speedUp.draw(g, this);
        }

        // Draw player on top
        drawPlayer(g);
        drawShots(g);

        // Speed Icon
        drawPowerUpUI(g);
        drawEnemies(g);
    }

    public void drawEnemies(Graphics g) {
        for (Enemy1 enemy1 : enemy1List) {
            enemy1.draw(g, this);
        }
        for (Enemy2 enemy2 : enemy2List) {
            enemy2.draw(g, this);
        }
    }

    public void drawPlayer(Graphics g) {
        if (player != null) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

    }

    public void drawShots(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    //_____________________________________________KeyListener
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_0) {
                System.out.println("🔁 Switching to Scene2...");
                game.loadScene2();
            }

            // Player shots
            if (key == KeyEvent.VK_SPACE && shots.size() < 4 && player.shootingDelay()) {
                shots.add(new Shot(x ,y));
                player.setLastShotTime(System.currentTimeMillis());
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }

    // _____________________________________________
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

    private void drawPowerUpUI(Graphics g) {
        if (currentSpeedLevel > 0 && speedIcon != null) {
            int iconSize = 48;
            int padding = 10;
            int x = BOARD_WIDTH - iconSize - padding;
            int y = padding;
            g.drawImage(speedIcon, x, y, iconSize, iconSize, this);

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
