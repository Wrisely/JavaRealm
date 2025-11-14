package data;

import java.io.Serializable;
import java.util.ArrayList;

public class DataStorage implements Serializable{

	// PLAYER STATS
	int level;
	int maxLife;
	int life;
	int exp;
	int nextLevelExp;
	int coin;
	int knowledge;
	
	// PLAYER INVENTORY
	ArrayList<String> itemNames = new ArrayList<>();
	ArrayList<Integer> itemAmounts = new ArrayList<>();
    ArrayList<Integer> itemDurabilities = new ArrayList<>(); // New list for durability
	int currentWeaponSlot;
	int currentShieldSlot;
	
	// OBJECT ON MAP
	String mapObjectNames[][];
	int mapObjectWorldX[][];
	int mapObjectWorldY[][];
	String mapObjectLootNames[][];
	boolean mapObjectOpened[][];
}