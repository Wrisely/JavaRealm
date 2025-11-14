package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Multiplication_Book extends Entity {

	public static final String objName = "Multiplication Scroll";
	
    public OBJ_Multiplication_Book(GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/multiplication_scroll", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nEnhances attack spells.";
    }
}
