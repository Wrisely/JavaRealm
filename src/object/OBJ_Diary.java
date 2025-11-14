package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Diary extends Entity {

	public static final String objName = "Diary";
	
    public OBJ_Diary(GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/diary", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nSomeone’s personal journey.";
    }
}
