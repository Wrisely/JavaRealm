package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Mysterious_Staff extends Entity {

	public static final String objName = "Mysterious Staff";
	
	public OBJ_Mysterious_Staff(GamePanel gp) {
		super(gp);

		type = type_weapon;
		name = objName;
		down1 = setup("/objects/mysterious_staff", gp.tileSize, gp.tileSize);
		
		attackValue = 10;
		knowledgeValue = 20;
		hpValue = 10;
		price = 200;
		durability = 25;
		
		description = "[" + name + "]\nA staff shrouded in mystery\nwith untold powers.";
		
	}
}
