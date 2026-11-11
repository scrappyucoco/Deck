package com.game.deck.occurrences;

import com.game.deck.model.GameState;
import javafx.scene.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/*
 * Occurrence is the abstract contract for zero-cost pre-round events.
 * Concrete occurrences implement execute/onPass/onFail and mutate GameState;
 * controllers can trigger them without coupling event rules to JavaFX views.
 */
public abstract class Occurrence {
    private static final Random RANDOM = new Random();
    private static final List<Supplier<? extends Occurrence>> OCCURRENCE_POOL = List.of(
            MonologueTrial::new,
            CashWindfallOccurrence::new
    );
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

    public boolean isInteractive() { return false; }

    public Node createView(GameState state, Runnable onComplete) { return null; }

    public static List<Occurrence> createRandomOffers(int count) {
        if (count < 1) throw new IllegalArgumentException("Occurrence offer count must be positive");
        List<Supplier<? extends Occurrence>> pool = new ArrayList<>(OCCURRENCE_POOL);
        Collections.shuffle(pool, RANDOM);
        int offerCount = Math.min(count, pool.size());
        List<Occurrence> offers = new ArrayList<>(offerCount);
        for (int i = 0; i < offerCount; i++) {
            offers.add(pool.get(i).get());
        }
        return List.copyOf(offers);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}