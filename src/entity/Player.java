package entity;

import java.awt.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import object.OBJ_Key;
import object.OBJ_Lantern;
import object.OBJ_Potion_Red;
import object.OBJ_Shield_Wood;
import object.OBJ_Wooden_Staff;
import trial2dgame.GamePanel;
import trial2dgame.KeyHandler;

public class Player extends Entity {

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    public boolean attackCanceled = false;
    public final int maxInventorySize = 20;
    public boolean lightUpdated = false;
    public int baseMaxLife;
    public int baseKnowledge;

    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp); // calling superclass (of this class) and passing this gp
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize) / 2;
        screenY = gp.screenHeight / 2 - (gp.tileSize) / 2;

        // SOLID AREA
        solidArea = new Rectangle(); // x, y, width, height
        solidArea.x = 8; // collision
        solidArea.y = 16; // collision
        solidAreaDefaultX = solidArea.x; // for obj
        solidAreaDefaultY = solidArea.y; // for obj
        solidArea.width = 30; // collisionRectangle of player
        solidArea.height = 30; // collisionRectangle of player
        // ATTACK AREA
        attackArea.width = 36; //default claws in sword_normal
        attackArea.height = 36; //default claws in sword_normal

        setDefaultValues();

    }

    public void setDefaultValues() {

        worldX = gp.tileSize * 29; //beach side
        worldY = gp.tileSize * 12; //beach side
        worldX = gp.tileSize * 30; //house
        worldY = gp.tileSize * 36; //house
        worldX = gp.tileSize * 33; //library
        worldY = gp.tileSize * 19; //library
        gp.currentMap = 0;
        speed = 6;
        direction = "down";

        // PLAYER STATUS

        baseKnowledge = 5;
        maxLife = 10;
        knowledge = baseKnowledge;
        life = getMaxLife();


        level = 1;
        baseAttack = 2;
        attack = getAttack();
        defense = getDefense();
        knowledge = 5;
        exp = 0;
        nextLevelExp = 5;
        coin = 0;
        currentWeapon = new OBJ_Wooden_Staff(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        currentLight = null;

        getPlayerImage();
        getPlayerAttackImage();
        setItems();
        setDialogue();

        if (life > getMaxLife()) {
            life = getMaxLife();
        }
    }


    public void setDefaultPositions() { //for restarting

        worldX = gp.tileSize * 25;
        worldY = gp.tileSize * 15;
        direction = "down";
    }

    public void setDialogue() {
        dialogues[0][0] = "You are level " + level + " now!" + " You feel stronger";
    }

    public void restoreStatus() {

        // Use dynamic max life (includes equipment bonuses)
        life = getMaxLife();
        invincible = false;
        lightUpdated = true;
    }

    public void setItems() {

        inventory.clear();
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Potion_Red(gp));
        inventory.add(new OBJ_Lantern(gp));

    }

    public int getAttack() {
        int attack = baseAttack;
        if (currentWeapon != null) attack += currentWeapon.attackValue;
        return attack;
    }

    public int getDefense() {
        return currentShield != null ? currentShield.defenseValue : 0;
    }

    public int getMaxLife() {
        int bonus = 0;
        if (currentWeapon != null) bonus += currentWeapon.hpValue;
        if (currentShield != null) bonus += currentShield.hpValue;
        return maxLife + bonus;
    }

    public int getCurrentWeaponSlot() {
        int currentWeaponSlot = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) == currentWeapon) {
                currentWeaponSlot = i;
            }
        }
        return currentWeaponSlot;
    }

    public int getCurrentShieldSlot() {
        int currentShieldSlot = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) == currentShield) {
                currentShieldSlot = i;
            }
        }
        return currentShieldSlot;
    }

    public void getPlayerImage() {

        up1 = setup("/playerCat/Nup", gp.tileSize, gp.tileSize);
        up2 = setup("/playerCat/Nup2", gp.tileSize, gp.tileSize);
        down1 = setup("/playerCat/Nfront", gp.tileSize, gp.tileSize);
        down2 = setup("/playerCat/Nfront2", gp.tileSize, gp.tileSize);
        left1 = setup("/playerCat/Nleft", gp.tileSize, gp.tileSize);
        left2 = setup("/playerCat/Nleft2", gp.tileSize, gp.tileSize);
        right1 = setup("/playerCat/Nright", gp.tileSize, gp.tileSize);
        right2 = setup("/playerCat/Nright2", gp.tileSize, gp.tileSize);
    }

    public void getPlayerAttackImage() {

        if (currentWeapon.type == type_weapon) {
            attackUp1 = setup("/playerCat/cat_attack_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup("/playerCat/cat_attack_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup("/playerCat/cat_attack_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup("/playerCat/cat_attack_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup("/playerCat/cat_attack_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup("/playerCat/cat_attack_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup("/playerCat/cat_attack_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup("/playerCat/cat_attack_right_2", gp.tileSize * 2, gp.tileSize);
        }

    }

    public void update() {

        if (attacking == true) {
            attacking();
        } else if (keyH.upPressed == true || keyH.downPressed == true ||
                keyH.leftPressed == true || keyH.rightPressed == true ||
                keyH.enterPressed == true) {

            if (keyH.upPressed == true) {
                direction = "up";
            } else if (keyH.downPressed == true) {
                direction = "down";
            } else if (keyH.leftPressed == true) {
                direction = "left";
            } else if (keyH.rightPressed == true) {
                direction = "right";
            }

            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // CHECK NPC COLLISION
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            if (npcIndex != 999) {
                collisionOn = true; // prevents passing through NPC
            }
            interactNPC(npcIndex);

            // CHECK MONSTER COLLISION
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            contactMonster(monsterIndex);

            // CHECK EVENT 
            gp.eHandler.checkEvent();

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (collisionOn == false && keyH.enterPressed == false) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }

            if (keyH.enterPressed == true && attackCanceled == false) {    // also handles durability
                gp.playSE(7);
                attacking = true;
                spriteCounter = 0;

                // DECREASE DURABILITY
                if (currentWeapon != null) {
                    currentWeapon.durability--;
                }
            }

            attackCanceled = false;
            // RESET FLAGS
            gp.keyH.enterPressed = false;

            // ANIMATION
            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {

            standCounter++;
            if (standCounter == 20) {
                spriteNum = 1; // returns to default walk after stopping walk, standstill
                standCounter = 0;
            }
        } //keyPressed if-else


        //This needs to be outside of key if statement
        if (invincible == true) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }

        if (life > getMaxLife()) {
            life = getMaxLife();
        }

        // Game over check: also trigger if dynamic max life is zero or below.
        if (life <= 0 || getMaxLife() <= 0) {
            gp.gameState = gp.gameOverState;
            gp.ui.commandNum = -1;
            gp.stopMusic();
            gp.playSE(10);

        }
        // Add the clamping logic here, after movement:
        int maxX = gp.maxWorldCol * gp.tileSize - gp.tileSize;
        int maxY = gp.maxWorldRow * gp.tileSize - gp.tileSize;

        if (worldX < 0) worldX = 0;
        if (worldY < 0) worldY = 0;
        if (worldX > maxX) worldX = maxX;
        if (worldY > maxY) worldY = maxY;

    }

    public void attacking() {

        spriteCounter++;

        if (spriteCounter == 1) {
            gp.playSE(7); // 🔊 Play attack sound when attack animation starts
        }

        if (spriteCounter <= 5) {
            spriteNum = 1;
        }
        if (spriteCounter > 5 && spriteCounter <= 25) {
            spriteNum = 2; //displays longer than spNum1

            // Save/store the current worldX, worldY, solidArea
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Adjust player's worldX/Y for the attackArea
            switch (direction) {
                case "up":
                    worldY -= attackArea.height;
                    break;
                case "down":
                    worldY += attackArea.height;
                    break;
                case "left":
                    worldX -= attackArea.width;
                    break;
                case "right":
                    worldX += attackArea.width;
                    break;
            }

            // attackArea becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;
            // Check monster collision with the updated worldX, worldY and solidArea
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            damageMonster(monsterIndex);

            // After checking collision, restore the original data
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }
        if (spriteCounter > 25) {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }

    }

    public void pickUpObject(int i) {

        if (i != 999) {

            // PICKUP ONLY ITEMS
            if (gp.obj[gp.currentMap][i].type == type_pickupOnly) {
                gp.obj[gp.currentMap][i].use(this);
                gp.obj[gp.currentMap][i] = null;

            }
            // OBSTACLE    
            else if (gp.obj[gp.currentMap][i].type == type_obstacle) {
                if (keyH.enterPressed == true) {
                    attackCanceled = true;
                    gp.obj[gp.currentMap][i].interact();
                }
            }
            // INVENTORY ITEMS
            else {
                String text;

                if (canObtainItem(gp.obj[gp.currentMap][i]) == true) { //if inventory is not full
                    gp.playSE(1);
                    text = "Obtained a " + gp.obj[gp.currentMap][i].name;
                } else {
                    text = "Inventory is full.";
                }
                gp.ui.addMessage(text);
                gp.obj[gp.currentMap][i] = null; //add [gp.currentMap] if null
            }
        }
    }

    public void interactNPC(int i) {
        if (i != 999) {
            if (gp.keyH.enterPressed == true) {
                attackCanceled = true;
                gp.npc[gp.currentMap][i].speak();
            }
        }

    }

    public void contactMonster(int i) {
        if (i != 999 && !invincible) {
            gp.playSE(6);

            int damage = gp.monster[gp.currentMap][i].attack;
            life -= damage;

            if (life < 0) life = 0;
            if (life > getMaxLife()) life = getMaxLife();

            invincible = true;
            invincibleCounter = 0;
        }
    }


    // =======================
    // KEY CHANGE: delegate to MON_GreenSlime when hitting a green slime
    // This enforces "no damage to wrong slime" and allows the slime's death handler
    // to mark question completed and trigger the next question.
    // =======================
    public void damageMonster(int i) {
        if (i != 999 && gp.monster[gp.currentMap][i] != null) {
            Entity monster = gp.monster[gp.currentMap][i];
            if (!monster.invincible) {
                // If this is a green slime on any map, let its own logic handle damage (it enforces correct answer behavior)
                if (monster instanceof monster.MON_GreenSlime) {
                    int damage = getAttack();
                    // Delegate — MON_GreenSlime.damageMonster handles correct/wrong logic and spawn/question advance
                    ((monster.MON_GreenSlime) monster).damageMonster(damage);
                } else {
                    gp.playSE(5);

                    int damage = getAttack();

                    monster.life -= damage;
                    System.out.println(damage + " damage!");
                    monster.invincible = true;
                    monster.damageReaction();

                    if (monster.life <= 0) {
                        monster.dying = true;
                        System.out.println("You killed a " + monster.name + "!");
                        System.out.println("Exp + " + monster.exp);
                        exp += monster.exp;
                        checkLevelUp();
                    }
                }
            }
        }
    }

    public void checkLevelUp() {

        if (exp >= nextLevelExp) {

            level++;
            nextLevelExp = nextLevelExp * 2;
            maxLife += 2;
            knowledge += 5;
            baseAttack++;
            attack = getAttack();
            defense = getDefense();

            // set to dynamic max life (includes equipment)
            life = getMaxLife(); // <- changed from life = maxLife

            gp.playSE(8);
            gp.gameState = gp.dialogueState;

            setDialogue();
            startDialogue(this, 0);

        }
    }

    public void selectItem() {

        int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);
        if (itemIndex < inventory.size()) {

            Entity selectedItem = inventory.get(itemIndex);

            if (selectedItem.type == type_weapon) {
                currentWeapon = selectedItem;
                attack = getAttack();
            }

            if (selectedItem.type == type_shield) {
                currentShield = selectedItem;
                defense = getDefense();
            }

            if (selectedItem.type == type_light) {
                if (currentLight == selectedItem) {
                    currentLight = null;
                } else {
                    currentLight = selectedItem;
                }
                lightUpdated = true;
            }
            if (selectedItem.type == type_consumable) {

                if (selectedItem.use(this) == true) {
                    if (selectedItem.amount > 1) {
                        selectedItem.amount--;
                    } else {
                        inventory.remove(itemIndex);
                    }
                }
            }
        }
    }

    public int searchItemInInventory(String itemName) {

        int itemIndex = 999;

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).name.equals(itemName)) {
                itemIndex = i;
                break;
            }
        }
        return itemIndex;
    }

    public boolean canObtainItem(Entity item) {

        boolean canObtain = false;

        Entity newItem = gp.eGenerator.getObject(item.name);

        // CHECK IF STACKABLE
        if (newItem.stackable == true) {

            int index = searchItemInInventory(newItem.name);

            if (index != 999) {
                inventory.get(index).amount++;
                canObtain = true;
            } else { // NEW ITEM, NEED TO CHECK FOR VACANCY
                if (inventory.size() != maxInventorySize) {
                    inventory.add(newItem);
                    canObtain = true;
                }
            }
        } else { // NOT STACKABLE so still check vacancy
            if (inventory.size() != maxInventorySize) {
                inventory.add(newItem);
                canObtain = true;
            }
        }
        return canObtain;
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;

        switch (direction) {
            case "up":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = up1;
                    }
                    if (spriteNum == 2) {
                        image = up2;
                    }
                }
                if (attacking == true) {
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) {
                        image = attackUp1;
                    }
                    if (spriteNum == 2) {
                        image = attackUp2;
                    }
                }
                break;
            case "down":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = down1;
                    }
                    if (spriteNum == 2) {
                        image = down2;
                    }
                }
                if (attacking == true) {
                    if (spriteNum == 1) {
                        image = attackDown1;
                    }
                    if (spriteNum == 2) {
                        image = attackDown2;
                    }
                }
                break;
            case "left":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = left1;
                    }
                    if (spriteNum == 2) {
                        image = left2;
                    }
                }
                if (attacking == true) {
                    tempScreenX = screenX - gp.tileSize;
                    if (spriteNum == 1) {
                        image = attackLeft1;
                    }
                    if (spriteNum == 2) {
                        image = attackLeft2;
                    }
                }
                break;
            case "right":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = right1;
                    }
                    if (spriteNum == 2) {
                        image = right2;
                    }
                }
                if (attacking == true) {
                    if (spriteNum == 1) {
                        image = attackRight1;
                    }
                    if (spriteNum == 2) {
                        image = attackRight2;
                    }
                }
                break;
        }

        if (invincible == true) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); //70% transparent

        }

        g2.drawImage(image, tempScreenX, tempScreenY, null);

        //RESET ALPHA
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); //no transparent

        //DEBUG
        g2.setColor(Color.red);
    }
}