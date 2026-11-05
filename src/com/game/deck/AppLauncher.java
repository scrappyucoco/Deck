package com.game.deck;

import com.game.deck.model.GameState;
import com.game.deck.views.GameView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class AppLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Deck - Philosophical Balatro Deckbuilder");

        Scene scene = new Scene(new GameView(new GameState()), 900, 600);
        attachStylesheet(scene, "assets/theme.css");
        attachStylesheet(scene, "assets/cards.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void attachStylesheet(Scene scene, String relativePath) {
        String withSlash = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        String withoutSlash = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        URL url = getClass().getResource(withSlash);
        if (url == null) {
            url = getClass().getClassLoader().getResource(withoutSlash);
        }
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
            return;
        }

        File rootFile = new File(withoutSlash);
        if (rootFile.isFile()) {
            scene.getStylesheets().add(rootFile.toURI().toString());
            return;
        }

        File sourceFile = new File("src", withoutSlash);
        if (sourceFile.isFile()) {
            scene.getStylesheets().add(sourceFile.toURI().toString());
            return;
        }

        System.err.println("[WARNING] Could not locate CSS stylesheet: " + relativePath);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
