package entity;

import java.awt.AlphaComposite;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;	
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import trial2dgame.GamePanel;
import trial2dgame.UtilityTool;

public class Entity {
    GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, 
    attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage image, image2, image3;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    public String dialogues[][] = new String[20][20];

    public int worldX, worldY;
    public String direction = "down";
    public int spriteNum = 1;
    public int dialogueSet = 0;
    public int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false;
    boolean attacking = false;
    public boolean alive = true;
    public boolean dying = false;
    public boolean hpBarOn = false;
    public boolean onPath = false;
    public Entity loot;
    public boolean opened = false;

    public int spriteCounter = 0;
    public int actionLockCounter = 0;
    public int invincibleCounter = 0;
    int dyingCounter = 0;
    int hpBarCounter = 0;

    public String name;
    public int speed;
    public int maxLife;
    public int life;
    public int baseAttack;
    public int attack;
    public int defense;
    public int level;
    public int exp;
    public int nextLevelExp;
    public int coin;
    public int knowledge;
    public int hpValue;
    public int knowledgeValue;

    public int typeChapter;
    public int typeDifficulty;
    public int answerIndex = -1;

    public final int chapter1 = 1;
    public final int chapter2 = 2;
    public final int chapter3 = 3;
    public final int chapter4 = 4;

    public final int easy = 0;
    public final int hard = 1;

    public Entity currentWeapon;
    public Entity currentShield;
    public Entity currentLight;

    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;
    public int value;
    public int attackValue;
    public int defenseValue;
    public String description = "";
    public int price;
    public boolean stackable = false;
    public int amount = 1;
    public int lightRadius;
    public int durability = 20;

    public int type;
    public final int type_player = 0;
    public final int type_npc = 1;
    public final int type_monster = 2;
    public final int type_weapon = 3;
    public final int type_questItem = 4;
    public final int type_shield = 5;
    public final int type_consumable = 6;
    public final int type_pickupOnly = 7;
    public final int type_obstacle = 8;
    public final int type_light = 9;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public int getLeftX(){return worldX + solidArea.x;}
    public int getRightX(){return worldX + solidArea.x + solidArea.width;}
    public int getTopY(){return worldY + solidArea.y;}
    public int getBottomY(){return worldY + solidArea.y + solidArea.height;}
    public int getCol(){return (worldX + solidArea.x)/gp.tileSize;}
    public int getRow(){return (worldY + solidArea.y)/gp.tileSize;}

    public void resetCounter(){
        spriteCounter = 0;
        actionLockCounter = 0;
        invincibleCounter = 0;
        dyingCounter = 0;
        hpBarCounter = 0;
    }
    public void setLoot(Entity loot){}
    public void setAction() {}
    public void damageReaction() {}
    public void speak() {}

    public void facePlayer() {
        switch(gp.player.direction) {
            case "up": direction = "down"; break;
            case "down": direction = "up"; break;
            case "left": direction = "right"; break;
            case "right": direction = "left"; break;
        }
    }
    public void startDialogue(Entity entity, int setNum){
        gp.gameState = gp.dialogueState;
        gp.ui.npc = entity;
        dialogueSet = setNum;
    }
    public void interact() {}

    public boolean use(Entity entity) {return false;}

    public void checkCollision() {
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkEntity(this, gp.npc);
        gp.cChecker.checkEntity(this, gp.monster);
        gp.cChecker.checkPlayer(this);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        if(this.type == type_monster && contactPlayer == true) {
            damagePlayer(attack);
        }
    }

    public boolean questionMarkedCompleted = false;

    public void update() {
        setAction();
        checkCollision();

        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

        spriteCounter++;
        if (spriteCounter > 24) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) {
                invincible = false;
                invincibleCounter = 0;
            }
        }

        // Clamp
        if (worldX < 0) worldX = 0;
        if (worldY < 0) worldY = 0;

        int maxX = gp.maxWorldCol * gp.tileSize - gp.tileSize;
        int maxY = gp.maxWorldRow * gp.tileSize - gp.tileSize;

        if (worldX > maxX) worldX = maxX;
        if (worldY > maxY) worldY = maxY;
    }

    public void damagePlayer(int attack) {
        if (gp.player.invincible == false) {
            gp.playSE(6);
            gp.player.life -= attack;
            if (gp.player.life < 0) gp.player.life = 0;
            if (gp.player.life > gp.player.getMaxLife()) gp.player.life = gp.player.getMaxLife();
            gp.player.invincible = true;
            gp.player.invincibleCounter = 0;
            if (gp.player.life <= 0) {
                gp.stopMusic();
                gp.playSE(10);
                gp.gameState = gp.gameOverState;
                gp.ui.commandNum = -1;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            switch (direction) {
                case "up":
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                    break;
                case "down":
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                    break;
                case "left":
                    if (spriteNum == 1) {image = left1;}
                    if (spriteNum == 2) {image = left2;}
                    break;
                case "right":
                    if (spriteNum == 1) {image = right1;}
                    if (spriteNum == 2) {image = right2;}
                    break;
            }

            // draw number for slimes on map 0
            if(type == type_monster && gp.currentMap == 0) {
                int questionIdx = this.answerIndex;
                long answer = gp.map0JSON != null && questionIdx >= 0 && questionIdx < gp.map0JSON.size()
                              ? (long)((JSONObject)gp.map0JSON.get(questionIdx)).get("answer")
                              : -1;
                String num = Long.toString(answer);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                int tx = screenX + (gp.tileSize - fm.stringWidth(num)) / 2;
                int ty = (screenY + 30) - gp.tileSize / 2;
                g2.drawString(num, tx, ty);
            }

            // Monster HP bar
            if(type == 2 && hpBarOn == true) {
                double oneScale = (double)gp.tileSize/maxLife;
                double hpBarValue = oneScale*life;
                g2.setColor(new Color(35,35,35)); // outline hp bar
                g2.fillRect(screenX - 1, screenY - 16, gp.tileSize+2, 12);
                g2.setColor(new Color(255,0,30));
                g2.fillRect(screenX, screenY - 15, (int)hpBarValue, 10);
                hpBarCounter++;
                if(hpBarCounter > 600) {
                    hpBarCounter = 0;
                    hpBarOn = false;
                }
            }

            if(invincible == true) {
                hpBarOn = true;
                hpBarCounter = 0;
                changeAlpha(g2, 0.4F);
            }
            if(dying == true) {
                dyingAnimation(g2);
            }

            g2.drawImage(image, screenX, screenY, null);
            changeAlpha(g2, 1F);
        }
    }

    public void dyingAnimation(Graphics2D g2) {
        dyingCounter++;
        int i = 5;
        if(dyingCounter <= i) {changeAlpha(g2, 0f);}
        if(dyingCounter > i && dyingCounter <= i*2) {changeAlpha(g2, 1f);}
        if(dyingCounter > i*2 && dyingCounter <= i*3) {changeAlpha(g2, 0f);}
        if(dyingCounter > i*3 && dyingCounter <= i*4) {changeAlpha(g2, 1f);}
        if(dyingCounter > i*4 && dyingCounter <= i*5) {changeAlpha(g2, 0f);}
        if(dyingCounter > i*5 && dyingCounter <= i*6) {changeAlpha(g2, 1f);}
        if(dyingCounter > i*6 && dyingCounter <= i*7) {changeAlpha(g2, 0f);}
        if(dyingCounter > i*7 && dyingCounter <= i*8) {changeAlpha(g2, 1f);}
        if(dyingCounter > i*8) {
            alive = false;
        }
    }

    public void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }
    public BufferedImage setup(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTool.scaleImage(image, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void searchPath(int goalCol, int goalRow) {
        int startCol = (worldX + solidArea.x)/gp.tileSize;
        int startRow = (worldY + solidArea.y)/gp.tileSize;
        gp.pFinder.setNodes(startCol, startRow, goalCol, goalRow);
        if(gp.pFinder.search() == true) {
            int nextX = gp.pFinder.pathList.get(0).col * gp.tileSize; 
            int nextY = gp.pFinder.pathList.get(0).row * gp.tileSize; 
            int enLeftX = worldX + solidArea.x;
            int enRightX = worldX + solidArea.x + solidArea.width;
            int enTopY = worldY + solidArea.y;
            int enBottomY = worldY + solidArea.y + solidArea.height;
            if((enTopY > nextY) && (enLeftX >= nextX) && (enRightX < nextX + gp.tileSize)) {
                direction = "up";
            }
            else if((enTopY < nextY) && (enLeftX >= nextX) && (enRightX < nextX + gp.tileSize)) {
                direction = "down";
            }
            if((enTopY >= nextY) && (enBottomY < nextY + gp.tileSize)) {
                if(enLeftX > nextX) {
                    direction = "left";
                }
                if(enLeftX < nextX) {
                    direction = "right";
                }
            }
            else if((enTopY > nextY) && (enLeftX > nextX)) {
                direction = "up";
                checkCollision();
                if(collisionOn == true) {
                    direction = "left";
                }
            }
            else if((enTopY > nextY) && (enLeftX < nextX)) {
                direction = "up";
                checkCollision();
                if(collisionOn == true) {
                    direction = "right";
                }
            }
            else if((enTopY < nextY) && (enLeftX > nextX)) {
                direction = "down";
                checkCollision();
                if(collisionOn == true) {
                    direction = "left";
                }
            }
            else if((enTopY < nextY) && (enLeftX < nextX)) {
                direction = "down";
                checkCollision();
                if(collisionOn == true) {
                    direction = "right";
                }
            }
        }
    }

    public int getDetected(Entity user, Entity target[][], String targetName) {
        int index = 999;
        int nextWorldX = user.getLeftX();
        int nextWorldY = user.getTopY();
        switch(user.direction) {
            case "up": nextWorldY = user.getTopY()-gp.player.speed; break;
            case "down": nextWorldY = user.getBottomY()+gp.player.speed; break;
            case "left": nextWorldX = user.getLeftX()-gp.player.speed; break;
            case "right": nextWorldX = user.getRightX()+gp.player.speed; break;	
        }
        int col = nextWorldX/gp.tileSize;
        int row = nextWorldY/gp.tileSize;
        for(int i = 0; i < target[gp.currentMap].length; i++) {
            if(target[gp.currentMap][i] != null) {
                if(target[gp.currentMap][i].getCol() == col &&
                   target[gp.currentMap][i].getRow() == row &&
                   target[gp.currentMap][i].name.equals(targetName)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
}