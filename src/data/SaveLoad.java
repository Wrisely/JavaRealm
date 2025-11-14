package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import entity.Entity;
import object.OBJ_Chest;
import trial2dgame.GamePanel;

public class SaveLoad {

	GamePanel gp;
	
	public SaveLoad(GamePanel gp) {
		this.gp = gp;
	}
	
	public void save() {
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));
			
			DataStorage ds = new DataStorage();
			
			// PLAYER STATS
			ds.level = gp.player.level;
			ds.maxLife = gp.player.maxLife;
			ds.life = gp.player.life;
			ds.exp = gp.player.exp;
			ds.nextLevelExp = gp.player.nextLevelExp;
			ds.coin = gp.player.coin;
			ds.knowledge = gp.player.knowledge;
		
			// PLAYER INVENTORY
			for(int i = 0; i < gp.player.inventory.size(); i++) {
				ds.itemNames.add(gp.player.inventory.get(i).name);
				ds.itemAmounts.add(gp.player.inventory.get(i).amount);
	            ds.itemDurabilities.add(gp.player.inventory.get(i).durability); // Save durability
			}
			
			// PLAYER EQUIPMENT
			ds.currentWeaponSlot = gp.player.getCurrentWeaponSlot();
			ds.currentShieldSlot = gp.player.getCurrentShieldSlot();
			
			// OBJECTS ON MAP
			ds.mapObjectNames = new String[gp.maxMap][gp.obj[1].length];
			ds.mapObjectWorldX = new int[gp.maxMap][gp.obj[1].length];
			ds.mapObjectWorldY = new int[gp.maxMap][gp.obj[1].length];
			ds.mapObjectLootNames = new String[gp.maxMap][gp.obj[1].length];			
			ds.mapObjectOpened = new boolean[gp.maxMap][gp.obj[1].length];

			for(int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
				
				for(int i = 0; i < gp.obj[1].length; i++) {
					
					if(gp.obj[mapNum][i] == null) {
						ds.mapObjectNames[mapNum][i] = "NA";
					}
					else {
						ds.mapObjectNames[mapNum][i] = gp.obj[mapNum][i].name;
						ds.mapObjectWorldX[mapNum][i] = gp.obj[mapNum][i].worldX;
						ds.mapObjectWorldY[mapNum][i] = gp.obj[mapNum][i].worldY;
						if(gp.obj[mapNum][i].loot != null) {
							ds.mapObjectLootNames[mapNum][i] = gp.obj[mapNum][i].loot.name;
						}
						ds.mapObjectOpened[mapNum][i] = gp.obj[mapNum][i].opened;
					}
				}
			}
			
			// WRITE THE DataStorage object (ds)
			oos.writeObject(ds);
			
		}
		catch(Exception e) {
			System.out.println("Save Exception!");
		}
	}
	
	public void load() {
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));
		
			// READ THE DataStorage object (ds)
			DataStorage ds = (DataStorage)ois.readObject();
			
			gp.player.level = ds.level;
			gp.player.maxLife = ds.maxLife;
			gp.player.life = ds.life;
			gp.player.exp = ds.exp;
			gp.player.nextLevelExp = ds.nextLevelExp;
			gp.player.coin = ds.coin;
			gp.player.knowledge = ds.knowledge;

			
			// PLAYER INVENTORY
			gp.player.inventory.clear();
			for(int i = 0; i < ds.itemNames.size(); i++) {
		        Entity item = gp.eGenerator.getObject(ds.itemNames.get(i));
	            item.amount = ds.itemAmounts.get(i);
	            item.durability = ds.itemDurabilities.get(i); // Load durability
	            gp.player.inventory.add(item);
			}
			
			// PLAYER EQUIPMENT
			gp.player.currentWeapon = gp.player.inventory.get(ds.currentWeaponSlot);
			gp.player.currentShield = gp.player.inventory.get(ds.currentShieldSlot);
			gp.player.getAttack();
			gp.player.getDefense();
			gp.player.getPlayerAttackImage();
			
			// OBJECTS ON MAP
			for(int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
				
				for(int i = 0; i < gp.obj[1].length; i++) {
					
					if(ds.mapObjectNames[mapNum][i].equals("NA")) {
						gp.obj[mapNum][i] = null;
					}
					else {
						gp.obj[mapNum][i] = gp.eGenerator.getObject(ds.mapObjectNames[mapNum][i]);
						gp.obj[mapNum][i].worldX = ds.mapObjectWorldX[mapNum][i];
						gp.obj[mapNum][i].worldY = ds.mapObjectWorldY[mapNum][i];
						if(ds.mapObjectLootNames[mapNum][i] != null) {
							gp.obj[mapNum][i].loot = gp.eGenerator.getObject(ds.mapObjectLootNames[mapNum][i]);
						}
						gp.obj[mapNum][i].opened = ds.mapObjectOpened[mapNum][i];
						if (gp.obj[mapNum][i] instanceof OBJ_Chest) {
                            ((OBJ_Chest) gp.obj[mapNum][i]).setDialogue();
                        }
						if(gp.obj[mapNum][i].opened == true) {
						   gp.obj[mapNum][i].down1 = gp.obj[mapNum][i].image2;
						}
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("Load Exception!");
		}
		
	}
	
}
