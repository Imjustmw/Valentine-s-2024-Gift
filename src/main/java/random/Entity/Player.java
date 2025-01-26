package random.Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import random.Main.GamePanel;
import random.Main.KeyHandler;
import random.Tile.Tile;


public class Player extends Entity{
    // Main classes
    KeyHandler keyHandler;
    GamePanel gamePanel;

    // Player attributes
    public int screenX, screenY;
    private boolean isMoving = false;
    private boolean isTurning = false;
    public long lastInteraction = System.nanoTime();

    // Player animations
    private int spriteWidth = 64;
    private int spriteHeight = 64;
    private BufferedImage[] animationSheet = new BufferedImage[1];   
    private int currentFrame = 0; 
    private long lastTurnTime = System.nanoTime();
    private long lastAnimTime = System.nanoTime();
    private double animationSpeed = 0.15;

    public Player(KeyHandler keyHandler, GamePanel gamePanel) {
        this.keyHandler = keyHandler;
        this.gamePanel = gamePanel;

        worldX = gamePanel.tileSize;
        worldY = 3*gamePanel.tileSize;
        int hbSize = gamePanel.tileSize/3;
        hitbox = new Rectangle((gamePanel.tileSize - hbSize)/2, (gamePanel.tileSize - hbSize)/2, hbSize, hbSize);
       
        setDefaultValues(gamePanel.originalTileSize * gamePanel.scale);
        getPlayerImage();
    }

    public void setDefaultValues(int i) {
        double multiplier = (double) i/gamePanel.tileSize;
        gamePanel.tileSize = i;

        // Screen values
        worldX *= multiplier;
        worldY *= multiplier;
        nextX = worldX;
        nextY = worldY;
        screenX = gamePanel.screenWidth/2 - (gamePanel.tileSize/2);
        screenY = gamePanel.screenHeight/2 - (gamePanel.tileSize/2);

        // Physics values
        speed = (double) gamePanel.worldWidth/gamePanel.defaultSpeedScale;
        int hbSize = gamePanel.tileSize/3;
        hitbox.width = hbSize;
        hitbox.height = hbSize;
        hitbox.x = (gamePanel.tileSize - hbSize)/2;
        hitbox.y = (gamePanel.tileSize - hbSize)/2;
    }

    public void getPlayerImage() {
        try {
            animationSheet[0] = ImageIO.read(getClass().getResourceAsStream("/Images/People/VictoriaV2.png")); // WalkSheet
            // Add more sprite sheets here for Player
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // Update the player's position
        move();
        animate();
    }

    public void render(Graphics2D g2) {
        // Render the player
        g2.drawImage(animationSheet[0].getSubimage(currentFrame * spriteWidth, directionIndex * spriteHeight, spriteWidth, spriteHeight), screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
    }

    private void animate() {
        // Calculate the frame index based on the animation speed
        long now = System.nanoTime();
        double deltaTime = (now - lastAnimTime) / 1e9;

        if (deltaTime >= animationSpeed) {
           lastAnimTime = now;

           if (isMoving) {
            currentFrame = (currentFrame + 1) % (animationSheet[0].getWidth()/spriteWidth);
           } else {
            currentFrame = 0;
           }
        }
       
    }
    
    private void move() {
        if (!isMoving && !interacting) { // Player input to move
            // Reset move direction
            direction[0] = 0;
            direction[1] = 0;

            // Update move direction
            if (keyHandler.isKeyPressed(KeyEvent.VK_W) || keyHandler.isKeyPressed(KeyEvent.VK_UP)) {
                if (directionIndex == 3) {
                    direction[1] = -1;
                } else {
                    lastTurnTime = System.nanoTime();
                    isTurning = true;
                }
                directionIndex = 3; // Direction of animations
            } else if (keyHandler.isKeyPressed(KeyEvent.VK_S) || keyHandler.isKeyPressed(KeyEvent.VK_DOWN)) {
                if (directionIndex == 0) {
                    direction[1] = 1;
                } else {
                    lastTurnTime = System.nanoTime();
                    isTurning = true;
                }
                directionIndex = 0;
            } else if (keyHandler.isKeyPressed(KeyEvent.VK_A) || keyHandler.isKeyPressed(KeyEvent.VK_LEFT)) {
                if (directionIndex == 1) {
                    direction[0] = -1;
                } else {
                    lastTurnTime = System.nanoTime();
                    isTurning = true;
                }
                directionIndex = 1;
            } else if (keyHandler.isKeyPressed(KeyEvent.VK_D) || keyHandler.isKeyPressed(KeyEvent.VK_RIGHT)) {
                if (directionIndex == 2) {
                    direction[0] = 1;
                } else {
                    lastTurnTime = System.nanoTime();
                    isTurning = true;
                }
                directionIndex = 2;
            }

            // Check collision
            collisionOn = false;
            gamePanel.collisionChecker.checkCollision(this);

            // Update player position
            if (!(direction[0] == 0 && direction[1] == 0) && !isTurning && !collisionOn) {
                isMoving = true;
                int tileSize = gamePanel.tileSize;
                nextX = (worldX + tileSize*direction[0]);
                nextY = (worldY + tileSize*direction[1]);

            }

        } else { // Move player to tile
            double distX = Math.abs(nextX - worldX);
            double distY = Math.abs(nextY - worldY);

            double clampedSpeedX = Math.max(0,Math.min(speed, distX));
            double clampedSpeedY = Math.max(0,Math.min(speed, distY));

            worldX += direction[0] * clampedSpeedX;
            worldY += direction[1] * clampedSpeedY;
            
            if (distX == 0 && distY == 0) {
                isMoving = false;
            }
        }

        if (isTurning) { // debounce flag for turning
            if ((System.nanoTime() - lastTurnTime) / 1e9 > 0.1) {
                isTurning = false;
            } 
        }

    }

    // Check for Objects to interact with:
    
    public NPC isNPC() {
        int xMul = 0;
        int yMul = 0;

        switch(directionIndex) {
            case 0: // Down
                yMul = 1;
                break;
            case 1: // Left
                xMul = -1;
                break;
            case 2: // Right
                xMul = 1;
                break;
            case 3: // Up
                yMul = -1;
        }
        
        for (NPC npc : gamePanel.npcs) {
            double diffX = Math.abs(worldX + (xMul * gamePanel.tileSize) - npc.worldX);
            double diffY = Math.abs(worldY + (yMul * gamePanel.tileSize) - npc.worldY);
            if (diffX < gamePanel.tileSize/2 && diffY < gamePanel.tileSize/2)
                return npc;
        }
        return null;
    }

    public Tile isObject() {
        if (isMoving)
            return null;

        int xMul = 0;
        int yMul = 0;

        switch(directionIndex) {
            case 0: // Down
                yMul = 1;
                break;
            case 1: // Left
                xMul = -1;
                break;
            case 2: // Right
                xMul = 1;
                break;
            case 3: // Up
                yMul = -1;
        }
        
        int tileX = (int) (worldX + (gamePanel.tileSize * xMul)) / gamePanel.tileSize;
        int tileY = (int) (worldY + (gamePanel.tileSize * yMul)) / gamePanel.tileSize;
        if (tileX >= gamePanel.maxWorldHeight || tileX < 0 || tileY >= gamePanel.maxWorldWidth || tileY < 0) {
            return null;
        }

        int tileNo = gamePanel.tileManager.overlayTileNo[tileY][tileX];
        if (gamePanel.tileManager.tile[tileNo].direction != -1 && gamePanel.tileManager.tile[tileNo].direction != directionIndex)
            return null;
        if (gamePanel.tileManager.tile[tileNo].interactive)
            return gamePanel.tileManager.tile[tileNo];
        return null;
    }

}
