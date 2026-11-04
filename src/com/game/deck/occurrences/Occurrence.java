package com.game.deck.occurrences;

import com.game.deck.model.GameState;

public abstract class Occurrence {
    private final String title;
    private final String description;

    public Occurrence(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public abstract void execute(GameState state);
    public abstract void onPass(GameState state);
    public abstract void onFail(GameState state);

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}