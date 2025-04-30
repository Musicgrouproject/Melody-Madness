#!/bin/sh
java --module-path /opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home/lib \
--add-modules=javafx.controls,javafx.base,javafx.graphics \
-jar target/rhythm-game-1.0-SNAPSHOT.jar
