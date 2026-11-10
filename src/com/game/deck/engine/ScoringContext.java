package com.game.deck.engine;

import com.game.deck.model.Card;
import com.game.deck.model.GameState;
import com.game.deck.model.PokerHand;
import com.game.deck.model.Rank;
import com.game.deck.model.Suit;

import java.util.List;

/*
 * ScoringContext is the mutable Engine accumulator for one hand score.
 * ScoringEngine, Joker, and Item implementations mutate it in pipeline order;
 * model classes remain independent of this calculation object. It also exposes
 * guarded run-economy operations so live Joker effects can modify cash.
 */
public class ScoringContext {
    private final long originalBaseChips;
    private final double originalBaseMult;
    private long baseChips;
    private double baseMult;
    private long chips;
    private double mult;
    private final PokerHand handType;
    private final GameState gameState;
    private final boolean preview;
    private final List<Card> playedCards;

    public ScoringContext(long baseChips, double baseMult) {
        this(baseChips, baseMult, null, null, false, List.of());
    }

    public ScoringContext(
            long baseChips,
            double baseMult,
            PokerHand handType,
            GameState gameState,
            boolean preview
    ) {
        this(baseChips, baseMult, handType, gameState, preview, List.of());
    }

    public ScoringContext(
            long baseChips,
            double baseMult,
            PokerHand handType,
            GameState gameState,
            boolean preview,
            List<Card> playedCards
    ) {
        this.originalBaseChips = baseChips;
        this.originalBaseMult = baseMult;
        this.baseChips = baseChips;
        this.baseMult = baseMult;
        this.chips = baseChips;
        this.mult = baseMult;
        this.handType = handType;
        this.gameState = gameState;
        this.preview = preview;
        this.playedCards = playedCards == null ? List.of() : List.copyOf(playedCards);
    }

    public void addChips(long amount) {
        // --- CHIP MODIFIERS ---
        long previous = chips;
        chips += amount;
        System.out.printf("[SCORING] Chips %d -> %d (%+d)%n", previous, chips, amount);
    }

    public void addMult(double amount) {
        // --- MULTIPLIER MODIFIERS ---
        double previous = mult;
        mult += amount;
        System.out.printf("[SCORING] Mult %.2f -> %.2f (%+.2f)%n", previous, mult, amount);
    }

    public void multiplyMult(double factor) {
        double previous = mult;
        mult *= factor;
        System.out.printf("[SCORING] Mult %.2f -> %.2f (x%.2f)%n", previous, mult, factor);
    }

    public void multiplyChips(double factor) {
        long previous = chips;
        chips = (long) (chips * factor);
        System.out.printf("[SCORING] Chips %d -> %d (x%.2f)%n", previous, chips, factor);
    }

    public void setChips(long value) {
        long previous = chips;
        chips = value;
        System.out.printf("[SCORING] Chips %d -> %d%n", previous, value);
    }

    public void setMult(double value) {
        double previous = mult;
        mult = value;
        System.out.printf("[SCORING] Mult %.2f -> %.2f%n", previous, value);
    }

    // [CONFIG: TWEAK SCORE VALUES HERE]
    // These methods let a Joker change hand base values without erasing
    // played-card bonuses; call them from modifyHand() before card scoring.
    public void setBaseChips(long value) {
        long previous = baseChips;
        baseChips = value;
        chips += value - previous;
        System.out.printf("[SCORING] Base Chips %d -> %d%n", previous, value);
    }

    public void addBaseChips(long amount) {
        setBaseChips(baseChips + amount);
    }

    public void setBaseMult(double value) {
        double previous = baseMult;
        if (previous == 0) {
            mult = value;
        } else {
            mult *= value / previous;
        }
        baseMult = value;
        System.out.printf("[SCORING] Base Mult %.2f -> %.2f%n", previous, value);
    }

    public void addBaseMult(double amount) {
        setBaseMult(baseMult + amount);
    }

    // --- RUN ECONOMY ---
    // Economy calls mutate GameState only during authoritative scoring;
    // previews expose the same API but never change the live run.
    public void addCash(int amount) {
        if (gameState == null || preview) {
            System.out.println("[ECONOMY PREVIEW] Cash change " + formatAmount(amount));
            return;
        }
        gameState.addCash(amount);
    }

    public boolean deductCash(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cash deduction cannot be negative");
        }
        if (gameState == null || preview) {
            System.out.println("[ECONOMY PREVIEW] Cash deduction " + formatAmount(-amount));
            return true;
        }
        return gameState.deductCash(amount);
    }

    public boolean isPreview() { return preview; }
    public PokerHand getHandType() { return handType; }
    public List<Card> getPlayedCards() { return playedCards; }

    public int countPlayedCards(Suit suit) {
        if (suit == null) {
            return 0;
        }
        int count = 0;
        for (Card card : playedCards) {
            if (card != null && card.getSuit() == suit) {
                count++;
            }
        }
        return count;
    }

    public boolean hasPlayedSuit(Suit suit) {
        return countPlayedCards(suit) > 0;
    }

    public boolean allPlayedCards(Suit suit) {
        return !playedCards.isEmpty() && countPlayedCards(suit) == playedCards.size();
    }

    public int countPlayedCards(Rank rank) {
        if (rank == null) {
            return 0;
        }
        int count = 0;
        for (Card card : playedCards) {
            if (card != null && card.getRank() == rank) {
                count++;
            }
        }
        return count;
    }

    public boolean hasPlayedRank(Rank rank) {
        return countPlayedCards(rank) > 0;
    }

    public boolean allPlayedCards(Rank rank) {
        return !playedCards.isEmpty() && countPlayedCards(rank) == playedCards.size();
    }

    public long getBaseChips() { return baseChips; }
    public double getBaseMult() { return baseMult; }
    public long getOriginalBaseChips() { return originalBaseChips; }
    public double getOriginalBaseMult() { return originalBaseMult; }

    public long calculateTotal() {
        // The game formula is always final chips multiplied by final mult.
        long total = Math.max(0, (long) (chips * mult));
        System.out.printf("[SCORING] Total = %d chips x %.2f mult = %d%n", chips, mult, total);
        return total;
    }

    public long getChips() { return chips; }
    public double getMult() { return mult; }

    private static String formatAmount(int amount) {
        return amount >= 0 ? "+" + amount : Integer.toString(amount);
    }
}