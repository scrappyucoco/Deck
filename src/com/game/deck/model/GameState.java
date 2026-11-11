package com.game.deck.model;

import com.game.deck.equipment.Joker;
import com.game.deck.equipment.TarotCards;

import java.util.ArrayList;
import java.util.Collections;
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
    private boolean inSpecialChallenge;
    private boolean roundActive;

    private final Deck deck;
    private final List<Card> hand;
    private final List<Joker> ownedJokers;
    private final List<TarotCards> ownedTarotCards;
    private static final int MAX_JOKERS = 2;
    private static final int MAX_TAROT_CARDS = 2;

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
        this.ownedTarotCards.clear();
        deck.resetFullDeck();
        System.out.println("[STATE] New run ready. Shop is open before the first round.");
    }

    public void startRound(long targetScore, int hands, int discards) {
        // --- ROUND LIFECYCLE ---
        if (targetScore < 0 || hands < 0 || discards < 0) {
            throw new IllegalArgumentException("Round values cannot be negative");
        }
        this.targetScore = targetScore;
        this.currentScore = 0;
        this.handsRemaining = hands;
        this.discardsRemaining = discards;
        this.roundActive = true;
        hand.clear();
        deck.resetFullDeck();
        refillHand();
        System.out.println("[STATE] Round started: ante=" + ante + ", target=" + targetScore
                + ", hands=" + hands + ", discards=" + discards);



                
    }

    public void startNextRound() {
        if (roundActive) {
            throw new IllegalStateException("Cannot start another round while the current round is active");
        }
        ante++;
        cash += 3; // [CONFIG: TWEAK CASH REWARD HERE]
        startRound(getTargetScoreForAnte(ante), 4, 3);
    }

    public void endRound() {
        roundActive = false;
        System.out.println("[STATE] Round ended. Shop is now available.");
    }

    public void refillHand() {
        int needed = maxHandSize - hand.size();
        if (needed > 0) {
            hand.addAll(deck.draw(needed));
        }
    }

    public void playSelectedCards(List<Card> selected) {
        // --- PLAYER ACTIONS ---
        List<Card> cardsToPlay = validCardsFromHand(selected);
        if (!roundActive || cardsToPlay.isEmpty() || cardsToPlay.size() > 5 || handsRemaining <= 0) {
            System.out.println("[STATE] Play rejected: round inactive, invalid hand, or no hands remaining.");
            return;
        }
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
    }

    public void addCash(int amount) {
        // --- ECONOMY ---
        this.cash += amount;
        System.out.println("[STATE] Cash updated: +$" + amount + " | Current Balance: $" + this.cash);
    }

    public boolean deductCash(int amount) {
        if (this.cash >= amount) {
            this.cash -= amount;
            System.out.println("[STATE] Cash deducted: -$" + amount + " | Remaining Balance: $" + this.cash);
            return true;
        }
        System.out.println("[STATE] Insufficient funds! Needed $" + amount + " but only have $" + this.cash);
        return false;
    }

    public int getAnte() { return ante; }
    public void setAnte(int ante) { this.ante = ante; }
    public int getCash() { return cash; }
    public int getHandsRemaining() { return handsRemaining; }
    public void useHand() { if (handsRemaining > 0) handsRemaining--; }
    public void resetHands() { this.handsRemaining = 4; }
    public int getDiscardsRemaining() { return discardsRemaining; }
    public void useDiscard() { if (discardsRemaining > 0) discardsRemaining--; }
    public void resetDiscards() { this.discardsRemaining = 3; }
    public void addExtraDiscard(int count) { this.discardsRemaining += count; }
    public long getTargetScore() { return targetScore; }
    public long getCurrentScore() { return currentScore; }
    public int getMaxHandSize() { return maxHandSize; }
    public void setMaxHandSize(int maxHandSize) {
        if (maxHandSize < 1) {
            throw new IllegalArgumentException("Hand size must be positive");
        }
        this.maxHandSize = maxHandSize;
        refillHand();
    }
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
    public boolean isGameOver() { return handsRemaining <= 0 && !isRoundWon(); }
    public long getCurrentBlindTarget() { return targetScore; }
    public void setCurrentBlindTarget(long target) {
        if (target < 0) {
            throw new IllegalArgumentException("Target cannot be negative");
        }
        this.targetScore = target;
    }

    public static long getTargetScoreForAnte(int ante) {
        if (ante < 1) {
            throw new IllegalArgumentException("Ante must be at least 1");
        }
        int index = Math.min(ante - 1, ANTE_TARGET_SCORES.length - 1);
        return ANTE_TARGET_SCORES[index];
    }
    public List<Card> getHand() { return hand; }
    public Deck getDeck() { return deck; }
    public boolean isRoundActive() { return roundActive; }
    public boolean isShopAccessible() { return !roundActive; }
    public int getMaxJokers() { return MAX_JOKERS; }
    public int getMaxTarotCards() { return MAX_TAROT_CARDS; }
    public List<Joker> getOwnedJokers() {
        return Collections.unmodifiableList(ownedJokers);
    }

    public List<TarotCards> getOwnedTarotCards() {
        return Collections.unmodifiableList(ownedTarotCards);
    }

    public boolean addTarotCard(TarotCards tarotCard) {
        if (tarotCard == null || ownedTarotCards.size() >= MAX_TAROT_CARDS) {
            return false;
        }
        ownedTarotCards.add(tarotCard);
        System.out.println("[TAROT] Added " + tarotCard.getName() + " to consumable slot "
                + ownedTarotCards.size() + "/" + MAX_TAROT_CARDS);
        return true;
    }

    public boolean useTarotCard(TarotCards tarotCard) {
        if (tarotCard == null || !ownedTarotCards.contains(tarotCard)) {
            return false;
        }
        tarotCard.use(this);
        ownedTarotCards.remove(tarotCard);
        System.out.println("[TAROT] Used " + tarotCard.getName() + ".");
        return true;
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
        return true;
    }

    public boolean sellJoker(Joker joker, int sellValue) {
        if (joker == null || sellValue < 0 || !ownedJokers.remove(joker)) {
            return false;
        }
        addCash(sellValue);
        System.out.println("[JOKER] Sold " + joker.getName() + " for $" + sellValue + ".");
        return true;
    }

    public boolean sellJoker(Joker joker) {
        return joker != null && sellJoker(joker, joker.getSellValue());
    }

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
    }
    public boolean isInSpecialChallenge() { return inSpecialChallenge; }
    public void setInSpecialChallenge(boolean active) { this.inSpecialChallenge = active; }

    private List<Card> validCardsFromHand(List<Card> selected) {
        if (selected == null || selected.isEmpty()) {
            return List.of();
        }
        List<Card> validCards = new ArrayList<>();
        for (Card card : selected) {
            if (card != null && hand.contains(card) && !validCards.contains(card)) {
                validCards.add(card);
            }
        }
        return validCards;
    }
}