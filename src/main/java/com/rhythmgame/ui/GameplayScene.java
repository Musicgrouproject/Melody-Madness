package com.rhythmgame.ui;

import com.rhythmgame.GameApp;
import com.rhythmgame.core.GameController;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * The main gameplay scene where the rhythm game action happens.
 * Integrates with GameController for gameplay mechanics.
 */
public class GameplayScene extends Scene {
    
    // UI Constants
    private static final int INFO_BAR_HEIGHT = 60;
    private static final int FOOTER_HEIGHT = 40;
    
    // Game components
    private final StackPane root;
    private final AnchorPane gameArea;
    private final HBox laneContainer;
    private final GameController gameController;
    
    // UI components
    private final Label scoreLabel;
    private final Label comboLabel;
    private final Label timerLabel;
    private final Label startPromptLabel;
    private final StackPane pauseOverlay;
    
    // Update timer
    private AnimationTimer uiUpdateTimer;
    
    /**
     * Create a new gameplay scene.
     */
    public GameplayScene() {
        super(new StackPane(), GameApp.WINDOW_WIDTH, GameApp.WINDOW_HEIGHT);
        
        // Initialize root layout
        root = (StackPane) getRoot();
        root.setStyle("-fx-background-color: #1E1E1E;");
        
        // Create game area and lane container
        gameArea = new AnchorPane();
        laneContainer = new HBox(10);
        laneContainer.setAlignment(Pos.CENTER);
        laneContainer.setPadding(new Insets(INFO_BAR_HEIGHT, 20, FOOTER_HEIGHT, 20));
        
        // Create the game controller that will manage gameplay
        gameController = new GameController(laneContainer);
        
        // Set up UI components
        scoreLabel = setupScoreLabel();
        comboLabel = setupComboLabel();
        timerLabel = setupTimerLabel();
        startPromptLabel = setupStartPrompt();
        pauseOverlay = setupPauseOverlay();
        
        // Set up layout
        setupLayout();
        
        // Set up keyboard handling
        setupKeyboardHandling();
        
        // Initialize UI update timer
        initUIUpdateTimer();
    }
    
    /**
     * Set up the score display label.
     * 
     * @return The created score label
     */
    private Label setupScoreLabel() {
        Label label = new Label("SCORE: 0");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label.setTextFill(Color.WHITE);
        
        // Add drop shadow for better visibility
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(2);
        label.setEffect(shadow);
        
        return label;
    }
    
    /**
     * Set up the combo display label.
     * 
     * @return The created combo label
     */
    private Label setupComboLabel() {
        Label label = new Label("COMBO: 0");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.YELLOW);
        
        // Add drop shadow for better visibility
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(2);
        label.setEffect(shadow);
        
        return label;
    }
    
    /**
     * Set up the timer display label.
     * 
     * @return The created timer label
     */
    private Label setupTimerLabel() {
        Label label = new Label("TIME: 60");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);
        
        // Add drop shadow for better visibility
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(2);
        label.setEffect(shadow);
        
        return label;
    }
    
    /**
     * Set up the start prompt label.
     * 
     * @return The created start prompt label
     */
    private Label setupStartPrompt() {
        Label label = new Label("Press SPACE to Start");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        label.setTextFill(Color.WHITE);
        label.setTextAlignment(TextAlignment.CENTER);
        
        // Add glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.AQUA);
        glow.setWidth(20);
        glow.setHeight(20);
        glow.setRadius(10);
        label.setEffect(glow);
        
        // Add pulsing animation
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1), label);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        
        return label;
    }
    
    /**
     * Set up the pause overlay.
     * 
     * @return The created pause overlay
     */
    private StackPane setupPauseOverlay() {
        // Create container
        StackPane overlay = new StackPane();
        overlay.setVisible(false);
        
        // Semi-transparent background
        Rectangle background = new Rectangle(GameApp.WINDOW_WIDTH, GameApp.WINDOW_HEIGHT);
        background.setFill(Color.color(0, 0, 0, 0.7));
        
        // Blur effect for game area
        GaussianBlur blur = new GaussianBlur(10);
        
        // Pause menu contents
        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPadding(new Insets(20));
        pauseMenu.setMaxWidth(300);
        pauseMenu.setMaxHeight(400);
        pauseMenu.setStyle("-fx-background-color: rgba(30, 30, 30, 0.9); -fx-background-radius: 10;");
        
        // Pause title
        Label pauseTitle = new Label("PAUSED");
        pauseTitle.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        pauseTitle.setTextFill(Color.WHITE);
        
        // Resume button
        Button resumeButton = new Button("RESUME");
        resumeButton.setPrefSize(200, 50);
        resumeButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        resumeButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        );
        resumeButton.setOnAction(e -> resumeGame());
        
        // Restart button
        Button restartButton = new Button("RESTART");
        restartButton.setPrefSize(200, 50);
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        restartButton.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        );
        restartButton.setOnAction(e -> restartGame());
        
        // Main menu button
        Button mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.setPrefSize(200, 50);
        mainMenuButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        mainMenuButton.setStyle(
                "-fx-background-color: #e74c3c;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        );
        mainMenuButton.setOnAction(e -> GameApp.showStartScene());
        
        // Add all elements to the pause menu
        pauseMenu.getChildren().addAll(pauseTitle, resumeButton, restartButton, mainMenuButton);
        
        // Add components to overlay
        overlay.getChildren().addAll(background, pauseMenu);
        
        return overlay;
    }
    
    /**
     * Set up the main layout.
     */
    private void setupLayout() {
        // Create top info bar with score, combo and timer
        HBox infoBar = new HBox(30);
        infoBar.setPadding(new Insets(10, 20, 10, 20));
        infoBar.setAlignment(Pos.CENTER_RIGHT);
        infoBar.setPrefHeight(INFO_BAR_HEIGHT);
        infoBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        infoBar.getChildren().addAll(timerLabel, comboLabel, scoreLabel);
        
        // Position info bar at the top
        AnchorPane.setTopAnchor(infoBar, 0.0);
        AnchorPane.setLeftAnchor(infoBar, 0.0);
        AnchorPane.setRightAnchor(infoBar, 0.0);
        
        // Position lane container in the center
        AnchorPane.setTopAnchor(laneContainer, 0.0);
        AnchorPane.setLeftAnchor(laneContainer, 0.0);
        AnchorPane.setRightAnchor(laneContainer, 0.0);
        AnchorPane.setBottomAnchor(laneContainer, 0.0);
        
        // Add components to the game area
        gameArea.getChildren().addAll(laneContainer, infoBar);
        
        // Center the start prompt
        StackPane.setAlignment(startPromptLabel, Pos.CENTER);
        
        // Add all layers to the root
        root.getChildren().addAll(gameArea, startPromptLabel, pauseOverlay);
    }
    
    /**
     * Set up keyboard input handling.
     */
    private void setupKeyboardHandling() {
        setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            
            // Handle game control keys
            switch (code) {
                case SPACE:
                    if (!gameController.isGameRunning()) {
                        startGame();
                    }
                    break;
                    
                case ESCAPE:
                    togglePause();
                    break;
                    
                default:
                    // Forward gameplay keys to the controller
                    gameController.handleKeyPress(event);
                    break;
            }
        });
        
        setOnKeyReleased(event -> {
            // Forward to game controller
            gameController.handleKeyRelease(event);
        });
    }
    
    /**
     * Initialize the UI update timer.
     */
    private void initUIUpdateTimer() {
        uiUpdateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update score display
                scoreLabel.setText("SCORE: " + gameController.getScore());
                
                // Update combo display
                int combo = gameController.getCombo();
                comboLabel.setText("COMBO: " + combo);
                
                // Change combo color based on value
                if (combo >= 10) {
                    comboLabel.setTextFill(Color.GOLD);
                    comboLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
                } else if (combo >= 5) {
                    comboLabel.setTextFill(Color.ORANGE);
                    comboLabel.setFont(Font.font("Arial", FontWeight.BOLD, 21));
                } else {
                    comboLabel.setTextFill(Color.YELLOW);
                    comboLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                }
                
                // Update timer display
                timerLabel.setText("TIME: " + gameController.getRemainingTime());
                
                // Pulse timer when low on time
                if (gameController.getRemainingTime() <= 10) {
                    timerLabel.setTextFill(Color.RED);
                } else {
                    timerLabel.setTextFill(Color.WHITE);
                }
            }
        };
        
        // Start the UI update timer
        uiUpdateTimer.start();
    }
    
    /**
     * Start the game.
     */
    private void startGame() {
        // Hide the start prompt
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), startPromptLabel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> startPromptLabel.setVisible(false));
        fadeOut.play();
        
        // Start the game controller
        gameController.startGame();
    }
    
    /**
     * Toggle game pause state.
     */
    private void togglePause() {
        if (gameController.isGameRunning()) {
            pauseGame();
        } else {
            resumeGame();
        }
    }
    
    /**
     * Pause the game.
     */
    private void pauseGame() {
        gameController.pauseGame();
        
        // Show pause overlay
        pauseOverlay.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), pauseOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        // Apply blur to game area
        gameArea.setEffect(new GaussianBlur(10));
    }
    
    /**
     * Resume the game.
     */
    private void resumeGame() {
        // Hide pause overlay
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pauseOverlay);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> pauseOverlay.setVisible(false));
        fadeOut.play();
        
        // Remove blur effect
        gameArea.setEffect(null);
        
        // Resume the game
        gameController.startGame();
    }
    
    /**
     * Restart the game.
     */
    private void restartGame() {
        // Hide pause overlay
        pauseOverlay.setVisible(false);
        
        // Remove blur effect
        gameArea.setEffect(null);
        
        // Reset and start the game
        gameController.resetGame();
        gameController.startGame();
    }
    
    /**
     * Clean up resources when the scene is no longer active.
     */
    public void cleanup() {
        if (uiUpdateTimer != null) {
            uiUpdateTimer.stop();
        }
    }
}

