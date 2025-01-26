package random.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import random.Main.GamePanel;

public class Transition {
    private GamePanel gamePanel;

    public Transition(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

     public void fadeIn(JPanel blackPanel, int transitionTimeInSeconds, Runnable onFadeComplete) {
        int steps = 255 * transitionTimeInSeconds / 1000; // Number of steps based on transition time
        int stepDelay = transitionTimeInSeconds * 1000 / steps; // Delay between each step

        Timer fadeTimer = new Timer(stepDelay, null);
        fadeTimer.addActionListener(new ActionListener() {
            private int alpha = 0; // Initial opacity

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 255 / steps; // Increase opacity gradually
                if (alpha >= 255) {
                    // Stop the timer
                    fadeTimer.stop();
                    onFadeComplete.run(); // Call the callback function
                    return;
                }
                blackPanel.setBackground(new Color(0, 0, 0, alpha));
                blackPanel.repaint();
            }
        });
        fadeTimer.start();
    }

    public void fadeOut(JPanel blackPanel, int transitionTimeInSeconds, Runnable onFadeComplete) {
        int steps = 255 * transitionTimeInSeconds / 1000; // Number of steps based on transition time
        int stepDelay = transitionTimeInSeconds * 1000 / steps; // Delay between each step

        Timer fadeTimer = new Timer(stepDelay, null);
        fadeTimer.addActionListener(new ActionListener() {
            private int alpha = 255; // Initial opacity

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 255 / steps; // Decrease opacity gradually
                if (alpha <= 0) {
                    // Stop the timer and remove the black panel
                    fadeTimer.stop();
                    gamePanel.remove(blackPanel);
                    gamePanel.repaint(); // Repaint the panel to remove the black overlay
                    onFadeComplete.run(); // Call the callback function
                    return;
                }
                blackPanel.setBackground(new Color(0, 0, 0, alpha));
                blackPanel.repaint();
            }
        });
        fadeTimer.start();
    }
    
}
