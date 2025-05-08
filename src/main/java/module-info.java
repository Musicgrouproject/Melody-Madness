module com.rhythmgame {
    // JavaFX core modules
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.media;
    
    // JSON processing
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    
    // Logging
    requires java.logging;
    
    // Allow JavaFX to access our UI components via reflection
    opens com.rhythmgame to javafx.graphics;
    opens com.rhythmgame.ui to javafx.graphics, javafx.fxml;
    opens com.rhythmgame.model to com.fasterxml.jackson.databind;
    
    // Export our packages
    exports com.rhythmgame;
    exports com.rhythmgame.ui;
    exports com.rhythmgame.core;
    exports com.rhythmgame.model;
    exports com.rhythmgame.audio;
}

