package random.Main;

import random.Entity.Entity;

public class CollisionChecker {
    private GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkCollision(Entity entity) {
        checkTile(entity);
        for (Entity entity2 : gp.entities) {
            if (!entity.equals(entity2)) {
                checkEntity(entity, entity2);
            }
        }
    }

    public void checkEntity(Entity entity1, Entity entity2) {
       int xMul = entity1.direction[0];
       int yMul = entity1.direction[1];
       
       double diffX = Math.abs(entity1.nextX + (gp.tileSize * xMul) - entity2.nextX);
       double diffY = Math.abs(entity1.nextY + (gp.tileSize * yMul) - entity2.nextY);

       if (diffX < gp.tileSize && diffY < gp.tileSize) {
            entity1.collisionOn = true;
       }
    }

    public void checkTile(Entity entity) {
        int xMul = entity.direction[0];
        int yMul = entity.direction[1];
        
        int tileX = (int) (entity.worldX + (gp.tileSize * xMul)) / gp.tileSize;
        int tileY = (int) (entity.worldY + (gp.tileSize * yMul)) / gp.tileSize;
        if (tileX >= gp.maxWorldHeight || tileX < 0 || tileY >= gp.maxWorldWidth || tileY < 0) {
            entity.collisionOn = true;
            return;
        }
        int tileNo = gp.tileManager.mapTileNo[tileY][tileX];
        int tileNo2 = gp.tileManager.overlayTileNo[tileY][tileX];
        if (gp.tileManager.tile[tileNo].collision || gp.tileManager.tile[tileNo2].collision) {
            entity.collisionOn = true;
        }

     }
    
}
