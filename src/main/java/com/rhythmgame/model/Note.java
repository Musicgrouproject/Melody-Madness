package com.rhythmgame.model;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Represents a note in the rhythm game that falls down a lane.
 * Contains position, speed, and visual elements.
 */
public class Note {
    
    // Visual representation constants
    private static final double WIDTH = 80;
    private static final double HEIGHT = 20;
    private static final double CORNER_RADIUS = 10;
    
    // Hit detection constants
    private static final double HIT_PERFECT_RANGE = 15;
    private static final double HIT_GOOD_RANGE = 40;
    
    // Hit scoring constants
    public static final int SCORE_PERFECT = 100;
    public static final int SCORE_GOOD = 50;
    public static final int SCORE_MISS = 0;
    
    // Visual representation
    private final Rectangle noteRect;
    
    // Game properties
    private double speed;
    private int laneNumber;
    private boolean isHit = false;
    private boolean isVisible = true;
    
    /**
     * Create a new note in the specified lane.
     * 
     * @param laneNumber The lane index (0-3)
     * @param startY The starting Y position
     * @param speed The falling speed in pixels per frame
     */
    public Note(int laneNumber, double startY, double speed) {
        this.laneNumber = laneNumber;
        this.speed = speed;
        
        // Create visual representation
        noteRect = new Rectangle(WIDTH, HEIGHT);
        noteRect.setArcWidth(CORNER_RADIUS);
        noteRect.setArcHeight(CORNER_RADIUS);
        noteRect.setFill(Color.AQUA);
        noteRect.setStroke(Color.WHITE);
        noteRect.setStrokeWidth(2);
        noteRect.setTranslateY(startY);
        
        // Center in lane (calculated based on lane width)
        double centerX = (WIDTH / 2);
        noteRect.setTranslateX(centerX - (WIDTH / 2));
    }
    
    /**
     * Update the note position based on its speed.
     * 
     * @return true if the note is still on screen, false if it should be removed
     */
    public boolean update() {
        if (!isVisible) {
            return false;
        }
        
        // Move the note down
        noteRect.setTranslateY(noteRect.getTranslateY() + speed);
        
        // Check if the note is off the bottom of the screen
        return !(noteRect.getTranslateY() > 700);
    }
    
    /**
     * Check if this note can be hit at the specified hit line position.
     * 
     * @param hitLineY The Y-coordinate of the hit line
     * @return The hit result type (PERFECT, GOOD, or MISS)
     */
    public HitResult checkHit(double hitLineY) {
        if (isHit || !isVisible) {
            return null;
        }
        
        double noteY = noteRect.getTranslateY() + (HEIGHT / 2);
        double distance = Math.abs(noteY - hitLineY);
        
        if (distance <= HIT_PERFECT_RANGE) {
            showHitEffect(Color.GOLD); // Perfect hit effect
            return HitResult.PERFECT;
        } else if (distance <= HIT_GOOD_RANGE) {
            showHitEffect(Color.SILVER); // Good hit effect
            return HitResult.GOOD;
        }
        
        // Too far to hit
        return null;
    }
    
    /**
     * Mark the note as hit and trigger visual feedback.
     * 
     * @param color The color for the hit effect
     */
    private void showHitEffect(Color color) {
        isHit = true;
        
        // Change appearance for hit feedback
        noteRect.setFill(color);
        
        // Scale animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), noteRect);
        scaleTransition.setToX(1.5);
        scaleTransition.setToY(1.5);
        
        // Fade out animation
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), noteRect);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> isVisible = false);
        
        // Play animations
        scaleTransition.play();
        fadeTransition.play();
    }
    
    /**
     * Mark the note as missed and trigger missed visual feedback.
     */
    public void showMissEffect() {
        if (isHit || !isVisible) {
            return;
        }
        
        isHit = true;
        
        // Change appearance for miss feedback
        noteRect.setFill(Color.RED);
        
        // Fade out animation
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), noteRect);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e -> isVisible = false);
        
        // Play animation
        fadeTransition.play();
    }
    
    /**
     * Add this note's visual representation to a parent pane.
     * 
     * @param parent The parent pane to add to
     */
    public void addToPane(Pane parent) {
        parent.getChildren().add(noteRect);
    }
    
    /**
     * Remove this note's visual representation from its parent pane.
     */
    public void removeFromPane() {
        Pane parent = (Pane) noteRect.getParent();
        if (parent != null) {
            parent.getChildren().remove(noteRect);
        }
    }
    
    /**
     * Get the current Y position of the note.
     * 
     * @return The Y position
     */
    public double getY() {
        return noteRect.getTranslateY();
    }
    
    /**
     * Get the lane number this note is in.
     * 
     * @return The lane number
     */
    public int getLaneNumber() {
        return laneNumber;
    }
    
    /**
     * Check if this note has been hit.
     * 
     * @return true if the note has been hit, false otherwise
     */
    public boolean isHit() {
        return isHit;
    }
    
    /**
     * Check if this note is still visible.
     * 
     * @return true if the note is visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * Represents the possible hit result types.
     */
    public enum HitResult {
        PERFECT,
        GOOD,
        MISS
    }
}

