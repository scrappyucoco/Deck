package com.game.deck.views;

import com.game.deck.model.Card;
import com.game.deck.model.Enhancement;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.InputStream;

public class CardNode extends StackPane {
    private final Card card;
    private boolean isSelected;

    public CardNode(Card card) {
        this.card = card;
        this.isSelected = false;

        setPrefSize(100, 140);
        getStyleClass().add("card-node");

        // Attempt loading individual PNG asset from assets/cards/
        boolean imageLoaded = false;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(card.getAssetPath());
            if (is != null) {
                ImageView cardImageView = new ImageView(new Image(is));
                cardImageView.setFitWidth(100);
                cardImageView.setFitHeight(140);
                cardImageView.setSmooth(false); // Sharp pixel art rendering
                getChildren().add(cardImageView);
                imageLoaded = true;
            }
        } catch (Exception ignored) {}

        // Fallback Shape/Text View using external CSS classes
        if (!imageLoaded) {
            String suitClass = (card.getSuit().name().equals("HEARTS") || card.getSuit().name().equals("DIAMONDS"))
                    ? "card-red-suit" : "card-black-suit";

            Label rankLabel = new Label(card.getRank().getAssetName().toUpperCase());
            rankLabel.getStyleClass().addAll("card-rank-text", suitClass);

            Label suitLabel = new Label(card.getSuit().getDisplayName());
            suitLabel.getStyleClass().addAll("card-suit-text", suitClass);

            VBox cardContent = new VBox(4, rankLabel, suitLabel);
            cardContent.setAlignment(Pos.CENTER);

            if (card.getEnhancement() != Enhancement.NONE) {
                Label enhLabel = new Label("[" + card.getEnhancement().name() + "]");
                enhLabel.getStyleClass().add("card-enhancement-badge");
                cardContent.getChildren().add(enhLabel);
            }

            getChildren().add(cardContent);
        }

        // Selection click toggle
        setOnMouseClicked(e -> toggleSelection());
    }

    public void toggleSelection() {
        isSelected = !isSelected;

        // Smooth translate animation
        TranslateTransition anim = new TranslateTransition(Duration.millis(150), this);
        anim.setByY(isSelected ? -25 : 25);
        anim.play();

        // Toggle CSS class for yellow border glow
        if (isSelected) {
            getStyleClass().add("card-node-selected");
        } else {
            getStyleClass().remove("card-node-selected");
        }
    }

    public Card getCard() { return card; }
    public boolean isSelected() { return isSelected; }
}