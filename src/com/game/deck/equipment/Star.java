package com.game.deck.equipment;

/**
 * Contains the Star source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.GameState;
import com.game.deck.model.Card;
import com.game.deck.model.Suit;
import java.util.List;

/**
 * Represents the star in the game's model, engine, equipment, occurrence, or view layer.
 */
public class Star extends TarotCards{
    private final int MAX_NUMBER_CARDS = 3;
    /**
     * Performs the star operation.
     */
    public Star(){
        super("The Star", "Transform upto 3 Cards into a Diamond Suit", "star_placeholder", 4, 3);
    }

    @Override 
    /**
     * Performs the appl\1 \2ffect operation.
     */
        protected void applyEffect(GameState state, List<Card> selectedCard){
            int i = 0;
            for(Card card: selectedCard){
                if(i == MAX_NUMBER_CARDS) break;
                card.setSuit(Suit.DIAMONDS);
                i++;
            }
        };
}
