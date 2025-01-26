package random.Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import random.Entity.NPC;
import random.Tile.Tile;

public class KeyHandler implements KeyListener {
    private GamePanel gamePanel;
    
    private boolean[] keyPressed = new boolean[256];

    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < keyPressed.length){
            return keyPressed[keyCode];
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() >= 0 && e.getKeyCode() < keyPressed.length) {
            keyPressed[e.getKeyCode()] = true;

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                interact();
            }
        }
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() >= 0 && e.getKeyCode() < keyPressed.length) {
            keyPressed[e.getKeyCode()] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    private void interact() {
        if (gamePanel.player.interacting) {
            // Continue Dialogue
            gamePanel.interaction.continueInteraction();
        } else {
            // Initiate Dialogue:
            NPC npc = gamePanel.player.isNPC();
            if (npc != null) { // NPC
                gamePanel.interaction.interactNPC(npc);
                return;
            }
            Tile object = gamePanel.player.isObject();
            if (object != null) { // Object
                gamePanel.interaction.interactObject(object);
                return;
            }
        }
    }

}
