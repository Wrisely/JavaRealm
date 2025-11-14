package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Shield_Blue extends Entity{
	
	public static final String objName = "Blue Shield";
	
	public OBJ_Shield_Blue(GamePanel gp) {
		super(gp);
	
		type = type_shield; //5
		name = objName;
		down1 = setup("/objects/shield_blue", gp.tileSize, gp.tileSize);
		
		defenseValue = 6;
		knowledgeValue = 4;
		hpValue = 5;
		
		description = "[" + name + "]\nScrapped shield with\nbetter defense.";
		price = 100;
	}
}