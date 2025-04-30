package com.rhythmgame.model;

import com.rhythmgame.model.Note.HitResult;

/**
 * Data class to store hit result information.
 */
public class HitData {
    private final HitResult result;
    private final int points;
    
    /**
     * Create a new hit data object.
     * 
     * @param result The hit result type
     * @param points The points awarded for this hit
     */
    public HitData(HitResult result, int points) {
        this.result = result;
        this.points = points;
    }
    
    /**
     * Get the hit result type.
     * 
     * @return The hit result type
     */
    public HitResult getResult() {
        return result;
    }
    
    /**
     * Get the points awarded for this hit.
     * 
     * @return The points
     */
    public int getPoints() {
        return points;
    }
}

