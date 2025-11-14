package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Formula_Book extends Entity {

	public static final String objName = "Formula Scroll";
	
    public OBJ_Formula_Book(GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/formula_scroll", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nA scroll of basic math.";
    }
}
