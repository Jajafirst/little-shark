package gdd.scene;

import gdd.Game;
import gdd.Global;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class TitleScene extends JPanel implements Scene {

    private BufferedImage titleImage;
    private Game game;

    public TitleScene(Game game) {
        this.game = game;

        setFocusable(true);
        setPreferredSize(new Dimension(Global.BOARD_WIDTH, Global.BOARD_HEIGHT));
        setBackground(Color.BLACK);

        try {
            URL imageUrl = getClass().getResource("/assets/background/title.png");
            if (imageUrl == null) throw new IllegalArgumentException("❌ title.png not found.");
            titleImage = ImageIO.read(imageUrl);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("❌ Error loading title image.");
            e.printStackTrace();
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    System.out.println("🔁 Switching to Scene1...");
                    game.loadScene1();
                }
            }
        });
    }

    @Override
    public void start() {
        System.out.println("✅ TitleScene started");
        setVisible(true);
        requestFocusInWindow();
    }

    @Override
    public void stop() {
        setVisible(false);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Graphics2D g) {
        if (titleImage != null) {
            int imgW = titleImage.getWidth();
            int imgH = titleImage.getHeight();
            int x = (Global.BOARD_WIDTH - imgW) / 2;
            int y = (Global.BOARD_HEIGHT - imgH) / 2;

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(titleImage, x, y, null);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("⚠️ Title image not found.", 100, 100);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }
}