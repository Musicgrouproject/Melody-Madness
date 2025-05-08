package com.rhythmgame.model;

import com.rhythmgame.model.HitData.HitResult;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Represents a single tap note in the rhythm game that falls down a lane.
 * Contains position, speed, and visual elements for tap notes.
 * Players must press the corresponding key when the note reaches the hit line
 * for perfect or good timing.
 */
public class Note {
    
    // Visual representation constants
    private static final double WIDTH = 80;
    private static final double HEIGHT = 20;
    private static final double CORNER_RADIUS = 10;
    
    // Hit detection constants
    private static final double HIT_PERFECT_RANGE = 15;
    private static final double HIT_GOOD_RANGE = 40;
    private static final double HIT_MISS_RANGE = 60;  // Distance in pixels for a miss
    
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
    private boolean missed = false; // Track if note was specifically missed
    
    /**
     * Create a new tap note in the specified lane.
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
        
        // Check if the note is completely off the bottom of the screen
        return !(noteRect.getTranslateY() > 700);
    }
    
    /**
     * Check if this note has been hit at the specified Y position.
     * 
     * @param hitLineY The Y-coordinate of the hit line
     * @return The hit result, or null if not hit
     */
    public HitResult checkHit(double hitLineY) {
        // Only process if the note has not been hit yet
        if (isHit) {
            return null;
        }
        
        // Calculate how close the note is to the hit line
        double distance = Math.abs(getY() - hitLineY);
        
        if (distance <= HIT_PERFECT_RANGE) {
            // Perfect hit - show gold effect
            showHitEffect(Color.GOLD);
            isHit = true;
            return HitResult.PERFECT;
        } else if (distance <= HIT_GOOD_RANGE) {
            // Good hit - show silver effect
            showHitEffect(Color.SILVER);
            isHit = true;
            return HitResult.GOOD;
        } else if (distance <= HIT_MISS_RANGE) {
            // Miss - show red effect
            showHitEffect(Color.RED);
            isHit = true;
            return HitResult.MISS;
        }
        
        // Not close enough to hit
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
        missed = true; // Mark as specifically missed
        
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
        // Add the note to the pane
        parent.getChildren().add(noteRect);
    }
    
    /**
     * Remove this note's visual representation from its parent pane.
     */
    public void removeFromPane() {
        if (noteRect.getParent() instanceof Pane parent) {
            // Remove note safely
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
     * Check if this note has been missed.
     * 
     * @return true if the note was missed, false otherwise
     */
    public boolean isMissed() {
        return missed;
    }
    
    /**
     * Set the missed status of this note.
     * 
     * @param missed true to mark as missed, false otherwise
     */
    public void setMissed(boolean missed) {
        this.missed = missed;
        
        // If marking as missed, also mark as hit since it can't be hit anymore
        if (missed) {
            this.isHit = true;
        }
    }
    
    
    // Using HitResult enum from HitData class
}
