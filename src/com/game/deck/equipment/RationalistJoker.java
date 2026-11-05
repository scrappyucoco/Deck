package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

/**
 * Copyable starter joker template. Duplicate this class and change the
 * metadata/effect to create another shop joker.
 */
public class RationalistJoker extends Joker {
    public RationalistJoker() {
        super("The Rationalist", "+4 Mult when a hand is played", "joker_1");
    }

    @Override
    protected void applyBaseEffect(ScoringContext ctx) {
        ctx.addMult(4.0);
    }
}
