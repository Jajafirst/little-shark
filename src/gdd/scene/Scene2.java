
package gdd.scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import gdd.sprite.Enemy1;
import gdd.sprite.Enemy2;
import gdd.sprite.Boss;
import gdd.sprite.BossBullet;
import gdd.sprite.Player;
import gdd.powerup.PowerUp;
import gdd.Game;

import static gdd.Global.BOARD_HEIGHT;
import static gdd.Global.BOARD_WIDTH;
import static gdd.Global.DELAY;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scene2 extends JPanel {

    private Game game;
    private Player player;
    private Timer timer;

    // Background and Parallax
    private Image staticBg;
    private Image parallaxBg;
    private double parallaxX = 0;
    private int scrollPower = 1;

    // Power Icon
    private static int lastCollectedLevel = 0;
    private PowerUp powerUp;
    private Image powerIcon;
    private int currentPowerLevel = 0;
    private boolean firstSpawned = false;
    private long lastSpawnTime = 0;
    private static final long SPAWN_INTERVAL = 6_000;

    // Enemies
    private List<Enemy2> enemies = new ArrayList<>();
    private List<Enemy1> enemy1List = new ArrayList<>();

    // Boss
    private boolean bossSpawned = false;
    private List<BossBullet> bossBullets = new ArrayList<>();
    private Boss boss;

    private Random rand = new Random();

    public static void setCollectedLevel(int level) {
        lastCollectedLevel = level;
    }

    public static int getCollectedLevel() {
        return lastCollectedLevel;
    }

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
        timer = new Timer(DELAY, new GameCycle());
        timer.start();
    }

    private void gameInit() {
        // load parallax
        ImageIcon parallaxIcon = new ImageIcon("./src/assets/background/final-scene2.png");
        parallaxBg = parallaxIcon.getImage();

        // powerUp
        ImageIcon powerIconImg = new ImageIcon("./src/assets/sprites/shotSkill1.png");
        powerIcon = powerIconImg.getImage();

        player = new Player();
    }

    public void update() {

        parallaxX -= scrollPower;
        if (parallaxBg != null && parallaxX <= -parallaxBg.getWidth(null)) {
            parallaxX = 0;
        }

        long currentTime = System.currentTimeMillis();
        // Spawn first PowerUp LV1
        if (!firstSpawned) {
            powerUp = new PowerUp(1, BOARD_WIDTH, BOARD_HEIGHT);
            lastSpawnTime = currentTime;
            firstSpawned = true;
            System.out.println("🟢 powerUp LV1 created at Y=" + powerUp.getY());
        }

        // Spawn next PowerUp after delay
        if (powerUp == null && currentPowerLevel < 4 && (currentTime - lastSpawnTime >= SPAWN_INTERVAL)) {
            int nextLevel = currentPowerLevel + 1;
            powerUp = new PowerUp(nextLevel, BOARD_WIDTH, BOARD_HEIGHT);
            lastSpawnTime = currentTime;
            System.out.println("🟢 powerUp LV" + nextLevel + " created at Y=" + powerUp.getY());
        }
        // Update PowerUp
        if (powerUp != null) {
            powerUp.update();

            Rectangle skillBox = powerUp.getBounds();
            Rectangle playerBox = new Rectangle(player.getX(), player.getY(), 128, 128);

            // Collected
            if (skillBox.intersects(playerBox)) {
                currentPowerLevel = powerUp.getLevel();
                updatePowerIcon(currentPowerLevel);
                PowerUp.setCollectedLevel(currentPowerLevel);
                powerUp = null;
                lastSpawnTime = System.currentTimeMillis();
                System.out.println("🎯 Collected powerUp LV" + currentPowerLevel);
            }

            // Missed
            else if (powerUp.getX() + powerUp.getWidth() < 0) {
                System.out.println("🗑️ Missed powerUp LV" + powerUp.getLevel());
                powerUp = null;
                lastSpawnTime = System.currentTimeMillis();
            }
        }

        // Enemy 1 : shot the bullet
        int MAX_ENEMY1 = 1;
        if (enemy1List.size() < MAX_ENEMY1 && rand.nextInt(100) < 2) {
            enemy1List.add(new Enemy1(BOARD_WIDTH, BOARD_HEIGHT));
            System.out.println("🐙 Spawned Enemy1 (total: " + enemy1List.size() + ")");
        }
        Iterator<Enemy1> it1 = enemy1List.iterator();
        while (it1.hasNext()) {
            Enemy1 e1 = it1.next();
            e1.update();
            if (e1.getX() + e1.getWidth() < 0) {
                it1.remove();
            }
        }

        // Enemy 2
        int MAX_ENEMIES = 2;
        if (enemies.size() < MAX_ENEMIES && rand.nextInt(100) < 2) {
            enemies.add(new Enemy2(BOARD_WIDTH, BOARD_HEIGHT));
            System.out.println("🦈 Spawned Enemy2 (total: " + enemies.size() + ")");
        }
        Iterator<Enemy2> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy2 e = it.next();
            e.update();
            if (e.getX() + e.getWidth() < 0) {
                it.remove();
            }
        }

        // Boss
        if (!bossSpawned && currentPowerLevel >= 4) {
            boss = new Boss(BOARD_WIDTH - 100, 195);
            bossSpawned = true;
        }

        if (boss != null) {
            boss.setLaserActive(true); // keep laser logic
            boss.update();

            // 🔥 Boss bullet spawning
            // 🔥 Boss bullet spawning
            if (rand.nextInt(100) < 2) {
                int bulletX = boss.getBulletX();

                // ✅ Random Y position within screen bounds
                int minY = 50; // avoid top UI area
                int maxY = BOARD_HEIGHT - 50 - 32; // avoid bottom edge (32 = bullet height)
                int randomY = rand.nextInt(maxY - minY) + minY;

                bossBullets.add(new BossBullet(bulletX, randomY));
            }

            // 🔄 Update bullets
            // 🔄 Update bullets
            Iterator<BossBullet> bulletIterator = bossBullets.iterator();
            while (bulletIterator.hasNext()) {
                BossBullet bullet = bulletIterator.next();
                bullet.update();
                if (!bullet.isActive()) {
                    bulletIterator.remove();
                }
            }
        }

        player.update();

    }

    public void draw(Graphics g) {
        if (staticBg != null) {
            g.drawImage(staticBg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        }

        if (parallaxBg != null) {
            int width = parallaxBg.getWidth(null);
            int height = parallaxBg.getHeight(null);
            int y = (BOARD_HEIGHT - height) / 4;
            int drawX = (int) parallaxX;

            g.drawImage(parallaxBg, drawX, y, null);
            g.drawImage(parallaxBg, drawX + width - 4, y, null);
        }

        if (powerUp != null) {
            powerUp.draw(g, this);
        }

        drawPlayer(g);

        drawPowerUpUI(g);

        for (Enemy1 e1 : enemy1List) {
            e1.draw(g, this);
        }

        for (Enemy2 enemy : enemies) {
            enemy.draw(g, this);
        }

        for (BossBullet bullet : bossBullets) {
            bullet.draw(g, this);
        }
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
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g); // background, enemies, etc.

        if (boss != null) {
            boss.drawLaser(g);
            boss.draw(g, this);

            if (boss.isLaserActive()) {
                Rectangle laserBounds = boss.getLaserBounds();
                if (player.getBounds().intersects(laserBounds)) {
                    System.out.println("🔥 Player hit by laser!");
                    // player.setDead(true);
                }
            }
        }
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
        if (currentPowerLevel > 0 && powerIcon != null) {
            int iconSize = 48;
            int padding = 10;
            int x = BOARD_WIDTH - iconSize - padding;
            int y = padding;
            g.drawImage(powerIcon, x, y, iconSize, iconSize, this);
        }
    }

    private void updatePowerIcon(int level) {
        String path = switch (level) {
            case 1 -> "./src/assets/sprites/shotSkill1.png";
            case 2 -> "./src/assets/sprites/shotSkill2.png";
            case 3 -> "./src/assets/sprites/shotSkill3.png";
            case 4 -> "./src/assets/sprites/shotSkill4.png";
            default -> null;
        };

        if (path != null) {
            powerIcon = new ImageIcon(path).getImage();
        }
    }

    public void setScrollPower(int i) {
        throw new UnsupportedOperationException("Unimplemented method 'setScrollPower'");
    }
}