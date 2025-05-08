package com.rhythmgame.core;

import com.rhythmgame.GameApp;
import com.rhythmgame.audio.AudioManager;
import com.rhythmgame.model.HitData;
import com.rhythmgame.model.Lane;
import com.rhythmgame.model.Note;
import com.rhythmgame.model.Song;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controls the gameplay mechanics for the rhythm game.
 * Manages lanes, notes, score, and game state.
 * The game focuses on tap notes with precise timing for hitting each note.
 */
public class GameController {
    
    // Game configuration
    private static final KeyCode[] LANE_KEYS = {KeyCode.D, KeyCode.F, KeyCode.J, KeyCode.K};
    private static final double HIT_LINE_Y = 450;
    private static final int INITIAL_NOTE_SPEED = 3;
    private static final int MAX_NOTE_SPEED = 8;
    private static final int GAME_DURATION_MS = Integer.MAX_VALUE; // Game continues until manually ended or all notes in the song pattern are completed
    
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
    
    // Audio and song management
    private AudioManager audioManager;
    private Song currentSong;
    private static final String SONG_PATTERN_PATH = "/songs/uptown_funk/pattern.json";
    private static final String SONG_AUDIO_PATH = "/songs/uptown_funk/uptown_funk.mp3";
    private double noteSpawnOffset = 2.0; // Seconds between spawn and hit time
    
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
        
        // Initialize audio manager and load song
        audioManager = new AudioManager();
        try {
            currentSong = Song.loadFromResource(SONG_PATTERN_PATH);
            audioManager.loadSong(SONG_AUDIO_PATH);
        } catch (IOException e) {
            System.err.println("Failed to load song resources: " + e.getMessage());
            // Continue without song loaded - will use random note generation
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
            
            if (currentSong != null) {
                currentSong.reset();
                audioManager.play();
            }
            
            gameLoop.start();
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            if (audioManager != null) {
                audioManager.resume();
            }
            gameLoop.start();
        }
    }
    
    /**
     * Pause the game.
     */
    public void pauseGame() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            if (audioManager != null) {
                audioManager.pause();
            }
            gameLoop.stop();
        }
    }
    
    /**
     * Stop the game and transition to the game over state.
     */
    public void endGame() {
        if (state == GameState.RUNNING || state == GameState.PAUSED) {
            state = GameState.GAME_OVER;
            if (audioManager != null) {
                audioManager.stop();
            }
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
        
        // Spawn notes based on song timing or random generation
        spawnSynchronizedNotes();
        
        // Update all lanes and notes
        updateLanes();
    }
    
    
    /**
     * Update difficulty based on elapsed time.
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
        
        // Add some randomness to spawn timing, but less for higher difficulties
        int maxVariation = Math.max(100, 300 - (currentDifficulty * 25)); // Less variation at higher difficulties
        int randomVariation = random.nextInt(maxVariation) - (maxVariation / 2); // +/- variation/2 ms
        int spawnInterval = baseSpawnInterval + randomVariation;
        
        // Ensure minimum interval for playability (avoids notes too close together)
        spawnInterval = Math.max(300, spawnInterval);
        
        return timeSinceLastSpawn >= spawnInterval;
    }
    
    /**
     * Spawn notes based on song timing or fallback to random generation.
     */
    private void spawnSynchronizedNotes() {
        if (currentSong == null || !audioManager.isPlaying()) {
            // Fallback to random note generation if no song is loaded
            if (shouldSpawnNote(System.currentTimeMillis())) {
                spawnRandomNote();
                lastSpawnTime = System.currentTimeMillis();
            }
            return;
        }
        
        // Get current playback time and adjust for spawn offset
        double currentTime = audioManager.getCurrentTime() + noteSpawnOffset;
        
        // Get notes that should be spawned at this time
        List<Song.NotePattern> notesToSpawn = currentSong.getNotesToSpawn(currentTime);
        
        // Spawn all pending notes
        for (Song.NotePattern note : notesToSpawn) {
            lanes[note.getLane()].spawnNote(currentNoteSpeed);
            notesSpawned++;
        }
        
        // Check if song is finished
        if (!currentSong.hasUnspawnedNotes() && notesSpawned > 0) {
            // Schedule game end after last note reaches bottom
            Timeline endDelay = new Timeline(new KeyFrame(
                Duration.seconds(3), // Adjust timing as needed
                e -> endGame()
            ));
            endDelay.play();
        }
    }
    
    /**
     * Spawn a random note - used as fallback when no song is loaded.
     */
    private void spawnRandomNote() {
        // Choose a random lane
        int laneIndex = random.nextInt(lanes.length);
        
        // Spawn a tap note
        lanes[laneIndex].spawnNote(currentNoteSpeed);
        notesSpawned++;
        
        // Occasionally spawn multiple notes at higher difficulties
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
            HitData missData = lane.updateNotes();
            
            // If a note was missed, process it
            if (missData != null && missData.getResult() == HitData.HitResult.MISS) {
                updateScore(missData);
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
            // Process the key release for this lane
            lanes[laneIndex].handleKeyRelease();
            
            event.consume();
        }
    }
    
    /**
     * Process a key press for a specific lane.
     * 
     * @param laneIndex The index of the lane
     */
    private void processLaneKeyPress(int laneIndex) {
        Lane lane = lanes[laneIndex];
        HitData hitData = lane.handleKeyPress();
        
        // Process the hit data if we got any
        if (hitData != null) {
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
        HitData.HitResult result = hitData.getResult();
        
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
                combo = 0; // Reset combo on miss
                break;
        }
        
        // Update max combo
        if (combo > maxCombo) {
            maxCombo = combo;
        }
        
        // Add combo bonus to score based on combo length
        // Smoother progression combo system:
        // - Base combo multiplier starts at 1.0
        // - Every 5 combo steps increases multiplier by 0.5
        // - Perfect hits get bonus combo points
        if (combo >= 5) {
            double comboMultiplier = 1.0 + (Math.floor((combo - 5) / 5.0) * 0.5);
            
            // Cap the multiplier at 5.0 for balance
            comboMultiplier = Math.min(5.0, comboMultiplier);
            
            // Calculate the combo bonus
            int comboBonus = (int)(combo * comboMultiplier);
            
            // Add bonus points for perfect hits
            if (result == HitData.HitResult.PERFECT) {
                comboBonus = (int)(comboBonus * 1.5); // 50% bonus for perfect hits
            }
            
            score += comboBonus;
            
            // Visual feedback for high combos
            if (combo % 10 == 0) {
                System.out.println("Combo x" + combo + " | Multiplier: " + comboMultiplier);
            }
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
    
    /**
     * Show a score popup for visual feedback.
     * 
     * @param text The text to display
     * @param color The color of the text
     */
    private void showScorePopup(String text, Color color) {
        // For now, we'll just print to console for debugging
        System.out.println("Score popup: " + text);
        
        // Create a floating text label for score feedback
        Label popupLabel = new Label(text);
        popupLabel.setTextFill(color);
        popupLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add to the lane container to display
        laneContainer.getChildren().add(popupLabel);
        
        // Position in the center of the lane container
        popupLabel.setTranslateX((laneContainer.getWidth() / 2) - 40);
        popupLabel.setTranslateY(HIT_LINE_Y - 100);
        
        // Animate upward and fade out
        TranslateTransition moveUp = new TranslateTransition(
            Duration.millis(800), popupLabel);
        moveUp.setByY(-50);
        
        FadeTransition fadeOut = new FadeTransition(
            Duration.millis(800), popupLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> laneContainer.getChildren().remove(popupLabel));
        
        // Play animations simultaneously
        ParallelTransition animation = new ParallelTransition(
            moveUp, fadeOut);
        animation.play();
    }
    
    /**
     * Cleanup resources when the game is closed.
     */
    public void cleanup() {
        if (audioManager != null) {
            audioManager.cleanup();
        }
    }
}

