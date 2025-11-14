package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Exponential_Book extends Entity {

	public static final String objName = "Exponential Scroll";
	
    public OBJ_Exponential_Book(GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/exponential_scroll", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nUnleashes powerful attack.";
    }
}
