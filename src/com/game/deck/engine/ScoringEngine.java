package com.game.deck.engine;

import com.game.deck.equipment.Joker;
import com.game.deck.model.*;
import java.util.List;

public class ScoringEngine {

    public static long calculateScore(
            List<Card> playedCards,
            List<Card> heldInHand,
            List<Joker> activeJokers,
            GameState gameState
    ) {
        PokerHand handType = PokerEvaluator.evaluate(playedCards);
        ScoringContext ctx = new ScoringContext(handType.getChips(), handType.getMult());

        System.out.println("\n------------------------------------------------------------");
        System.out.println("[PARSER LOG] Hand Evaluated: " + handType.getDisplayName() +
                           " (Lvl " + handType.getLevel() + ") | Base Chips: " + ctx.getChips() + " | Base Mult: " + ctx.getMult());

        // Stage 1: Pre-Scoring Phase
        System.out.println("[STAGE 1] Executing Pre-Scoring Triggers...");

        // Stage 2: Scored Played Cards
        System.out.println("[STAGE 2] Scoring Played Cards (Left-to-Right)...");
        for (Card card : playedCards) {
            if (!card.isDebuffed()) {
                long rankChips = card.getChipValue();
                ctx.addChips(rankChips);
                System.out.println("  -> Scored: " + card + " (+" + rankChips + " Rank Chips)");

                // Apply Card Enhancements
                if (card.getEnhancement() == Enhancement.BONUS) {
                    ctx.addChips(30);
                    System.out.println("     [ENHANCEMENT] Bonus Card -> +30 Chips");
                } else if (card.getEnhancement() == Enhancement.MULT) {
                    ctx.addMult(4);
                    System.out.println("     [ENHANCEMENT] Mult Card -> +4 Mult");
                } else if (card.getEnhancement() == Enhancement.GLASS) {
                    ctx.multiplyMult(2.0);
                    System.out.println("     [ENHANCEMENT] Glass Card -> x2 Mult");
                }
            }
        }

        // Stage 3: Held-in-Hand Effects
        System.out.println("[STAGE 3] Evaluating Held Cards in Hand...");
        for (Card card : heldInHand) {
            if (card.getEnhancement() == Enhancement.STEEL) {
                ctx.multiplyMult(1.5);
                System.out.println("  -> Held Steel Card: " + card + " -> x1.5 Mult");
            } else if (card.getEnhancement() == Enhancement.GOLD) {
                gameState.addCash(3);
                System.out.println("  -> Held Gold Card: " + card + " -> +$3 End-of-Round Gold");
            }
        }

        // Stage 4: Joker & Socketed Item Pipeline
        System.out.println("[STAGE 4] Executing Jokers & Socketed Items (Left-to-Right)...");
        for (int i = 0; i < activeJokers.size(); i++) {
            Joker joker = activeJokers.get(i);
            System.out.println("  -> Triggering Joker #" + (i + 1) + ": " + joker.getName());
            joker.evaluate(ctx);
        }

        long finalScore = ctx.calculateTotal();
        System.out.println("[SCORE TRACE SUMMARY] Final Chips: " + ctx.getChips() + " | Final Mult: " + String.format("%.2f", ctx.getMult()) + " = TOTAL SCORE: " + finalScore);
        System.out.println("------------------------------------------------------------\n");

        return finalScore;
    }
}