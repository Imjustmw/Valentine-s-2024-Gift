package random.Tile;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image;
    public boolean collision = false;
    public int direction = -1;
    public boolean interactive = false;
    public String interactionCode = "";
}