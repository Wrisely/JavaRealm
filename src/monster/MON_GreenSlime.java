package monster;
import java.util.Random;
import entity.Entity;
import trial2dgame.GamePanel;

public class MON_GreenSlime extends Entity{

    GamePanel gp;

    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = type_monster;
        typeChapter = chapter2;
        typeDifficulty = easy;

        name = "Green Slime";
        speed = 1;
        maxLife = 60;
        life = maxLife;
        attack = 3;
        defense = 2;
        exp = 2;

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        up2 = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
    }

    public void update(){
        super.update();

        int xDistance = Math.abs(worldX - gp.player.worldX);
        int yDistance = Math.abs(worldY - gp.player.worldY);
        int tileDistance = (xDistance + yDistance)/gp.tileSize;

        if(onPath == false && tileDistance < 5) {
            int i = new Random().nextInt(100)+1;
            if(i > 50) {
                onPath = true;
            }
        }
        if(onPath == true && tileDistance > 10) {
            onPath = false;
        }
    }

    public void damageMonster(int attack) {
        // Only allow damage for slime that matches current question index
        if (this.answerIndex == gp.currentQuestionIndex) {
            if (life > 0) {
                life -= attack;
                if (life <= 0) {
                    alive = false;
                    gp.playSE(5); // correct slime killed
                    if (this.answerIndex >= 0 && this.answerIndex < gp.questionCompleted.length) {
                        gp.questionCompleted[this.answerIndex] = true;
                    }
                    // unassign so that UI won't select it anymore
                    gp.markQuestionUnassigned(this.answerIndex);
                    // choose another visible question (if any)
                    gp.setNextQuestion();
                    // NO respawn: leader requested no new spawns after killing
                }
            }
        } else {
            // wrong slime cannot be killed
            gp.playSE(7);
        }
    }

    public void setAction() {
        if(onPath == true) {
            int goalCol = (gp.player.worldX + gp.player.solidArea.x)/gp.tileSize;
            int goalRow = (gp.player.worldY + gp.player.solidArea.y)/gp.tileSize;
            searchPath(goalCol, goalRow);
        } else {
            actionLockCounter++;
            if(actionLockCounter == 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                if (i <= 25) {direction = "up";}
                if (i > 25 && i <= 50) {direction = "down";}
                if (i > 50 && i <= 75) {direction = "left";}
                if (i > 75 && i <= 100) {direction = "down";}
                actionLockCounter = 0;
            }
        }
    }

    public void damageReaction() {
        actionLockCounter = 0;
        onPath = true;
    }
}