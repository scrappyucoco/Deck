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

        // Try loading PNG asset image
        boolean imageLoaded = false;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(card.getAssetPath());
            if (is != null) {
                ImageView cardImageView = new ImageView(new Image(is));
                cardImageView.setFitWidth(100);
                cardImageView.setFitHeight(140);
                getChildren().add(cardImageView);
                imageLoaded = true;
            }
        } catch (Exception ignored) {}

        // Fallback Inline CSS Card View (If asset PNG isn't in assets/ folder yet)
        if (!imageLoaded) {
            String suitColor = (card.getSuit().name().equals("HEARTS") || card.getSuit().name().equals("DIAMONDS")) ? "#e74c3c" : "#2c3e50";

            // Outer Card Frame with Rounded Corners & Shadow Effect
            setStyle("-fx-background-color: white; " +
                     "-fx-border-color: #bdc3c7; " +
                     "-fx-border-width: 2px; " +
                     "-fx-border-radius: 8px; " +
                     "-fx-background-radius: 8px; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 3);");

            Label rankLabel = new Label(card.getRank().getAssetName().toUpperCase());
            rankLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + suitColor + ";");

            Label suitLabel = new Label(card.getSuit().getDisplayName());
            suitLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + suitColor + ";");

            VBox cardContent = new VBox(5, rankLabel, suitLabel);
            cardContent.setAlignment(Pos.CENTER);

            // Display Enhancement Tag if Card is Buffed
            if (card.getEnhancement() != Enhancement.NONE) {
                Label enhLabel = new Label("[" + card.getEnhancement().name() + "]");
                enhLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                cardContent.getChildren().add(enhLabel);
            }

            getChildren().add(cardContent);
        }

        // Click Event: Pop card up/down when clicked
        setOnMouseClicked(e -> toggleSelection());
    }

    public void toggleSelection() {
        isSelected = !isSelected;
        TranslateTransition anim = new TranslateTransition(Duration.millis(150), this);
        anim.setByY(isSelected ? -25 : 25); // Lift card up 25px when clicked
        anim.play();

        // Highlight border when selected
        if (isSelected) {
            setStyle(getStyle() + "-fx-border-color: #f1c40f; -fx-border-width: 3px;");
        } else {
            setStyle(getStyle().replace("-fx-border-color: #f1c40f; -fx-border-width: 3px;", "-fx-border-color: #bdc3c7; -fx-border-width: 2px;"));
        }
    }

    public Card getCard() { return card; }
    public boolean isSelected() { return isSelected; }
}