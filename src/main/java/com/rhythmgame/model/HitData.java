package com.rhythmgame.model;

/**
 * Data class to hold hit result information.
 */
public class HitData {
    /**
     * Possible hit result types.
     */
    public enum HitResult {
        PERFECT,  // Perfect hit on a tap note
        GOOD,     // Good hit on a tap note  
        MISS      // Missed note completely
    }

    private final HitResult result;
    private final int points;
    
    /**
     * Create a new hit data object.
     * 
     * @param result The hit result (PERFECT, GOOD, MISS)
     * @param points The points earned for this hit
     */
    public HitData(HitResult result, int points) {
        this.result = result;
        this.points = points;
    }
    
    /**
     * Get the hit result.
     * 
     * @return The hit result
     */
    public HitResult getResult() {
        return result;
    }
    
    /**
     * Get the points earned for this hit.
     * 
     * @return The points earned
     */
    public int getPoints() {
        return points;
    }
}
