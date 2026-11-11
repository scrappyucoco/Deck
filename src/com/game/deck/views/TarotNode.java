package com.game.deck.views;

import com.game.deck.equipment.TarotCards;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * View for a shop-offered or owned Tarot consumable.
 */
public class TarotNode extends VBox {
    private final TarotCards tarotCard;

    public TarotNode(TarotCards tarotCard) {
        this(tarotCard, null);
    }

    public TarotNode(TarotCards tarotCard, Runnable useAction) {
        this.tarotCard = Objects.requireNonNull(tarotCard, "tarotCard");
        setAlignment(Pos.CENTER);
        setSpacing(5);

        StackPane artwork = new StackPane();
        artwork.setMinSize(100, 130);
        artwork.setPrefSize(100, 130);
        artwork.getStyleClass().add("tarot-node");
        Tooltip.install(artwork, new Tooltip(
                tarotCard.getName() + "\n" + tarotCard.getDescription()));

        Image image = loadImage(tarotCard.getAssetPath());
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(130);
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
        getChildren().add(artwork);

        if (useAction != null) {
            Button useButton = new Button("Use");
            useButton.getStyleClass().add("btn-primary");
            useButton.setOnAction(event -> useAction.run());
            getChildren().add(useButton);
        }
    }

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
