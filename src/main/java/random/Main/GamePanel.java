package random.Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;

import javax.swing.JPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import random.Entity.Entity;
import random.Entity.Interaction;
import random.Entity.Player;
import random.Entity.NPC;
import random.Tile.TileManager;


public class GamePanel extends JPanel implements Runnable{
    // SCREEN SETTINGS
    public final int originalTileSize = 16; // 16x16 pixel tile
    public final int scale = 4;
    public int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenWidth = 14; // Max Width in Tiles
    public final int maxScreenHeight = 10; // Max Height in Tiles
    public final int screenWidth = tileSize * maxScreenWidth; // Width in pixels
    public final int screenHeight = tileSize * maxScreenHeight; // Height in pixels
    

    // WORLD SETTINGS
    public int maxWorldWidth = 8; // Max Width of world map in Tiles
    public int maxWorldHeight = 7; // Max Height of world map in Tiles
    public int worldWidth = tileSize * maxWorldWidth;
    public int worldHeight = tileSize * maxWorldHeight;
    public int defaultSpeedScale = worldWidth/4; // Entity speed scaled with screen (speed = 4)

    // GAME LOOP
    private boolean isRunning = false;
    private int FPS = 60;
    private Thread gameThread;

    // ALL ENTITIES
    public Player player;
    public ArrayList<NPC> npcs;
    public ArrayList<Entity> entities;
    private KeyHandler keyHandler;
    public TileManager tileManager;
    public CollisionChecker collisionChecker;
    public Interaction interaction;
    public Dialogue dialogue;
    private MediaPlayer mediaPlayer;

    // Set up GamePanel
     public GamePanel() {
        // Set Window Preference
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        // Initialize JavaFX
        new JFXPanel();

        // Background music
        String audioFile = "/Sounds/BGMusic.mp3"; // Path relative to the classpath
        Media media = new Media(getClass().getResource(audioFile).toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
        mediaPlayer.setVolume(0.05);
        
        
        // Initialize other game-related components: Load Sprites, initialize game state etc.
        tileManager = new TileManager(this);
        collisionChecker = new CollisionChecker(this);

        keyHandler = new KeyHandler(this);
        this.addKeyListener(keyHandler);

        dialogue = new Dialogue(keyHandler,this);
        add(dialogue);

        entities = new ArrayList<>();
        player = new Player(keyHandler, this);
        player.setDefaultValues(originalTileSize * scale * 2);
        entities.add(player);

        interaction = new Interaction(this);

        npcs = new ArrayList<>();
    }

    // GAME LOOP STUFF
    @Override
    public void run() {
      double secondsPerRender = 1.0/FPS;
      double nextRenderTime = System.nanoTime() + secondsPerRender * 1e9;

      while (gameThread != null && isRunning) {

        update(); // Update Positions
        repaint(); // Draw Screen
        
        try {
            double remainingTime = nextRenderTime - System.nanoTime();
            remainingTime = remainingTime / 1e6; // Convert to milliseconds
            if (remainingTime < 0) {remainingTime = 0;}

            Thread.sleep((long) remainingTime);
            nextRenderTime += secondsPerRender * 1e9;
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      }
    }

    public void startGame() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame() {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        player.update();

        for (NPC npc : npcs) {
            npc.update();
        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        tileManager.draw(g2);

        for (NPC npc : npcs) {
            npc.render(g2);
        }

        player.render(g2);
        

        if (dialogue.isDialogueActive()) {
            dialogue.update();
        }
    }

}
