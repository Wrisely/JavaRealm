package object;

import entity.Entity;
import trial2dgame.GamePanel;

public class OBJ_Equation_Of_Ruin extends Entity {

	public static final String objName = "The Equation of Ruin";
	
    public OBJ_Equation_Of_Ruin(GamePanel gp) {
        super(gp);

        name = objName;
        down1 = setup("/objects/equation_of_ruin", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nA mysterious book\nshrouded in dark secrets.";
    }
}
