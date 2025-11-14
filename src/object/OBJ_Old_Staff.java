package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Old_Staff extends Entity {

	public static final String objName = "Old Staff";
	
	public OBJ_Old_Staff(GamePanel gp) {
		super(gp);

		type = type_weapon;
		name = objName;
		down1 = setup("/objects/old_staff", gp.tileSize, gp.tileSize);
		
		attackValue = 2;
		knowledgeValue = 10;
		hpValue = 2;
		price = 100;
		durability = 10;
		
		description = "[" + name + "]\nAn ancient staff that\nholds forgotten wisdom.";
		
	}
}
