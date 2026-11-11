package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

/*
 * PolishedGem is a concrete Item that adds flat chips on each Joker trigger.
 * It is reusable and demonstrates the standard Item extension pattern.
 */
public class PolishedGem extends Item {
    public PolishedGem() {
        super("Polished Gem", "+15 Flat Chips", ItemRarity.COMMON, false, "gem_polished");
    }

    @Override
    public void onJokerTrigger(ScoringContext ctx) {
        ctx.addChips(15);
        System.out.println("       [ITEM: Polished Gem] +15 Chips");
    }
}