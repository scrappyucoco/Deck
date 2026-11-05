package com.game.deck.engine;

import com.game.deck.equipment.Joker;
import com.game.deck.model.Card;
import com.game.deck.model.Enchantment;
import com.game.deck.model.PokerHand;

import java.util.ArrayList;
import java.util.List;

public final class BasePokerEngine {
    private BasePokerEngine() {
    }

    public static final class ScoreResult {
        public final PokerHand handType;
        public final long baseChips;
        public final long cardChips;
        public final long totalChips;
        public final double mult;
        public final long finalScore;

        public ScoreResult(PokerHand handType, long baseChips, long cardChips, double mult) {
            this.handType = handType;
            this.baseChips = baseChips;
            this.cardChips = cardChips;
            this.totalChips = baseChips + cardChips;
            this.mult = mult;
            this.finalScore = (long) (totalChips * mult);
        }
    }

    public static ScoreResult evaluateAndScore(List<Card> playedCards) {
        return evaluateAndScore(playedCards, List.of());
    }

    public static ScoreResult evaluateAndScore(List<Card> playedCards, List<Joker> activeJokers) {
        if (playedCards == null || playedCards.isEmpty()) {
            return new ScoreResult(PokerHand.HIGH_CARD, 0, 0, 0);
        }

        List<Card> validCards = new ArrayList<>();
        for (Card card : playedCards) {
            if (card != null) {
                validCards.add(card);
            }
        }
        if (validCards.isEmpty()) {
            return new ScoreResult(PokerHand.HIGH_CARD, 0, 0, 0);
        }

        PokerHand handType = PokerEvaluator.evaluate(validCards);
        ScoringContext context = new ScoringContext(handType.getChips(), handType.getMult());
        for (Card card : validCards) {
            if (card.isDebuffed()) {
                continue;
            }
            long rankChips = card.getChipValue();
            context.addChips(rankChips);
            if (card.getEnchantment() == Enchantment.BONUS) {
                context.addChips(30);
            } else if (card.getEnchantment() == Enchantment.MULT) {
                context.addMult(4);
            } else if (card.getEnchantment() == Enchantment.GLASS) {
                context.multiplyMult(2.0);
            }
        }
        if (activeJokers != null) {
            for (Joker joker : activeJokers) {
                if (joker != null) {
                    joker.preview(context);
                }
            }
        }

        return new ScoreResult(
                handType,
                handType.getChips(),
                context.getChips() - handType.getChips(),
                context.getMult()
        );
    }
}
