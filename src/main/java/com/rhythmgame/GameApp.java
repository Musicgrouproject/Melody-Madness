package com.rhythmgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.rhythmgame.ui.StartScene;
import com.rhythmgame.ui.GameplayScene;
import com.rhythmgame.ui.GameOverScene;

/**
 * Main application class for the Rhythm Game.
 * Handles scene management and application lifecycle.
 */
public class GameApp extends Application {

    // Window dimensions
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    
    // Game title
    private static final String GAME_TITLE = "Rhythm Game";
    
    // Stage reference for scene transitions
    private static Stage primaryStage;
    
    // Static reference to the application instance
    private static GameApp instance;
    
    // Scene references
    private Scene startScene;
    private Scene gameplayScene;
    private Scene gameOverScene;

    @Override
    public void start(Stage stage) {
        // Store references
        primaryStage = stage;
        instance = this;
        
        // Configure the primary stage
        primaryStage.setTitle(GAME_TITLE);
        primaryStage.setResizable(false);
        
        // Initialize scenes
        initScenes();
        
        // Set the initial scene to the start screen
        primaryStage.setScene(startScene);
        primaryStage.show();
    }
    
    /**
     * Initialize all game scenes.
     */
    private void initScenes() {
        // Create the start scene
        startScene = new StartScene();
        
        // Create the gameplay scene
        gameplayScene = new GameplayScene();
        
        // Create the game over scene
        gameOverScene = new GameOverScene();
    }
    
    /**
     * Switch to the start scene.
     */
    public static void showStartScene() {
        Platform.runLater(() -> {
            if (primaryStage != null && instance != null) {
                primaryStage.setScene(instance.startScene);
            }
        });
    }
    
    /**
     * Switch to the gameplay scene.
     */
    public static void showGameplayScene() {
        Platform.runLater(() -> {
            if (primaryStage != null && instance != null) {
                primaryStage.setScene(instance.gameplayScene);
            }
        });
    }
    
    /**
     * Switch to the game over scene.
     * @param finalScore The player's final score to display
     */
    public static void showGameOverScene(int finalScore) {
        Platform.runLater(() -> {
            if (primaryStage != null && instance != null) {
                // Set the final score on the game over scene
                GameOverScene gameOver = (GameOverScene) instance.gameOverScene;
                gameOver.setFinalScore(finalScore);
                
                // Switch to the game over scene
                primaryStage.setScene(gameOver);
            }
        });
    }

    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

