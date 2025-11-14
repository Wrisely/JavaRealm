package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import trial2dgame.GamePanel;
import trial2dgame.UtilityTool;

public class TileManager {

	GamePanel gp;
	public Tile[] tile;
	public int mapTileNum[][][];
	boolean drawPath = false;	// TO SEE PATHFINDER
	ArrayList<String> fileNames = new ArrayList<>();
	ArrayList<String> collisionStatus = new ArrayList<>();

	
	public TileManager(GamePanel gp) {

		this.gp = gp;
		
		// READ TILE DATA FILE
		InputStream is = getClass().getResourceAsStream("/maps/tileDataCollision.txt"); //collision data
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		// GETTING TILE NAMES AND COLLISION INFO FROM THE FILE
		String line;
		
		try {
			while((line = br.readLine()) != null) {
				fileNames.add(line);
				collisionStatus.add(br.readLine());
			}
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// INITIALIZE THE TILE ARRAY BASED ON THE fileNames size
		tile = new Tile[fileNames.size()];
		getTileImage();

		// GET THE maxWorldCol & Row
		is = getClass().getResourceAsStream("/maps/GrasslandMap.txt"); // map tiles numbers data
		br = new BufferedReader(new InputStreamReader(is));
		
		try {
			String line2 = br.readLine();
			String maxTile[] = line2.split(" ");
			
			gp.maxWorldCol = maxTile.length;
			gp.maxWorldRow = maxTile.length;
			mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

			br.close();
			
		} catch(IOException e) {
			System.out.println("Exception!");
		}
		
		loadMap("/maps/GrasslandMap.txt",0);	// initial
		loadMap("/maps/GrasslandMap.txt",1);	// grassland
		loadMap("/maps/GrasslandMap.txt",2);	// lava
		loadMap("/maps/GrasslandMap.txt",3);	// frozen
		loadMap("/maps/GrasslandMap.txt",4);	// future
		loadMap("/maps/GrasslandMap.txt",5);	// time
		loadMap("/maps/GrasslandMap.txt",6);	// store
		loadMap("/maps/GrasslandMap.txt",7);	// final
		
//		loadMap("/maps/Beach (2).txt",0); //beachside
//		loadMap("/maps/House.txt",1); //inside house
//		loadMap("/maps/Library.txt",2); //library
//		loadMap("/maps/trialRoom.txt",3); //dorm room

		
	}

	public void getTileImage() {
		
		for(int i = 0; i < fileNames.size(); i++) {
			
			String fileName;
			boolean collision;
			
			// GET A FILE NAME
			fileName = fileNames.get(i);
			
			// GET A COLLISION STATUS
			if(collisionStatus.get(i).equals("true")) {
				collision = true;
			}
			else {
				collision = false;
			}
			
			setup(i, fileName, collision);
		}
	}

	public void setup(int index, String imageName, boolean collision) {
		
		UtilityTool uTool = new UtilityTool();
		
		 try {
	           tile[index] = new Tile();
	           InputStream imageStream = getClass().getResourceAsStream("/tiles/" + imageName);
	           if (imageStream == null) {
	               System.out.println("Image not found: /tiles/" + imageName);
	               return; // Skip this tile if the image is not found
	           }
	           tile[index].image = ImageIO.read(imageStream);
	           tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
	           tile[index].collision = collision; 
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
	   }
	
	public void loadMap(String filePath, int map) {
		try {

			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			int col = 0;
			int row = 0;

			while (col < gp.maxWorldCol && row < gp.maxWorldRow) {

				String line = br.readLine();

				while (col < gp.maxWorldCol) {

					String numbers[] = line.split(" ");

					int num = Integer.parseInt(numbers[col]);

					mapTileNum[map][col][row] = num;
					col++;
				}
				if (col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g2) {

		int worldCol = 0;
		int worldRow = 0;

		while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

			int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];

			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;

			//ONLY DRAW TILES THAT ARE ON-SCREEN
			if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX && 
				worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
				worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
				worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

				g2.drawImage(tile[tileNum].image, screenX, screenY, null);

			}

			worldCol++;

			if (worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;

			}

		}
		
		if(drawPath == true) {	// debug color red pathfinder
			g2.setColor(new Color(255, 0, 0, 70));
			
			for(int i = 0; i < gp.pFinder.pathList.size(); i++) {
				
				int worldX = gp.pFinder.pathList.get(i).col * gp.tileSize;
				int worldY = gp.pFinder.pathList.get(i).row * gp.tileSize;
				int screenX = worldX - gp.player.worldX + gp.player.screenX;
				int screenY = worldY - gp.player.worldY + gp.player.screenY;

				g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
			}
		}
		
	}

}