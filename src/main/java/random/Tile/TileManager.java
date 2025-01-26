package random.Tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

import random.Main.GamePanel;


public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNo[][];
    public int overlayTileNo[][];

    public TileManager (GamePanel gp) {
        this.gp = gp;
        tile = new Tile[70];

        getTileImage();

        resizeArrays(gp.maxWorldWidth, gp.maxWorldHeight);
        loadMap(mapTileNo,"Upstairs.txt", gp.maxWorldWidth, gp.maxWorldHeight);
        loadMap(overlayTileNo,"Upstairs_Overlay.txt", gp.maxWorldWidth, gp.maxWorldHeight);
    }

    public void resizeArrays(int width, int height) {
        mapTileNo = new int[width][height];
        overlayTileNo = new int[width][height];
        gp.maxWorldWidth = width;
        gp.maxWorldHeight = height;
        gp.worldWidth = gp.tileSize *gp.maxWorldWidth;
        gp.worldHeight = gp.tileSize * gp.maxWorldHeight;
        gp.defaultSpeedScale = gp.worldWidth/4;
    }

    public void loadMap(int arr[][], String mapID, int width, int height) { // Read text file with map tiles as int
        try {
            InputStream IS = getClass().getResourceAsStream("/Maps/" + mapID);
            BufferedReader br = new BufferedReader(new InputStreamReader(IS));

            for (int i = 0; i < width; i++) {
                String line [] = br.readLine().split(" ");
                for (int j = 0; j < height; j++) {
                    int n = Integer.parseInt(line[j]);
                    arr[i][j] = n;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        // Base Tiles
        for (int y = 0; y < gp.maxWorldWidth ; y++) {
            for (int x = 0; x < gp.maxWorldHeight; x++) {
                int worldX = x * gp.tileSize; // X is horizontal
                int worldY = y * gp.tileSize; // Y is vertical
                double screenX = worldX - gp.player.worldX + gp.player.screenX;
                double screenY = worldY - gp.player.worldY + gp.player.screenY;
                int tileNo = mapTileNo[y][x];
                int tileNo2 = overlayTileNo[y][x];
                
                if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                        g2.drawImage(tile[tileNo].image, (int)screenX, (int)screenY, gp.tileSize, gp.tileSize, null);
                        if (tileNo2 != 0) {
                            g2.drawImage(tile[tileNo2].image, (int)screenX, (int)screenY, gp.tileSize, gp.tileSize, null);
                        }
                    }

            }
        }
    }

    public void getTileImage() {
        try {
            // Tiles
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Floor4.png"));
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Wall1.png"));
            tile[1].collision = true;
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Wall2.png"));
            tile[2].collision = true;

            // Overlay
            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Upstairs1_1.png"));
            tile[4] = new Tile();
            tile[4].interactive = true;
            tile[4].interactionCode = "Upstairs";
            tile[4].direction = 2;
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Upstairs1_2.png"));
            tile[4].collision = true;
            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Upstairs1_3.png"));
            tile[6] = new Tile();
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Upstairs1_4.png"));
            tile[6].collision = true;

            tile[7] = new Tile();
            tile[7].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Shelf1.png"));
            tile[8] = new Tile();
            tile[8].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/PictureFrame1.png"));
            tile[9] = new Tile();
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Clock.png"));
            tile[10] = new Tile();
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Window2_1.png"));
            tile[11] = new Tile();
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Window2_2.png"));

            tile[12] = new Tile();
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Desk1_1.png"));
            tile[12].interactive = true;
            tile[12].interactionCode = "Desktop";
            tile[13] = new Tile();
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Desk1_2.png"));
            tile[14] = new Tile();
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/BookShelf_1.png"));
            tile[15] = new Tile();
            tile[15].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/BookShelf_2.png"));
            tile[15].interactive = true;
            tile[15].interactionCode = "BookShelf";
            tile[16] = new Tile();
            tile[16].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/BookShelf_3.png"));
            tile[17] = new Tile();
            tile[17].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Bed_1.png"));
            tile[17].collision = true;
            tile[17].interactive = true;
            tile[17].interactionCode = "Bed";
            tile[18] = new Tile();
            tile[18].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Bed_2.png"));
            tile[18].collision = true;
            tile[19] = new Tile();
            tile[19].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Dresser_1.png"));
            tile[19].collision = true;
            tile[19].interactive = true;
            tile[19].interactionCode = "Makeup";
            tile[20] = new Tile();
            tile[20].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Dresser_2.png"));
            tile[20].collision = true;
            tile[21] = new Tile();
            tile[21].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Dresser_3.png"));
            tile[21].collision = true;
            tile[21].interactive = true;
            tile[21].interactionCode = "Note";

            tile[22] = new Tile();
            tile[22].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Door1_1.png"));
            tile[23] = new Tile();
            tile[23].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Door1_2.png"));

            tile[24] = new Tile();
            tile[24].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Rug1_1.png"));
            tile[25] = new Tile();
            tile[25].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Rug1_2.png"));
            tile[26] = new Tile();
            tile[26].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Rug1_3.png"));
            tile[27] = new Tile();
            tile[27].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Rug1_4.png"));
            tile[28] = new Tile();
            tile[28].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Panda.png"));
            tile[28].collision = true;
            tile[28].interactive = true;
            tile[28].interactionCode = "Panda";
            
            tile[29] = new Tile();
            tile[29].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Floor5.png"));

            tile[30] = new Tile();
            tile[30].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Downstairs1_1.png"));
            tile[31] = new Tile();
            tile[31].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Downstairs1_2.png"));
            tile[32] = new Tile();
            tile[32].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Downstairs1_3.png"));
            tile[32].collision = true;
            tile[33] = new Tile();
            tile[33].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Downstairs1_4.png"));
            tile[33].collision = true;
            tile[33].interactive = true;
            tile[33].interactionCode = "Downstairs";
            tile[33].direction = 1;

            tile[34] = new Tile();
            tile[34].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter1.png"));
            tile[34].collision = true;
            tile[35] = new Tile();
            tile[35].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter2.png"));
            tile[35].collision = true;
            tile[36] = new Tile();
            tile[36].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter3.png"));
            tile[36].collision = true;
            tile[37] = new Tile();
            tile[37].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter4.png"));
            tile[37].collision = true;
            tile[38] = new Tile();
            tile[38].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter5.png"));
            tile[38].collision = true;
            tile[39] = new Tile();
            tile[39].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter6.png"));
            tile[39].collision = true;
            tile[40] = new Tile();
            tile[40].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter7.png"));
            tile[40].collision = true;
            tile[41] = new Tile();
            tile[41].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter8.png"));
            tile[41].collision = true;
            tile[42] = new Tile();
            tile[42].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Rug2.png"));
            tile[43] = new Tile();
            tile[43].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Fridge_1.png"));
            tile[43].collision = true;
            tile[44] = new Tile();
            tile[44].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Fridge_2.png"));
            tile[44].collision = true;
            tile[45] = new Tile();
            tile[45].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Stove.png"));
            tile[45].collision = true;

            tile[46] = new Tile();
            tile[46].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/TV1_1.png"));
            tile[46].collision = true;
            tile[47] = new Tile();
            tile[47].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/TV1_2.png"));
            tile[47].collision = true;
            tile[47].interactive = true;
            tile[47].interactionCode = "Television";
            tile[48] = new Tile();
            tile[48].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/TV1_3.png"));
            tile[49] = new Tile();
            tile[49].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/TV1_4.png"));
            tile[50] = new Tile();
            tile[50].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Counter9.png"));
            tile[50].collision = true;
            tile[50].interactive = true;
            tile[50].interactionCode = "Chocolates";

            tile[51] = new Tile();
            tile[51].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Couch1_1.png"));
            tile[51].collision = true;
            tile[52] = new Tile();
            tile[52].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Couch1_2.png"));

            tile[53] = new Tile();
            tile[53].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Couch2_1.png"));
            tile[53].collision = true;
            tile[54] = new Tile();
            tile[54].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Couch2_2.png"));
            tile[54].collision = true;
            tile[55] = new Tile();
            tile[55].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Couch2_3.png"));
            tile[55].collision = true;
            tile[56] = new Tile();
            tile[56].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Couch3_1.png"));
            tile[56].collision = true;
            tile[57] = new Tile();
            tile[57].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Couch3_2.png"));
            tile[58] = new Tile();
            tile[58].image = ImageIO.read(getClass().getResourceAsStream("/Images/Tiles/Table1.png"));
            tile[58].collision = true;
            
        } catch(IOException e) {
            e.printStackTrace();
        } 
    }
}
