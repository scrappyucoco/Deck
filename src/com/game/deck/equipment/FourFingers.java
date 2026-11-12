package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

/*
 * GreedyJoker rewards a hand made entirely of Diamond cards.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class FourFingers extends Joker {
    private int MinimumOfCards = 4;

    public FourFingers() {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        //(name, description, assetName, shopCost, sellValue)
        super("Four Fingers", "Straight and Flush can be made with 4 cards", "joker_2", 0, 1);
    }

    public int GetMinimumOfCards(){
        return MinimumOfCards;
    }

    //[JOKER ABILITIES HERE]
    @Override
    protected void applyBaseEffect(ScoringContext ctx) {}
}
