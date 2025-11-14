package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Fractal_Book extends Entity {

	public static final String objName = "Fractal Scroll";
	
    public OBJ_Fractal_Book (GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/fractal_scroll", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nSummons illusions.";
    }
}
