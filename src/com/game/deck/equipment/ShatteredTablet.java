package com.game.deck.equipment;

/**
 * Contains the ShatteredTablet source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.engine.ScoringContext;

/*
 * ShatteredTablet is a consumable Item that doubles accumulated chips once.
 * Joker removes consumable sockets after evaluation completes.
 */
public class ShatteredTablet extends Item {
    /**
     * Performs the shattere\1 \2ablet operation.
     */
    public ShatteredTablet() {
        super("Shattered Tablet", "Doubles Base Chips (Single-Use)", ItemRarity.RARE, true, "mirror_shattered");
    }

    @Override
    /**
     * Performs the o\1 \2oke\1 \2rigger operation.
     */
    public void onJokerTrigger(ScoringContext ctx) {
        ctx.multiplyChips(2.0);
        System.out.println("       [ITEM: Shattered Mirror] x2 Base Chips -> Consumed & Shattered!");
    }
}
