package gdd.scene;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public interface Scene {
    void update();
    void draw(Graphics2D g);
    void keyPressed(KeyEvent e);
    void keyReleased(KeyEvent e);
}