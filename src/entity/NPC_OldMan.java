package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import trial2dgame.GamePanel;

public class NPC_OldMan extends Entity {

	public NPC_OldMan(GamePanel gp) {
		super(gp);

		solidArea.x = 8; // SHOULD EQUAL TO 48
		solidArea.y = 16;
		solidArea.width = 30; 
		solidArea.height = 30;
		solidAreaDefaultX = solidArea.x; 
		solidAreaDefaultY = solidArea.y;	
		direction = "down";
		speed = 1;
		dialogueSet = 0; // Start with the first dialogue set
		dialogueIndex = 0; // Start with the first dialogue index		
		getImage();
		setDialogue();
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
	}
	public void getImage() {
		up1 = setup("/npc/oldman_up_1", gp.tileSize, gp.tileSize);
		up2 = setup("/npc/oldman_up_2", gp.tileSize, gp.tileSize);
		down1 = setup("/npc/oldman_down_1", gp.tileSize, gp.tileSize);
		down2 = setup("/npc/oldman_down_2", gp.tileSize, gp.tileSize);
		left1 = setup("/npc/oldman_left_1", gp.tileSize, gp.tileSize);
		left2 = setup("/npc/oldman_left_2", gp.tileSize, gp.tileSize);
		right1 = setup("/npc/oldman_right_1", gp.tileSize, gp.tileSize);
		right2 = setup("/npc/oldman_right_2", gp.tileSize, gp.tileSize);
	}
	public void setDialogue() {		
		dialogues[0][0] = "";
		dialogues[0][1] = "Hello there.";
		dialogues[0][2] = "So you've come to this seaside to\nexplore?";
		dialogues[0][3] = "I will be waiting here for the\n meantime.";
		dialogues[1][0] = "Rest here in the coast side.";
		dialogues[1][1] = "But remember, the monsters will\ncome back later.";
		dialogues[1][2] = "Make sure to heavily equip yourself\nwith weapons and potions.";
		dialogues[2][0] = "Why are you not in school?";
	}
	public void setAction() {
		if(onPath == true) {
//			int goalCol = 3;	// follows a specific path
//			int goalRow = 28;	// follows a specific path
			int goalCol = (gp.player.worldX + gp.player.solidArea.x)/gp.tileSize;
			int goalRow = (gp.player.worldY + gp.player.solidArea.y)/gp.tileSize;

			searchPath(goalCol, goalRow);		
		}
		else {
			
			actionLockCounter++;
		
			if(actionLockCounter == 120) {
				Random random = new Random();
				int i = random.nextInt(100) + 1; // 1-100
		
				if (i <= 25) {direction = "up";}
				if (i > 25 && i <= 50) {direction = "down";}
				if (i > 50 && i <= 75) {direction = "left";}
				if (i > 75 && i <= 100) {direction = "down";}
		
				actionLockCounter = 0;
			}
		}
	
	}
	
	public void speak() {
		
		facePlayer(); // Ensure the NPC faces the player

	    if (dialogueSet >= 0 && dialogueSet < dialogues.length) {
	    	
	    	if (dialogues[dialogueSet][dialogueIndex] != null) {
	            startDialogue(this, dialogueSet); // Display current dialogue line
	        }

	        if (gp.keyH.enterPressed) {
	            gp.keyH.enterPressed = false; // Reset the key press flag
	            dialogueIndex++;

	            if (dialogueIndex >= dialogues[dialogueSet].length || dialogues[dialogueSet][dialogueIndex] == null) {
	                dialogueIndex = 0;
	                
	                if (dialogueSet == 2) {
	                    dialogueSet = 1; // Repeat set 1
	                } else {
	                    dialogueSet++;
	                }

	                if (dialogueSet >= dialogues.length || dialogues[dialogueSet][0] == null) {
	                    dialogueSet = dialogues.length - 1; // Stay at the last valid set
	                }
	            }
	        }
	    }		
//		if(gp.player.life < gp.player.maxLife/3) {
//			dialogueSet = 1;
//		}
//		onPath = true;	// follows a specific path
	}
}
