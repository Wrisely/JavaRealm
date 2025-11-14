package trial2dgame;

import entity.Entity;

public class EventHandler {
	
	GamePanel gp;
	EventRect eventRect[][][];
	Entity eventMaster;
	
	int previousEventX, previousEventY;
	boolean canTouchEvent = true;
	int tempMap, tempCol, tempRow;
	
	public EventHandler(GamePanel gp) {
		this.gp = gp;
		
		eventMaster = new Entity(gp);
		
		eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
		
		int map = 0;
		int col = 0;
		int row = 0;
		while(map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow) {
			
			eventRect[map][col][row] = new EventRect();
			eventRect[map][col][row].x = 23;
			eventRect[map][col][row].y = 23;
			eventRect[map][col][row].width = 2;
			eventRect[map][col][row].height = 2;
			eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
			eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;
		
			col++;
			if(col == gp.maxWorldCol) {
				col = 0;
				row++;
				
				if(row == gp.maxWorldRow) {
					row = 0;
					map++;
				}
			}
			
		}
		setDialogue();		
	}	
		
	public void setDialogue() {
		
		eventMaster.dialogues[0][0] = "You fell into a pit!";

		eventMaster.dialogues[1][0] = "You drink the water. Your life recovered.\n(The progress has been saved)";
	}
	
	public void checkEvent () {
		
		// CHECK IF THE MC IS MORE THAN 1 TILE AWAY FROM LAST EVENT
		int xDistance = Math.abs(gp.player.worldX - previousEventX); //ABSOLUTE NUM
		int yDistance = Math.abs(gp.player.worldY - previousEventY);
		int distance = Math.max(xDistance,  yDistance);
		if(distance > gp.tileSize) {
			canTouchEvent = true;
		}
		
		if(canTouchEvent == true) { //map,x,y,direction
//			if(hit(0,15,8,"any") == true) {
//				teleport(0,30,15);
//			}
//			
//			else if(hit(0,35,18,"any") == true) {
//				teleport(0,3,28);
//			}
//			
//			else if(hit(0,4,38,"any") == true) {
//				teleport(1,25,37);
//			}
//			
//			else if(hit(0,12,8,"right") == true) {
//				damagePit(gp.dialogueState);
//			}
//			
//			else if(hit(0,18,20,"any") == true) {
//				healingPool(gp.dialogueState);
//			}
//			
//			else if(hit(0,38,1,"any") == true) {// teleport to house
//				teleport(1,25,37);
//			} 
//			
//			else if(hit(1,25,37,"any") == true) {// teleport to beach side
//				teleport(0,38,1);
//			} 
//			
//			else if(hit(1,25,33,"up") == true) {// [in which map][npc Number[i] in AssetSetter]
//				speak(gp.npc[1][0]);	// to access trade in front of table
//			}
//			else if (hit(2, 40, 16, "any") == true) { //Boss interaction
//	            teleport(2, 9, 44);
//	        }
//			else if (hit(2, 9, 44, "any") == true) { //back to library
//	            teleport(2, 40, 16);
//	        }
//			else if (hit(2, 40, 22, "any") == true) { //library to beach
//	            teleport(0, 1, 26);
//	        }
//			else if (hit(0, 6, 26, "any") == true) { //beach to library
//	            teleport(2, 33, 22);
//	        }
//			else if (hit(2, 32, 23, "any") == true || hit(2, 33, 23, "any") == true) { //library to trial dorm room
//	            teleport(3, 25, 23);
//	        }
//			else if (hit(3, 25, 22, "any") == true ) { //trial dorm room to library
//	            teleport(2, 33, 22);
//	        }
		} 
	}
	
	public boolean hit (int map, int col, int row, String reqDirection) {
			
		boolean hit = false;

		if(map == gp.currentMap) { //cannot hit event in other maps
			gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
			gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
			eventRect[map][col][row].x = col * gp.tileSize + eventRect[map][col][row].x;
			eventRect[map][col][row].y = row * gp.tileSize + eventRect[map][col][row].y;
							
			if (gp.player.solidArea.intersects(eventRect[map][col][row]) && eventRect[map][col][row].eventDone == false) {
			   if(gp.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")) {
			        hit = true;
			        
			        previousEventX = gp.player.worldX;
			        previousEventY = gp.player.worldY;
			        
			    }
			}
				
			gp.player.solidArea.x = gp.player.solidAreaDefaultX;
			gp.player.solidArea.y = gp.player.solidAreaDefaultY;
			eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
			eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;					
		}
			
		return hit;
	}	
	//40,13
	public void teleport(int map, int col, int row) {
		
		gp.gameState = gp.transitionState;
		tempMap = map;
		tempCol = col;
		tempRow = row;
		canTouchEvent = false;
		gp.playSE(2);
	}
	
	public void damagePit(int gameState) {		
		
		gp.gameState = gameState;
		gp.playSE(6);
		eventMaster.startDialogue(eventMaster, 0); // (eventMaster, dialogue set dialogues[this one][])
		gp.player.life -= 1;
//		eventRect[col][row].eventDone = true;
		canTouchEvent = false; // LET'S MC MOVE AWAY 1 TILE AFTER TRIGGER
//		gp.playSE(11);
	}
	
	public void healingPool(int gameState) { // gp.saveLoad.save(); // TO SAVE PROGRESS
	
		if(gp.keyH.enterPressed == true) {
			gp.gameState = gameState;
			gp.player.attackCanceled = true;
			gp.playSE(2);
			eventMaster.startDialogue(eventMaster, 1);
			gp.player.life = gp.player.getMaxLife();
			gp.aSetter.setMonster(); //respawn monster
			gp.saveLoad.save(); // TO SAVE PROGRESS
		}
	}
	
	public void speak(Entity entity) {
		
		if(gp.keyH.enterPressed == true) {
			gp.gameState = gp.dialogueState;
			gp.player.attackCanceled = true;
			entity.speak();
		}
	}
	
}

	


