package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Wooden_Staff extends Entity {

	public static final String objName = "Wooden Staff";
	
	public OBJ_Wooden_Staff(GamePanel gp) {
		super(gp);

		type = type_weapon;
		name = objName;
		down1 = setup("/objects/wooden_staff", gp.tileSize, gp.tileSize);
		
		attackValue = 50;
		knowledgeValue = 5;
		hpValue = 3;
		price = 50;
		durability = 5;
		
		description = "[" + name + "]\nA basic wooden staff\nwith modest abilities.";
	}
}
