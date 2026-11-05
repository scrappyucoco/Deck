package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

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