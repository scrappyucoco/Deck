package com.game.deck.equipment;

/**
 * Contains the PolishedGem source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.engine.ScoringContext;

/*
 * PolishedGem is a concrete Item that adds flat chips on each Joker trigger.
 * It is reusable and demonstrates the standard Item extension pattern.
 */
public class PolishedGem extends Item {
    /**
     * Performs the polishe\1 \2em operation.
     */
    public PolishedGem() {
        super("Polished Gem", "+15 Flat Chips", ItemRarity.COMMON, false, "gem_polished");
    }

    @Override
    /**
     * Performs the o\1 \2oke\1 \2rigger operation.
     */
    public void onJokerTrigger(ScoringContext ctx) {
        ctx.addChips(15);
        System.out.println("       [ITEM: Polished Gem] +15 Chips");
    }
}
