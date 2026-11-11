package com.game.deck.engine;

/**
 * Contains the BasePokerEngine source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.equipment.Joker;
import com.game.deck.model.Card;
import com.game.deck.model.Enchantment;
import com.game.deck.model.GameState;
import com.game.deck.model.PokerHand;

import java.util.ArrayList;
import java.util.List;

/*
 * BasePokerEngine is the lightweight Engine scorer used by UI previews.
 * It evaluates played cards without mutating consumable Joker/Item state;
 * ScoringEngine is the authoritative full four-stage execution path.
 */
public final class BasePokerEngine {
    private BasePokerEngine() {
    }

    /*
     * ScoreResult is an immutable Engine snapshot used by preview labels.
     * It separates hand-base chips from played-card chips so the View can
     * explain score composition without reaching into ScoringContext.
     */
    public static final class ScoreResult {
        public final PokerHand handType;
        public final long baseChips;
        public final long cardChips;
        public final long totalChips;
        public final double mult;
        public final long finalScore;

    /**
     * Performs the scor\1 \2esult operation.
     */
        public ScoreResult(PokerHand handType, long baseChips, long cardChips, double mult) {
            this.handType = handType;
            this.baseChips = baseChips;
            this.cardChips = cardChips;
            this.totalChips = baseChips + cardChips;
            this.mult = mult;
            this.finalScore = (long) (totalChips * mult);
        }
    }

    /**
     * Performs the evaluat\1 \2n\1 \2core operation.
     */
    public static ScoreResult evaluateAndScore(List<Card> playedCards) {
        return evaluateAndScore(playedCards, null);
    }

    /**
     * Performs the evaluat\1 \2n\1 \2core operation.
     */
    public static ScoreResult evaluateAndScore(List<Card> playedCards, GameState gameState) {
        // --- INPUT NORMALIZATION ---
        if (playedCards == null || playedCards.isEmpty()) {
            return new ScoreResult(PokerHand.HIGH_CARD, 0, 0, 0);
        }
        List<Joker> activeJokers = (gameState == null) ? List.of() : gameState.getOwnedJokers();
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
        ScoringContext context = new ScoringContext(
                handType.getChips(), handType.getMult(), handType, gameState, true, validCards);
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Preview the same hand-base hook used by the authoritative scorer.
        if (activeJokers != null) {
            for (Joker joker : activeJokers) {
                if (joker != null) {
                    joker.modifyHand(context);
                }
            }
        }
        for (Card card : validCards) {
            if (card.isDebuffed()) {
                continue;
            }
            long rankChips = card.getChipValue();
            context.addChips(rankChips);
            // [EXTENSION: CARD ENCHANTMENTS]
            // Add preview-only handling here for a new Enchantment enum value;
            // mirror the authoritative behavior in ScoringEngine afterward.
            if (card.getEnchantment() == Enchantment.BONUS) {
                context.addChips(30);
            } else if (card.getEnchantment() == Enchantment.MULT) {
                context.addMult(4);
            } else if (card.getEnchantment() == Enchantment.GLASS) {
                context.multiplyMult(2.0);
            }
        }
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Preview registered Joker instances with joker.preview(context); do not
        // call evaluate() here because previews must not consume socket items.
        if (activeJokers != null) {
            for (Joker joker : activeJokers) {
                if (joker != null) {
                    joker.preview(context);
                }
            }
        }

        return new ScoreResult(
                handType,
                context.getBaseChips(),
                context.getChips() - context.getBaseChips(),
                context.getMult()
        );
    }
}
