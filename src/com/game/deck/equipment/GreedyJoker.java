package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;
import com.game.deck.engine.ScoringEngine;
import com.game.deck.model.*;

/*
 * RationalistJoker is the first concrete Joker available from Shop.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class GreedyJoker extends Joker {
    public GreedyJoker() {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        //(name, description, assetName, shopCost, sellValue)
        super("The Greedy", "+3 Mult when a Diamond Suit is played", "joker_2", 3, 2);
    }

    //[JOKER ABILITIES HERE]
    @Override
    protected void applyBaseEffect(ScoringContext ctx) {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // Change 4.0 to tune this Joker's additive multiplier effect.
        for(Card card: ScoringEngine.PlayedCards){
            if(card.getSuit() == Suit.DIAMONDS){
                ctx.addMult(3.0);
                break;
            }
        }
    }
}
