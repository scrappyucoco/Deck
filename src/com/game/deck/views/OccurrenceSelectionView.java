package com.game.deck.views;

import com.game.deck.model.GameState;
import com.game.deck.occurrences.Occurrence;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;

public final class OccurrenceSelectionView extends StackPane {
    private final GameState gameState;
    private final Runnable onComplete;
    private final VBox content = new VBox(16);

    public OccurrenceSelectionView(GameState gameState, Runnable onComplete) {
        this.gameState = gameState;
        this.onComplete = onComplete;
        getStyleClass().add("occurrence-modal-backdrop");
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("occurrence-modal");
        getChildren().add(content);
        showOffers();
    }

    private void showOffers() {
        Label title = new Label("PRE-ROUND OCCURRENCE");
        title.getStyleClass().add("occurrence-title");
        Label subtitle = new Label("Choose one free event before starting the round.");
        subtitle.getStyleClass().add("occurrence-subtitle");
        HBox cards = new HBox(18);
        cards.setAlignment(Pos.CENTER);
        for (Occurrence occurrence : Occurrence.createRandomOffers(2)) {
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER);
            card.setPrefWidth(230);
            card.getStyleClass().add("occurrence-card");
            Label name = new Label(occurrence.getTitle());
            name.getStyleClass().add("occurrence-card-title");
            Label description = new Label(occurrence.getDescription());
            description.setWrapText(true);
            description.getStyleClass().add("occurrence-description");
            Button choose = new Button("Choose (Free)");
            choose.getStyleClass().add("btn-primary");
            choose.setOnAction(event -> select(occurrence));
            card.getChildren().addAll(name, description, choose);
            cards.getChildren().add(card);
        }
        content.getChildren().setAll(title, subtitle, cards);
    }

    private void select(Occurrence occurrence) {
        if (!occurrence.isInteractive()) {
            occurrence.execute(gameState);
            onComplete.run();
            return;
        }
        Node eventView = occurrence.createView(gameState, onComplete);
        if (eventView == null) throw new IllegalStateException("Interactive occurrence did not provide a view");
        content.getChildren().setAll(eventView);
    }
}
