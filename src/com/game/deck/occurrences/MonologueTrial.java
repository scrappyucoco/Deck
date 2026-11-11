package com.game.deck.occurrences;

import com.game.deck.model.GameState;
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

    public MonologueTrial(String prompt, List<String> fragments, String targetPattern, boolean isHighRisk) {
        super("Inner Monologue Trial", "Debate identity and probability to earn rewards.");
        this.promptQuestion = prompt;
        this.availableFragments = fragments;
        this.targetAlignedPattern = targetPattern;
        this.isHighRiskDire = isHighRisk;
    }

    @Override
    public void execute(GameState state) {
        // --- EVENT PRESENTATION ---
        System.out.println("\n=== [POST-ROUND OCCURRENCE: MONOLOGUE TRIAL] ===");
        System.out.println("PROMPT: \"" + promptQuestion + "\"");
        System.out.println("Available Sentence Fragments: " + availableFragments);
    }

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
    public void onPass(GameState state) {
        state.addCash(15);
        System.out.println("[REWARD] Passed Monologue Trial! Awarded +$15 Cash.");
    }

    @Override
    public void onFail(GameState state) {
        if (isHighRiskDire) {
            state.deductCash(10);
            System.out.println("[DIRE CONFLICT] High-Risk Trial Failed! Penalty: -$10 Cash lost!");
        } else {
            System.out.println("[RESULT] Monologue Trial Failed. No rewards gained.");
        }
    }

    public String getPromptQuestion() { return promptQuestion; }
    public List<String> getAvailableFragments() { return availableFragments; }
}