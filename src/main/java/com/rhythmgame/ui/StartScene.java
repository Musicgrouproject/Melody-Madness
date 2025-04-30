package com.rhythmgame.ui;

import com.rhythmgame.GameApp;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
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
import javafx.util.Duration;

/**
 * The start screen of the rhythm game.
 * Displays the game title and a start button.
 */
public class StartScene extends Scene {

    private static final int SPACING = 50;
    private static final int PADDING = 20;
    
    /**
     * Create a new start scene.
     */
    public StartScene() {
        super(new VBox(), GameApp.WINDOW_WIDTH, GameApp.WINDOW_HEIGHT);
        
        VBox root = (VBox) getRoot();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(SPACING);
        root.setPadding(new Insets(PADDING));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a2a6c, #b21f1f, #fdbb2d);");
        
        setupUI(root);
    }
    
    /**
     * Set up the UI components.
     * 
     * @param root The root layout container
     */
    private void setupUI(VBox root) {
        // Create title label with fancy styling
        Label titleLabel = createTitleLabel();
        
        // Create start button
        Button startButton = createStartButton();
        
        // Add components to layout
        root.getChildren().addAll(titleLabel, startButton);
        
        // Apply animations
        animateTitle(titleLabel);
    }
    
    /**
     * Create a styled title label.
     * 
     * @return The styled title label
     */
    private Label createTitleLabel() {
        Label titleLabel = new Label("RHYTHM GAME");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        titleLabel.setTextFill(Color.WHITE);
        
        // Add a drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5));
        titleLabel.setEffect(dropShadow);
        
        return titleLabel;
    }
    
    /**
     * Create a styled start button.
     * 
     * @return The styled start button
     */
    private Button createStartButton() {
        Button startButton = new Button("START GAME");
        startButton.setPrefSize(200, 80);
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Button styling
        startButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );
        
        // Add hover effect
        startButton.setOnMouseEntered(e -> 
            startButton.setStyle(
                "-fx-background-color: #45a049;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);"
            )
        );
        
        startButton.setOnMouseExited(e -> 
            startButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Set button action - transition to gameplay scene
        startButton.setOnAction(e -> GameApp.showGameplayScene());
        
        return startButton;
    }
    
    /**
     * Apply animations to the title label.
     * 
     * @param titleLabel The title label to animate
     */
    private void animateTitle(Label titleLabel) {
        // Create a fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), titleLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Create a scale animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), titleLabel);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        
        // Combine animations
        SequentialTransition sequentialTransition = new SequentialTransition(fadeIn, scaleTransition);
        sequentialTransition.play();
    }
}

