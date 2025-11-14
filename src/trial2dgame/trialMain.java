package trial2dgame;
import java.io.File;
import java.util.Scanner;

import javax.swing.JFrame;

//NEW CODE!!
public class trialMain {

	public static void main(String[] args) {

        // Create window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("2D Adventure");

        // Create game panel
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // If save exists, load automatically
        File saveFile = new File("save.dat");
        if (saveFile.exists()) {
            gamePanel.saveLoad.load();  // load saved game
        } else {
            gamePanel.setupGame(false); // start new game
        }

        // Start game loop	
        gamePanel.startGameThread();
		
	}
}