// package gdd.sprite;

// import gdd.Global;
// import java.awt.*;
// import java.io.File;
// import java.io.IOException;
// import javax.imageio.ImageIO;

// public class Explosion {
//     private int x, y;
//     private Image image;
//     private boolean visible = true;
//     private int life = 20;

//     public Explosion(int x, int y) {
//         this.x = x;
//         this.y = y;
//         try {
//             image = ImageIO.read(new File(Global.IMG_EXPLOSION));
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public void update() {
//         life--;
//         if (life <= 0) visible = false;
//     }

//     public boolean isVisible() {
//         return visible;
//     }

//     public void draw(Graphics g) {
//         if (visible && image != null) {
//             g.drawImage(image, x, y, 64, 64, null);
//         }
//     }
// }