package com.game.deck.equipment;

/**
 * Contains the RationalistJoker source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.engine.ScoringContext;

/*
 * RationalistJoker is the first concrete Joker available from Shop.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class RationalistJoker extends Joker {
    /**
     * Performs the rationalis\1 \2oker operation.
     */
    public RationalistJoker() {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // (name, description, assetName, shopCost, sellValue).
        super("The Rational", "+4 Mult when a hand is played", "joker_2", 4, 3);
    }

    //[JOKER ABILITIES HERE]
    @Override
    /**
     * Performs the appl\1 \2as\1 \2ffect operation.
     */
    protected void applyBaseEffect(ScoringContext ctx) {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // Change 4.0 to tune this Joker's additive multiplier effect.
        ctx.addMult(4.0);
    }
}
