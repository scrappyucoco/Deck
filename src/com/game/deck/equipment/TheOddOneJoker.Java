package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;
import com.game.deck.model.Suit;

/*
 * GreedyJoker rewards a hand made entirely of Diamond cards.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class TheOddOne extends Joker {
    public TheOddOne() {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        //(name, description, assetName, shopCost, sellValue)
        super("The Greedy", "+5 Mult when a Diamond Suit is played", "joker_2", 4, 3);
    }

    //[JOKER ABILITIES HERE]
    @Override
    protected void applyBaseEffect(ScoringContext ctx) {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // Change 4.0 to tune this Joker's additive multiplier effect.
        boolean hasValidCard = ctx.getPlayedCards().stream()
                .anyMatch(card -> card.getRank().isOdd());

        if (hasValidCard) {
            ctx.addMult(5.0);
        }
    }
}
