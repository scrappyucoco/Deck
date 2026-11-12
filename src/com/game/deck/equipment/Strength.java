package com.game.deck.equipment;

/**
 * Contains the Star source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.GameState;
import com.game.deck.model.Card;
import com.game.deck.model.Rank;
import java.util.List;

/**
 * Represents the star in the game's model, engine, equipment, occurrence, or view layer.
 */
public class Strength extends TarotCards{
    private final int MAX_NUMBER_CARDS = 2;
    /**
     * Performs the star operation.
     */
    public Strength(){
        super("Strength", "Increase the rank of 2 Cards", "star_placeholder", 4, 3);
    }

    private Rank increaseRank(Rank rank){
        return switch(rank.getValue()){
            case 2 -> Rank.THREE;
            case 3 -> Rank.FOUR;
            case 4 -> Rank.FIVE;
            case 5 -> Rank.SIX;
            case 6 -> Rank.SEVEN;
            case 7 -> Rank.EIGHT;
            case 8 -> Rank.NINE;
            case 9 -> Rank.TEN;
            case 10 -> Rank.JACK;
            case 11 -> Rank.QUEEN;
            case 12 -> Rank.KING;
            case 13 -> Rank.ACE;
            case 14 -> Rank.TWO;
            default -> Rank.TWO;
        };
    }

    @Override 
        protected void applyEffect(GameState state, List<Card> selectedCard){
            int i = 0;
            for(Card card: selectedCard){
                if(i == MAX_NUMBER_CARDS) break;
                card.setRank(increaseRank(card.getRank()));
                i++;
            }
        };
}
