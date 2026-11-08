package com.game.deck.views;

import com.game.deck.equipment.Joker;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/*
 * JokerNode is the View representation of an owned or shop-offered Joker.
 * It renders the asset/name, exposes a click-to-sell control for owned Jokers,
 * and participates in JavaFX drag-and-drop ordering managed by GameView.
 */
public class JokerNode extends StackPane {
    private final Joker joker;
    private final Button sellButton;

    public JokerNode(Joker joker) {
        this(joker, null);
    }

    public JokerNode(Joker joker, Runnable sellAction) {
        // --- JOKER VISUAL CONSTRUCTION ---
        this.joker = joker;
        setPrefSize(100, 130);
        getStyleClass().add("joker-node");

        Image image = loadImage(joker.getAssetPath());
        if (image == null) {
            image = loadImage("assets/cards/joker_1.png");
        }
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(getPrefWidth());
            imageView.setFitHeight(getPrefHeight());
            imageView.setPreserveRatio(true);
            imageView.setSmooth(false);
            getChildren().add(imageView);
        } else {
            Label placeholder = new Label(joker.getName());
            placeholder.getStyleClass().add("joker-placeholder");
            setAlignment(Pos.CENTER);
            getChildren().add(placeholder);
        }

        Label nameLabel = new Label(joker.getName());
        nameLabel.getStyleClass().add("joker-name");
        nameLabel.setMouseTransparent(true);
        VBox details = new VBox(nameLabel);
        details.setAlignment(Pos.BOTTOM_CENTER);
        details.setMouseTransparent(true);
        getChildren().add(details);

        sellButton = new Button("Sell");
        sellButton.getStyleClass().add("btn-sell");
        sellButton.setVisible(false);
        sellButton.setManaged(false);
        sellButton.setOnAction(event -> {
            System.out.println("[UI] Sell clicked for " + joker.getName());
            if (sellAction != null) {
                sellAction.run();
            }
        });
        StackPane.setAlignment(sellButton, Pos.TOP_RIGHT);
        getChildren().add(sellButton);

        setOnMouseClicked(event -> {
            // Owned nodes expose sell controls; shop offers pass a null action.
            if (sellAction != null) {
                sellButton.setVisible(!sellButton.isVisible());
                sellButton.setManaged(sellButton.isVisible());
                System.out.println("[UI] Joker selected: " + joker.getName()
                        + " | sellButtonVisible=" + sellButton.isVisible());
            }
        });

        setOnDragDetected(event -> {
            // Drag payload is the Joker name; GameView resolves the source node.
            if (sellAction == null) {
                return;
            }
            ClipboardContent content = new ClipboardContent();
            content.putString(joker.getName());
            startDragAndDrop(TransferMode.MOVE).setContent(content);
            setOpacity(0.7);
            event.consume();
        });
        setOnDragDone(event -> setOpacity(1.0));
    }

    private Image loadImage(String relativePath) {
        String withSlash = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        String withoutSlash = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        try {
            URL url = getClass().getResource(withSlash);
            if (url != null) {
                return new Image(url.toExternalForm());
            }
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(withoutSlash)) {
                if (stream != null) {
                    return new Image(stream);
                }
            }
            File rootFile = new File(withoutSlash);
            if (rootFile.isFile()) {
                return new Image(rootFile.toURI().toString());
            }
            File sourceFile = new File("src", withoutSlash);
            if (sourceFile.isFile()) {
                return new Image(sourceFile.toURI().toString());
            }
        } catch (Exception exception) {
            System.err.println("[WARNING] Could not load joker image " + relativePath
                    + ": " + exception.getMessage());
        }
        return null;
    }

    public Joker getJoker() {
        return joker;
    }
}
