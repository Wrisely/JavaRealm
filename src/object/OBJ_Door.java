package object;

import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Door extends Entity{

	GamePanel gp;
	public static final String objName = "Door";
	
	public OBJ_Door(GamePanel gp) {
		
		super(gp);
		this.gp = gp;
		
		type = type_obstacle;
		name = objName;
		down1 = setup("/objects/door_moss", gp.tileSize, gp.tileSize);
		collision = true;
		
		solidArea.x = 0; // SHOULD EQUAL TO 48
		solidArea.y = 30;
		solidArea.width = 48; 
		solidArea.height = 18;
		solidAreaDefaultX = solidArea.x; 
		solidAreaDefaultY = solidArea.y;
		
		setDialogue();
		
	}
	public void setDialogue() {
		
		dialogues[0][0] = "You need a key to open this door.";
		
	//	dialogues[1][0] = "There might be something useful around here...";

	}
	public void interact() {
		
		startDialogue(this, 0);
	
	}
	
}
