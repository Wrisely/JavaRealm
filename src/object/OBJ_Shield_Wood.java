package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Shield_Wood extends Entity{

	public static final String objName = "Wooden Shield";
	
	public OBJ_Shield_Wood(GamePanel gp) {
		super(gp);
	
		type = type_shield; //5
		name = objName;
		down1 = setup("/objects/shield_wood", gp.tileSize, gp.tileSize);
		
		defenseValue = 3;
		knowledgeValue = 1;
		hpValue = 2;
		
		price = 20;
		durability = 5;
		
		description = "[" + name + "]\nA shield to protect\nnoobs.";
	}
}
