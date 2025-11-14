package monster;

import java.util.Random;
import entity.Entity;
import trial2dgame.GamePanel;

public class MON_LHound extends Entity {

    GamePanel gp;

    public MON_LHound(GamePanel gp) {
        super(gp);
        
        this.gp = gp;

        type = type_monster; // 2
        typeChapter = chapter3;
        typeDifficulty = hard;
        
        name = "Lava Hound";
        speed = 1;
        maxLife = 150;
        life = maxLife;
        attack = 5;
        defense = 5;
        exp = 8;
    
        solidArea.x = 3; // SHOULD EQUAL TO 48
        solidArea.y = 18;
        solidArea.width = 42; 
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x; 
        solidAreaDefaultY = solidArea.y;    
        
        getImage();
    }
    
    public void getImage() {        
        up1 = setup("/monster/LHound_down_1", gp.tileSize, gp.tileSize);
        up2 = setup("/monster/LHound_down_2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/LHound_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/LHound_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/LHound_down_1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/LHound_down_2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/LHound_down_1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/LHound_down_2", gp.tileSize, gp.tileSize);
    }

    public void update() {
        super.update();
        
        int xDistance = Math.abs(worldX - gp.player.worldX);
        int yDistance = Math.abs(worldY - gp.player.worldY);
        int tileDistance = (xDistance + yDistance) / gp.tileSize;
        
        if (!onPath && tileDistance < 3) { // IF NEAR 5 TILES, GAIN AGGRO
            int i = new Random().nextInt(100) + 1;
            if (i > 50) { // RANDOM AGGRO PERCENTAGE
                onPath = true;
            }
        }
        if (onPath && tileDistance > 10) { // IF AWAY BY 10 TILES, LOSE AGGRO
            onPath = false;
        }
    }
    
    public void setAction() {
        if (onPath) {
            int goalCol = (gp.player.worldX + gp.player.solidArea.x) / gp.tileSize;
            int goalRow = (gp.player.worldY + gp.player.solidArea.y) / gp.tileSize;

            searchPath(goalCol, goalRow);    
        } else {
            actionLockCounter++;
        
            if (actionLockCounter == 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1; // 1-100
        
                if (i <= 25) { direction = "up"; }
                else if (i <= 50) { direction = "down"; }
                else if (i <= 75) { direction = "left"; }
                else { direction = "right"; } // Changed to "right"
        
                actionLockCounter = 0;
            }
        }
    }
    
    public void damageReaction() { // follows player when attacked, aggro
        actionLockCounter = 0;
        // direction = gp.player.direction;
        onPath = true; // WHEN ATTACKED, GAINS AGGRO 100%
    }
}

