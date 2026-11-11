package com.game.deck.engine;

import com.game.deck.equipment.Joker;
import com.game.deck.model.*;
import java.util.List;

/*
 * ScoringEngine is the authoritative Engine implementation of the four-stage
 * score pipeline. It coordinates model cards/GameState with equipment.Joker
 * effects and equipment.Item sockets, preserving left-to-right trigger order.
 */
public class ScoringEngine {

    public static long calculateScore(
            List<Card> playedCards,
            List<Card> heldInHand,
            List<Joker> activeJokers,
            GameState gameState
    ) {
        if (playedCards == null || playedCards.isEmpty()) {
            System.out.println("[SCORE] No cards were played; score is 0.");
            return 0;
        }
        PokerHand handType = PokerEvaluator.evaluate(playedCards);
        ScoringContext ctx = new ScoringContext(
                handType.getChips(), handType.getMult(), handType, gameState, false, playedCards);

        System.out.println("\n------------------------------------------------------------");
        System.out.println("[PARSER LOG] Hand Evaluated: " + handType.getDisplayName() +
                           " (Lvl " + handType.getLevel() + ") | Base Chips: " + ctx.getChips() + " | Base Mult: " + ctx.getMult());

        // --- STAGE 1: PRE-SCORING ---
        // [CONFIG: TWEAK SCORE VALUES HERE]
        // PokerHand owns base values; change its enum entries or level scaling.
        System.out.println("[STAGE 1] Executing Pre-Scoring Triggers...");
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Override Joker.modifyHand() to change this hand's base chips/mult.
        for (Joker joker : activeJokers) {
            if (joker != null) {
                joker.modifyHand(ctx);
            }
        }

        // --- STAGE 2: PLAYED CARDS ---
        System.out.println("[STAGE 2] Scoring Played Cards (Left-to-Right)...");
        for (Card card : playedCards) {
            if (!card.isDebuffed()) {
                long rankChips = card.getChipValue();
                ctx.addChips(rankChips);
                System.out.println("  -> Scored: " + card + " (+" + rankChips + " Rank Chips)");

                // [EXTENSION: CARD ENCHANTMENTS]
                // Add a new Enchantment branch here using ctx.addChips/addMult/
                // multiplyMult, then mirror it in BasePokerEngine for previews.
                if (card.getEnchantment() == Enchantment.BONUS) {
                    ctx.addChips(30);
                    System.out.println("     [Enchantment] Bonus Card -> +30 Chips");
                } else if (card.getEnchantment() == Enchantment.MULT) {
                    ctx.addMult(4);
                    System.out.println("     [Enchantment] Mult Card -> +4 Mult");
                } else if (card.getEnchantment() == Enchantment.GLASS) {
                    ctx.multiplyMult(2.0);
                    System.out.println("     [Enchantment] Glass Card -> x2 Mult");
                }
            }
        }

        // --- STAGE 3: HELD-IN-HAND EFFECTS ---
        System.out.println("[STAGE 3] Evaluating Held Cards in Hand...");
        for (Card card : heldInHand) {
            if (card.getEnchantment() == Enchantment.STEEL) {
                ctx.multiplyMult(1.5);
                System.out.println("  -> Held Steel Card: " + card + " -> x1.5 Mult");
            } else if (card.getEnchantment() == Enchantment.GOLD) {
                gameState.addCash(3);
                System.out.println("  -> Held Gold Card: " + card + " -> +$3 End-of-Round Gold");
            }
        }

        // --- STAGE 4: JOKERS AND SOCKETS ---
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Add concrete Joker instances to activeJokers before this loop; each
        // subclass implements applyBaseEffect(ScoringContext).
        System.out.println("[STAGE 4] Executing Jokers & Socketed Items (Left-to-Right)...");
        for (int i = 0; i < activeJokers.size(); i++) {
            Joker joker = activeJokers.get(i);
            System.out.println("  -> Triggering Joker #" + (i + 1) + ": " + joker.getName());
            joker.evaluate(ctx);
        }

        // [HOOK: ADD CUSTOM OCCURRENCES/EVENTS]
        // Trigger post-score Occurrence logic after calculateTotal() in a
        // controller, passing the same GameState to Occurrence.execute(state).
        long finalScore = ctx.calculateTotal();
        System.out.println("[SCORE TRACE SUMMARY] Final Chips: " + ctx.getChips() + " | Final Mult: " + String.format("%.2f", ctx.getMult()) + " = TOTAL SCORE: " + finalScore);
        System.out.println("------------------------------------------------------------\n");

        return finalScore;
    }
}