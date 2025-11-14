package object;



import java.util.Random;

import entity.Entity;
import entity.Player;
import trial2dgame.GamePanel;

public class OBJ_Coin extends Entity{

	GamePanel gp;
	public static final String objName = "Coin";
    
	public OBJ_Coin(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		
		type = type_pickupOnly; // 7
		name = objName;
		value = 1;
		down1 = setup("/objects/coin", gp.tileSize, gp.tileSize);

	}
	
	@Override
    public boolean use(Entity entity) {
        // Coins are not "used", but picked up immediately — so this can be empty
        if (entity instanceof Player) {
            Player player = (Player) entity; // Make sure the entity is a player
            
            int coinValue = 50 + new Random().nextInt(51);
            player.coin += coinValue;
            gp.playSE(1);
            gp.ui.addMessage("Picked up " + coinValue + " coins!");
        }
        
        return true;
	
	}

}
