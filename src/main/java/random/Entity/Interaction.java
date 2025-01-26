package random.Entity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import random.Main.GamePanel;
import random.Main.Main;
import random.Tile.Tile;

public class Interaction {
    private Player player;
    private GamePanel gamePanel;
    private Map<String, String[]> objectDialogue;
    private MediaPlayer mediaPlayer;

    private boolean tookChocolate = false;
    private boolean tv = false;

    public Interaction(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.player = gamePanel.player;
        this.objectDialogue = new HashMap<>();
        initializeObjectDialogue();

        // SFX
        String audioFile = "/Sounds/Interact.mp3"; // Path relative to the classpath
        Media media = new Media(getClass().getResource(audioFile).toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.2);
    }

    // Play interact sound

    public void playSFX() {
        mediaPlayer.stop();
        mediaPlayer.play();
    }

    // List of object Interactions:
    public void interactNPC(NPC npc) {
        player.interacting = true;
        playSFX();

        try {
            Method method = getClass().getMethod(npc.name);
            if (method != null)
                method.invoke(this);
        } catch (NoSuchMethodException e) {
           
        } catch (Exception e) {
            
        }

        String[] dialogue = objectDialogue.get(npc.name);
        if (dialogue != null){
            player.interacting = true;
            gamePanel.dialogue.startDialogue(dialogue);
        }
    }

    public void interactObject(Tile object) {
        try {
            Method method = getClass().getMethod(object.interactionCode);
            if (method != null)
                method.invoke(this);
        } catch (NoSuchMethodException e) {
           
        } catch (Exception e) {
            
        }

        String[] dialogue = objectDialogue.get(object.interactionCode);
        if (dialogue != null){
            playSFX();
            player.interacting = true;
            gamePanel.dialogue.startDialogue(dialogue);
        }
    }

    public void continueInteraction() {
        if (!gamePanel.dialogue.typing) {
            playSFX();
            gamePanel.dialogue.proceedDialogue();
        }
    }
    
    public void Upstairs() {
        if (gamePanel.tileManager.overlayTileNo[7][1] == 20) { // Note has not been taken.
            String[] dialogue = objectDialogue.get("Upstairs");
            if (dialogue != null)
                objectDialogue.remove("Upstairs");
            
            // Walk downstairs.
            int width = 12;
            int height = 15;
            player.worldX = gamePanel.tileSize*2;
            player.worldY = gamePanel.tileSize*3;
            player.nextX = player.worldX;
            player.nextY = player.worldY;

            gamePanel.tileManager.resizeArrays(width, height);
            
            gamePanel.tileManager.loadMap(gamePanel.tileManager.mapTileNo, "Downstairs.txt", width, height);
            gamePanel.tileManager.loadMap(gamePanel.tileManager.overlayTileNo, "Downstairs_Overlay.txt", width, height);

            if (tv){
                gamePanel.tileManager.overlayTileNo[9][5] = 40;
                // Add Jonathan:
                NPC Jonathan = new NPC(gamePanel, "Jonathan");
                Jonathan.worldX = gamePanel.tileSize*13;
                Jonathan.worldY = gamePanel.tileSize*4;
                Jonathan.directionIndex = 1;
                Jonathan.setDefaultValues(gamePanel.originalTileSize * gamePanel.scale * 2);
                double path[][] = {{gamePanel.tileSize*13,gamePanel.tileSize*4}};
                Jonathan.newPath(path,false);
                Jonathan.startInteraction(gamePanel.player);
                gamePanel.npcs.add(Jonathan);
                gamePanel.entities.add(Jonathan);
            }
                
        }
    }

    public void Downstairs() {
        // Walk Upstairs
        player.worldX = gamePanel.tileSize*5;
        player.worldY = gamePanel.tileSize*6;
        player.nextX = player.worldX;
        player.nextY = player.worldY;

        gamePanel.tileManager.resizeArrays(8, 7);
            
        gamePanel.tileManager.loadMap(gamePanel.tileManager.mapTileNo, "Upstairs.txt", 8, 7);
        gamePanel.tileManager.loadMap(gamePanel.tileManager.overlayTileNo, "Upstairs_Overlay.txt", 8, 7);
        gamePanel.tileManager.overlayTileNo[7][1] = 20;

        // Remove Jonathan
        for (NPC npc : gamePanel.npcs) {
            if (npc.name.equals("Jonathan")) {
                gamePanel.npcs.remove(npc);
                gamePanel.entities.remove(npc);
                break;
            }
        }
    }

    public void Note() {
        gamePanel.tileManager.overlayTileNo[7][1] = 20;
    }

    public void Jonathan() {
        for (NPC npc : gamePanel.npcs) {
            if (npc.name.equals("Jonathan")) {
                npc.startInteraction(gamePanel.player);
                break;
            }
        }
    }

    public void Television() {
        if (this.tookChocolate) {
            this.tv = true;
            String[] dialogue = objectDialogue.get("Television");
            if (dialogue != null)
                objectDialogue.remove("Television");
            // Add Jonathan:
            gamePanel.player.interacting = true;
            gamePanel.player.directionIndex = 1;

            NPC Jonathan = new NPC(gamePanel, "Jonathan");
            Jonathan.worldX = gamePanel.tileSize*8;
            Jonathan.worldY = gamePanel.tileSize*3;
            Jonathan.directionIndex = 0;
            Jonathan.setDefaultValues(gamePanel.originalTileSize * gamePanel.scale * 2);
            double path[][] = {
                {gamePanel.tileSize*10,gamePanel.tileSize*3},
                {gamePanel.tileSize*10,gamePanel.tileSize*4},
                {gamePanel.tileSize*11,gamePanel.tileSize*4},
                {gamePanel.tileSize*11,gamePanel.tileSize*3},
                {gamePanel.tileSize*12,gamePanel.tileSize*3},
            };
            Jonathan.newPath(path,false);
            gamePanel.npcs.add(Jonathan);
            gamePanel.entities.add(Jonathan);
        }
    }

    public void Chocolates() {
        this.tookChocolate = true;
        gamePanel.tileManager.overlayTileNo[9][5] = 40;
    }

    public void initializeObjectDialogue() {
        objectDialogue.put("Panda", new String[]{"It's a panda...", "So cute!", "...", "There's a note!", "'To: Pookie'\n'From: Boyfriend'\n'I love you BEARY much! LMAO NAH I AINT DOING THAT AGAIN ASKDASDK-'"});
        objectDialogue.put("Desktop", new String[]{"There doesn't seem to be anything entertaining..."});
        objectDialogue.put("BookShelf", new String[]{"I've read everything here already.", "I need to update my book collection."});
        objectDialogue.put("Bed", new String[]{"I'm not really tired right now...", "Maybe I should go see what boyfriend is doing."});
        objectDialogue.put("Upstairs", new String[]{"Hmmm... there seems to be a note on the dresser. Maybe I should check it out."});
        objectDialogue.put("Note", new String[]{"It's a note from boyfriend!", "'Hey Pookie,\nI left you a little something downstairs in the kitchen to show my appreciation.\nI really hope you like it. I worked really hard on it.'\n\n-Boyfriend."});
        objectDialogue.put("Makeup", new String[]{"My Makeup!!! My most prized possession.", "Should I beat my face today?...", "Nah I'm EFFORTLESSLY SEXY."});
        objectDialogue.put("Chocolates", new String[]{"Flowers and Chocolates!!!!!\nMY FAVOURITE!","There's another note....","'To: Pookie'\n'From: Boyfriend'\n'For being the best person in the entire universe.'\n'Love, Boyfriend.'","'PS. There's one last present at the television.'"});
        objectDialogue.put("Television", new String[]{"There doesn't seem to be anything entertaining..."});
        objectDialogue.put("Jonathan", new String[]{
            "Hi Princess.\nI hope you liked this so far...\nI GENUINELY worked so hard and long on this one >.<", 
            "There is so much more I wanted to do and this final part was supposed to be a big thing but I just didnt get the time to do everything and I'm really sorry for that because I put sooooo much time and effort into this and you deserve better.",
            "I know we've been having a lot of disagreements lately and I'm really sorry for that, my communication skills are really poor. I am working on that one for you!",
            "I don't want a silly fight to break us up\nI don't want you to feel like I'm too comfortable or complacent in the relationship so I stopped trying. That simply isn't true at all.", 
            "I know it feels like I'm not contributing enough or at least not even close to as much as you but I really am trying my absolute hardest to be the best for you, and that has and will never change.\nI haven't slowed down on the effort or anything like that.", 
            "You've made me so passionate and motivation about everything I put my mind to and I do it all for you.\nI know you want me to do it for myself which I do as well but without you there would be no motivation to improve to this level.",
            "This project right here is literally the entirety of one of my courses this semester.\nI completed this entire syllabus right here.\nEverything I needed to learn in this course I did and used right here!",
            "I will NEVER EVER stop trying for you and you are crazy if you think I'll ever fall out with you.\nYou're crazy to think you're feelings are too much for me when I'm literally still here after you tried to break up with me so many times.\nSorry, but I'm just not leaving. I am not running away.",
            "You can try your hardest to push me away but I am not leaving you.\nI am not that type of person regardless if in the back of your mind you think I am just because I am a man and you're traumatized.\nYou say I dont know you or understand you but I believe that there is no one who understands you better than I do.", 
            "I genuinely, purely, and unconditionally....from the bottom of my heart....Victoria Renee Bharath....love you, more than anything in this entire universe, dimension, lifetime, and many more lifetimes to come.", 
            "And with that I have one thing to ask of you...", "Will you be my Valentines? <3"});
    }
}
