package object;

import entity.Entity;
import entity.Player;
import trial2dgame.GamePanel;

public class OBJ_Potion_Red extends Entity{

	GamePanel gp;
	public static final String objName = "HP Potion";
	
	public OBJ_Potion_Red(GamePanel gp) {
		super(gp);
	
		this.gp = gp;
		
		type = type_consumable; // 5
		name = objName;
		value = 5;
		down1 = setup("/objects/potion_red", gp.tileSize, gp.tileSize);
		description = "[" + name + "]\nRestores " + value + " health\npoints.";
		price = 25;
		stackable = true;
		
		setDialogue();
	}
	public void setDialogue() {
		
		dialogues[0][0] = "You drank a " + name + ".\nRecovered " + value + " HP.";
		
	}
	
	public boolean use(Entity entity) {
	    startDialogue(this, 0);
	    if (entity instanceof Player) {
	        Player player = (Player) entity;
	        
	        gp.player.life += value;
	        
	        if (gp.player.life > gp.player.getMaxLife()) {
	            gp.player.life = gp.player.getMaxLife();
	        }
	    }
	    gp.playSE(2);
	    return true;
	}
}
