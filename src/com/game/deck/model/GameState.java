package com.game.deck.model;

/**
 * Contains the GameState source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.equipment.Joker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * GameState is the central Model aggregate for a run and its active round.
 * It owns the deck, hand, score resources, ante progression, Jokers, and Tarot
 * consumables.
 * GameView invokes these domain methods; Shop and occurrences use the same
 * validated API without depending on JavaFX classes.
 */
public class GameState {
    // ==========================================
    // [CONFIG: TWEAK SCORE VALUES HERE]
    // Each array element is the target for one ante; add a value to extend the
    // table, or change getTargetScoreForAnte() for a different late-run policy.
    private static final long[] ANTE_TARGET_SCORES = {
            300, 
            500, 
            850, 
            1200, 
            2000, 
            3000, 
            4500, 
            6000
    };

    private int ante;
    private long targetScore;
    private long currentScore;
    private int handsRemaining;
    private int discardsRemaining;
    private int maxHandSize;
    private int cash;
    private int baseChipsModifier;
    private boolean inSpecialChallenge;
    private boolean roundActive;

    private final Deck deck;
    private final List<Card> hand;
    private final List<Joker> ownedJokers;
    private static final int MAX_JOKERS = 2;

    public GameState() {
        // --- RUN INITIALIZATION ---
        this.deck = new Deck();
        this.hand = new ArrayList<>();
        this.ownedJokers = new ArrayList<>();
        this.ownedTarotCards = new ArrayList<>();
        this.maxHandSize = 8;
        startNewRun();
    }

    public void startNewRun() {
        this.ante = 1;
        this.cash = 4;
        this.inSpecialChallenge = false;
        this.roundActive = false;
        this.currentScore = 0;
        this.targetScore = getTargetScoreForAnte(ante);
        this.hand.clear();
        deck.resetFullDeck();
        System.out.println("[STATE] New run ready. Shop is open before the first round.");
    }

    public void startRound(long targetScore, int hands, int discards) {
        // --- ROUND LIFECYCLE ---
        if (targetScore < 0 || hands < 0 || discards < 0) {
            throw new InvalidGameActionException(
                    "Cannot start a round with negative values: target=" + targetScore
                            + ", hands=" + hands + ", discards=" + discards + ".");
        }
        this.targetScore = targetScore;
        this.currentScore = 0;
        this.handsRemaining = hands;
        this.discardsRemaining = discards;
        hand.clear();
        // Reuse the persistent master deck so permanent card modifications
        // survive across rounds and antes.
        deck.prepareForRound();
        refillHand();
        this.roundActive = true;
        System.out.println("[STATE] Round started: ante=" + ante + ", target=" + targetScore
                + ", hands=" + hands + ", discards=" + discards);
    }

    public void startNextRound() {
        if (roundActive) {
            throw new InvalidGameActionException(
                    "Cannot start another round while the current round is active.");
        }
        ante++;
        try {
            startRound(getTargetScoreForAnte(ante), 4, 3);
        } catch (InvalidGameActionException exception) {
            ante--;
            throw exception;
        }
    }

    public void endRound() {
        roundActive = false;
        System.out.println("[STATE] Round ended. Shop is now available.");
    }

    public void refillHand() {
        int needed = maxHandSize - hand.size();
        if (needed > 0) {
            hand.addAll(deck.draw(needed));
            notifyStateChanged();
        }
    }

    public void playSelectedCards(List<Card> selected) {
        // --- PLAYER ACTIONS ---
        if (!roundActive) {
            throw new InvalidGameActionException("Cannot play cards while no round is active.");
        }
        if (handsRemaining <= 0) {
            throw new InvalidGameActionException("Cannot play cards: no hands remain in this round.");
        }
        List<Card> cardsToPlay = validCardsFromHand(selected, "play");
        hand.removeAll(cardsToPlay);
        deck.discard(cardsToPlay);
        handsRemaining--;
        System.out.println("[STATE] Played " + cardsToPlay.size() + " cards. Hands remaining=" + handsRemaining);
    }

    public void discardSelectedCards(List<Card> selected) {
        List<Card> cardsToDiscard = validCardsFromHand(selected);
        if (!roundActive || cardsToDiscard.isEmpty() || cardsToDiscard.size() > 5 || discardsRemaining <= 0) {
            System.out.println("[STATE] Discard rejected: round inactive, invalid hand, or no discards remaining.");
            return;
        }
        hand.removeAll(cardsToDiscard);
        deck.discard(cardsToDiscard);
        discardsRemaining--;
        refillHand();
        System.out.println("[STATE] Discarded " + cardsToDiscard.size() + " cards. Discards remaining=" + discardsRemaining);
        notifyStateChanged();
    }

    /**
     * Performs the ad\1 \2ash operation.
     */
    public void addCash(int amount) {
        // --- ECONOMY ---
        this.cash += amount;
        System.out.println("[STATE] Cash updated: +$" + amount + " | Current Balance: $" + this.cash);
        notifyStateChanged();
    }

    /**
     * Performs the deduc\1 \2ash operation.
     */
    public boolean deductCash(int amount) {
        if (this.cash >= amount) {
            this.cash -= amount;
            System.out.println("[STATE] Cash deducted: -$" + amount + " | Remaining Balance: $" + this.cash);
            notifyStateChanged();
            return true;
        }
        System.out.println("[STATE] Insufficient funds! Needed $" + amount + " but only have $" + this.cash);
        return false;
    }

    public int getAnte() { return ante; }
    /**
     * Performs the se\1 \2nte operation.
     */
    public void setAnte(int ante) { this.ante = ante; }
    /**
     * Performs the ge\1 \2ash operation.
     */
    public int getCash() { return cash; }
    /**
     * Performs the ge\1 \2and\1 \2emaining operation.
     */
    public int getHandsRemaining() { return handsRemaining; }
    /**
     * Performs the us\1 \2and operation.
     */
    public void useHand() { if (handsRemaining > 0) handsRemaining--; }
    /**
     * Performs the rese\1 \2ands operation.
     */
    public void resetHands() { this.handsRemaining = 4; }
    /**
     * Performs the ge\1 \2iscard\1 \2emaining operation.
     */
    public int getDiscardsRemaining() { return discardsRemaining; }
    /**
     * Performs the us\1 \2iscard operation.
     */
    public void useDiscard() { if (discardsRemaining > 0) discardsRemaining--; }
    /**
     * Performs the rese\1 \2iscards operation.
     */
    public void resetDiscards() { this.discardsRemaining = 3; }
    public void addExtraDiscard(int count) { this.discardsRemaining += count; }
    public long getTargetScore() { return targetScore; }
    /**
     * Performs the ge\1 \2urren\1 \2core operation.
     */
    public long getCurrentScore() { return currentScore; }
    /**
     * Performs the ge\1 \2a\1 \2an\1 \2ize operation.
     */
    public int getMaxHandSize() { return maxHandSize; }
    public void setMaxHandSize(int maxHandSize) {
        if (maxHandSize < 1) {
            throw new InvalidGameActionException(
                    "Hand size must be at least 1 (requested " + maxHandSize + ").");
        }
        this.maxHandSize = maxHandSize;
        refillHand();
    }
    /**
     * Performs the ad\1 \2core operation.
     */
    public void addScore(long points) {
        // --- SCORE STATE ---
        if (points < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        long previousScore = currentScore;
        this.currentScore += points;
        System.out.println("[STATE] Score changed: " + previousScore + " -> " + currentScore
                + " (+" + points + ")");
    }
    public boolean isRoundWon() { return currentScore >= targetScore; }
    /**
     * Performs the i\1 \2am\1 \2ver operation.
     */
    public boolean isGameOver() { return handsRemaining <= 0 && !isRoundWon(); }
    /**
     * Performs the ge\1 \2urren\1 \2lin\1 \2arget operation.
     */
    public long getCurrentBlindTarget() { return targetScore; }
    /**
     * Performs the se\1 \2urren\1 \2lin\1 \2arget operation.
     */
    public void setCurrentBlindTarget(long target) {
        if (target < 0) {
            throw new IllegalArgumentException("Target cannot be negative");
        }
        this.targetScore = target;
    }

    /**
     * Performs the ge\1 \2arge\1 \2cor\1 \2o\1 \2nte operation.
     */
    public static long getTargetScoreForAnte(int ante) {
        if (ante < 1) {
            throw new IllegalArgumentException("Ante must be at least 1");
        }
        int index = Math.min(ante - 1, ANTE_TARGET_SCORES.length - 1);
        return ANTE_TARGET_SCORES[index];
    }
    public List<Card> getHand() { return hand; }
    public Deck getDeck() { return deck; }
    /**
     * Performs the i\1 \2oun\1 \2ctive operation.
     */
    public boolean isRoundActive() { return roundActive; }
    /**
     * Performs the i\1 \2ho\1 \2ccessible operation.
     */
    public boolean isShopAccessible() { return !roundActive; }
    /**
     * Performs the ge\1 \2a\1 \2okers operation.
     */
    public int getMaxJokers() { return MAX_JOKERS; }
    /**
     * Performs the ge\1 \2a\1 \2aro\1 \2ards operation.
     */
    public int getMaxTarotCards() { return MAX_TAROT_CARDS; }
    /**
     * Performs the ge\1 \2wne\1 \2okers operation.
     */
    public List<Joker> getOwnedJokers() {
        return Collections.unmodifiableList(ownedJokers);
    }

    public boolean addJoker(Joker joker) {
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Register a concrete Joker instance with addJoker(new MyJoker()) from
        // Shop or another controller; ownedJokers is the active slot list.
        if (joker == null || ownedJokers.size() >= MAX_JOKERS) {
            return false;
        }
        ownedJokers.add(joker);
        System.out.println("[JOKER] Added " + joker.getName() + " to slot " + ownedJokers.size() + "/" + MAX_JOKERS);
        notifyStateChanged();
        return true;
    }

    /**
     * Performs the sel\1 \2oker operation.
     */
    public boolean sellJoker(Joker joker, int sellValue) {
        if (joker == null || sellValue < 0 || !ownedJokers.remove(joker)) {
            return false;
        }
        addCash(sellValue);
        System.out.println("[JOKER] Sold " + joker.getName() + " for $" + sellValue + ".");
        return true;
    }

    /**
     * Performs the sel\1 \2oker operation.
     */
    public boolean sellJoker(Joker joker) {
        return joker != null && sellJoker(joker, joker.getSellValue());
    }

    public boolean sellTarotCard(TarotCards tarotCards, int sellValue) {
        if (tarotCards == null || sellValue < 0 || !ownedTarotCards.remove(tarotCards)) {
            return false;
        }
        addCash(sellValue);
        System.out.println("[JOKER] Sold " + tarotCards.getName() + " for $" + sellValue + ".");
        return true;
    }

    public boolean sellTarotCard(TarotCards tarotCards) {
        return tarotCards != null && sellTarotCard(tarotCards, tarotCards.getSellValue());
    }

    /**
     * Performs the mov\1 \2oker operation.
     */
    public void moveJoker(Joker joker, int targetIndex) {
        // Reordering changes left-to-right Stage 4 trigger order.
        int sourceIndex = ownedJokers.indexOf(joker);
        if (sourceIndex < 0) {
            return;
        }
        int boundedIndex = Math.max(0, Math.min(targetIndex, ownedJokers.size() - 1));
        if (sourceIndex == boundedIndex) {
            return;
        }
        ownedJokers.remove(sourceIndex);
        ownedJokers.add(boundedIndex, joker);
        System.out.println("[JOKER] Reordered " + joker.getName() + " to slot " + (boundedIndex + 1));
        notifyStateChanged();
    }
    public boolean isInSpecialChallenge() { return inSpecialChallenge; }
    /**
     * Performs the se\1 \2\1 \2pecia\1 \2hallenge operation.
     */
    public void setInSpecialChallenge(boolean active) { this.inSpecialChallenge = active; }

    /**
     * Validates a selected hand subset for a play or discard action.
     *
     * @throws InvalidGameActionException when the selection is null, empty,
     *         larger than 5, contains null/duplicate cards, or contains a card
     *         that is not currently in the hand
     */
    private List<Card> validCardsFromHand(List<Card> selected, String action)
            throws InvalidGameActionException {
        if (selected == null || selected.isEmpty()) {
            throw new InvalidGameActionException(
                    "Cannot " + action + " cards: select between 1 and 5 cards.");
        }
        if (selected.size() > 5) {
            throw new InvalidGameActionException(
                    "Cannot " + action + " " + selected.size() + " cards: the maximum is 5.");
        }
        List<Card> validCards = new ArrayList<>();
        for (Card card : selected) {
            if (card == null) {
                throw new InvalidGameActionException(
                        "Cannot " + action + " a null card.");
            }
            if (!hand.contains(card)) {
                throw new InvalidGameActionException(
                        "Cannot " + action + " " + card + " because it is not in your hand.");
            }
            if (validCards.contains(card)) {
                throw new InvalidGameActionException(
                        "Cannot " + action + " the same card more than once: " + card + ".");
            }
            validCards.add(card);
        }
        return validCards;
    }
}
