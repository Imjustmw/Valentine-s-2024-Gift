package random.Entity;

import java.awt.Rectangle;

public class Entity {
    public double worldX, worldY;
    public double nextX, nextY;
    public double speed;

    public int directionIndex;
    public int[] direction = new int[2];

    public Rectangle hitbox;
    public boolean collisionOn = true;

    public boolean interacting = false;
    public boolean interactive = false;
    public String[] dialogue;
}
