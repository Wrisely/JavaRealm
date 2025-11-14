package trial2dgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.Entity;
import object.OBJ_Coin;

/**
 * Full-file replacement for UI.java.
 * - Uses GamePanel as the source of questions (gp.map0JSON, gp.currentQuestionIndex).
 * - No dependency on quiz.Question or QuestionLoader.
 * - Displays permanent bottom-centered question box (when on map 0 and a question is active).
 * - Shows toggle hint "Use ← / → to toggle".
 * - Keeps the original UI features required by other systems.
 */
public class UI {

    GamePanel gp;
    Graphics2D g2;
    public Font maruMonica;
    Font poynterText;
    BufferedImage coin;
    public boolean messageOn = false;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();

    public boolean gameFinished = false;
    public boolean dialogueActive = false;
    public String currentDialogue = "";
    public int dialogueIndex = 0;
    public int dialogueSet = 0;

    public int commandNum = 0;
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;
    public int subState = 0;
    int counter = 0; // for transition
    public Entity npc;
    public boolean isBuying = false;
    private Random random = new Random();

    // Quiz/Battle feedback
    public String quizFeedback = ""; // "" means no feedback
    public long quizFeedbackTime = 0;
    public static final int FEEDBACK_DURATION = 2600; // milliseconds

    // Monster slain notification state
    public boolean monsterSlainNotification = false;
    public long monsterSlainTime = 0;
    public static final int MONSTER_SLAIN_DISPLAY_TIME = 1400; // ms

    // Minimal quiz structure used only to keep compatibility with other code
    public List<QuizQuestion> currentQuiz = new ArrayList<>();
    public int quizIndex = 0;

    // For battle UI rendering placeholders
    public BufferedImage playerBattleImage; // assign these from your player/monster art if needed
    public BufferedImage monsterBattleImage;
    public Entity currentBattleMonster;

    public boolean showSaving = false;
    public boolean showLoading = false;
    private long saveLoadMessageTime = 0;
    private final long SAVELOAD_MESSAGE_DURATION = 2000; // milliseconds

    // Permanent question box
    public boolean showQuestion = false; // UI-level override if needed
    public String questionText = "";

    public UI(GamePanel gp) {
        this.gp = gp;

        // No QuestionLoader used here; GamePanel loads map0 JSON centrally.
        // Prepare a placeholder coin image to avoid NPEs in trade UI
        coin = new OBJ_Coin(gp).down1;

        try {
            InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            if (is != null) maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
            else maruMonica = new Font("Arial", Font.PLAIN, 18);

            is = getClass().getResourceAsStream("/font/PoynterText Regular.ttf");
            if (is != null) poynterText = Font.createFont(Font.TRUETYPE_FONT, is);
            else poynterText = new Font("Arial", Font.PLAIN, 14);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            maruMonica = new Font("Arial", Font.PLAIN, 18);
            poynterText = new Font("Arial", Font.PLAIN, 14);
        }

        // Ensure currentQuiz non-null so other code that references it won't NPE
        currentQuiz = new ArrayList<>();
    }

    // Minimal inner quiz-question class to preserve compatibility
    public static class QuizQuestion {
        private String question;
        private List<String> choices;

        public QuizQuestion(String q, List<String> choices) {
            this.question = q;
            this.choices = choices;
        }

        public String getQuestion() { return question; }
        public List<String> getChoices() { return choices; }
    }

    // === Draw Monster HP Bar (helper) ===
    public void drawMonsterLifeBar2D(int x, int y, int width, int life, int maxLife, String name) {
        Graphics2D g2 = this.g2;
        g2.setColor(Color.gray);
        g2.fillRect(x, y, width, 20);

        double hpPercent = (double) life / Math.max(1, maxLife);
        int hpWidth = (int) (width * hpPercent);

        Color barColor = (hpPercent < 0.3) ? new Color(220, 40, 40) : new Color(80, 200, 80);
        g2.setColor(barColor);
        g2.fillRect(x, y, hpWidth, 20);

        g2.setColor(Color.black);
        g2.drawRect(x, y, width, 20);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        int textWidth = g2.getFontMetrics().stringWidth(name);
        int textX = x + (width - textWidth) / 2;
        int textY = y - 8;
        g2.setColor(Color.white);
        g2.drawString(name, textX, textY);
    }

    // === Draw Player HP Bar ===
    public void drawPlayerLife() {
        int offsetX = 40;
        int x = gp.tileSize / 2 + offsetX;
        int y = gp.tileSize / 2;

        int currentLife = Math.max(gp.player.life, 0);
        int maxLife = gp.player.getMaxLife();

        int barWidth = gp.tileSize * 8;
        int barHeight = 24;

        double oneScale = (double) barWidth / Math.max(1, maxLife);
        int hpBarValue = (int) (oneScale * currentLife);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(Color.white);
        g2.drawString("HP", x - 35, y + 20);

        g2.setColor(new Color(35, 35, 35));
        g2.fillRoundRect(x - 2, y - 2, barWidth + 4, barHeight + 4, 14, 14);

        Color hpColor;
        double hpPercent = (double) currentLife / Math.max(1, maxLife);
        if (hpPercent > 0.5) hpColor = new Color(0, 200, 0);
        else if (hpPercent > 0.25) hpColor = new Color(255, 200, 0);
        else hpColor = new Color(220, 0, 0);

        g2.setColor(hpColor);
        g2.fillRoundRect(x, y, hpBarValue, barHeight, 14, 14);

        String hpStatus = currentLife + " / " + maxLife;
        int textX = x + (barWidth / 2) - g2.getFontMetrics().stringWidth(hpStatus) / 2;
        int textY = y + barHeight - 5;
        g2.setColor(Color.white);
        g2.drawString(hpStatus, textX, textY);
    }

    public void addMessage(String text) {
        message.add(text);
        messageCounter.add(0);
    }

    public void drawMessage() {
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25F));

        for (int i = 0; i < message.size(); i++) {
            if (message.get(i) != null) {
                g2.setColor(Color.black);
                g2.drawString(message.get(i), messageX + 2, messageY + 2);
                g2.setColor(Color.white);
                g2.drawString(message.get(i), messageX, messageY);

                int counterVal = messageCounter.get(i) + 1;
                messageCounter.set(i, counterVal);
                messageY += 50;

                if (messageCounter.get(i) > 180) {
                    message.remove(i);
                    messageCounter.remove(i);
                    i--;
                }
            }
        }
    }

    // === Simplified Attack Logic (keeps compatibility) ===
    public void handleAttack() {
        Entity monster = getCurrentBattleMonster();
        if (monster == null) return;

        int damage = gp.player.getAttack();
        monster.life -= damage;
        addMessage("You hit the " + monster.name + "! HP -" + damage);
        gp.playSE(5);

        if (monster.life <= 0) {
            monster.life = 0;
            monster.dying = true;
            addMessage(monster.name + " defeated!");
            gp.playSE(7);

            if (gp.player.currentWeapon != null) {
                gp.player.currentWeapon.durability--;
                addMessage("[Weapon] Durability: " + Math.max(0, gp.player.currentWeapon.durability));
                if (gp.player.currentWeapon.durability <= 0) {
                    addMessage("Your weapon broke!");
                    gp.player.inventory.remove(gp.player.currentWeapon);
                    gp.player.currentWeapon = null;
                }
            }

            if (gp.player.currentShield != null) {
                gp.player.currentShield.durability--;
                addMessage("[Shield] Durability: " + Math.max(0, gp.player.currentShield.durability));
                if (gp.player.currentShield.durability <= 0) {
                    addMessage("Your shield broke!");
                    gp.player.inventory.remove(gp.player.currentShield);
                    gp.player.currentShield = null;
                }
            }

            monster.alive = false;
            monster.dying = true;
            gp.gameState = gp.playState;
        }
    }

    private Entity getCurrentBattleMonster() {
        if (currentBattleMonster != null && currentBattleMonster.alive && !currentBattleMonster.dying) return currentBattleMonster;
        for (Entity m : gp.monster[gp.currentMap]) {
            if (m != null && m.alive && !m.dying) return m;
        }
        return null;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        if (maruMonica != null) g2.setFont(maruMonica);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);

        if (gp.gameState == gp.titleState) drawTitleScreen();
        if (gp.gameState == gp.playState) { drawPlayerLife(); drawMessage(); }
        if (gp.gameState == gp.pauseState) { drawPlayerLife(); drawPauseScreen(); }
        if (gp.gameState == gp.dialogueState) { drawPlayerLife(); drawDialogueScreen(); }
        if (gp.gameState == gp.characterState) { drawCharacterScreen(); drawInventory(gp.player, true); }
        if (gp.gameState == gp.optionsState) drawOptionsScreen();
        if (gp.gameState == gp.gameOverState) drawGameOverScreen();
        if (gp.gameState == gp.transitionState) drawTransition();
        if (gp.gameState == gp.tradeState) drawTradeScreen();

        // Always draw question box if either UI requests it or GamePanel has an active question on map0
        if ((showQuestion || gp.currentQuestionIndex >= 0) && gp.currentMap == 0) {
            drawQuestionBox(g2);
        }
    }

    public void drawTitleScreen() {
        g2.setColor(Color.decode("#FFB3DE"));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 95F));
        String text = "Java Realms";
        int y = gp.tileSize * 4;

        for (String line : text.split("\n")) {
            int x = getXforCenteredText(line);
            g2.setColor(Color.decode("#802B00"));
            g2.drawString(line, x + 4, y + 4);
            g2.setColor(Color.white);
            g2.drawString(line, x, y);
            y += gp.tileSize * 2;
        }

        int NWidth = gp.tileSize * 2;
        int NHeight = gp.tileSize * 2;
        int NX = gp.screenWidth / 2 - NWidth / 2;
        int NY = y - gp.tileSize;
        g2.drawImage(gp.player.down1, NX, NY, NWidth, NHeight, null);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "NEW GAME";
        int x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if (commandNum == 0) g2.drawString(">", x - gp.tileSize, y);

        text = "LOAD GAME";
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 1) g2.drawString(">", x - gp.tileSize, y);

        text = "QUIT";
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 2) g2.drawString(">", x - gp.tileSize, y);
    }

    public void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = (gp.screenHeight / 2) + gp.tileSize / 2;
        g2.drawString(text, x, y);
    }

    // Draw the permanent question box (bottom centered) with Left/Right toggle hint
    public void drawQuestionBox(Graphics2D g2) {
        String text = null;

        // Prefer UI's cached questionText if set, otherwise read from GamePanel's currentQuestionIndex
        if (this.questionText != null && !this.questionText.isEmpty()) {
            text = this.questionText;
        } else if (gp.currentQuestionIndex >= 0 && gp.map0JSON != null && gp.currentQuestionIndex < gp.map0JSON.size()) {
            try {
                org.json.simple.JSONObject q = (org.json.simple.JSONObject) gp.map0JSON.get(gp.currentQuestionIndex);
                text = (String) q.get("question");
            } catch (Exception e) {
                text = gp.activeQuestionText;
            }
        }

        if (text == null || text.isEmpty()) return;

        int width = gp.screenWidth - 100;
        int height = 100;
        int x = (gp.screenWidth - width) / 2;
        int y = gp.screenHeight - height - 50;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(x, y, width, height, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, width, height, 20, 20);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        g2.setColor(Color.WHITE);

        String[] lines = text.split("\n");
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();
        int textY = y + 30;
        for (String line : lines) {
            int textX = x + 20;
            g2.drawString(line, textX, textY);
            textY += lineHeight;
        }

        // Toggle hint (Left / Right arrows)
        String hint = "Use \u2190 / \u2192 to toggle";
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16F));
        int hintX = x + width - 20 - g2.getFontMetrics().stringWidth(hint);
        int hintY = y + height - 12;
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(hint, hintX, hintY);
    }

    public void drawDialogueScreen() {
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;
        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        x += gp.tileSize;
        y += gp.tileSize;

        if (npc != null && npc.dialogues[npc.dialogueSet][npc.dialogueIndex] != null) {
            currentDialogue = npc.dialogues[npc.dialogueSet][npc.dialogueIndex];

            if (gp.keyH.enterPressed) {
                npc.dialogueIndex++;
                gp.keyH.enterPressed = false;
            }
        } else {
            if (npc != null) npc.dialogueIndex = 0;
            if (gp.gameState == gp.dialogueState) gp.gameState = gp.playState;
        }

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawCharacterScreen() {
        final int frameX = gp.tileSize;
        final int frameY = gp.tileSize;
        final int frameWidth = gp.tileSize * 5;
        final int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(35F));

        int textX = frameX + 20;
        int textY = frameY + gp.tileSize;
        final int lineHeight = 38;

        g2.drawString("Level", textX, textY);
        textY += lineHeight;
        g2.drawString("Life", textX, textY);
        textY += lineHeight;
        g2.drawString("Attack", textX, textY);
        textY += lineHeight;
        g2.drawString("Defense", textX, textY);
        textY += lineHeight;
        g2.drawString("Knowledge", textX, textY);
        textY += lineHeight;
        g2.drawString("Exp", textX, textY);
        textY += lineHeight;
        g2.drawString("Next Level", textX, textY);
        textY += lineHeight;
        g2.drawString("Coin", textX, textY);
        textY += lineHeight + 20;
        g2.drawString("Weapon", textX, textY);
        textY += lineHeight + 15;
        g2.drawString("Shield", textX, textY);
        textY += lineHeight;

        int tailX = (frameX + frameWidth) - 30;

        textY = frameY + gp.tileSize;
        String value;

        value = String.valueOf(gp.player.level);
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.life + "/" + gp.player.getMaxLife());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.getAttack());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.getDefense());
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.exp);
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.nextLevelExp);
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.coin);
        textX = getXforAlignToRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;

        if (gp.player.currentWeapon != null && gp.player.currentWeapon.down1 != null) {
            g2.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize, textY - 14, null);
        }
        textY += gp.tileSize;
        if (gp.player.currentShield != null && gp.player.currentShield.down1 != null) {
            g2.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize, textY - 14, null);
        }
    }

    public void drawInventory(Entity entity, boolean cursor) {
        int frameX, frameY, frameWidth, frameHeight, slotCol, slotRow;
        if (entity == gp.player) {
            frameX = gp.tileSize * 9;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize * 6;
            frameHeight = gp.tileSize * 5;
            slotCol = playerSlotCol;
            slotRow = playerSlotRow;
        } else {
            frameX = gp.tileSize;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize * 6;
            frameHeight = gp.tileSize * 5;
            slotCol = npcSlotCol;
            slotRow = npcSlotRow;
        }

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;

        for (int i = 0; i < entity.inventory.size(); i++) {
            if (entity.inventory.get(i) == entity.currentWeapon ||
               entity.inventory.get(i) == entity.currentShield ||
               entity.inventory.get(i) == entity.currentLight) {
                g2.setColor(new Color(240, 190, 90));
                g2.fillRoundRect(slotX, slotY, gp.tileSize, gp.tileSize, 10, 10);
            }

            g2.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);

            if (entity == gp.player && entity.inventory.get(i).amount > 1) {
                g2.setFont(g2.getFont().deriveFont(32f));
                String s = "" + entity.inventory.get(i).amount;
                int amountX = getXforAlignToRightText(s, slotX + 44);
                int amountY = slotY + gp.tileSize;
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(s, amountX, amountY);
                g2.setColor(Color.white);
                g2.drawString(s, amountX - 3, amountY - 3);
            }

            slotX += slotSize;
            if (i == 4 || i == 9 || i == 14) {
                slotX = slotXstart;
                slotY += slotSize;
            }
        }

        if (cursor) {
            int cursorX = slotXstart + (slotSize * slotCol);
            int cursorY = slotYstart + (slotSize * slotRow);
            int cursorWidth = gp.tileSize;
            int cursorHeight = gp.tileSize;

            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

            int dFrameX = frameX;
            int dFrameY = frameY + frameHeight;
            int dFrameWidth = frameWidth;
            int dFrameHeight = gp.tileSize * 3;
            int textX = dFrameX + 20;
            int textY = dFrameY + gp.tileSize;
            g2.setFont(g2.getFont().deriveFont(28F));

            int itemIndex = getItemIndexOnSlot(slotCol, slotRow);

            if (itemIndex < entity.inventory.size()) {
                drawSubWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight);
                for (String line : entity.inventory.get(itemIndex).description.split("\n")) {
                    g2.drawString(line, textX, textY);
                    textY += 32;
                }
                g2.drawString("Durability: " + entity.inventory.get(itemIndex).durability, textX, textY + 100);
            }
        }
    }

    public void drawGameOverScreen() {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));
        text = "Game Over";

        g2.setColor(Color.black);
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 4;
        g2.drawString(text, x, y);

        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y - 4);

        g2.setFont(g2.getFont().deriveFont(50f));
        text = "Retry";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if (commandNum == 0) g2.drawString(">", x - 40, y);

        text = "Quit";
        x = getXforCenteredText(text);
        y += 55;
        g2.drawString(text, x, y);
        if (commandNum == 1) g2.drawString(">", x - 40, y);
    }

    public void drawOptionsScreen() {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(25F));

        int frameX = gp.tileSize * 4;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch (subState) {
            case 0: options_top(frameX, frameY); break;
            case 1: options_control(frameX, frameY); break;
            case 2: options_endGameConfirmation(frameX, frameY); break;
        }
    }

    public void triggerSavingMessage() {
        showSaving = true;
        saveLoadMessageTime = System.currentTimeMillis();
    }
    public void triggerLoadingMessage() {
        showLoading = true;
        saveLoadMessageTime = System.currentTimeMillis();
    }

    public void options_top(int frameX, int frameY) {
        int textX;
        int textY;
        String text = "Options";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        textX = frameX + gp.tileSize;
        textY += gp.tileSize;
        g2.drawString("Save Game", textX, textY);
        if (showSaving) {
            g2.setColor(Color.YELLOW);
            g2.drawString("Saving...", textX + 165, textY);
            if (System.currentTimeMillis() - saveLoadMessageTime > SAVELOAD_MESSAGE_DURATION) {
                showSaving = false;
            }
            g2.setColor(Color.white);
        }
        if (commandNum == 0) g2.drawString(">", textX - 25, textY);

        textY += gp.tileSize;
        g2.drawString("Load Game", textX, textY);
        if (showLoading) {
            g2.setColor(Color.YELLOW);
            g2.drawString("Loading...", textX + 165, textY);
            if (System.currentTimeMillis() - saveLoadMessageTime > SAVELOAD_MESSAGE_DURATION) {
                showLoading = false;
            }
            g2.setColor(Color.white);
        }
        if (commandNum == 1) g2.drawString(">", textX - 25, textY);

        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if (commandNum == 2) g2.drawString(">", textX - 25, textY);

        textY += gp.tileSize;
        g2.drawString("Sound Effects", textX, textY);
        if (commandNum == 3) g2.drawString(">", textX - 25, textY);

        textY += gp.tileSize;
        g2.drawString("Controls", textX, textY);
        if (commandNum == 4) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed) {
                subState = 1;
                commandNum = 0;
                gp.keyH.enterPressed = false;
            }
        }

        textY += gp.tileSize;
        g2.drawString("Exit Game", textX, textY);
        if (commandNum == 5) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed) {
                subState = 2;
                commandNum = 0;
                gp.keyH.enterPressed = false;
            }
        }

        textY += gp.tileSize;
        g2.drawString("Back", textX, textY);
        if (commandNum == 6) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed) {
                gp.gameState = gp.playState;
                commandNum = 0;
                gp.keyH.enterPressed = false;
            }
        }

        textX = frameX + gp.tileSize * 4 + 24;
        textY = frameY + gp.tileSize * 2 + 24;

        textY += gp.tileSize;
        g2.drawRect(textX, textY, 130, 24);
        int volumeWidth = 26 * gp.music.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        textY += gp.tileSize;
        g2.drawRect(textX, textY, 130, 24);
        volumeWidth = 26 * gp.se.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        gp.config.saveConfig();
    }

    public void options_control(int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize;
        g2.drawString("Controls", getXforCenteredText("Controls"), frameY + gp.tileSize);
        g2.drawString("Movements", textX, textY + gp.tileSize);
        g2.drawString("Confirm/Attack", textX, textY + gp.tileSize * 2);
        g2.drawString("Character Screen", textX, textY + gp.tileSize * 3);
        g2.drawString("Whole Map", textX, textY + gp.tileSize * 4);
        g2.drawString("Mini-Map", textX, textY + gp.tileSize * 5);
        g2.drawString("Pause", textX, textY + gp.tileSize * 6);
        g2.drawString("Options", textX, textY + gp.tileSize * 7);

        textX = frameX + gp.tileSize * 6;
        textY = frameY + gp.tileSize * 2;
        g2.drawString("WASD", textX, textY);
        g2.drawString("ENTER", textX, textY + gp.tileSize);
        g2.drawString("C", textX, textY + gp.tileSize * 2);
        g2.drawString("M", textX, textY + gp.tileSize * 3);
        g2.drawString("X", textX, textY + gp.tileSize * 4);
        g2.drawString("P", textX, textY + gp.tileSize * 5);
        g2.drawString("ESC", textX, textY + gp.tileSize * 6);

        textX = frameX + gp.tileSize;
        textY = frameY + gp.tileSize * 9;
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed) {
                subState = 0;
                commandNum = 4;
                gp.keyH.enterPressed = false;
            }
        }
    }

    public void options_endGameConfirmation(int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 3;
        currentDialogue = "Quit the game and \nreturn to the title screen?";

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        String text = "Yes";
        textX = getXforCenteredText(text);
        textY += gp.tileSize * 3;
        g2.drawString(text, textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed) {
                subState = 0;
                gp.gameState = gp.titleState;
                gp.resetGame(true);
                gp.stopMusic();
                gp.keyH.enterPressed = false;
            }
        }

        text = "No";
        textX = getXforCenteredText(text);
        textY += gp.tileSize;
        g2.drawString(text, textX, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed) {
                subState = 0;
                commandNum = 5;
                gp.keyH.enterPressed = false;
            }
        }
    }

    public void drawTransition() {
        counter++;
        g2.setColor(new Color(0, 0, 0, counter * 4));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (counter == 60) {
            counter = 0;
            gp.gameState = gp.playState;
            gp.currentMap = gp.eHandler.tempMap;
            gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
            gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
            gp.eHandler.previousEventX = gp.player.worldX;
            gp.eHandler.previousEventY = gp.player.worldY;
        }
    }

    public void drawTradeScreen() {
        switch (subState) {
            case 0:
                trade_select();
                break;
            case 1:
                trade_buy();
                break;
            case 2:
                trade_sell();
                break;
        }
    }

    public void trade_select() {
        npc.dialogueSet = 0;
        drawDialogueScreen();

        int x = gp.tileSize * 11;
        int y = gp.tileSize * 5;
        int width = gp.tileSize * 3;
        int height = (int) (gp.tileSize * 3.7);
        drawSubWindow(x, y, width, height);

        x += gp.tileSize;
        y += gp.tileSize;

        g2.drawString("Buy", x, y);
        if (commandNum == 0) g2.drawString(">", x - 24, y);
        y += gp.tileSize;

        g2.drawString("Sell", x, y);
        if (commandNum == 1) g2.drawString(">", x - 24, y);
        y += gp.tileSize;

        g2.drawString("Leave", x, y);
        if (commandNum == 2) g2.drawString(">", x - 24, y);

        gp.keyH.enterPressed = false;
    }

    public void trade_buy() {
        drawInventory(gp.player, false);
        drawInventory(npc, true);

        int x = gp.tileSize;
        int y = gp.tileSize * 9;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 2;
        drawSubWindow(x, y, width, height);
        g2.drawString("[ESC]  Back", x + 24, y + 60);

        int itemIndex = getItemIndexOnSlot(npcSlotCol, npcSlotRow);
        if (itemIndex < npc.inventory.size()) {
            x = (int) (gp.tileSize * 4.5);
            y = (int) (gp.tileSize * 5.5);
            width = (int) (gp.tileSize * 2.5);
            height = gp.tileSize;
            drawSubWindow(x, y, width, height);
            g2.drawImage(coin, x + 10, y + 9, 30, 30, null);

            int price = npc.inventory.get(itemIndex).price;
            String text = "" + price;
            x = getXforAlignToRightText(text, gp.tileSize * 7 - 12);
            g2.drawString(text, x, y + 34);
        }
    }

    public void trade_sell() {
        drawInventory(gp.player, true);

        int x = gp.tileSize;
        int y = gp.tileSize * 9;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 2;
        drawSubWindow(x, y, width, height);
        g2.drawString("[ESC]  Back", x + 24, y + 60);

        int itemIndex = getItemIndexOnSlot(playerSlotCol, playerSlotRow);
        if (itemIndex < gp.player.inventory.size()) {
            x = (int) (gp.tileSize * 12.5);
            y = (int) (gp.tileSize * 5.5);
            width = (int) (gp.tileSize * 2.5);
            height = gp.tileSize;
            drawSubWindow(x, y, width, height);
            g2.drawImage(coin, x + 10, y + 9, 30, 30, null);

            int price = gp.player.inventory.get(itemIndex).price;
            String text = "" + (price - (int) (price * 0.25));
            x = getXforAlignToRightText(text, gp.tileSize * 15 - 12);
            g2.drawString(text, x, y + 34);
        }
    }

    public int getItemIndexOnSlot(int slotCol, int slotRow) {
        int itemIndex = slotCol + (slotRow * 5); // 4col+(2rowx5) = itemIndex is 14 (0-19)
        return itemIndex;
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5)); //defines the width of outlines of graphics rendered with a Graphics2D
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);

    }
    public int getXforCenteredText(String text) {

        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = (gp.screenWidth - length) / 2;
        return x;
    }
    public int getXforAlignToRightText(String text, int tailX) {

        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - length;
        return x;
    }

} 