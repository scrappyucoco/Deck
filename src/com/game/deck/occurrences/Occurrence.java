package com.game.deck.occurrences;

import com.game.deck.model.GameState;

/*
 * Occurrence is the abstract Model/Domain contract for post-round events.
 * Concrete occurrences implement execute/onPass/onFail and mutate GameState;
 * controllers can trigger them without coupling event rules to JavaFX views.
 */
public abstract class Occurrence {
    private final String title;
    private final String description;

    public Occurrence(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // [HOOK: ADD CUSTOM OCCURRENCES/EVENTS]
    // Create class MyOccurrence extends Occurrence and implement these three
    // lifecycle methods, then invoke execute(state) from a round controller.
    public abstract void execute(GameState state);
    public abstract void onPass(GameState state);
    public abstract void onFail(GameState state);

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}