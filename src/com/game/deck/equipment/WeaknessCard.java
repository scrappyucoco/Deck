package com.game.deck.equipment;


import com.game.deck.model.GameState;
import com.game.deck.model.Card;
import com.game.deck.model.Rank;
import java.util.List;


public class WeaknessCard extends TarotCards{
    private final int MAX_NUMBER_CARDS = 2;
    
    public WeaknessCard(){
        super("Weakness", "Decrease the rank of 2 Cards", "star_placeholder", 4, 3);
    }

    private Rank decreaseRank(Rank rank){
        return switch(rank.getValue()){
            case 2 -> Rank.ACE;
            case 3 -> Rank.TWO;
            case 4 -> Rank.THREE;
            case 5 -> Rank.FOUR;
            case 6 -> Rank.FIVE;
            case 7 -> Rank.SIX;
            case 8 -> Rank.SEVEN;
            case 9 -> Rank.EIGHT;
            case 10 -> Rank.NINE;
            case 11 -> Rank.TEN;
            case 12 -> Rank.JACK;
            case 13 -> Rank.QUEEN;
            case 14 -> Rank.KING;
            default -> Rank.TWO;
        };
    }

    @Override 
        protected void applyEffect(GameState state, List<Card> selectedCard){
            int i = 0;
            for(Card card: selectedCard){
                if(i == MAX_NUMBER_CARDS) break;
                card.setRank(decreaseRank(card.getRank()));
                i++;
            }
        };
}
