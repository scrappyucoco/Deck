package com.game.deck;

import com.game.deck.model.*;
import com.game.deck.views.CardNode;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Philosophical Deckbuilder - JavaFX Frontend");

        // Header Section
        Label headerLabel = new Label("Philosophical Deckbuilder");
        headerLabel.getStyleClass().add("header-title");

        Label subLabel = new Label("Click cards to select them for your hand!");
        subLabel.getStyleClass().add("header-subtitle");

        // Sample Cards
        Card c1 = new Card(Rank.ACE, Suit.SPADES, Enhancement.BONUS);
        Card c2 = new Card(Rank.TEN, Suit.HEARTS, Enhancement.GLASS);
        Card c3 = new Card(Rank.KING, Suit.DIAMONDS, Enhancement.STEEL);
        Card c4 = new Card(Rank.QUEEN, Suit.CLUBS, Enhancement.GOLD);

        CardNode node1 = new CardNode(c1);
        CardNode node2 = new CardNode(c2);
        CardNode node3 = new CardNode(c3);
        CardNode node4 = new CardNode(c4);

        // Hand Box Container
        HBox handBox = new HBox(15, node1, node2, node3, node4);
        handBox.getStyleClass().add("hand-container");

        // Action Button
        Button playButton = new Button("Play Selected Cards");
        playButton.getStyleClass().add("btn-primary");

        playButton.setOnAction(e -> {
            System.out.println("\n[GUI EVENT] 'Play Selected Cards' Clicked!");
            for (CardNode node : new CardNode[]{node1, node2, node3, node4}) {
                if (node.isSelected()) {
                    System.out.println("  -> Selected: " + node.getCard());
                }
            }
        });

        // Main Scene Root
        VBox root = new VBox(20, headerLabel, subLabel, handBox, playButton);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("poker-table");

        Scene scene = new Scene(root, 850, 520);

        // Attach External CSS Files Dynamically from assets/
        attachStylesheet(scene, "assets/theme.css");
        attachStylesheet(scene, "assets/cards.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Dynamic Resource Resolver: Works in Eclipse IDE, raw terminal, and inside executable .jar files!
     */
    private void attachStylesheet(Scene scene, String relativePath) {
        String pathWithSlash = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        String pathNoSlash = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        // 1. Try Class.getResource
        URL url = getClass().getResource(pathWithSlash);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
            return;
        }

        // 2. Try ClassLoader.getResource
        url = getClass().getClassLoader().getResource(pathNoSlash);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
            return;
        }

        // 3. Fallback to Local File System
        File file = new File(pathNoSlash);
        if (file.exists()) {
            try {
                scene.getStylesheets().add(file.toURI().toURL().toExternalForm());
                return;
            } catch (Exception ignored) {}
        }

        System.err.println("[WARNING] Could not locate CSS stylesheet: " + relativePath);
    }

    public static void main(String[] args) {
        launch(args);
    }
}