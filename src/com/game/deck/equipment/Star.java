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
    private static final int MAX_NUMBER_CARDS = 3;
    /**
     * Performs the star operation.
     */
    public Star(){
        super("The Star", "Transform up to 3 Cards into a Diamond Suit",
                "star_placeholder", 5, 4, MAX_NUMBER_CARDS);
    }

    @Override 
    /**
     * Performs the appl\1 \2ffect operation.
     */
        protected void applyEffect(GameState state, List<Card> selectedCard){
            int transformed = 0;
            for(Card card: selectedCard){
                if(transformed == MAX_NUMBER_CARDS) break;
                String previousCard = card.toString();
                card.setSuit(Suit.DIAMONDS);
                transformed++;
                System.out.println("[TAROT] The Star transformed " + previousCard
                        + " into " + card + ".");
            }
            System.out.println("[TAROT] The Star transformed " + transformed + " card(s) total.");
        };
}
