package com.rhythmgame.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;

public class AudioManager {
    private MediaPlayer mediaPlayer;
    private double volume = 0.8; // Default volume at 80%
    private boolean isReady = false;
    private boolean isPaused = false;
    
    public void loadSong(String songPath) {
        try {
            URL resource = getClass().getResource(songPath);
            if (resource == null) {
                throw new IllegalArgumentException("Song not found: " + songPath);
            }
            
            // Create media player
            Media media = new Media(resource.toString());
            mediaPlayer = new MediaPlayer(media);
            
            // Configure media player
            mediaPlayer.setOnReady(() -> {
                isReady = true;
                mediaPlayer.setVolume(volume);
            });
            
            mediaPlayer.setOnEndOfMedia(() -> {
                stop();
                mediaPlayer.seek(Duration.ZERO);
            });
            
            // Set initial volume
            mediaPlayer.setVolume(volume);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load song: " + songPath, e);
        }
    }
    
    public void play() {
        if (mediaPlayer != null && isReady) {
            mediaPlayer.play();
            isPaused = false;
        }
    }
    
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }
    
    public void resume() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.play();
            isPaused = false;
        }
    }
    
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.ZERO);
            isPaused = false;
        }
    }
    
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.volume);
        }
    }
    
    public double getVolume() {
        return volume;
    }
    
    public double getCurrentTime() {
        return mediaPlayer != null ? mediaPlayer.getCurrentTime().toSeconds() : 0.0;
    }
    
    public boolean isPlaying() {
        return mediaPlayer != null && 
               mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public boolean isReady() {
        return isReady;
    }
    
    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        isReady = false;
        isPaused = false;
    }
}

