package com.game.deck.views;

import com.game.deck.model.Card;
import com.game.deck.model.Enchantment;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/*
 * CardNode is the View representation of one model Card.
 * It loads the card asset, crops transparent texture padding, renders fallback
 * text/enchantment state, and exposes selection state to GameView controllers.
 */
public class CardNode extends StackPane {
    private static final double CARD_WIDTH = 90;
    private static final double CARD_HEIGHT = 130;
    private static final Rectangle2D CARD_VIEWPORT = new Rectangle2D(38, 0, 180, 256);

    private final Card card;
    private boolean selected;

    public CardNode(Card card) {
        this(card, card.getAssetPath());
    }

    public CardNode(Card card, String imagePath) {
        // --- CARD VISUAL CONSTRUCTION ---
        this.card = Objects.requireNonNull(card, "card");
        Objects.requireNonNull(imagePath, "imagePath");
        setMinSize(CARD_WIDTH, CARD_HEIGHT);
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        getStyleClass().add("card-node");

        Image image = loadImage(imagePath);
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setViewport(CARD_VIEWPORT);
            imageView.setFitWidth(CARD_WIDTH);
            imageView.setFitHeight(CARD_HEIGHT);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(false);
            getChildren().add(imageView);
        } else {
            String suitClass = card.getSuit().isRed() ? "card-red-suit" : "card-black-suit";
            Label rankLabel = new Label(card.getRank().getAssetName());
            rankLabel.getStyleClass().addAll("card-rank-text", suitClass);
            Label suitLabel = new Label(card.getSuit().getSymbol());
            suitLabel.getStyleClass().addAll("card-suit-text", suitClass);
            VBox fallback = new VBox(2, rankLabel, suitLabel);
            fallback.setAlignment(Pos.CENTER);
            getChildren().add(fallback);
        }

        if (card.getEnchantment() != null && card.getEnchantment() != Enchantment.NONE) {
            Label badge = new Label("[" + card.getEnchantment().name() + "]");
            badge.getStyleClass().add("card-enhancement-badge");
            VBox overlay = new VBox(badge);
            overlay.setAlignment(Pos.BOTTOM_CENTER);
            overlay.setMouseTransparent(true);
            getChildren().add(overlay);
        }

        setOnMouseClicked(event -> toggleSelection());
    }

    private Image loadImage(String relativePath) {
        // Use the shared four-tier asset strategy for IDE, classpath, and JAR runs.
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
            System.err.println("[WARNING] Could not load card image " + relativePath + ": " + exception.getMessage());
        }
        return null;
    }

    public void toggleSelection() {
        setSelected(!selected);
    }

    public void setSelected(boolean selected) {
        // --- SELECTION STATE ---
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        if (selected) {
            if (!getStyleClass().contains("card-node-selected")) {
                getStyleClass().add("card-node-selected");
            }
        } else {
            getStyleClass().remove("card-node-selected");
        }
        System.out.printf("[CARD] %s | rankValue=%d | baseChips=%d | chipValue=%d | selected=%s%n",
                card,
                card.getRank().getValue(),
                card.getRank().getBaseChips(),
                card.getChipValue(),
                selected);
    }

    public Card getCard() {
        return card;
    }

    public boolean isSelected() {
        return selected;
    }
}
