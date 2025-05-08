package com.rhythmgame.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Song {
    @JsonProperty("songInfo")
    private SongInfo songInfo;
    
    @JsonProperty("notePattern")
    private List<NotePattern> notePattern;
    
    public static class SongInfo {
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("artist")
        private String artist;
        
        @JsonProperty("bpm")
        private double bpm;
        
        @JsonProperty("offset")
        private double offset;
        
        @JsonProperty("audioFile")
        private String audioFile;
        
        @JsonProperty("difficulty")
        private String difficulty;
        
        // Getters
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public double getBpm() { return bpm; }
        public double getOffset() { return offset; }
        public String getAudioFile() { return audioFile; }
        public String getDifficulty() { return difficulty; }
    }
    
    public static class NotePattern {
        @JsonProperty("time")
        private double time;
        
        @JsonProperty("lane")
        private int lane;
        
        private boolean spawned;
        
        // Getters and setters
        public double getTime() { return time; }
        public int getLane() { return lane; }
        public boolean isSpawned() { return spawned; }
        public void setSpawned(boolean spawned) { this.spawned = spawned; }
    }
    
    // Static loader method
    public static Song loadFromResource(String patternPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = Song.class.getResourceAsStream(patternPath);
        if (is == null) {
            throw new IOException("Could not find pattern file: " + patternPath);
        }
        try {
            return mapper.readValue(is, Song.class);
        } finally {
            is.close();
        }
    }
    
    // Get notes that should spawn based on current time
    public List<NotePattern> getNotesToSpawn(double currentTime) {
        List<NotePattern> notesToSpawn = new ArrayList<>();
        for (NotePattern note : notePattern) {
            if (!note.spawned && note.time <= currentTime) {
                note.spawned = true;
                notesToSpawn.add(note);
            }
        }
        return notesToSpawn;
    }
    
    // Reset all notes' spawn status
    public void reset() {
        if (notePattern != null) {
            notePattern.forEach(note -> note.setSpawned(false));
        }
    }
    
    // Check if there are any unspawned notes left
    public boolean hasUnspawnedNotes() {
        return notePattern != null && 
               notePattern.stream().anyMatch(note -> !note.isSpawned());
    }
    
    // Getters
    public SongInfo getSongInfo() { return songInfo; }
    public List<NotePattern> getNotePattern() { return notePattern; }
}

