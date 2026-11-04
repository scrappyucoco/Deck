package com.game.deck.occurrences;

import com.game.deck.model.GameState;

public class RandomEvent extends Occurrence {
    public RandomEvent() {
        super("Chance Encounter", "A mysterious event alters your upcoming round.");
    }

    @Override
    public void execute(GameState state) {
        double roll = Math.random();
        System.out.println("\n=== [POST-ROUND OCCURRENCE: RANDOM EVENT] ===");
        if (roll > 0.5) {
            state.addCash(10);
            System.out.println("[EVENT RESULT] Windfall! Gained +$10 Cash.");
        } else {
            state.addExtraDiscard(1);
            System.out.println("[EVENT RESULT] Tactical Foresight! Granted +1 Extra Discard.");
        }
    }

    @Override public void onPass(GameState state) {}
    @Override public void onFail(GameState state) {}
}