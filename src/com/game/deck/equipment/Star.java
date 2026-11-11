package com.game.deck.equipment;

import com.game.deck.model.GameState;
import com.game.deck.model.Card;
import com.game.deck.model.Suit;
import java.util.List;

public class Star extends TarotCards{
    private final int MAX_NUMBER_CARDS = 3;
    public Star(){
        super("The Star", "Transform upto 3 Cards into a Diamond Suit", "star_placeholder", 5, 4);
    }

    @Override 
        protected void applyEffect(GameState state, List<Card> selectedCard){
            int i = 0;
            for(Card card: selectedCard){
                if(i == MAX_NUMBER_CARDS) break;
                card.setSuit(Suit.DIAMONDS);
                i++;
            }
        };
}
