package com.rhythmgame.core;

import com.rhythmgame.GameApp;
import com.rhythmgame.model.HitData;
import com.rhythmgame.model.Lane;
import com.rhythmgame.model.Note;
import com.rhythmgame.model.Note.HitResult;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Controls the gameplay mechanics for the rhythm game.
 * Manages lanes, notes, score, and game state.
 */
public class GameController {
    
    // Game configuration
    private static final KeyCode[] LANE_KEYS = {KeyCode.D, KeyCode.F, KeyCode.J, KeyCode.K};
    private static final double HIT_LINE_Y = 450;
    private static final int INITIAL_NOTE_SPEED = 3;
    private static final int MAX_NOTE_SPEED = 8;
    private static final int GAME_DURATION_MS = 60000; // 60 seconds
    
    // Game state
    private enum GameState {
        READY, RUNNING, PAUSED, GAME_OVER
    }
    
    private GameState state = GameState.READY;
    private long gameStartTime;
    private long lastSpawnTime;
    private int currentNoteSpeed;
    private int notesSpawned;
    private int currentDifficulty = 1;
    
    // Scoring
    private int score;
    private int combo;
    private int maxCombo;
    private int perfectHits;
    private int goodHits;
    private int missedHits;
    
    // Game components
    private final Lane[] lanes;
    private final Map<KeyCode, Integer> keyToLaneMap;
    private final Random random;
    private AnimationTimer gameLoop;
    private final HBox laneContainer;
    
    /**
     * Create a new game controller.
     * 
     * @param laneContainer The container where lanes will be displayed
     */
    public GameController(HBox laneContainer) {
        this.laneContainer = laneContainer;
        this.random = new Random();
        
        // Initialize lanes
        lanes = new Lane[4];
        keyToLaneMap = new HashMap<>();
        
        for (int i = 0; i < 4; i++) {
            lanes[i] = new Lane(i, LANE_KEYS[i], HIT_LINE_Y);
            keyToLaneMap.put(LANE_KEYS[i], i);
            laneContainer.getChildren().add(lanes[i].getPane());
        }
        
        // Initialize game variables
        resetGame();
        
        // Set up game loop
        initGameLoop();
    }
    
    /**
     * Reset all game variables to initial state.
     */
    public void resetGame() {
        score = 0;
        combo = 0;
        maxCombo = 0;
        perfectHits = 0;
        goodHits = 0;
        missedHits = 0;
        notesSpawned = 0;
        currentNoteSpeed = INITIAL_NOTE_SPEED;
        state = GameState.READY;
    }
    
    /**
     * Start the game.
     */
    public void startGame() {
        if (state == GameState.READY || state == GameState.GAME_OVER) {
            resetGame();
            state = GameState.RUNNING;
            gameStartTime = System.currentTimeMillis();
            lastSpawnTime = gameStartTime;
            gameLoop.start();
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            gameLoop.start();
        }
    }
    
    /**
     * Pause the game.
     */
    public void pauseGame() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            gameLoop.stop();
        }
    }
    
    /**
     * Stop the game and transition to the game over state.
     */
    public void endGame() {
        if (state == GameState.RUNNING || state == GameState.PAUSED) {
            state = GameState.GAME_OVER;
            gameLoop.stop();
            
            // Transition to game over screen
            GameApp.showGameOverScene(score);
        }
    }
    
    /**
     * Initialize the game loop.
     */
    private void initGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (state == GameState.RUNNING) {
                    update();
                }
            }
        };
    }
    
    /**
     * Update game state each frame.
     */
    private void update() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - gameStartTime;
        
        // Check if game time is up
        if (elapsedTime >= GAME_DURATION_MS) {
            endGame();
            return;
        }
        
        // Update difficulty based on elapsed time
        updateDifficulty(elapsedTime);
        
        // Spawn notes based on timing
        if (shouldSpawnNote(currentTime)) {
            spawnNote();
            lastSpawnTime = currentTime;
        }
        
        // Update all lanes and notes
        updateLanes();
    }
    
    /**
     * Update difficulty as the game progresses.
     * 
     * @param elapsedTime Time elapsed since game start in milliseconds
     */
    private void updateDifficulty(long elapsedTime) {
        // Increase difficulty every 10 seconds
        int newDifficulty = 1 + (int)(elapsedTime / 10000);
        
        if (newDifficulty > currentDifficulty) {
            currentDifficulty = newDifficulty;
            
            // Increase note speed with difficulty, up to max
            currentNoteSpeed = Math.min(MAX_NOTE_SPEED, INITIAL_NOTE_SPEED + (currentDifficulty - 1));
        }
    }
    
    /**
     * Determine if a new note should be spawned based on timing.
     * 
     * @param currentTime The current system time
     * @return true if a note should be spawned, false otherwise
     */
    private boolean shouldSpawnNote(long currentTime) {
        // Calculate time since last note spawn
        long timeSinceLastSpawn = currentTime - lastSpawnTime;
        
        // Base spawn interval decreases with difficulty (faster notes)
        int baseSpawnInterval = Math.max(500, 1000 - (currentDifficulty * 50));
        
        // Add some randomness to spawn timing
        int randomVariation = random.nextInt(300) - 150; // +/- 150ms
        int spawnInterval = baseSpawnInterval + randomVariation;
        
        return timeSinceLastSpawn >= spawnInterval;
    }
    
    /**
     * Spawn a new note in a random lane.
     */
    private void spawnNote() {
        // Choose a random lane
        int laneIndex = random.nextInt(lanes.length);
        
        // Spawn the note
        lanes[laneIndex].spawnNote(currentNoteSpeed);
        notesSpawned++;
        
        // Occasionally spawn multiple notes at once at higher difficulties
        if (currentDifficulty >= 3 && random.nextDouble() < 0.2) {
            // Choose a different lane for second note
            int secondLane;
            do {
                secondLane = random.nextInt(lanes.length);
            } while (secondLane == laneIndex);
            
            lanes[secondLane].spawnNote(currentNoteSpeed);
            notesSpawned++;
        }
    }
    
    /**
     * Update all lanes and their notes.
     */
    private void updateLanes() {
        for (Lane lane : lanes) {
            boolean notesRemoved = lane.updateNotes();
            
            // If notes were removed without being hit, they were missed
            if (notesRemoved) {
                missedHits++;
                combo = 0; // Reset combo on miss
            }
        }
    }
    
    /**
     * Handle keyboard key press events.
     * 
     * @param event The key event
     */
    public void handleKeyPress(KeyEvent event) {
        if (state != GameState.RUNNING) {
            return;
        }
        
        KeyCode code = event.getCode();
        
        // Check if the key corresponds to a lane
        Integer laneIndex = keyToLaneMap.get(code);
        if (laneIndex != null) {
            processLaneKeyPress(laneIndex);
            event.consume();
        }
    }
    
    /**
     * Handle keyboard key release events.
     * 
     * @param event The key event
     */
    public void handleKeyRelease(KeyEvent event) {
        if (state != GameState.RUNNING) {
            return;
        }
        
        KeyCode code = event.getCode();
        
        // Check if the key corresponds to a lane
        Integer laneIndex = keyToLaneMap.get(code);
        if (laneIndex != null) {
            lanes[laneIndex].handleKeyRelease();
            event.consume();
        }
    }
    
    /**
     * Process a key press for the specified lane.
     * 
     * @param laneIndex The lane index
     */
    private void processLaneKeyPress(int laneIndex) {
        Lane lane = lanes[laneIndex];
        HitData hitData = lane.handleKeyPress();
        
        if (hitData != null) {
            // Update score and combo
            updateScore(hitData);
        }
    }
    
    /**
     * Update score and combo based on hit result.
     * 
     * @param hitData The hit result data
     */
    private void updateScore(HitData hitData) {
        // Add points to score
        score += hitData.getPoints();
        
        // Update hit counts and combo
        HitResult result = hitData.getResult();
        switch (result) {
            case PERFECT:
                perfectHits++;
                combo++;
                break;
                
            case GOOD:
                goodHits++;
                combo++;
                break;
                
            case MISS:
                missedHits++;
                combo = 0;
                break;
        }
        
        // Update max combo
        if (combo > maxCombo) {
            maxCombo = combo;
        }
        
        // Add combo bonus to score (2 points per combo item after 5)
        if (combo >= 5) {
            score += (combo - 4) * 2;
        }
    }
    
    /**
     * Get the current score.
     * 
     * @return The current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Get the current combo count.
     * 
     * @return The current combo
     */
    public int getCombo() {
        return combo;
    }
    
    /**
     * Get the maximum combo achieved.
     * 
     * @return The maximum combo
     */
    public int getMaxCombo() {
        return maxCombo;
    }
    
    /**
     * Get the current game state.
     * 
     * @return The game state
     */
    public boolean isGameRunning() {
        return state == GameState.RUNNING;
    }
    
    /**
     * Get the remaining game time in seconds.
     * 
     * @return The remaining time in seconds
     */
    public int getRemainingTime() {
        if (state != GameState.RUNNING && state != GameState.PAUSED) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - gameStartTime;
        int remainingTime = (GAME_DURATION_MS - (int)elapsedTime) / 1000;
        
        return Math.max(0, remainingTime);
    }
}

