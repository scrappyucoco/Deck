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

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Philosophical Deckbuilder - JavaFX Visual Test");

        // 1. Header Title Label
        Label headerLabel = new Label("Philosophical Deckbuilder - Visual Stage");
        headerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        Label subLabel = new Label("Click on cards to select / lift them up!");
        subLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");

        // 2. Create Sample Cards
        Card c1 = new Card(Rank.ACE, Suit.SPADES, Enhancement.BONUS);
        Card c2 = new Card(Rank.TEN, Suit.HEARTS, Enhancement.GLASS);
        Card c3 = new Card(Rank.KING, Suit.DIAMONDS, Enhancement.STEEL);
        Card c4 = new Card(Rank.QUEEN, Suit.CLUBS, Enhancement.GOLD);

        // 3. Wrap Cards in CardNodes
        CardNode node1 = new CardNode(c1);
        CardNode node2 = new CardNode(c2);
        CardNode node3 = new CardNode(c3);
        CardNode node4 = new CardNode(c4);

        // 4. Horizontal Hand Layout (Table Area)
        HBox handBox = new HBox(15, node1, node2, node3, node4);
        handBox.setAlignment(Pos.CENTER);
        handBox.setStyle("-fx-padding: 30px;");

        // 5. Action Button with Inline Styling
        Button playButton = new Button("Play Selected Cards");
        playButton.setStyle("-fx-font-size: 15px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-color: #27ae60; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 10px 20px; " +
                            "-fx-background-radius: 5px; " +	
                            "-fx-cursor: hand;");

        playButton.setOnAction(e -> {
            System.out.println("\n[GUI EVENT] 'Play Selected Cards' Button Clicked!");
            for (CardNode node : new CardNode[]{node1, node2, node3, node4}) {
                if (node.isSelected()) {
                    System.out.println("  -> Selected: " + node.getCard());
                }
            }
        });

        // 6. Main Table Layout (Poker Table Dark-Green Felt Style)
        VBox root = new VBox(20, headerLabel, subLabel, handBox, playButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1e372a; -fx-padding: 40px;"); // Dark felt green

        // 7. Render Window
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}