package com.game.deck.views;

/**
 * Contains the JokerNode source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.equipment.Joker;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URISyntaxException;

/*
 * JokerNode is the View representation of an owned or shop-offered Joker.
 * It renders the asset/name, exposes a click-to-sell control for owned Jokers,
 * and participates in JavaFX drag-and-drop ordering managed by GameView.
 */
public class JokerNode extends StackPane {
    public static final double CARD_WIDTH = 150.0;
    public static final double CARD_HEIGHT = 210.0;
    private static final Rectangle2D SPRITE_VIEWPORT = new Rectangle2D(38, 0, 180, 256);
    private final Joker joker;
    private final Button sellButton;

    /**
     * Performs the joke\1 \2ode operation.
     */
    public JokerNode(Joker joker) {
        this(joker, null);
    }

    /**
     * Performs the joke\1 \2ode operation.
     */
    public JokerNode(Joker joker, Runnable sellAction) {
        // --- JOKER VISUAL CONSTRUCTION ---
        this.joker = joker;
        setMinSize(CARD_WIDTH, CARD_HEIGHT);
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        getStyleClass().add("joker-node");
        Tooltip tooltip = new Tooltip(joker.getName() + "\n" + joker.getDescription());
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(240);
        tooltip.setShowDelay(javafx.util.Duration.millis(250));
        tooltip.getStyleClass().add("equipment-tooltip");
        Tooltip.install(this, tooltip);

        Image image = loadImage(joker.getAssetPath());
        if (image == null) {
            image = loadImage(Joker.DEFAULT_ASSET_PATH);
        }
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setViewport(SPRITE_VIEWPORT);
            imageView.setFitWidth(CARD_WIDTH);
            imageView.setFitHeight(CARD_HEIGHT);
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
            // --- CLASSPATH/JAR RESOLUTION ---
            URL url = getClass().getResource(withSlash);
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                if (!image.isError()) {
                    return image;
                }
            }
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(withoutSlash)) {
                if (stream != null) {
                    Image image = new Image(stream);
                    if (!image.isError()) {
                        return image;
                    }
                }
            }

            // --- IDE/LOOSE RESOURCE RESOLUTION ---
            // Eclipse may leave resources under resources/ instead of copying
            // them into bin/, so check project and application layouts.
            File applicationDirectory = getApplicationDirectory();
            File[] projectCandidates = {
                    new File(withoutSlash),
                    new File("resources", withoutSlash),
                    new File("src", withoutSlash),
                    new File("src", "resources/" + withoutSlash)
            };
            for (File candidate : projectCandidates) {
                if (candidate.isFile()) {
                    Image image = new Image(candidate.toURI().toString());
                    if (!image.isError()) {
                        System.out.println("[ASSET] Loaded Joker image: " + candidate.getPath());
                        return image;
                    }
                }
            }

            if (applicationDirectory != null) {
                File[] applicationCandidates = {
                        new File(applicationDirectory, withoutSlash),
                        new File(applicationDirectory, "resources/" + withoutSlash)
                };
                for (File candidate : applicationCandidates) {
                    if (candidate.isFile()) {
                        Image image = new Image(candidate.toURI().toString());
                        if (!image.isError()) {
                            System.out.println("[ASSET] Loaded Joker image: " + candidate.getPath());
                            return image;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("[WARNING] Could not load joker image " + relativePath
                    + ": " + exception.getMessage());
        }
        return null;
    }

    private File getApplicationDirectory() {
        try {
            URL codeSource = getClass().getProtectionDomain().getCodeSource().getLocation();
            File location = new File(codeSource.toURI());
            return location.isFile() ? location.getParentFile() : location;
        } catch (URISyntaxException | SecurityException | NullPointerException exception) {
            System.err.println("[WARNING] Could not resolve application asset directory: "
                    + exception.getMessage());
            return null;
        }
    }

    /**
     * Performs the ge\1 \2oker operation.
     */
    public Joker getJoker() {
        return joker;
    }
}
