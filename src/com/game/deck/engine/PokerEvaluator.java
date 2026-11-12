package com.game.deck.engine;

/**
 * Contains the PokerEvaluator source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.*;
import com.game.deck.equipment.Joker;
import com.game.deck.equipment.FourFingers;
import java.util.*;

/*
 * PokerEvaluator is the pure Engine classifier for played cards.
 * It reads model Card/Rank/Suit/Enchantment data and returns a PokerHand;
 * ScoringEngine then applies chips, multipliers, held effects, and Jokers.
 */
public class PokerEvaluator {
    private static int MinimumOfCards = 5;
    private static final int DEFFAULT_MINIMUM = 5;

    /**
     * Performs the evaluate operation.
     */
    public static PokerHand evaluate(List<Card> playedCards, List<Joker> activeJokers) {
        // --- HAND CLASSIFICATION ---
        if (playedCards == null || playedCards.isEmpty()) return PokerHand.HIGH_CARD;

        Map<Rank, Integer> rankCounts = new HashMap<>();
        Map<Suit, Integer> suitCounts = new HashMap<>();

        for (Card card : playedCards) {
            // Stone cards contribute chips but do not participate in hand identity.
            if (card.getEnchantment() != Enchantment.STONE) {
                rankCounts.put(card.getRank(), rankCounts.getOrDefault(card.getRank(), 0) + 1);
                suitCounts.put(card.getSuit(), suitCounts.getOrDefault(card.getSuit(), 0) + 1);
            }
        }

        CheckFourFingers(activeJokers);
        boolean isFlush = isFlush(playedCards, suitCounts);
        boolean isStraight = isStraight(playedCards);

        if (rankCounts.containsValue(5)) return isFlush ? PokerHand.FLUSH_FIVE : PokerHand.FIVE_OF_A_KIND;
        if (isFlush && rankCounts.containsValue(3) && rankCounts.containsValue(2)) return PokerHand.FLUSH_HOUSE;
        if (isFlush && isStraight) return PokerHand.STRAIGHT_FLUSH;
        if (rankCounts.containsValue(4)) return PokerHand.FOUR_OF_A_KIND;
        if (rankCounts.containsValue(3) && rankCounts.containsValue(2)) return PokerHand.FULL_HOUSE;
        if (isFlush) return PokerHand.FLUSH;
        if (isStraight) return PokerHand.STRAIGHT;
        if (rankCounts.containsValue(3)) return PokerHand.THREE_OF_A_KIND;

        int pairCount = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) pairCount++;
        }
        if (pairCount == 2) return PokerHand.TWO_PAIR;
        if (pairCount == 1) return PokerHand.PAIR;

        return PokerHand.HIGH_CARD;
    }

    private static boolean isFlush(List<Card> cards, Map<Suit, Integer> suitCounts) {
        if (cards.size() < MinimumOfCards) return false;
        int maxSuit = 0;
        for (int count : suitCounts.values()) maxSuit = Math.max(maxSuit, count);
        return maxSuit >= MinimumOfCards;
    }

    private static boolean isStraight(List<Card> cards) {
        // Deduplicate ranks before testing consecutive values, including A-2-3-4-5.
        if (cards.size() < MinimumOfCards) return false;
        List<Integer> values = new ArrayList<>();
        for (Card c : cards) {
            if (c.getEnchantment() != Enchantment.STONE) {
                if (!values.contains(c.getRank().getValue())) values.add(c.getRank().getValue());
            }
        }
        Collections.sort(values);
        if (values.size() < MinimumOfCards) return false;

        int minimumDifference = MinimumOfCards-1;
        for (int i = 0; i <= values.size() - MinimumOfCards; i++) {
            if (values.get(i + minimumDifference) - values.get(i) == minimumDifference) return true;
        }
        // Ace-low straight check (A, 2, 3, 4, 5)
        if(values.contains(14)){
            for(int i = 1; i < values.size()-1; i++){
                if(values.get(i) > MinimumOfCards)
                    return false;
            }
            return true;
        }
        return false;
    }

    private static void CheckFourFingers(List<Joker> activeJokers){
        for(Joker joker: activeJokers){
            if(joker instanceof FourFingers){
                MinimumOfCards = ((FourFingers) joker).GetMinimumOfCards();
                return;
            }
        }
        MinimumOfCards = DEFFAULT_MINIMUM;
    }
}