package object;
import entity.Entity;
import entity.Player;
import trial2dgame.GamePanel;

public class OBJ_Scroll_Violet extends Entity {
	
	GamePanel gp;
	public static final String objName = "Scroll";
	
    public OBJ_Scroll_Violet(GamePanel gp) {
        super(gp);
        this.gp = gp;
        
        type = type_consumable;
        name = objName;
        down1 = setup("/objects/scroll_violet", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nA mystical scroll that\ncontains math knowledge.";
       
        price = 150; // Set a price for the scroll if needed
        knowledge = 2;
        stackable = true;
        
        setDialogue();
    }
    
    public void setDialogue() {
		
    	dialogues[0][0] = "You read a " + name + " and increased your\nknowledge by " + knowledge + ".";
		
	}
    
    public boolean use(Entity entity) {
        startDialogue(this, 0);
        if (entity instanceof Player) {
            ((Player) entity).baseKnowledge += knowledge;
        }
        gp.playSE(2);
        return true;
    }
}