package com.game.deck.equipment;

import com.game.deck.model.GameState;
import com.game.deck.model.Card;
import com.game.deck.model.Enchantment;
import java.util.List;
import java.util.Random;


public class WheelOfFortuneCard extends TarotCards{
    private final Random rand = new Random();
    
    public WheelOfFortuneCard(){
        super("The Wheel of Fortune", "Each card in hand has a 3 in 5 chance to gain a random Enchantment", "star_placeholder", 9, 7);
    }

    @Override 
    protected void applyEffect(GameState state, List<Card> selectedCard){
        List<Card> hand = state.getHand();
        for(Card card: hand){
            if(rand.nextInt(1,6) <= 3){
                Enchantment enchant = Enchantment.values()[rand.nextInt(1, Enchantment.values().length)];
                System.out.printf("[TAROT] %s Card gained randomized Enchantment %s\n", card, enchant);
                card.setEnchantment(enchant);
            }
        }
    };
}
