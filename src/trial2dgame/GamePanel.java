package trial2dgame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;

import ai.PathFinder;
import data.SaveLoad;
import entity.Entity;
import entity.Player;
import environment.EnvironmentManager;
import tile.Map;
import tile.TileManager;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale - 1; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 px (48x16)
    public final int screenHeight = tileSize * maxScreenRow; // 576 px (48x12)
    
    public int[] map0QuestionsOrder;
    public int currentQuestionIndex = -1;
    public boolean[] questionCompleted;
    public String activeQuestionText = "";

    // whether a question is currently assigned to a slime on the map
    public boolean[] questionAssigned;

    // WORLD SETTINGS
    public int maxWorldCol;
    public int maxWorldRow;
    public final int maxMap = 10;
    public int currentMap = 0;

    // FPS
    int FPS = 60;

    // SYSTEM
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    Config config = new Config(this);
    public PathFinder pFinder = new PathFinder(this);
    public EnvironmentManager eManager = new EnvironmentManager(this);
    Map map = new Map(this);
    SaveLoad saveLoad = new SaveLoad(this);
    public EntityGenerator eGenerator = new EntityGenerator(this);
    Thread gameThread;
    public int currentMusicIndex;
    public boolean musicPlaying = true;
    

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public Entity obj[][] = new Entity[maxMap][20];
    public Entity npc[][] = new Entity[maxMap][10];
    public Entity monster[][] = new Entity[maxMap][20];
    ArrayList<Entity> entityList = new ArrayList<>();

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int optionsState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;
    public final int tradeState = 8;
    public final int mapState = 9;
    public final int battleState = 10;
        
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        pFinder = new PathFinder(this);
        pFinder.instantiateNodes();
    }

    // ===========================
    // Setup + Save
    // ===========================
    public void setupGame(boolean loadSave) {
        if (loadSave) {
            saveLoad.load();
        } else {
            aSetter.setObject();
            aSetter.setNPC();

            // Load map0 JSON and spawn all slimes for map 0 (one per question)
            loadMap0JSON("res/data/all_question_chap1.json");
            shuffleQuestions();
            aSetter.setMonster(); // now spawns all map0 slimes at once
        }

        // Start with the first question (only picks assigned ones if any)
        setNextQuestion();

        eManager.setup();
        gameState = titleState;
    }

    public void saveGame() {
        saveLoad.save();
        System.out.println("Game saved successfully.");
    }

    public void resetGame(boolean restart) {
        player.setDefaultPositions();
        player.restoreStatus();
        player.resetCounter();
        aSetter.setNPC();
        aSetter.setMonster();
        eManager.setup();
        if (restart) {
            player.setDefaultValues();
            aSetter.setObject();
            eManager.lighting.resetDay();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();

                if (gameState == titleState && musicPlaying) {
                    stopMusic();
                }

                repaint();
                delta--;
                drawCount++;
            }
            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            player.update();
            eHandler.checkEvent();

            for (int i = 0; i < npc[1].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }

            boolean allDead = true;
            for (int i = 0; i < monster[1].length; i++) {
                if (monster[currentMap][i] != null) {
                    if (monster[currentMap][i].alive && !monster[currentMap][i].dying) {
                        monster[currentMap][i].update();
                        allDead = false;
                    }
                    if (!monster[currentMap][i].alive) {
                        monster[currentMap][i] = null;
                    }
                }
            }

            if (allDead && !aSetter.allMonstersDefeated) {
                aSetter.allMonstersDefeated = true;
                aSetter.startNextWave(currentMap);
            }

            eManager.update();
        }

        if (gameState == pauseState) {
            // paused
        }

        if (gameState == dialogueState && keyH.enterPressed) {
            gameState = playState;
        }

        if (gameState == titleState && musicPlaying) {
            stopMusic();
        }

        keyH.enterPressed = false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            ui.draw(g2);
        } else if (gameState == mapState) {
            map.drawFullMapScreen(g2);
        } else {
            drawGameWorld(g2);
            map.drawMiniMap(g2);
            ui.draw(g2);
        }
        g.dispose();
    }

    // Draw Game World
    public void drawGameWorld(Graphics2D g2) {
        tileM.draw(g2);
        entityList.add(player);

        for (int i = 0; i < npc[1].length; i++) {
            if (npc[currentMap][i] != null) entityList.add(npc[currentMap][i]);
        }
        for (int i = 0; i < obj[1].length; i++) {
            if (obj[currentMap][i] != null) entityList.add(obj[currentMap][i]);
        }
        for (int i = 0; i < monster[1].length; i++) {
            if (monster[currentMap][i] != null) entityList.add(monster[currentMap][i]);
        }

        Collections.sort(entityList, (e1, e2) -> Integer.compare(e1.worldY, e2.worldY));
        for (Entity e : entityList) {
            if (e != null) {
                try {
                    e.draw((Graphics2D) g2);
                } catch (Exception ex) {
                    System.out.println("Error drawing entity: " + e + " -> " + ex.getMessage());
                    g2.setColor(Color.RED);
                    g2.fillRect(e.worldX, e.worldY, e.solidArea.width, e.solidArea.height);
                }
            }
        }
        entityList.clear();
        if (eManager != null) eManager.draw(g2);
    }

    public void playMusic(int i) {
        if (!musicPlaying) {
            currentMusicIndex = i;
            music.setFile(i);
            music.setVolume(-30.0f);
            music.play();
            music.loop();
            musicPlaying = true;
        }
    }

    public void stopMusic() {
        music.stop();
        musicPlaying = false;
    }

    public void playSE(int i) {
        se.setFile(i);
        int adjustment = 0;
        switch (i) {
            case 1 -> adjustment = -5;
            case 5 -> adjustment = -30;
            case 6 -> adjustment = -10;
            case 7 -> adjustment = +6;
            default -> adjustment = 0;
        }
        se.setVolumeByScaleAndAdjustment(adjustment);
        se.play();
    }

    // JSON storage for map0 questions
    public JSONArray map0JSON; // store all questions as a JSONArray

    // loadMap0JSON: tries to find the JSON file, accepts object with "monsters" array
    public void loadMap0JSON(String filePath) {
        JSONParser parser = new JSONParser();
        JSONArray sourceArray = null;

        String userDir = System.getProperty("user.dir");
        String[] candidates = new String[] {
            filePath,
            "res/data/all_question_chap1.json",
            "res/data/all_questions_chap1.json",
            "res/data/questions.json",
            "all_question_chap1.json",
            "all_questions_chap1.json",
            userDir + File.separator + "res" + File.separator + "data" + File.separator + "all_question_chap1.json"
        };

        for (String candidate : candidates) {
            try {
                Reader reader = null;
                File f = new File(candidate);
                if (f.exists()) {
                    reader = new FileReader(f);
                    System.out.println("loadMap0JSON: using filesystem path: " + f.getAbsolutePath());
                } else {
                    String resourcePath = candidate.startsWith("res/") ? "/" + candidate : "/" + candidate;
                    InputStream is = getClass().getResourceAsStream(resourcePath);
                    if (is == null) {
                        String alt = resourcePath.replaceFirst("^/res/", "/");
                        is = getClass().getResourceAsStream(alt);
                    }
                    if (is != null) {
                        reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                        System.out.println("loadMap0JSON: using classpath resource: " + resourcePath);
                    }
                }

                if (reader != null) {
                    Object parsed = parser.parse(reader);
                    if (parsed instanceof JSONArray) {
                        sourceArray = (JSONArray) parsed;
                    } else if (parsed instanceof JSONObject) {
                        JSONObject root = (JSONObject) parsed;
                        if (root.get("monsters") instanceof JSONArray) {
                            sourceArray = (JSONArray) root.get("monsters");
                        } else if (root.get("questions") instanceof JSONArray) {
                            sourceArray = (JSONArray) root.get("questions");
                        } else {
                            for (Object v : root.values()) {
                                if (v instanceof JSONArray) {
                                    sourceArray = (JSONArray) v;
                                    break;
                                }
                            }
                        }
                    }
                    reader.close();
                }
            } catch (IOException | ParseException e) {
                // try next
            }

            if (sourceArray != null) break;
        }

        if (sourceArray == null) {
            System.err.println("loadMap0JSON: no JSON file found among candidates; map0 arrays initialized empty.");
            map0JSON = new JSONArray();
            map0QuestionsOrder = new int[0];
            questionCompleted = new boolean[0];
            questionAssigned = new boolean[0];
            return;
        }

        map0JSON = new JSONArray();
        for (Object obj : sourceArray) {
            if (!(obj instanceof JSONObject)) continue;
            JSONObject q = (JSONObject) obj;
            Object mapVal = q.get("map");
            if (mapVal == null) {
                map0JSON.add(q);
            } else {
                long mapNum = 0;
                try {
                    if (mapVal instanceof Number) {
                        mapNum = ((Number) mapVal).longValue();
                    } else {
                        mapNum = Long.parseLong(mapVal.toString());
                    }
                } catch (Exception ex) {
                    mapNum = 0;
                }
                if (mapNum == 0) map0JSON.add(q);
            }
        }

        int size = map0JSON.size();
        map0QuestionsOrder = new int[size];
        questionCompleted = new boolean[size];
        questionAssigned = new boolean[size];
        for (int i = 0; i < size; i++) {
            map0QuestionsOrder[i] = i;
            questionCompleted[i] = false;
            questionAssigned[i] = false;
        }
        System.out.println("Loaded " + size + " questions for map 0.");
    }

    // shuffle the question order (no-op for <=1)
    public void shuffleQuestions() {
        if (map0QuestionsOrder == null || map0QuestionsOrder.length <= 1) return;
        for (int i = 0; i < map0QuestionsOrder.length; i++) {
            int rand = (int)(Math.random() * map0QuestionsOrder.length);
            int temp = map0QuestionsOrder[i];
            map0QuestionsOrder[i] = map0QuestionsOrder[rand];
            map0QuestionsOrder[rand] = temp;
        }
    }

    // set the next active question (only picks assigned & not completed)
    public void setNextQuestion() {
        currentQuestionIndex = -1;
        activeQuestionText = "";
        ui.showQuestion = false;
        ui.questionText = "";

        if (map0QuestionsOrder == null || map0QuestionsOrder.length == 0) {
            return;
        }

        for (int i = 0; i < map0QuestionsOrder.length; i++) {
            int idx = map0QuestionsOrder[i];
            if (idx < 0 || idx >= questionCompleted.length) continue;
            if (!questionCompleted[idx] && questionAssigned[idx]) {
                currentQuestionIndex = idx;
                JSONObject questionObj = (JSONObject) map0JSON.get(idx);
                activeQuestionText = (String) questionObj.get("question");
                ui.showQuestion = true;
                ui.questionText = activeQuestionText;
                break;
            }
        }
    }

    // mark assigned/unassigned
    public void markQuestionAssigned(int idx) {
        if (idx >= 0 && idx < questionAssigned.length) {
            questionAssigned[idx] = true;
            if (currentQuestionIndex == -1) setNextQuestion();
        }
    }
    public void markQuestionUnassigned(int idx) {
        if (idx >= 0 && idx < questionAssigned.length) {
            questionAssigned[idx] = false;
            if (currentQuestionIndex == idx) setNextQuestion();
        }
    }

    // Cycle through assigned & not-completed questions: delta = +1 (next) or -1 (prev)
    public void cycleAssignedQuestion(int delta) {
        if (map0QuestionsOrder == null || map0QuestionsOrder.length == 0) return;
        // build list of active indices
        ArrayList<Integer> active = new ArrayList<>();
        for (int i = 0; i < map0QuestionsOrder.length; i++) {
            int idx = map0QuestionsOrder[i];
            if (!questionCompleted[idx] && questionAssigned[idx]) active.add(idx);
        }
        if (active.isEmpty()) {
            currentQuestionIndex = -1;
            ui.showQuestion = false;
            ui.questionText = "";
            return;
        }
        // find current position
        int pos = 0;
        if (currentQuestionIndex >= 0) {
            pos = active.indexOf(currentQuestionIndex);
            if (pos == -1) pos = 0;
        }
        pos = (pos + delta) % active.size();
        if (pos < 0) pos += active.size();
        currentQuestionIndex = active.get(pos);
        JSONObject questionObj = (JSONObject) map0JSON.get(currentQuestionIndex);
        activeQuestionText = (String) questionObj.get("question");
        ui.showQuestion = true;
        ui.questionText = activeQuestionText;
    }
}