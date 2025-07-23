// TODO
// - call loadScene1() later

package gdd;

import gdd.scene.TitleScene;
import gdd.scene.Scene1;
import gdd.scene.Scene2;

import javax.swing.JFrame;

public class Game extends JFrame {

    private TitleScene titleScene;
    private Scene1 scene1;
    private Scene2 scene2;

    public Game() {
        System.out.println("Game constructor called");
        initUI();

        titleScene = new TitleScene(this);
        scene1 = new Scene1(this);
        scene2 = new Scene2(this);

        loadTitle();
    }

    private void initUI() {
        setTitle("Shark Shooter");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }
    public void loadTitle() {
        getContentPane().removeAll();
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        getContentPane().removeAll();
        // titleScene.stop(); 
        add(scene1);
        scene1.start();
        revalidate();
        repaint();
    }
    
    public void loadScene2() {
        getContentPane().removeAll();
        // titleScene.stop(); 
        add(scene2);
        scene2.start();
        revalidate();
        repaint();
    }

}