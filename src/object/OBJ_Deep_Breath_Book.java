package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Deep_Breath_Book extends Entity {

	public static final String objName = "Scroll of Deep Breath";
	
    public OBJ_Deep_Breath_Book(GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/deep_breath_scroll", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nAncient spell scroll\ngrants underwater breathing.";
    }
}
