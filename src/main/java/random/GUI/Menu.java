package random.GUI;

import java.awt.*;
import javax.swing.*;
import random.Main.GamePanel;

public class Menu extends JPanel {
    private GamePanel gamePanel;

    public Menu(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        int x = (int) (this.gamePanel.screenWidth * 0.3);
        int y = (int) (this.gamePanel.screenHeight* 0.7);

        setLayout(null);
        setBackground(Color.white);
        setPreferredSize(new Dimension(x, y));
        setLocation((int) (this.gamePanel.screenHeight * 0.7), (int) (this.gamePanel.screenHeight * 0.3 / 2));
        setVisible(false);
    }

    public void toggleVisibility() {
        setVisible(!this.isVisible());
    }
}
