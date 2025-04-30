package com.rhythmgame.model;

import com.rhythmgame.model.Note.HitResult;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a lane in the rhythm game where notes fall.
 * Manages the notes within the lane, handles hit detection, and visual representation.
 */
public class Lane {
    
    // Visual representation constants
    private static final double WIDTH = 100;
    private static final double HEIGHT = 500;
    private static final double CORNER_RADIUS = 10;
    
    // Visual elements
    private final Pane lanePane;
    private final Rectangle background;
    private final Rectangle hitMarker;
    private final Label keyLabel;
    
    // Game properties
    private final int laneNumber;
    private final KeyCode keyCode;
    private final double hitLineY;
    private boolean keyPressed = false;
    
    // Notes in this lane
    private final List<Note> notes = new ArrayList<>();
    
    // Temporary visual effects
    private Label hitFeedbackLabel;
    
    /**
     * Create a new game lane.
     * 
     * @param laneNumber The lane index (0-3)
     * @param keyCode The keyboard key mapped to this lane
     * @param hitLineY The Y-coordinate of the hit line
     */
    public Lane(int laneNumber, KeyCode keyCode, double hitLineY) {
        this.laneNumber = laneNumber;
        this.keyCode = keyCode;
        this.hitLineY = hitLineY;
        
        // Create container for lane elements
        lanePane = new Pane();
        lanePane.setPrefSize(WIDTH, HEIGHT);
        
        // Create lane background
        background = new Rectangle(WIDTH, HEIGHT);
        background.setFill(Color.color(0.15, 0.15, 0.15));
        background.setStroke(Color.GRAY);
        background.setStrokeWidth(1);
        background.setArcWidth(CORNER_RADIUS);
        background.setArcHeight(CORNER_RADIUS);
        
        // Create hit marker at the hit line
        hitMarker = new Rectangle(WIDTH, 10);
        hitMarker.setFill(Color.color(0.3, 0.3, 0.3));
        hitMarker.setTranslateY(hitLineY - 5); // Center on the hit line
        
        // Create key label
        keyLabel = new Label(keyCode.getName());
        keyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        keyLabel.setTextFill(Color.WHITE);
        keyLabel.setTranslateX((WIDTH - keyLabel.getWidth()) / 2);
        keyLabel.setTranslateY(hitLineY + 20);
        
        // Add all elements to the lane
        lanePane.getChildren().addAll(background, hitMarker, keyLabel);
    }
    
    /**
     * Add a new note to this lane.
     * 
     * @param note The note to add
     */
    public void addNote(Note note) {
        notes.add(note);
        note.addToPane(lanePane);
    }
    
    /**
     * Spawn a new note at the top of the lane.
     * 
     * @param speed The speed of the note
     * @return The newly created note
     */
    public Note spawnNote(double speed) {
        Note note = new Note(laneNumber, 0, speed);
        addNote(note);
        return note;
    }
    
    /**
     * Update all notes in this lane.
     * 
     * @return true if any notes were removed, false otherwise
     */
    public boolean updateNotes() {
        boolean notesRemoved = false;
        
        Iterator<Note> iterator = notes.iterator();
        while (iterator.hasNext()) {
            Note note = iterator.next();
            
            // Update note position
            boolean stillVisible = note.update();
            
            // Check if note was missed
            if (!note.isHit() && note.getY() > hitLineY + 50) {
                note.showMissEffect();
            }
            
            // Remove notes that are no longer visible
            if (!stillVisible) {
                note.removeFromPane();
                iterator.remove();
                notesRemoved = true;
            }
        }
        
        return notesRemoved;
    }
    
    /**
     * Handle key press in this lane.
     * 
     * @return The hit result and points, or null if no hit
     */
    public HitData handleKeyPress() {
        if (keyPressed) {
            return null; // Already pressed
        }
        
        keyPressed = true;
        
        // Highlight hit marker
        hitMarker.setFill(Color.YELLOW);
        
        // Check for note hits
        for (Note note : notes) {
            HitResult result = note.checkHit(hitLineY);
            if (result != null) {
                showHitFeedback(result);
                
                // Calculate score based on hit quality
                int points = switch (result) {
                    case PERFECT -> Note.SCORE_PERFECT;
                    case GOOD -> Note.SCORE_GOOD;
                    default -> Note.SCORE_MISS;
                };
                
                return new HitData(result, points);
            }
        }
        
        // No hit
        return null;
    }
    
    /**
     * Handle key release in this lane.
     */
    public void handleKeyRelease() {
        keyPressed = false;
        
        // Reset hit marker color
        hitMarker.setFill(Color.color(0.3, 0.3, 0.3));
    }
    
    /**
     * Get the lane's visual representation.
     * 
     * @return The lane pane
     */
    public Pane getPane() {
        return lanePane;
    }
    
    /**
     * Get the number of notes currently in this lane.
     * 
     * @return The number of notes
     */
    public int getNoteCount() {
        return notes.size();
    }
    
    /**
     * Get the lane number.
     * 
     * @return The lane number
     */
    public int getLaneNumber() {
        return laneNumber;
    }
    
    /**
     * Get the key code assigned to this lane.
     * 
     * @return The key code
     */
    public KeyCode getKeyCode() {
        return keyCode;
    }
    
    /**
     * Get the Y position of the hit line.
     * 
     * @return The hit line Y position
     */
    public double getHitLineY() {
        return hitLineY;
    }
    
    /**
     * Display temporary hit feedback text.
     * 
     * @param result The hit result to display
     */
    private void showHitFeedback(HitResult result) {
        // Remove previous feedback if it exists
        if (hitFeedbackLabel != null) {
            lanePane.getChildren().remove(hitFeedbackLabel);
        }
        
        // Create appropriate text and color based on hit result
        String text;
        Color color;
        
        switch (result) {
            case PERFECT:
                text = "PERFECT!";
                color = Color.GOLD;
                break;
            case GOOD:
                text = "GOOD";
                color = Color.SILVER;
                break;
            default:
                text = "MISS";
                color = Color.RED;
                break;
        }
        
        // Create and style the label
        hitFeedbackLabel = new Label(text);
        hitFeedbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        hitFeedbackLabel.setTextFill(color);
        hitFeedbackLabel.setTranslateX((WIDTH - 80) / 2);
        hitFeedbackLabel.setTranslateY(hitLineY - 40);
        
        // Add to the lane
        lanePane.getChildren().add(hitFeedbackLabel);
        
        // Set up fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), hitFeedbackLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> lanePane.getChildren().remove(hitFeedbackLabel));
        fadeOut.play();
    }
}

