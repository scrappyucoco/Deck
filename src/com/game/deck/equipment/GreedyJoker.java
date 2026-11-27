package com.game.deck.equipment;

/**
 * Contains the GreedyJoker source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.engine.ScoringContext;
import com.game.deck.model.Suit;

/*
 * GreedyJoker rewards a hand made entirely of Diamond cards.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class GreedyJoker extends Joker {
    /**
     * Performs the greed\1 \2oker operation.
     */
    public GreedyJoker() {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        //(name, description, assetName, shopCost, sellValue)
        super("The Greedy", "+5 Mult when a Diamond Suit is played", "joker_2", 4, 3);
    }

    //[JOKER ABILITIES HERE]
    @Override
    /**
     * Performs the appl\1 \2as\1 \2ffect operation.
     */
    protected void applyBaseEffect(ScoringContext ctx) {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // Change 4.0 to tune this Joker's additive multiplier effect.
        if (ctx.hasPlayedSuit(Suit.DIAMONDS)) {
            ctx.addMult(5.0);
        }
    }
}
