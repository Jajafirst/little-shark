package gdd;

import gdd.scene.Scene1;
import gdd.scene.TitleScene;

import javax.swing.JFrame;

public class Game extends JFrame {

    private TitleScene titleScene;
    private Scene1 scene1;

    public Game() {
        System.out.println("Game constructor called");
        initUI();

        titleScene = new TitleScene(this);
        scene1 = new Scene1();

        // add(titleScene);
        // titleScene.start();

        loadTitle();
    }

    private void initUI() {
        setTitle("Shark Shooter");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void loadScene1() {
        getContentPane().removeAll();
        // titleScene.stop(); 
        add(scene1);
        scene1.start();
        revalidate();
        repaint();
    }

    public void loadTitle() {
        getContentPane().removeAll();
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }
}