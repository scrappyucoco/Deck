package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

public class EgoRelic extends Item {
    public EgoRelic() {
        super("Ego Relic", "Permanent +3 Mult per Enhanced Played Card", ItemRarity.GOLD, false, "relic_ego");
    }

    @Override
    public void onJokerTrigger(ScoringContext ctx) {
        ctx.addMult(3.0);
        System.out.println("       [ITEM: Ego Relic] +3 Mult Triggered!");
    }
}