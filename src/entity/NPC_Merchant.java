package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import object.OBJ_Key;
import object.OBJ_Lantern;
import object.OBJ_Mysterious_Staff;
import object.OBJ_Old_Staff;
import object.OBJ_Potion_Red;
import object.OBJ_Scroll_Violet;
import object.OBJ_Shield_Blue;
import object.OBJ_Shield_Wood;
import object.OBJ_Wooden_Staff;
import trial2dgame.GamePanel;

public class NPC_Merchant extends Entity{
	
	public NPC_Merchant(GamePanel gp) {
		super(gp);

		solidArea.x = 8; // SHOULD EQUAL TO 48
		solidArea.y = 16;
		solidArea.width = 30; 
		solidArea.height = 30;
		solidAreaDefaultX = solidArea.x; 
		solidAreaDefaultY = solidArea.y;
		
		direction = "down";
		speed = 1;

		getImage();
		setDialogue();
		setItems();
	}

	@Override
	public void draw(Graphics2D g2) {
	    BufferedImage image = null;

	    switch (direction) {
	        case "up": image = (spriteNum == 1) ? up1 : up2; break;
	        case "down": image = (spriteNum == 1) ? down1 : down2; break;
	        case "left": image = (spriteNum == 1) ? left1 : left2; break;
	        case "right": image = (spriteNum == 1) ? right1 : right2; break;
	    }

	    int screenX = worldX - gp.player.worldX + gp.player.screenX;
	    int screenY = worldY - gp.player.worldY + gp.player.screenY;

	    // Draw NPC
	    g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

	    // 🔴 Draw collision box (DEBUG)
//	    g2.setColor(Color.RED);
//	    g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
	}
	
	public void getImage() {

		up1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
		up2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
		down1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
		down2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
		left1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
		left2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
		right1 = setup("/npc/merchant_down_1", gp.tileSize, gp.tileSize);
		right2 = setup("/npc/merchant_down_2", gp.tileSize, gp.tileSize);
	}

	public void setDialogue() {
		
		dialogues[0][0] = "Welcome to my shop.\nWhat do you want to do?";
		dialogues[1][0] = "See you next time.";
		dialogues[2][0] = "You don't have enough coins\nto buy that.";		
		dialogues[3][0] = "Inventory full.";
		dialogues[4][0] = "You cannot sell an equipped item.";		
	//	dialogues[5][0] = "See you next time.";	
	
	}

	public void setItems() {
		
		inventory.add(new OBJ_Potion_Red(gp));
		inventory.add(new OBJ_Key(gp));
		inventory.add(new OBJ_Shield_Wood(gp));
		inventory.add(new OBJ_Shield_Blue(gp));
		inventory.add(new OBJ_Lantern(gp));
		
		inventory.add(new OBJ_Mysterious_Staff(gp));
		inventory.add(new OBJ_Old_Staff(gp));
		inventory.add(new OBJ_Wooden_Staff(gp));
		inventory.add(new OBJ_Scroll_Violet(gp));

	}
	
	public void speak() {
		
		facePlayer();
		gp.gameState = gp.tradeState;
		gp.ui.npc = this; // to access inventory of this npc
			
	}
	
	@Override
	public void setAction() {
	    // Don't change direction or position — idle animation will still occur
	}

	
	
	
	
	
}
