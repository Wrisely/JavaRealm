package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Lantern extends Entity{

	public static final String objName = "Lantern";
	
	public OBJ_Lantern(GamePanel gp) {
		super(gp);
		
		type = type_light;
		name = objName;
		down1 = setup("/objects/lantern", gp.tileSize, gp.tileSize);
		description = "[" + name + "]\nLet there be light.";
		price = 200;
		lightRadius = 250;		
	}
}
