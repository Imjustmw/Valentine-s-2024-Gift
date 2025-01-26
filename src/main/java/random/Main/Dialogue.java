package random.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.io.IOException;
import java.io.InputStream;


public class Dialogue extends JPanel {
    private JLabel dialogueLabel;
    private JTextArea dialogueTextArea;
    private Font arcade;
    private ImageIcon leftImageIcon;
    private ImageIcon rightImageIcon;
    private ImageIcon midImageIcon;
    private ImageIcon spaceImageIcon;

    private String[] dialogueTexts;
    private int currentTextIndex;

    private GamePanel gamePanel;

    private Timer tweenTimer;
    private int targetY;
    private int currentY;

    private int x, y;

    private long lastSub;
    private int sub;
    public boolean typing;

    public Dialogue(KeyHandler keyHandler, GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        // Load custom font
        try {
            InputStream fontStream = getClass().getResourceAsStream("/Fonts/8-bit2.ttf");
            arcade = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 26);
            
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        // Label
        x = (int) (gamePanel.screenWidth * 0.98);
        y = (int) (gamePanel.screenHeight * 0.2);

        setLayout(null);

        // Dialogue Label
        dialogueLabel = new JLabel();
        dialogueLabel.setBounds(0, 0, x, y);
        dialogueLabel.setOpaque(false);
        dialogueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dialogueLabel.setVerticalAlignment(SwingConstants.TOP);
        add(dialogueLabel);   
        
        // Text Area
        dialogueTextArea = new JTextArea();
        dialogueTextArea.setEditable(false);
        dialogueTextArea.setFocusable(false);
        dialogueTextArea.setOpaque(false);
        dialogueTextArea.setBounds(0, 0, x, y);
        add(dialogueTextArea);

        // Space Image
        spaceImageIcon = new ImageIcon(getClass().getResource("/Images/UI/Space.gif"));
        JLabel spaceImageLabel = new JLabel(spaceImageIcon);
        spaceImageLabel.setBounds(x - (int)(spaceImageIcon.getIconWidth()*1.1), y - (int)(spaceImageIcon.getIconHeight()/1.2), spaceImageIcon.getIconWidth(), spaceImageIcon.getIconHeight());
        add(spaceImageLabel);

        // Left Image
        leftImageIcon = new ImageIcon(getClass().getResource("/Images/UI/Dialogue_Left.png"));
        JLabel leftImageLabel = new JLabel(leftImageIcon);
        leftImageLabel.setBounds(0, 0, leftImageIcon.getIconWidth(), y);
        add(leftImageLabel);

        // Right Image
        rightImageIcon = new ImageIcon(getClass().getResource("/Images/UI/Dialogue_Right.png"));
        JLabel rightImageLabel = new JLabel(rightImageIcon);
        rightImageLabel.setBounds(x - rightImageIcon.getIconWidth(), 0, rightImageIcon.getIconWidth(), y);
        add(rightImageLabel);

        // Middle Image
        midImageIcon = new ImageIcon(getClass().getResource("/Images/UI/Dialogue_Mid.png"));
        Image midImage = midImageIcon.getImage().getScaledInstance(x - rightImageIcon.getIconWidth() - leftImageIcon.getIconWidth(), y, Image.SCALE_SMOOTH);
        midImageIcon = new ImageIcon(midImage);
        JLabel midImageLabel = new JLabel(midImageIcon);
        midImageLabel.setBounds(leftImageIcon.getIconWidth(), 0, x - rightImageIcon.getIconWidth() - leftImageIcon.getIconWidth(), y);
        add(midImageLabel);

        // Add margins to the JLabel
        int topMargin = 15; 
        int leftMargin = 15;
        int bottomMargin = 15;
        int rightMargin = 15; 
        dialogueTextArea.setBorder(new EmptyBorder(topMargin, leftMargin, bottomMargin, rightMargin));

        // Wrap text
        dialogueTextArea.setLineWrap(true);
        dialogueTextArea.setWrapStyleWord(true);

        if (arcade != null) { dialogueTextArea.setFont(arcade); } // Font

        setPreferredSize(new Dimension(x, y));
        this.setOpaque(false);
        setVisible(false);
        typing = false;

        tweenTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTween();
            }
        });
    }

    private void updateTween() {
        int speed = 5; // Adjust the speed of the tweening

        if (currentY < targetY) {
            currentY += speed;
            if (currentY >= targetY) {
                currentY = targetY;
                tweenTimer.stop();
            }
        } else if (currentY > targetY) {
            currentY -= speed;
            if (currentY <= targetY) {
                currentY = targetY;
                tweenTimer.stop();
            }
        }

        setLocation(getX(), currentY);
    }

    public void startDialogue(String[] dialogueTexts) {
        this.dialogueTexts = dialogueTexts;
        typing = true;
        currentTextIndex = 0;
        sub = 0;
        lastSub = System.nanoTime();

        setVisible(true);
        
        currentY = gamePanel.screenHeight;

        // Center the dialogue box relative to the game window
        Point dialogLocation = new Point(
                (gamePanel.screenWidth - x) / 2,
                currentY
        );
        setLocation(dialogLocation);

        // Set the target Y for the tweening
        targetY = (gamePanel.screenHeight - y - 5);

        // Start the tweening timer
        tweenTimer.start();
    }

    public void proceedDialogue() {
        currentTextIndex++;
        if (currentTextIndex < dialogueTexts.length) {
            typing = true;
            sub = 0;
            lastSub = System.nanoTime();
            dialogueLabel.setText("");
        } else {
            typing = false;
            endDialogue();
        }
    }

    private void showText() {
        // Typerwriter animation
        long now = System.nanoTime();

        if (currentTextIndex < dialogueTexts.length && sub < dialogueTexts[currentTextIndex].length()) {
            if ((now - lastSub) / 1e9 > 0.015) {
                sub++;
                dialogueTextArea.setText(dialogueTexts[currentTextIndex].substring(0, sub));
                lastSub = now;
            }
            if(sub == dialogueTexts[currentTextIndex].length()) {
                typing = false;
            }
        }

        // Center the dialogue box relative to the game window (It bugs out if I don't do this)
        if (currentY == targetY) {
            Point dialogLocation = new Point(
                (gamePanel.screenWidth - x) / 2,
                targetY
            );
            setLocation(dialogLocation);
        }
        
    }

    private void endDialogue() {
        // Set the target Y off-screen
        targetY = gamePanel.screenHeight;
        tweenTimer.start();

        gamePanel.player.lastInteraction = System.nanoTime();
        gamePanel.player.interacting = false;
        
    }

    public boolean isDialogueActive() {
        return isVisible();
    }

    public void update() {
        if (isDialogueActive()) {
            showText();
        } 
    }
}
