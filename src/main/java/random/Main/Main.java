package random.Main;

import java.io.IOException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Victorious Quest");

        try {
            BufferedImage icon = ImageIO.read(Main.class.getResource("/Images/Icons/PokeIcon.png"));
            window.setIconImage(icon); // Set Game Icon Image
        } catch (IOException e) {
            e.printStackTrace();
        }

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Start game loop
        gamePanel.startGame();
    }
}