// TODO 
// - Frame isn't used yet, but will be useful for animations later
// - add sound background music

// ADD 
// - make title page more interactive, appealing.

package gdd.scene;

import gdd.Game;
import static gdd.Global.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.net.URL;

public class TitleScene extends JPanel {

    private Image titleImage;
    private Game game;

    private Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private int frame = 0; // Frame counter for animations, use later

    public TitleScene(Game game) {
        this.game = game;

        // setFocusable(true);
        // setPreferredSize(new Dimension(Global.BOARD_WIDTH, Global.BOARD_HEIGHT));
        // setBackground(Color.BLACK);

        // try {
        //     URL imageUrl = getClass().getResource("/assets/background/title.png");
        //     if (imageUrl == null)
        //         throw new IllegalArgumentException("❌ title.png not found.");
        //     titleImage = ImageIO.read(imageUrl);
        // } catch (IOException | IllegalArgumentException e) {
        //     System.err.println("❌ Error loading title image.");
        //     e.printStackTrace();
        // }
    }

    public void start() {
        // requestFocusInWindow();

        System.out.println("✅ TitleScene started");
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    System.out.println("🔁 Switching to Scene1...");
                    game.loadScene1();
                }
            }
        });
        setFocusable(true);
        setBackground(Color.BLACK);

        gameInit();
    }

    public void stop() {
    }

    public void update() {
    }

    private void gameInit() {
        ImageIcon titleIcon = new ImageIcon(IMG_TITLE);
        titleImage = titleIcon.getImage();
    }

    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        if (titleImage != null) {
            // x, y are kinda messed up, not using it rn
            int x = (BOARD_WIDTH - titleImage.getWidth(null)) / 2;
            int y = (BOARD_HEIGHT - titleImage.getHeight(null)) / 2;
            g.drawImage(titleImage, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("⚠️ Title image not found", 100, 100);
        }

        Toolkit.getDefaultToolkit().sync(); // Important, don't remove
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
}