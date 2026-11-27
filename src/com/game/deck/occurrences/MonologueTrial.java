package com.game.deck.occurrences;

/**
 * Contains the MonologueTrial source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

/*
 * MonologueTrial is a concrete Occurrence mini-game that validates a sentence.
 * It reads fragments from the event model and rewards or penalizes GameState;
 * the current console/UI controllers decide when to display or submit it.
 */
public class MonologueTrial extends Occurrence {
    private final String promptQuestion;
    private final List<String> availableFragments;
    private final String targetAlignedPattern;
    private final boolean isHighRiskDire;

    /**
     * Performs the monologu\1 \2rial operation.
     */
    public MonologueTrial() {
        this(
                "When probability fails, how do you respond?",
                List.of("I adapt my hand", "through calculated discards", "I accept defeat"),
                "I adapt my hand through calculated discards",
                false
        );
    }

    /**
     * Performs the monologu\1 \2rial operation.
     */
    public MonologueTrial(String prompt, List<String> fragments, String targetPattern, boolean isHighRisk) {
        super("Inner Monologue Trial", "Debate identity and probability to earn rewards.");
        this.promptQuestion = prompt;
        this.availableFragments = fragments;
        this.targetAlignedPattern = targetPattern;
        this.isHighRiskDire = isHighRisk;
    }

    @Override
    /**
     * Performs the execute operation.
     */
    public void execute(GameState state) {
        // --- EVENT PRESENTATION ---
        System.out.println("\n=== [POST-ROUND OCCURRENCE: MONOLOGUE TRIAL] ===");
        System.out.println("PROMPT: \"" + promptQuestion + "\"");
        System.out.println("Available Sentence Fragments: " + availableFragments);
    }

    @Override
    /**
     * Performs the i\1 \2nteractive operation.
     */
    public boolean isInteractive() { return true; }

    @Override
    /**
     * Performs the creat\1 \2iew operation.
     */
    public Node createView(GameState state, Runnable onComplete) {
        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("occurrence-trial");
        Label prompt = new Label(promptQuestion);
        prompt.getStyleClass().add("occurrence-prompt");
        prompt.setWrapText(true);
        Label sentence = new Label("Select fragments in order.");
        sentence.getStyleClass().add("occurrence-result");
        List<String> selected = new ArrayList<>();
        FlowPane fragments = new FlowPane(8, 8);
        fragments.setAlignment(Pos.CENTER);
        for (String fragment : availableFragments) {
            Button button = new Button(fragment);
            button.getStyleClass().add("occurrence-fragment");
            button.setOnAction(event -> {
                selected.add(fragment);
                button.setDisable(true);
                sentence.setText(String.join(" ", selected));
            });
            fragments.getChildren().add(button);
        }
        Button submit = new Button("Submit Trial");
        submit.getStyleClass().add("btn-primary");
        submit.setOnAction(event -> {
            boolean passed = submitSentence(selected, state);
            sentence.setText(passed ? "Trial passed. Reward granted." : "Trial failed.");
            submit.setDisable(true);
            Button continueButton = new Button("Continue to Round");
            continueButton.getStyleClass().add("btn-primary");
            continueButton.setOnAction(ignored -> onComplete.run());
            root.getChildren().add(continueButton);
        });
        root.getChildren().addAll(prompt, sentence, fragments, submit);
        return root;
    }

    /**
     * Performs the submi\1 \2entence operation.
     */
    public boolean submitSentence(List<String> selectedFragments, GameState state) {
        // Join fragments in player order before comparing against the target.
        String builtSentence = String.join(" ", selectedFragments);
        System.out.println("[MONOLOGUE] Built Sentence: \"" + builtSentence + "\"");

        if (builtSentence.contains(targetAlignedPattern)) {
            System.out.println("[MONOLOGUE LOG] Sentence Alignment: PASSED!");
            onPass(state);
            return true;
        } else {
            System.out.println("[MONOLOGUE LOG] Sentence Alignment: FAILED!");
            onFail(state);
            return false;
        }
    }

    @Override
    /**
     * Performs the o\1 \2ass operation.
     */
    public void onPass(GameState state) {
        state.addCash(15);
        System.out.println("[REWARD] Passed Monologue Trial! Awarded +$15 Cash.");
    }

    @Override
    /**
     * Performs the o\1 \2ail operation.
     */
    public void onFail(GameState state) {
        if (isHighRiskDire) {
            state.deductCash(10);
            System.out.println("[DIRE CONFLICT] High-Risk Trial Failed! Penalty: -$10 Cash lost!");
        } else {
            System.out.println("[RESULT] Monologue Trial Failed. No rewards gained.");
        }
    }

    /**
     * Performs the ge\1 \2romp\1 \2uestion operation.
     */
    public String getPromptQuestion() { return promptQuestion; }
    /**
     * Performs the ge\1 \2vailabl\1 \2ragments operation.
     */
    public List<String> getAvailableFragments() { return availableFragments; }
}
