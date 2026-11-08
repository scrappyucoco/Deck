package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

/*
 * RationalistJoker is the first concrete Joker available from Shop.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class RationalistJoker extends Joker {
    public RationalistJoker() {
        super("The Rationalist", "+4 Mult when a hand is played", "joker_1");
    }

    @Override
    protected void applyBaseEffect(ScoringContext ctx) {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // Change 4.0 to tune this Joker's additive multiplier effect.
        ctx.addMult(4.0);
    }
}
