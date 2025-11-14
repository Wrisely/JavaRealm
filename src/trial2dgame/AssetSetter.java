package trial2dgame;

import entity.Entity;
import entity.NPC_Merchant;
import entity.NPC_OldMan;
import monster.MON_GreenSlime;
import monster.MON_LHound;
import object.*;

import java.util.Random;
import java.awt.Point;
import java.util.HashSet;

public class AssetSetter {

    GamePanel gp;
    public int currentWave = 0;
    private final int totalWaves = 3;
    public boolean allMonstersDefeated = false;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    private void addObject(int mapNum, int index, Entity obj, int tileX, int tileY) {
        gp.obj[mapNum][index] = obj;
        gp.obj[mapNum][index].worldX = tileX * gp.tileSize;
        gp.obj[mapNum][index].worldY = tileY * gp.tileSize;
    }

    private void addNPC(int mapNum, int index, Entity npc, int tileX, int tileY) {
        gp.npc[mapNum][index] = npc;
        gp.npc[mapNum][index].worldX = tileX * gp.tileSize;
        gp.npc[mapNum][index].worldY = tileY * gp.tileSize;
    }

    private void addMonster(int mapNum, int index, Entity monster, int tileX, int tileY) {
        gp.monster[mapNum][index] = monster;
        gp.monster[mapNum][index].worldX = tileX * gp.tileSize;
        gp.monster[mapNum][index].worldY = tileY * gp.tileSize;
    }

    // RANDOM MONSTER SPAWNER (used for map 1+ waves and helper for map0 mass spawn)
    public void spawnRandomMonsters(int mapNum, Class<? extends Entity> monsterClass, int count) {
        Random rand = new Random();
        HashSet<Point> usedTiles = new HashSet<>();

        for (int i = 0; i < count; ) {
            int randomTileX = rand.nextInt(Math.max(1, gp.maxWorldCol));
            int randomTileY = rand.nextInt(Math.max(1, gp.maxWorldRow));
            Point pos = new Point(randomTileX, randomTileY);

            if (usedTiles.contains(pos)) continue;
            boolean blocked = gp.tileM.tile[gp.tileM.mapTileNum[mapNum][randomTileX][randomTileY]].collision;
            if (blocked) continue;

            Entity monster = null;
            try {
                monster = monsterClass.getConstructor(GamePanel.class).newInstance(gp);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            for (int j = 0; j < gp.monster[mapNum].length; j++) {
                if (gp.monster[mapNum][j] == null) {
                    addMonster(mapNum, j, monster, randomTileX, randomTileY);
                    usedTiles.add(pos);
                    i++;
                    break;
                }
            }
        }
    }

    // OBJECTS (unchanged)
    public void setObject() {
        // keep your object setup code
    }

    // NPC (unchanged)
    public void setNPC() {
        int mapNum = 0, i = 0;
        addNPC(mapNum, i++, new NPC_OldMan(gp), 25, 14);

        mapNum = 1;
        i = 0;
        addNPC(mapNum, i++, new NPC_Merchant(gp), 25, 31);
    }

    // MONSTER WAVES / INITIAL SPAWN
    // For map 0: spawn one slime per question (no respawn)
    public void setMonster() {
        currentWave = 0; // reset for new map

        // Map 0: spawn all map0 slimes (one per question)
        if (gp.map0JSON == null || gp.map0QuestionsOrder == null || gp.map0QuestionsOrder.length == 0) {
            // ensure JSON is loaded
            gp.loadMap0JSON("res/data/all_question_chap1.json");
            gp.shuffleQuestions();
        }

        if (gp.map0JSON != null && gp.map0JSON.size() > 0) {
            spawnAllMap0Slimes();
        }

        // For other maps/waves, fallback to original wave behavior if desired
        // Start wave 1 for map 1+ (keeps existing behavior)
        // Example: start waves for map 1 only
        // startNextWave(1);
    }

    // Spawn one slime for each map0 question, assign answerIndex and mark assigned
    private void spawnAllMap0Slimes() {
        Random rand = new Random();
        HashSet<Point> usedTiles = new HashSet<>();

        for (int k = 0; k < gp.map0QuestionsOrder.length; k++) {
            int qidx = gp.map0QuestionsOrder[k];
            if (qidx < 0 || qidx >= gp.map0JSON.size()) continue;
            if (gp.questionCompleted[qidx]) continue; // skip already completed if any

            // try to create and place slime
            Entity monster = null;
            try {
                monster = MON_GreenSlime.class.getConstructor(GamePanel.class).newInstance(gp);
                ((MON_GreenSlime) monster).answerIndex = qidx;
                gp.markQuestionAssigned(qidx);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            boolean placed = false;
            // attempt a number of times to find a non-blocked tile
            for (int attempt = 0; attempt < 200 && !placed; attempt++) {
                int randomTileX = rand.nextInt(Math.max(1, gp.maxWorldCol));
                int randomTileY = rand.nextInt(Math.max(1, gp.maxWorldRow));
                Point pos = new Point(randomTileX, randomTileY);
                if (usedTiles.contains(pos)) continue;
                boolean blocked = gp.tileM.tile[gp.tileM.mapTileNum[0][randomTileX][randomTileY]].collision;
                if (blocked) continue;

                // find empty monster slot
                for (int j = 0; j < gp.monster[0].length; j++) {
                    if (gp.monster[0][j] == null) {
                        addMonster(0, j, monster, randomTileX, randomTileY);
                        usedTiles.add(pos);
                        placed = true;
                        break;
                    }
                }
            }
            if (!placed) {
                // if couldn't place, unassign to avoid dangling questionAssigned
                gp.markQuestionUnassigned(qidx);
            }
        }
        // All slimes for map0 have been attempted to spawn; UI will show only assigned ones
    }

    // The original wave mechanism retained for other maps
    public void startNextWave(int mapNum) {
        if (currentWave >= totalWaves) return;
        currentWave++;
        int monsterCount = 0;
        Class<? extends Entity> monsterType = null;

        if (mapNum == 1) {
            monsterType = MON_LHound.class;
            switch (currentWave) {
                case 1 -> monsterCount = 3;
                case 2 -> monsterCount = 4;
                case 3 -> monsterCount = 5;
            }
        }

        if (monsterType != null && monsterCount > 0) {
            spawnRandomMonsters(mapNum, monsterType, monsterCount);
            allMonstersDefeated = false;
        }
    }
}