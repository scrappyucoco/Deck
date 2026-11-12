package com.game.deck.engine;

/**
 * Contains the ScoringContext source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.Card;
import com.game.deck.model.GameState;
import com.game.deck.model.PokerHand;
import com.game.deck.model.Rank;
import com.game.deck.model.Suit;
import com.game.deck.equipment.Joker;

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

    /**
     * Performs the scorin\1 \2ontext operation.
     */
    public ScoringContext(long baseChips, double baseMult) {
        this(baseChips, baseMult, null, null, false, List.of());
    }

    /**
     * Performs the scorin\1 \2ontext operation.
     */
    public ScoringContext(
            long baseChips,
            double baseMult,
            PokerHand handType,
            GameState gameState,
            boolean preview
    ) {
        this(baseChips, baseMult, handType, gameState, preview, List.of());
    }

    /**
     * Performs the scorin\1 \2ontext operation.
     */
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

    /**
     * Performs the ad\1 \2hips operation.
     */
    public void addChips(long amount) {
        // --- CHIP MODIFIERS ---
        long previous = chips;
        chips += amount;
        System.out.printf("[SCORING] Chips %d -> %d (%+d)%n", previous, chips, amount);
    }

    /** Subtracts Chips from the current score calculation, clamped at zero. */
    public void subtractChips(long amount) {
        if (amount < 0) throw new IllegalArgumentException("Chip loss cannot be negative");
        addChips(-Math.min(amount, chips));
    }

    /**
     * Performs the ad\1 \2ult operation.
     */
    public void addMult(double amount) {
        // --- MULTIPLIER MODIFIERS ---
        double previous = mult;
        mult += amount;
        System.out.printf("[SCORING] Mult %.2f -> %.2f (%+.2f)%n", previous, mult, amount);
    }

    /** Subtracts Mult from the current score calculation, clamped at zero. */
    public void subtractMult(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Multiplier loss cannot be negative");
        addMult(-Math.min(amount, mult));
    }

    /**
     * Performs the multipl\1 \2ult operation.
     */
    public void multiplyMult(double factor) {
        double previous = mult;
        mult *= factor;
        System.out.printf("[SCORING] Mult %.2f -> %.2f (x%.2f)%n", previous, mult, factor);
    }

    /**
     * Performs the multipl\1 \2hips operation.
     */
    public void multiplyChips(double factor) {
        long previous = chips;
        chips = (long) (chips * factor);
        System.out.printf("[SCORING] Chips %d -> %d (x%.2f)%n", previous, chips, factor);
    }

    /**
     * Performs the se\1 \2hips operation.
     */
    public void setChips(long value) {
        long previous = chips;
        chips = value;
        System.out.printf("[SCORING] Chips %d -> %d%n", previous, value);
    }

    /**
     * Performs the se\1 \2ult operation.
     */
    public void setMult(double value) {
        double previous = mult;
        mult = value;
        System.out.printf("[SCORING] Mult %.2f -> %.2f%n", previous, value);
    }

    // [CONFIG: TWEAK SCORE VALUES HERE]
    // These methods let a Joker change hand base values without erasing
    // played-card bonuses; call them from modifyHand() before card scoring.
    /**
     * Performs the se\1 \2as\1 \2hips operation.
     */
    public void setBaseChips(long value) {
        long previous = baseChips;
        baseChips = value;
        chips += value - previous;
        System.out.printf("[SCORING] Base Chips %d -> %d%n", previous, value);
    }

    /**
     * Performs the ad\1 \2as\1 \2hips operation.
     */
    public void addBaseChips(long amount) {
        setBaseChips(baseChips + amount);
    }

    /**
     * Performs the se\1 \2as\1 \2ult operation.
     */
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

    /**
     * Performs the ad\1 \2as\1 \2ult operation.
     */
    public void addBaseMult(double amount) {
        setBaseMult(baseMult + amount);
    }

    // --- RUN ECONOMY ---
    // Economy calls mutate GameState only during authoritative scoring;
    // previews expose the same API but never change the live run.
    /**
     * Performs the ad\1 \2ash operation.
     */
    public void addCash(int amount) {
        if (gameState == null || preview) {
            System.out.println("[ECONOMY PREVIEW] Cash change " + formatAmount(amount));
            return;
        }
        gameState.addCash(amount);
    }

    /**
     * Performs the deduc\1 \2ash operation.
     */
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

    //Getters
    /**
     * Performs the i\1 \2review operation.
     */
    public boolean isPreview() { return preview; }
    /**
     * Performs the ge\1 \2an\1 \2ype operation.
     */
    public PokerHand getHandType() { return handType; }
    /**
     * Performs the ge\1 \2laye\1 \2ards operation.
     */
    public List<Card> getPlayedCards() { return playedCards; }

    /**
     * Performs the ge\1 \2okers operation.
     */
    public List<Joker> getJokers(){
        return gameState.getOwnedJokers();
    }

    /**
     * Performs the coun\1 \2laye\1 \2ards operation.
     */
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

    /**
     * Performs the ha\1 \2laye\1 \2uit operation.
     */
    public boolean hasPlayedSuit(Suit suit) {
        return countPlayedCards(suit) > 0;
    }

    /**
     * Performs the al\1 \2laye\1 \2ards operation.
     */
    public boolean allPlayedCards(Suit suit) {
        return !playedCards.isEmpty() && countPlayedCards(suit) == playedCards.size();
    }

    /**
     * Performs the coun\1 \2laye\1 \2ards operation.
     */
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

    /**
     * Performs the ha\1 \2laye\1 \2ank operation.
     */
    public boolean hasPlayedRank(Rank rank) {
        return countPlayedCards(rank) > 0;
    }

    /**
     * Performs the al\1 \2laye\1 \2ards operation.
     */
    public boolean allPlayedCards(Rank rank) {
        return !playedCards.isEmpty() && countPlayedCards(rank) == playedCards.size();
    }

    /**
     * Performs the ge\1 \2as\1 \2hips operation.
     */
    public long getBaseChips() { return baseChips; }
    /**
     * Performs the ge\1 \2as\1 \2ult operation.
     */
    public double getBaseMult() { return baseMult; }
    /**
     * Performs the ge\1 \2rigina\1 \2as\1 \2hips operation.
     */
    public long getOriginalBaseChips() { return originalBaseChips; }
    /**
     * Performs the ge\1 \2rigina\1 \2as\1 \2ult operation.
     */
    public double getOriginalBaseMult() { return originalBaseMult; }

    /**
     * Performs the calculat\1 \2otal operation.
     */
    public long calculateTotal() {
        // The game formula is always final chips multiplied by final mult.
        long total = Math.max(0, (long) (chips * mult));
        System.out.printf("[SCORING] Total = %d chips x %.2f mult = %d%n", chips, mult, total);
        return total;
    }

    /**
     * Performs the ge\1 \2hips operation.
     */
    public long getChips() { return chips; }
    /**
     * Performs the ge\1 \2ult operation.
     */
    public double getMult() { return mult; }

    private static String formatAmount(int amount) {
        return amount >= 0 ? "+" + amount : Integer.toString(amount);
    }
}
