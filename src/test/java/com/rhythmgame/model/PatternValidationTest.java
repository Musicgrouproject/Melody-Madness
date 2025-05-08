package com.rhythmgame.model;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Test class to validate the pattern.json file for the original song.
 */
public class PatternValidationTest {

    private static final String PATTERN_PATH = "/songs/original_song/pattern.json";
    private static final int MIN_LANE = 0;
    private static final int MAX_LANE = 3;

    /**
     * Test that the pattern file can be loaded successfully.
     */
    @Test
    public void testPatternCanBeLoaded() {
        try {
            Song song = Song.loadFromResource(PATTERN_PATH);
            assertNotNull("Song should not be null", song);
            assertNotNull("Song info should not be null", song.getSongInfo());
            assertNotNull("Note pattern should not be null", song.getNotePattern());
            
            System.out.println("Successfully loaded pattern with " + 
                    song.getNotePattern().size() + " notes");
            
        } catch (IOException e) {
            fail("Failed to load pattern file: " + e.getMessage());
        }
    }
    
    /**
     * Test that the song info is properly set.
     */
    @Test
    public void testSongInfoIsValid() {
        try {
            Song song = Song.loadFromResource(PATTERN_PATH);
            Song.SongInfo info = song.getSongInfo();
            
            assertNotNull("Title should not be null", info.getTitle());
            assertNotNull("Artist should not be null", info.getArtist());
            assertNotNull("Audio file should not be null", info.getAudioFile());
            assertNotNull("Difficulty should not be null", info.getDifficulty());
            
            assertTrue("BPM should be positive", info.getBpm() > 0);
            assertEquals("BPM should be 120", 120.0, info.getBpm(), 0.001);
            
            System.out.println("Song info: " + info.getTitle() + " by " + info.getArtist() + 
                    " (BPM: " + info.getBpm() + ", Difficulty: " + info.getDifficulty() + ")");
            
        } catch (IOException e) {
            fail("Failed to load pattern file: " + e.getMessage());
        }
    }
    
    /**
     * Test that note timings are in sequential order.
     */
    @Test
    public void testNoteTimingsAreSequential() {
        try {
            Song song = Song.loadFromResource(PATTERN_PATH);
            List<Song.NotePattern> notes = song.getNotePattern();
            
            double previousTime = -1.0;
            for (int i = 0; i < notes.size(); i++) {
                Song.NotePattern note = notes.get(i);
                assertTrue("Note time should be greater than or equal to previous note time", 
                        note.getTime() >= previousTime);
                previousTime = note.getTime();
            }
            
            System.out.println("All note timings are sequential. First note at " + 
                    notes.get(0).getTime() + "s, last note at " + 
                    notes.get(notes.size() - 1).getTime() + "s");
            
        } catch (IOException e) {
            fail("Failed to load pattern file: " + e.getMessage());
        }
    }
    
    /**
     * Test that all lane numbers are within valid bounds.
     */
    @Test
    public void testLaneNumbersAreValid() {
        try {
            Song song = Song.loadFromResource(PATTERN_PATH);
            List<Song.NotePattern> notes = song.getNotePattern();
            
            for (int i = 0; i < notes.size(); i++) {
                Song.NotePattern note = notes.get(i);
                int lane = note.getLane();
                assertTrue("Lane number should be >= " + MIN_LANE + " (found: " + lane + " at index " + i + ")",
                        lane >= MIN_LANE);
                assertTrue("Lane number should be <= " + MAX_LANE + " (found: " + lane + " at index " + i + ")",
                        lane <= MAX_LANE);
            }
            
            System.out.println("All lane numbers are within valid range [" + 
                    MIN_LANE + "-" + MAX_LANE + "]");
            
        } catch (IOException e) {
            fail("Failed to load pattern file: " + e.getMessage());
        }
    }
    
    /**
     * Test that note distribution is reasonable across lanes.
     */
    @Test
    public void testNoteDistribution() {
        try {
            Song song = Song.loadFromResource(PATTERN_PATH);
            List<Song.NotePattern> notes = song.getNotePattern();
            
            int[] laneCounts = new int[MAX_LANE + 1];
            for (Song.NotePattern note : notes) {
                laneCounts[note.getLane()]++;
            }
            
            for (int i = 0; i <= MAX_LANE; i++) {
                System.out.println("Lane " + i + " has " + laneCounts[i] + " notes");
                assertTrue("Lane " + i + " should have at least some notes", laneCounts[i] > 0);
            }
            
        } catch (IOException e) {
            fail("Failed to load pattern file: " + e.getMessage());
        }
    }
}

