module com.rhythmgame {
    // JavaFX core modules
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    
    // Allow JavaFX to access our UI components via reflection
    opens com.rhythmgame to javafx.graphics;
    opens com.rhythmgame.ui to javafx.graphics, javafx.fxml;
    
    // Export our packages
    exports com.rhythmgame;
    exports com.rhythmgame.ui;
    exports com.rhythmgame.core;
    exports com.rhythmgame.model;
}

