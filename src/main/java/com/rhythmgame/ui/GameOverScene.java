package com.rhythmgame.ui;

import com.rhythmgame.GameApp;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * The game over screen shown after gameplay ends.
 * Displays the final score and options to play again or return to the main menu.
 */
public class GameOverScene extends Scene {

    private static final int SPACING = 40;
    private static final int PADDING = 20;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;
    
    private final Label scoreLabel;
    private int finalScore = 0;
    
    /**
     * Create a new game over scene.
     */
    public GameOverScene() {
        super(new VBox(), GameApp.WINDOW_WIDTH, GameApp.WINDOW_HEIGHT);
        
        VBox root = (VBox) getRoot();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(SPACING);
        root.setPadding(new Insets(PADDING));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a6c, #b21f1f, #fdbb2d);");
        
        // Create the game over title
        Label titleLabel = createTitleLabel();
        
        // Create the score label (to be updated later)
        scoreLabel = createScoreLabel();
        
        // Create buttons
        Button playAgainButton = createPlayAgainButton();
        Button mainMenuButton = createMainMenuButton();
        
        // Add all components to the layout
        root.getChildren().addAll(titleLabel, scoreLabel, playAgainButton, mainMenuButton);
    }
    
    /**
     * Create a styled title label.
     * 
     * @return The styled title label
     */
    private Label createTitleLabel() {
        Label titleLabel = new Label("GAME OVER");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        
        // Add a drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5));
        titleLabel.setEffect(dropShadow);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), titleLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        return titleLabel;
    }
    
    /**
     * Create a styled score label.
     * 
     * @return The styled score label
     */
    private Label createScoreLabel() {
        Label label = new Label("SCORE: 0");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        label.setTextFill(Color.YELLOW);
        label.setTextAlignment(TextAlignment.CENTER);
        
        // Add glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setWidth(20);
        glow.setHeight(20);
        glow.setRadius(10);
        label.setEffect(glow);
        
        return label;
    }
    
    /**
     * Create a styled "Play Again" button.
     * 
     * @return The styled button
     */
    private Button createPlayAgainButton() {
        Button button = new Button("PLAY AGAIN");
        button.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Button styling
        button.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: #45a049;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Set button action - start a new game
        button.setOnAction(e -> GameApp.showGameplayScene());
        
        return button;
    }
    
    /**
     * Create a styled "Back to Menu" button.
     * 
     * @return The styled button
     */
    private Button createMainMenuButton() {
        Button button = new Button("BACK TO MENU");
        button.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        // Button styling
        button.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: #2980b9;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Set button action - go back to start screen
        button.setOnAction(e -> GameApp.showStartScene());
        
        return button;
    }
    
    /**
     * Set the final score and animate its display.
     * 
     * @param score The final score to display
     */
    public void setFinalScore(int score) {
        this.finalScore = score;
        
        // Animate the score counting up
        animateScoreCount(0, score);
    }
    
    /**
     * Animate the score counting up from start to end value.
     * 
     * @param startValue The starting score value
     * @param endValue The ending score value
     */
    private void animateScoreCount(int startValue, int endValue) {
        // Reset the label
        scoreLabel.setText("SCORE: " + startValue);
        
        // Create a timeline to animate the score count
        Timeline timeline = new Timeline();
        
        // Duration based on score (faster for smaller scores)
        double duration = Math.min(3.0, 1.0 + (endValue / 10000.0) * 2.0);
        
        // Add the counter animation
        timeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(duration),
                new KeyValue(scoreLabel.textProperty(), "SCORE: " + endValue)
            )
        );
        
        // Use a separate timeline for the actual number count
        Timeline countTimeline = new Timeline();
        
        // Calculate number of frames (more frames for higher scores)
        int frameCount = Math.min(60, Math.max(20, endValue / 100));
        double frameTime = duration / frameCount;
        
        // Add key frames for each step
        for (int i = 0; i <= frameCount; i++) {
            int frameValue = startValue + (endValue - startValue) * i / frameCount;
            countTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(frameTime * i),
                    e -> scoreLabel.setText("SCORE: " + frameValue)
                )
            );
        }
        
        // Add the final exact value
        countTimeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(duration),
                e -> scoreLabel.setText("SCORE: " + endValue)
            )
        );
        
        // Play the animation
        countTimeline.play();
    }
}

