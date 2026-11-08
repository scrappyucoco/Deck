package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

/*
 * ShatteredTablet is a consumable Item that doubles accumulated chips once.
 * Joker removes consumable sockets after evaluation completes.
 */
public class ShatteredTablet extends Item {
    public ShatteredTablet() {
        super("Shattered Tablet", "Doubles Base Chips (Single-Use)", ItemRarity.RARE, true, "mirror_shattered");
    }

    @Override
    public void onJokerTrigger(ScoringContext ctx) {
        ctx.multiplyChips(2.0);
        System.out.println("       [ITEM: Shattered Mirror] x2 Base Chips -> Consumed & Shattered!");
    }
}