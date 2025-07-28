package gdd.sprite;

import static gdd.Global.*;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 100;
    private static final int V_SPACE = -10;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {
        var icon = new ImageIcon(IMG_SHOT);
        if (icon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
            System.err.println("Error: Shot image not loaded properly");
        }
        setImage(icon.getImage());

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void act() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }
}
