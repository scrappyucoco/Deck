package com.game.deck.views;

/**
 * Contains the TarotNode source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.equipment.TarotCards;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * View for a shop-offered or owned Tarot consumable.
 */
public class TarotNode extends StackPane {
    public static final double CARD_WIDTH = 150.0;
    public static final double CARD_HEIGHT = 210.0;
    private static final Rectangle2D SPRITE_VIEWPORT = new Rectangle2D(38, 0, 180, 256);
    private final TarotCards tarotCard;
    private final Button sellButton;

    /**
     * Performs the taro\1 \2ode operation.
     */
    public TarotNode(TarotCards tarotCard) {
        this(tarotCard, null, null);
    }

    /**
     * Performs the taro\1 \2ode operation.
     */
    public TarotNode(TarotCards tarotCard, Runnable useAction, Runnable sellAction) {
        this.tarotCard = Objects.requireNonNull(tarotCard, "tarotCard");
        setMinSize(CARD_WIDTH, CARD_HEIGHT);
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        StackPane artwork = new StackPane();
        artwork.setMinSize(CARD_WIDTH, CARD_HEIGHT);
        artwork.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        artwork.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        artwork.getStyleClass().add("tarot-node");
        artwork.setMouseTransparent(true);
        Tooltip tooltip = new Tooltip(tarotCard.getName() + "\n" + tarotCard.getDescription());
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(240);
        tooltip.setShowDelay(Duration.millis(250));
        tooltip.getStyleClass().add("equipment-tooltip");
        Tooltip.install(this, tooltip);

        Image image = loadImage(tarotCard.getAssetPath());
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setViewport(SPRITE_VIEWPORT);
            imageView.setFitWidth(CARD_WIDTH);
            imageView.setFitHeight(CARD_HEIGHT);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(false);
            artwork.getChildren().add(imageView);
        } else {
            Label placeholder = new Label(tarotCard.getName());
            placeholder.getStyleClass().add("tarot-placeholder");
            placeholder.setWrapText(true);
            placeholder.setMaxWidth(90);
            artwork.getChildren().add(placeholder);
        }
        Label nameLabel = new Label(tarotCard.getName());
        nameLabel.getStyleClass().add("equipment-name");
        nameLabel.setMouseTransparent(true);
        StackPane.setAlignment(nameLabel, Pos.BOTTOM_CENTER);
        artwork.getChildren().add(nameLabel);

        

        sellButton = new Button("Sell");
        sellButton.getStyleClass().add("btn-sell");
        sellButton.setVisible(false);
        sellButton.setManaged(false);
        sellButton.setOnAction(event -> {
            System.out.println("[UI] Sell clicked for " + tarotCard.getName());
            if (sellAction != null) {
                sellAction.run();
            }
        });
        StackPane.setAlignment(sellButton, Pos.TOP_RIGHT);

        if (useAction != null) {
            Button useButton = new Button("Use");
            useButton.getStyleClass().add("btn-use");
            useButton.setVisible(false);
            useButton.setManaged(false);
            useButton.setOnAction(event -> useAction.run());
            StackPane.setAlignment(useButton, Pos.BOTTOM_CENTER);
            getChildren().addAll(artwork, sellButton, useButton);
            setOnMouseClicked(event -> {
                useButton.setVisible(!useButton.isVisible());
                useButton.setManaged(useButton.isVisible());
                sellButton.setVisible(!sellButton.isVisible());
                sellButton.setManaged(sellButton.isVisible());
                System.out.println("[UI] Tarot selected: " + tarotCard.getName()
                        + " | useButtonVisible=" + useButton.isVisible()
                        + " | tarotBounds=" + getBoundsInParent());
            });
            useButton.setOnMouseClicked(event -> {
                System.out.println("[UI] Use clicked for " + tarotCard.getName());
                event.consume();
            });
        } else {
            getChildren().addAll(artwork, sellButton);
        }
    }

    /**
     * Performs the ge\1 \2aro\1 \2ard operation.
     */
    public TarotCards getTarotCard() {
        return tarotCard;
    }

    private Image loadImage(String relativePath) {
        String withSlash = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        String withoutSlash = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        try {
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

            File[] candidates = {
                    new File(withoutSlash),
                    new File("resources", withoutSlash)
            };
            for (File candidate : candidates) {
                if (candidate.isFile()) {
                    Image image = new Image(candidate.toURI().toString());
                    if (!image.isError()) {
                        return image;
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("[WARNING] Could not load Tarot image " + relativePath
                    + ": " + exception.getMessage());
        }
        return null;
    }
}
