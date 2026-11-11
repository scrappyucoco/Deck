package com.game.deck.occurrences;

import com.game.deck.model.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.concurrent.ThreadLocalRandom;

public class CashWindfallOccurrence extends Occurrence {
    public CashWindfallOccurrence() {
        super("Cash Windfall", "Receive a random immediate reward of $5-$15.");
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public void execute(GameState state) {
        int reward = ThreadLocalRandom.current().nextInt(5, 16);
        state.addCash(reward);
        System.out.println("[OCCURRENCE] Cash Windfall: +$" + reward);
    }

    @Override
    public Node createView(GameState state, Runnable onComplete) {
        int reward = ThreadLocalRandom.current().nextInt(5, 16);
        state.addCash(reward);
        System.out.println("[OCCURRENCE] Cash Windfall: +$" + reward);

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(25));
        box.getStyleClass().add("occurrence-trial");

        Label header = new Label("Windfall Granted!");
        header.getStyleClass().add("occurrence-prompt");

        Label winLabel = new Label("You won $" + reward + "!");
        winLabel.getStyleClass().add("occurrence-result");
        winLabel.setStyle("-fx-font-size: 20px;");

        Label balanceLabel = new Label("Current Cash: $" + state.getCash());
        balanceLabel.getStyleClass().add("occurrence-description");

        Button proceedBtn = new Button("Collect & Start Round");
        proceedBtn.getStyleClass().add("btn-primary");
        proceedBtn.setOnAction(e -> onComplete.run());

        box.getChildren().addAll(header, winLabel, balanceLabel, proceedBtn);
        return box;
    }

    @Override public void onPass(GameState state) { }
    @Override public void onFail(GameState state) { }
}
