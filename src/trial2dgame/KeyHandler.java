package trial2dgame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * KeyHandler - full-file replacement to use Left/Right arrows for question toggling on map 0.
 * Left  -> previous assigned question
 * Right -> next assigned question
 *
 * Also plays a small SE and logs to console when toggling.
 */
public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;

    // Debug
    boolean showDebugText = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            titleState(code);
            return;
        }

        // PLAY STATE
        if (gp.gameState == gp.playState) {
            playState(code);
            return;
        }

        // PAUSE STATE
        if (gp.gameState == gp.pauseState) {
            pauseState(code);
            return;
        }

        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState) {
            dialogueState(code);
            return;
        }

        // CHARACTER STATE
        if (gp.gameState == gp.characterState) {
            characterState(code);
            return;
        }

        // OPTIONS STATE
        if (gp.gameState == gp.optionsState) {
            optionsState(code);
            return;
        }

        // GAME OVER STATE
        if (gp.gameState == gp.gameOverState) {
            gameOverState(code);
            return;
        }

        // TRADE STATE
        if (gp.gameState == gp.tradeState) {
            tradeState(code);
            return;
        }

        // MAP STATE
        if (gp.gameState == gp.mapState) {
            mapState(code);
            return;
        }

        // BATTLE STATE
        if (gp.gameState == gp.battleState) {
            battleState(code);
            return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) enterPressed = false;
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
    }

    // -------------------------
    // State handlers
    // -------------------------
    public void titleState(int code) {
        if (code == KeyEvent.VK_W) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) gp.ui.commandNum = 2;
        }
        if (code == KeyEvent.VK_S) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 2) gp.ui.commandNum = 0;
        }
        if (code == KeyEvent.VK_ENTER) {
            if (gp.ui.commandNum == 0) {
                gp.resetGame(true);
                gp.gameState = gp.playState;
                gp.playMusic(0);
            } else if (gp.ui.commandNum == 1) {
                gp.saveLoad.load();
                gp.aSetter.setMonster();
                gp.gameState = gp.playState;
                gp.playMusic(0);
            } else if (gp.ui.commandNum == 2) {
                System.exit(0);
            }
        }
    }

    public void playState(int code) {
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;

        if (code == KeyEvent.VK_P) gp.gameState = gp.pauseState;
        if (code == KeyEvent.VK_C) gp.gameState = gp.characterState;
        if (code == KeyEvent.VK_ENTER) enterPressed = true;
        if (code == KeyEvent.VK_ESCAPE) gp.gameState = gp.optionsState;
        if (code == KeyEvent.VK_M) gp.gameState = gp.mapState;
        if (code == KeyEvent.VK_X) gp.map.miniMapOn = !gp.map.miniMapOn;

        if (code == KeyEvent.VK_T) showDebugText = !showDebugText;

        if (code == KeyEvent.VK_R) {
            switch (gp.currentMap) {
                case 0: gp.tileM.loadMap("/maps/Beach (2).txt", 0); break;
                case 1: gp.tileM.loadMap("/maps/House.txt", 1); break;
                case 2: gp.tileM.loadMap("/maps/Library.txt", 1); break;
                case 3: gp.tileM.loadMap("/maps/trialRoom.txt", 1); break;
            }
        }

        // NEW: toggle assigned questions on map 0 with Left/Right arrows
        if (gp.currentMap == 0) {
            boolean toggled = false;
            if (code == KeyEvent.VK_LEFT) {
                gp.cycleAssignedQuestion(-1);
                toggled = true;
            } else if (code == KeyEvent.VK_RIGHT) {
                gp.cycleAssignedQuestion(+1);
                toggled = true;
            }
            if (toggled) {
                gp.ui.showQuestion = true; // ensure UI box is visible
                gp.playSE(7); // feedback sound
                System.out.println("[KeyHandler] Toggled question -> currentQuestionIndex=" + gp.currentQuestionIndex);
            }
        }
    }

    public void pauseState(int code) {
        if (code == KeyEvent.VK_P) gp.gameState = gp.playState;
    }

    public void dialogueState(int code) {
        if (code == KeyEvent.VK_ENTER) enterPressed = true;
    }

    public void characterState(int code) {
        if (code == KeyEvent.VK_C) gp.gameState = gp.playState;
        if (code == KeyEvent.VK_ENTER) handleCharacter();
        playerInventory(code);
    }

    public void optionsState(int code) {
        if (code == KeyEvent.VK_ESCAPE) gp.gameState = gp.playState;
        if (code == KeyEvent.VK_ENTER) handleEnter();

        int maxCommandNum = 0;
        switch (gp.ui.subState) {
            case 0: maxCommandNum = 6; break;
            case 2: maxCommandNum = 1; break;
        }

        if (code == KeyEvent.VK_W) {
            gp.ui.commandNum--;
            gp.playSE(7);
            if (gp.ui.commandNum < 0) gp.ui.commandNum = maxCommandNum;
        }
        if (code == KeyEvent.VK_S) {
            gp.ui.commandNum++;
            gp.playSE(7);
            if (gp.ui.commandNum > maxCommandNum) gp.ui.commandNum = 0;
        }

        if (code == KeyEvent.VK_A) {
            if (gp.ui.subState == 0) {
                if (gp.ui.commandNum == 2 && gp.music.volumeScale > 0) {
                    gp.music.volumeScale--;
                    gp.music.checkVolume();
                    if (gp.music.volumeScale == 0) { gp.music.stop(); gp.musicPlaying = false; }
                    gp.playSE(7);
                }
                if (gp.ui.commandNum == 3 && gp.se.volumeScale > 0) {
                    gp.se.volumeScale--;
                    gp.se.checkVolume();
                    if (gp.se.volumeScale == 0) gp.se.stop();
                    gp.playSE(7);
                }
            }
        }
        if (code == KeyEvent.VK_D) {
            if (gp.ui.subState == 0) {
                if (gp.ui.commandNum == 2 && gp.music.volumeScale < 5) {
                    gp.music.volumeScale++;
                    gp.music.checkVolume();
                    if (gp.music.volumeScale > 0 && !gp.musicPlaying) {
                        gp.playMusic(gp.currentMusicIndex);
                        gp.musicPlaying = true;
                    }
                    gp.playSE(7);
                }
                if (gp.ui.commandNum == 3 && gp.se.volumeScale < 5) {
                    gp.se.volumeScale++;
                    gp.se.checkVolume();
                    gp.playSE(7);
                }
            }
        }
    }

    public void gameOverState(int code) {
        if (code == KeyEvent.VK_W) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) gp.ui.commandNum = 1;
            gp.playSE(10);
        }
        if (code == KeyEvent.VK_S) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 1) gp.ui.commandNum = 0;
            gp.playSE(10);
        }
        if (code == KeyEvent.VK_ENTER) handleGameOver();
    }

    public void tradeState(int code) {
        if (gp.ui.subState == 0) {
            if (code == KeyEvent.VK_ENTER) handleTrade();
            if (code == KeyEvent.VK_W) { gp.ui.commandNum--; if (gp.ui.commandNum < 0) gp.ui.commandNum = 2; gp.playSE(7); }
            if (code == KeyEvent.VK_S) { gp.ui.commandNum++; if (gp.ui.commandNum > 2) gp.ui.commandNum = 0; gp.playSE(7); }
        } else if (gp.ui.subState == 1) {
            npcInventory(code); playerInventory(code);
            if (code == KeyEvent.VK_ESCAPE) gp.ui.subState = 0;
            if (code == KeyEvent.VK_ENTER) handleTrade();
        } else if (gp.ui.subState == 2) {
            playerInventory(code);
            if (code == KeyEvent.VK_ESCAPE) gp.ui.subState = 0;
            if (code == KeyEvent.VK_ENTER) handleTrade();
        }
    }

    public void mapState(int code) {
        if (code == KeyEvent.VK_M) gp.gameState = gp.playState;
    }

    public void battleState(int code) {
        int choiceCount = Math.max(1, gp.ui.currentQuiz.size());
        if (code == KeyEvent.VK_A) {
            gp.ui.commandNum--;
            gp.playSE(7);
            if (gp.ui.commandNum < 0) gp.ui.commandNum = choiceCount - 1;
        }
        if (code == KeyEvent.VK_D) {
            gp.ui.commandNum++;
            gp.playSE(7);
            if (gp.ui.commandNum >= choiceCount) gp.ui.commandNum = 0;
        }
        if (code == KeyEvent.VK_ENTER) enterPressed = true;
    }

    public void playerInventory(int code) {
        if (code == KeyEvent.VK_W) { if (gp.ui.playerSlotRow != 0) { gp.ui.playerSlotRow--; gp.playSE(7); } }
        if (code == KeyEvent.VK_A) { if (gp.ui.playerSlotCol != 0) { gp.ui.playerSlotCol--; gp.playSE(7); } }
        if (code == KeyEvent.VK_S) { if (gp.ui.playerSlotRow != 3) { gp.ui.playerSlotRow++; gp.playSE(7); } }
        if (code == KeyEvent.VK_D) { if (gp.ui.playerSlotCol != 4) { gp.ui.playerSlotCol++; gp.playSE(7); } }
    }

    public void npcInventory(int code) {
        if (code == KeyEvent.VK_W) { if (gp.ui.npcSlotRow != 0) { gp.ui.npcSlotRow--; gp.playSE(7); } }
        if (code == KeyEvent.VK_A) { if (gp.ui.npcSlotCol != 0) { gp.ui.npcSlotCol--; gp.playSE(7); } }
        if (code == KeyEvent.VK_S) { if (gp.ui.npcSlotRow != 3) { gp.ui.npcSlotRow++; gp.playSE(7); } }
        if (code == KeyEvent.VK_D) { if (gp.ui.npcSlotCol != 4) { gp.ui.npcSlotCol++; gp.playSE(7); } }
    }

    // Helpers for menu handling
    private void handleEnter() {
        if (gp.gameState == gp.dialogueState) handleDialogue();
        else if (gp.gameState == gp.characterState) handleCharacter();
        else if (gp.gameState == gp.optionsState) handleOptions();
        else if (gp.gameState == gp.gameOverState) handleGameOver();
        else if (gp.gameState == gp.tradeState) handleTrade();
    }

    private void handleDialogue() { }
    private void handleCharacter() { gp.player.selectItem(); }

    private void handleOptions() {
        if (gp.ui.subState == 0) {
            switch (gp.ui.commandNum) {
                case 0: gp.saveLoad.save(); gp.aSetter.setMonster(); gp.ui.triggerSavingMessage(); break;
                case 1: gp.saveLoad.load(); gp.aSetter.setMonster(); gp.ui.triggerLoadingMessage(); break;
                case 4: gp.ui.subState = 1; gp.ui.commandNum = 0; break;
                case 5: gp.ui.subState = 2; gp.ui.commandNum = 0; break;
                case 6: gp.gameState = gp.playState; break;
            }
        } else if (gp.ui.subState == 1) {
            if (gp.ui.commandNum == 0) { gp.ui.subState = 0; gp.ui.commandNum = 4; }
        } else if (gp.ui.subState == 2) {
            if (gp.ui.commandNum == 0) { gp.stopMusic(); gp.gameState = gp.titleState; gp.ui.subState = 0; }
            if (gp.ui.commandNum == 1) { gp.ui.subState = 0; gp.ui.commandNum = 5; }
        }
        enterPressed = false;
    }

    private void handleGameOver() {
        if (gp.ui.commandNum == 0) { gp.gameState = gp.playState; gp.resetGame(false); gp.playMusic(0); }
        else if (gp.ui.commandNum == 1) { gp.gameState = gp.titleState; gp.resetGame(true); }
    }

    private void handleTrade() {
        switch (gp.ui.subState) {
            case 0:
                switch (gp.ui.commandNum) {
                    case 0: gp.ui.subState = 1; gp.ui.commandNum = 0; break;
                    case 1: gp.ui.subState = 2; gp.ui.commandNum = 0; break;
                    case 2: gp.ui.commandNum = 0; gp.ui.npc.startDialogue(gp.ui.npc, 1); break;
                }
                break;
            case 1:
                int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.npcSlotCol, gp.ui.npcSlotRow);
                if (gp.ui.npc.inventory.get(itemIndex).price > gp.player.coin) { gp.ui.subState = 0; gp.ui.commandNum = 0; gp.ui.npc.startDialogue(gp.ui.npc, 2); }
                else {
                    if (gp.player.canObtainItem(gp.ui.npc.inventory.get(itemIndex))) gp.player.coin -= gp.ui.npc.inventory.get(itemIndex).price;
                    else { gp.ui.subState = 0; gp.ui.npc.startDialogue(gp.ui.npc, 3); gp.ui.commandNum = 0; }
                }
                break;
            case 2:
                itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);
                if (gp.player.inventory.get(itemIndex) == gp.player.currentWeapon || gp.player.inventory.get(itemIndex) == gp.player.currentShield || gp.player.inventory.get(itemIndex) == gp.player.currentLight) {
                    gp.ui.commandNum = 0; gp.ui.subState = 0; gp.ui.npc.startDialogue(gp.ui.npc, 4);
                } else {
                    if (gp.player.inventory.get(itemIndex).amount > 1) gp.player.inventory.get(itemIndex).amount--;
                    else gp.player.inventory.remove(itemIndex);
                    gp.player.coin += gp.player.inventory.get(itemIndex).price;
                }
                break;
        }
        enterPressed = false;
    }
}