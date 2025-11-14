package object;

import java.io.IOException;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Key_Purple extends Entity{
	
	GamePanel gp;
	public static final String objName = "Purple Key";

	public OBJ_Key_Purple(GamePanel gp) {	
		super(gp);
		this.gp = gp;
		
		type = type_consumable;
		name = objName;
		down1 = setup("/objects/key_chest_purple", gp.tileSize, gp.tileSize);
		description = "[" + name + "]\nA key that opens a\nspecific door.";
		price = 500;
		stackable = true;
		
		setDialogue();
	}
	public void setDialogue() {
		dialogues[0][0] = "You used the " + name + " and opened\nthe door.";
		
		dialogues[1][0] = "What are you doing?";
	}
	
	public boolean use(Entity entity) {
		
		int objIndex = getDetected(entity, gp.obj, "Door_Purple");
		
		if(objIndex != 999) {
			startDialogue(this, 0);
			gp.playSE(3);
			gp.obj[gp.currentMap][objIndex] = null;
			
			 // Reset dialogue index if used for progression somewhere
            if (dialogueIndex >= dialogues[dialogueSet].length) {
                dialogueIndex = 0;
            }
            // Reset currentDialogue and combined text if your dialogue system uses them
            gp.ui.currentDialogue = "";

            // Ensure game state is set to dialogue state for next dialogues to function
            gp.gameState = gp.dialogueState;
            
			
			return true;
		}
		else {
	        if (dialogues.length > 1 && dialogues[1].length > 0) {
	            startDialogue(this, 1); // Dialogue for not being near a door
	        } else {
	            System.out.println("Dialogue not set up correctly for the key.");
	        }
			return false;
		}
	}
		
}


