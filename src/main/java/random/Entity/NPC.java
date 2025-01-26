package random.Entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import random.Main.GamePanel;

public class NPC extends Entity {
    GamePanel gamePanel;
    public String name;

    // NPC animations
    private int spriteWidth = 64;
    private int spriteHeight = 64;
    private int currentFrame;
    private BufferedImage[] animationSheet = new BufferedImage[1];
    private long lastAnimTime = System.nanoTime(); 
    private double animationSpeed = 0.15;

    // Walking
    public boolean isMoving = false;
    private double pathX, pathY;
    private double[][] path;
    private boolean loopPath = false;
    private int pathNo;
    private Entity interactWith;

    public NPC(GamePanel gamePanel, String NPC) {
        this.gamePanel = gamePanel;
        this.name = NPC;
        
        currentFrame = 0;
        directionIndex = 0;
        path = new double[1][2];
        path[0][0] = path[0][1] = 0;

        worldX = 0;
        worldY = 0;
        int hbSize = gamePanel.tileSize/3;
        hitbox = new Rectangle((gamePanel.tileSize - hbSize)/2, 0, hbSize, hbSize);
       
        setDefaultValues(gamePanel.originalTileSize * gamePanel.scale);
        getNPCImage(NPC);
    }

    public void setDefaultValues(int i) {
        double multiplier = (double) i/gamePanel.tileSize;
        gamePanel.tileSize = i;

        worldX *= multiplier;
        worldY *= multiplier;
        nextX = worldX;
        nextY = worldY;

        // Physics values
        speed = (double) gamePanel.worldWidth/gamePanel.defaultSpeedScale;
        int hbSize = gamePanel.tileSize/3;
        hitbox.width = hbSize;
        hitbox.height = hbSize;
        hitbox.x = (gamePanel.tileSize - hbSize)/2;
        hitbox.y = 0;

    }

    public void getNPCImage(String NPC) {
        try {
            animationSheet[0] = ImageIO.read(getClass().getResourceAsStream("/Images/People/" + NPC + ".png")); // WalkSheet
            // Add more sprite sheets here for NPC
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // Update the NPC's position
        move();
        animate();
    }

    public void render(Graphics2D g2) {
        // Render the NPC
       
        double screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
        double screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;
                
        if (worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX &&
            worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX &&
            worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY &&
            worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY) {
                Image image = animationSheet[0].getSubimage(currentFrame * spriteWidth, directionIndex * spriteHeight, spriteWidth, spriteHeight);
                g2.drawImage(image, (int) screenX,(int) screenY, gamePanel.tileSize, gamePanel.tileSize, null);
            }
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

    public void lookAt(Entity entity) {
        if (Math.abs(entity.worldX-worldX) < gamePanel.tileSize) { // Character is either up or down
            directionIndex = (entity.worldY < worldY)?3:0;
        } else if (Math.abs(entity.worldY-worldY) < gamePanel.tileSize) {
            directionIndex = (entity.worldX < worldX)?1:2;
        }
    }

    public void setDirection() {
        if (direction[1] == -1) { // Up
            directionIndex = 3;
        } else if (direction[1] == 1) { // Down
            directionIndex = 0;
        } else if (direction[0] == -1) { // Left
            directionIndex = 1;
        } else if (direction[0] == 1) { // Right
            directionIndex = 2;
        }
    }

    public void newPath(double[][] p, boolean loop) {
        this.path = p;
        this.loopPath = loop;
        pathNo = 0;
        isMoving = false;
    }

    public void move() {
        if (interacting) { 
            if (worldX != nextX || worldY != nextY) {
                double distX = Math.abs(nextX - worldX);
                double distY = Math.abs(nextY - worldY);
    
                double clampedSpeedX = Math.max(0,Math.min(speed, distX));
                double clampedSpeedY = Math.max(0,Math.min(speed, distY));
    
                worldX += direction[0] * clampedSpeedX;
                worldY += direction[1] * clampedSpeedY;
    
                // Next path or restart
                if (worldX == pathX && worldY == pathY) {
                    pathNo++;
                    if (pathNo >= path.length) {pathNo = 0;}
                }
    
                if (Math.abs(worldX - nextX) == 0 && Math.abs(worldY - nextY) == 0) {
                    isMoving = false;
                }
            } else {
                lookAt(interactWith);
            }
            return;
        }

        if (pathNo >= path.length && !loopPath)
            return;

        if (!isMoving) {
            if (!(pathX == path[pathNo][0] && pathY == path[pathNo][1])) {
                // Get Next Position of Path
                pathX = path[pathNo][0];
                pathY = path[pathNo][1];

                direction[0] = 0;
                direction[1] = 0;
            }

            // Face Direction
            if (pathX == worldX && !(pathY == worldY)) { // Next Position is either up or down
                directionIndex = (pathY < worldY)?3:0;
                direction[1] = (pathY < worldY)?-1:1;
            } else if (pathY == worldY && !(pathX == worldX)) {
                directionIndex = (pathX < worldX)?1:2;
                direction[0] = (pathX < worldX)?-1:1;
            }
            
            // Check collision
            collisionOn = false;
            gamePanel.collisionChecker.checkCollision(this);

            // Update NPC position by tile
            if (!(direction[0] == 0 && direction[1] == 0) && !collisionOn) { // *Add collision checker here*
                isMoving = true;
                int tileSize = gamePanel.tileSize;
                nextX = (worldX + tileSize*direction[0]);
                nextY = (worldY + tileSize*direction[1]);
            } else {
                direction[0] = 0;
                direction[1] = 0;
                nextX = worldX;
                nextY = worldY;
                pathNo = path.length;
            }

        } else if (worldX != pathX || worldY != pathY) {
            double distX = Math.abs(nextX - worldX);
            double distY = Math.abs(nextY - worldY);

            double clampedSpeedX = Math.max(0,Math.min(speed, distX));
            double clampedSpeedY = Math.max(0,Math.min(speed, distY));

            worldX += direction[0] * clampedSpeedX;
            worldY += direction[1] * clampedSpeedY;

            // Next path or restart
            if (worldX == pathX && worldY == pathY) {
                pathNo++;
                if (pathNo >= path.length && this.loopPath) {pathNo = 0;}
            }

            if (Math.abs(worldX - nextX) == 0 && Math.abs(worldY - nextY) == 0) {
                isMoving = false;
            }
        }
    }

    public void startInteraction(Entity entity) {
        interactWith = entity;
        interacting = true;
    }

    public void stopInteraction() {
        interacting = false;
    }

}
