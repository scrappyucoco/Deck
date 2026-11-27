package com.game.deck.equipment;

/**
 * Contains the EgoRelic source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.engine.ScoringContext;

/*
 * EgoRelic is a concrete Item that adds multiplier during a Joker trigger.
 * It depends on ScoringContext only and is socketed through Joker.socketItem().
 */
public class EgoRelic extends Item {
    /**
     * Performs the eg\1 \2elic operation.
     */
    public EgoRelic() {
        super("Ego Relic", "Permanent +3 Mult per Enchanted Played Card", ItemRarity.GOLD, false, "relic_ego");
    }

    @Override
    /**
     * Performs the o\1 \2oke\1 \2rigger operation.
     */
    public void onJokerTrigger(ScoringContext ctx) {
        ctx.addMult(3.0);
        System.out.println("       [ITEM: Ego Relic] +3 Mult Triggered!");
    }
}
