package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Prime_Book extends Entity {

	public static final String objName = "Prime Scroll";
	
    public OBJ_Prime_Book 	(GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/prime_scroll", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nSeals dark energy.";
    }
}
