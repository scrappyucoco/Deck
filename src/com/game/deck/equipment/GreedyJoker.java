package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;
import com.game.deck.model.Suit;

/*
 * GreedyJoker rewards a hand made entirely of Diamond cards.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class GreedyJoker extends Joker {
    public GreedyJoker() {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        //(name, description, assetName, shopCost, sellValue)
        super("The Odd One", "+5 Mult for each Odd-Numbered Cart is played", "joker_2", 4, 3);
    }

    //[JOKER ABILITIES HERE]
    @Override
    protected void applyBaseEffect(ScoringContext ctx) {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // Change 4.0 to tune this Joker's additive multiplier effect.
        if (ctx.hasPlayedSuit(Suit.DIAMONDS)) {
            ctx.addMult(5.0);
        }
    }
}
