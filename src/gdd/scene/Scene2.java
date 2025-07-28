// TODO 
// - fix Octopus Bullet disappear when it dies
// - fix Boss pic(don’t know the code, wait for First)
// - add boss health

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
import gdd.sprite.EnemyBullet;
import gdd.sprite.Boss;
import gdd.sprite.BossBullet;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import gdd.powerup.PowerUp;
import gdd.Game;

import static gdd.Global.BOARD_HEIGHT;
import static gdd.Global.BOARD_WIDTH;
import static gdd.Global.DELAY;
import static gdd.Global.PARALLAX_SCROLL_SPEED;

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
    private List<Enemy1> enemy1List = new ArrayList<>();
    private List<Enemy2> enemy2List = new ArrayList<>();
    private List<EnemyBullet> enemyBullets = new ArrayList<>();

    // Player shots
    private List<Shot> shots = new ArrayList<>();

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

    public void start(int health) {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        gameInit(health);
        System.out.println("✅ Scene2 started");

        timer = new Timer(DELAY, new GameCycle());
        timer.start();
    }

    private void gameInit(int health) {
        // load parallax
        ImageIcon parallaxIcon = new ImageIcon("./src/assets/background/final-scene2.png");
        parallaxBg = parallaxIcon.getImage();

        // powerUp
        ImageIcon powerIconImg = new ImageIcon("./src/assets/sprites/shotSkill1.png");
        powerIcon = powerIconImg.getImage();

        player = new Player();
        player.setHealth(health);
    }

    public void update() {
        // Scroll parallax background
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
            Rectangle playerBox = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

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
            Enemy1 e1 = new Enemy1(BOARD_WIDTH, BOARD_HEIGHT);
            e1.setShootListener(b -> enemyBullets.add(b)); // ✅ attach bullets to Scene2
            enemy1List.add(e1);
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

        Iterator<EnemyBullet> bulletIter = enemyBullets.iterator();
        while (bulletIter.hasNext()) {
            EnemyBullet bullet = bulletIter.next();
            bullet.update();
            if (bullet.getX() + bullet.getWidth() < 0) { // off-screen cleanup
                bulletIter.remove();
            }
        }

        // Enemy 2
        int MAX_ENEMIES = 2;
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
            if (enemy2.getBounds()
                    .intersects(new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight()))) {
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
            if (enemy1.getBounds()
                    .intersects(new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight()))) {
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
        drawShots(g);

        drawPowerUpUI(g);
        drawEnemies(g);

        for (BossBullet bullet : bossBullets) {
            bullet.draw(g, this);
        }

        for (EnemyBullet bullet : enemyBullets) {
            bullet.draw(g, this);
        }
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

    // _____________________________________________KeyListener
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            // Player shots
            if (key == KeyEvent.VK_SPACE && shots.size() < 4 && player.shootingDelay()) {
                shots.add(new Shot(x, y));
                player.setLastShotTime(System.currentTimeMillis());
            }

            // After game over
            if (key == KeyEvent.VK_R && !inGame) {
                System.out.println("🔄 Restarting Scene1...");
                game.restartGame();
            }

            if (key == KeyEvent.VK_Q && !inGame) {
                System.out.println("❌ Quitting game...");
                System.exit(0);
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
        draw(g); // background, enemies, etc.

        if (boss != null) {
            boss.drawLaser(g);
            boss.draw(g, this);

            if (boss.isLaserActive()) {
                Rectangle laserBounds = boss.getLaserBounds();
                if ((new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight()))
                        .intersects(laserBounds)) {
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

    //_____________________________________________
    private void gameOver(Graphics g) {
        // Create a semi-transparent dark overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Add a subtle animated background effect (stars or particles)
        drawAnimatedBackground(g);

        // Main game over box with gradient and border
        int boxWidth = BOARD_WIDTH - 100;
        int boxHeight = 200;
        int boxX = 50;
        int boxY = BOARD_HEIGHT / 2 - boxHeight / 2;

        // Gradient background
        GradientPaint gradient = new GradientPaint(
                boxX, boxY, new Color(20, 20, 40),
                boxX, boxY + boxHeight, new Color(0, 10, 20));
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(gradient);
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Glossy border effect
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(100, 150, 255, 100));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(new Color(200, 220, 255));
        g2d.drawRoundRect(boxX + 1, boxY + 1, boxWidth - 2, boxHeight - 2, 20, 20);

        // Title text with shadow
        Font titleFont = new Font("Impact", Font.BOLD, 48);
        String title = "GAME OVER";

        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();

        // Shadow
        g2d.setColor(new Color(200, 0, 0, 150));
        g2d.drawString(title,
                (BOARD_WIDTH - fm.stringWidth(title)) / 2 + 3,
                boxY + 60 + 3);

        // Main text
        GradientPaint textGradient = new GradientPaint(
                BOARD_WIDTH / 2 - fm.stringWidth(title) / 2, boxY + 50,
                new Color(255, 80, 80),
                BOARD_WIDTH / 2 + fm.stringWidth(title) / 2, boxY + 70,
                new Color(255, 180, 180),
                false);
        g2d.setPaint(textGradient);
        g2d.drawString(title,
                (BOARD_WIDTH - fm.stringWidth(title)) / 2,
                boxY + 60);

        // Score/status information
        Font infoFont = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(infoFont);
        fm = g2d.getFontMetrics();

        String scoreText = "Final Score: " + score;
        g2d.setColor(new Color(220, 220, 255));
        g2d.drawString(scoreText,
                (BOARD_WIDTH - fm.stringWidth(scoreText)) / 2,
                boxY + 100);

        // Message/instruction
        Font msgFont = new Font("Arial", Font.PLAIN, 16);
        g2d.setFont(msgFont);
        fm = g2d.getFontMetrics();

        String instruction = "Press R to restart or Q to quit";
        g2d.setColor(new Color(180, 180, 255));
        g2d.drawString(instruction,
                (BOARD_WIDTH - fm.stringWidth(instruction)) / 2,
                boxY + 140);

        // Decorative elements
        drawPulsingSkullIcon(g2d, BOARD_WIDTH / 2, boxY + 170);
    }

    private void drawAnimatedBackground(Graphics g) {
        // Draw twinkling stars
        Random rand = new Random();
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < 50; i++) {
            int x = rand.nextInt(BOARD_WIDTH);
            int y = rand.nextInt(BOARD_HEIGHT);
            int size = 1 + rand.nextInt(3);
            int alpha = 100 + rand.nextInt(155);

            g2d.setColor(new Color(255, 255, 255, alpha));
            g2d.fillOval(x, y, size, size);
        }
    }

    private void drawPulsingSkullIcon(Graphics2D g2d, int x, int y) {
        // Simple pulsing animation
        int pulseSize = (int) (5 * Math.abs(Math.sin(System.currentTimeMillis() * 0.005)));

        // Skull icon
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(x - 15 - pulseSize / 2, y - 15 - pulseSize / 2, 30 + pulseSize, 30 + pulseSize);

        // Eye sockets
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 10, y - 8, 6, 6);
        g2d.fillOval(x + 4, y - 8, 6, 6);

        // Mouth
        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc(x - 8, y, 16, 10, 0, -180);
    }

    // Add this to your Scene1 class
    private void drawPlayerHealth(Graphics g) {
        // Health box dimensions and position
        int boxWidth = 150;
        int boxHeight = 30;
        int boxX = 10; // 10px from left edge
        int boxY = 10; // 10px from top

        // Get current health (assuming player is accessible)
        int currentHealth = player.getHealth();
        int maxHealth = 100; // Or whatever your max health is

        // Health percentage for color and bar width
        float healthPercent = (float) currentHealth / maxHealth;

        // Health box background
        g.setColor(new Color(30, 30, 40, 200)); // Semi-transparent dark
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 5, 5);

        // Health bar (color changes based on health)
        Color healthColor;
        if (healthPercent > 0.6f) {
            healthColor = new Color(100, 220, 100); // Green
        } else if (healthPercent > 0.3f) {
            healthColor = new Color(220, 220, 100); // Yellow
        } else {
            healthColor = new Color(220, 100, 100); // Red
        }

        int barWidth = (int) ((boxWidth - 4) * healthPercent);
        g.setColor(healthColor);
        g.fillRoundRect(boxX + 2, boxY + 2, barWidth, boxHeight - 4, 3, 3);

        // Health box border
        g.setColor(new Color(200, 200, 200, 150));
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 5, 5);

        // Health text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));

        String healthText = currentHealth + "/" + maxHealth;
        FontMetrics fm = g.getFontMetrics();
        int textX = boxX + (boxWidth - fm.stringWidth(healthText)) / 2;
        int textY = boxY + (boxHeight + fm.getAscent() - fm.getDescent()) / 2;

        // Text shadow for better readability
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(healthText, textX + 1, textY + 1);

        g.setColor(Color.WHITE);
        g.drawString(healthText, textX, textY);
    }    
}