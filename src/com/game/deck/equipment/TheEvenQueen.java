package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;
import com.game.deck.model.Rank;
import com.game.deck.model.Suit;

/*
 * GreedyJoker rewards a hand made entirely of Diamond cards.
 * It is a copyable Equipment template: duplicate the class, change the
 * constructor metadata, and implement applyBaseEffect(ScoringContext).
 */
public class TheEvenQueen extends Joker {
    public TheEvenQueen() {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        //(name, description, assetName, shopCost, sellValue)
        super("The Even Queen", "+5 Mult when an Even card is played, +10 chips if Queen card is played", "joker_2", 4, 3);
    }

    //[JOKER ABILITIES HERE]
    @Override
    protected void applyBaseEffect(ScoringContext ctx) {
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // Change 4.0 to tune this Joker's additive multiplier effect.
        boolean hasValidCard = ctx.getPlayedCards().stream()
                .anyMatch(card -> card.getRank().isEven());

        if (hasValidCard) {
            ctx.addMult(5.0);
        }

        boolean hasQUEEN = ctx.getPlayedCards().stream()
                .anyMatch(card -> card.getRank() == Rank.QUEEN);

        if (hasQUEEN) {
            ctx.addChips(10);
        }

        //im not sure what im doing but i wanted it to behave like instead of just having at least one pressence of even
        //number, it should add mult for each even numbers played. Same with odd
    }
}
