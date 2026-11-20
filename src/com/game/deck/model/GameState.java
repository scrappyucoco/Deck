package com.game.deck.model;

/**
 * Contains the GameState source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.equipment.Joker;
import com.game.deck.equipment.TarotCards;
import com.game.deck.exception.InvalidGameActionException;

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
    private final List<TarotCards> ownedTarotCards;
    private final List<Runnable> stateChangeListeners = new ArrayList<>();
    private static final int MAX_JOKERS = 3;
    private static final int MAX_TAROT_CARDS = 2;

    /**
     * Performs the gam\1 \2tate operation.
     */
    public GameState() {
        // --- RUN INITIALIZATION ---
        this.deck = new Deck();
        this.hand = new ArrayList<>();
        this.ownedJokers = new ArrayList<>();
        this.ownedTarotCards = new ArrayList<>();
        this.maxHandSize = 8;
        startNewRun();
    }

    /**
     * Performs the star\1 \2e\1 \2un operation.
     */
    public void startNewRun() {
        this.ante = 1;
        this.cash = 4;
        this.baseChipsModifier = 0;
        this.inSpecialChallenge = false;
        this.roundActive = false;
        this.currentScore = 0;
        this.targetScore = getTargetScoreForAnte(ante);
        this.hand.clear();
        this.ownedJokers.clear();
        this.ownedTarotCards.clear();
        deck.resetToStandardDeck();
        System.out.println("[STATE] New run ready: jokers and Tarots cleared, "
                + deck.totalCards() + " run cards available. Shop is open before the first round.");
        notifyStateChanged();
    }

    /**
     * Performs the star\1 \2ound operation.
     */
    public void startRound(long targetScore, int hands, int discards)
            throws InvalidGameActionException {
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
                + ", hands=" + hands + ", discards=" + discards
                + ", runDeck=" + deck.totalCards());
        notifyStateChanged();
    }

    /**
     * Performs the star\1 \2ex\1 \2ound operation.
     */
    public void startNextRound() throws InvalidGameActionException {
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
    
    /**
     * Performs the en\1 \2ound operation.
     */
    public void endRound() {
        // --- ROUND END ---
        int earned = 3 + handsRemaining;
        cash += earned;
        // Drop the round hand so shop-phase Tarot or UI actions cannot reach
        // stale cards that were already returned to the run deck.
        hand.clear();
        roundActive = false;
        System.out.println("[STATE] Round ended. Hand cleared. Shop is now available.");
        notifyStateChanged();
    }

    /**
     * Performs the refil\1 \2and operation.
     */
    public void refillHand() throws InvalidGameActionException {
        int needed = maxHandSize - hand.size();
        if (needed > 0) {
            hand.addAll(deck.draw(needed));
            notifyStateChanged();
        }
    }

    /**
     * Performs the pla\1 \2electe\1 \2ards operation.
     *
     * @throws InvalidGameActionException when the round is inactive, no hands
     *         remain, or the selection is not 1-5 cards from the hand
     */
    public void playSelectedCards(List<Card> selected) throws InvalidGameActionException {
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
        notifyStateChanged();
    }

    /**
     * Performs the discar\1 \2electe\1 \2ards operation.
     *
     * @throws InvalidGameActionException when the round is inactive, no
     *         discards remain, or the selection is not 1-5 cards from the hand
     */
    public void discardSelectedCards(List<Card> selected) throws InvalidGameActionException {
        if (!roundActive) {
            throw new InvalidGameActionException("Cannot discard cards while no round is active.");
        }
        if (discardsRemaining <= 0) {
            throw new InvalidGameActionException("Cannot discard cards: no discards remain in this round.");
        }
        List<Card> cardsToDiscard = validCardsFromHand(selected, "discard");
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

    /** Removes cash without allowing the run balance to become negative. */
    public void loseCash(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cash loss cannot be negative");
        }
        deductCash(Math.min(amount, cash));
    }

    /**
     * Spends cash for a purchase and rejects the action when funds are short.
     * Optional scoring/occurrence effects continue to use deductCash().
     *
     * @throws InvalidGameActionException when the amount is negative or the
     *         current balance cannot cover it
     */
    public void spendCash(int amount) throws InvalidGameActionException {
        if (amount < 0) {
            throw new InvalidGameActionException(
                    "Cannot spend a negative cash amount: $" + amount + ".");
        }
        if (cash < amount) {
            throw new InvalidGameActionException(
                    "Insufficient cash: need $" + amount + " but only have $" + cash + ".");
        }
        cash -= amount;
        System.out.println("[STATE] Cash spent: -$" + amount + " | Remaining Balance: $" + cash);
        notifyStateChanged();
    }

    /**
     * Returns the cumulative run-level modifier applied to every hand's base chips.
     */
    public int getBaseChipsModifier() {
        return baseChipsModifier;
    }

    /**
     * Deducts base chips from every scored hand in the current run.
     */
    public void deductBaseChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Base chip deduction cannot be negative");
        }
        this.baseChipsModifier -= amount;
        System.out.println("[STATE] Base Chips deducted: -" + amount
                + " | Net Base Chip Modifier: " + this.baseChipsModifier);
        notifyStateChanged();
    }

    /**
     * Adds base chips to every scored hand in the current run.
     */
    public void addBaseChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Base chip bonus cannot be negative");
        }
        this.baseChipsModifier += amount;
        System.out.println("[STATE] Base Chips added: +" + amount
                + " | Net Base Chip Modifier: " + this.baseChipsModifier);
        notifyStateChanged();
    }

    /**
     * Pulls one random Joker instance from the global shop pool.
     */
    public Joker getRandomShopJoker() {
        return Joker.createRandomJoker();
    }

    /**
     * Performs the ge\1 \2nte operation.
     */
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
    /**
     * Performs the ad\1 \2xtr\1 \2iscard operation.
     */
    public void addExtraDiscard(int count) {
        this.discardsRemaining += count;
        notifyStateChanged();
    }
    /** Adds or removes available discards, clamped at zero. */
    public void changeDiscards(int amount) {
        discardsRemaining = Math.max(0, discardsRemaining + amount);
        notifyStateChanged();
    }
    /** Adds or removes available hands, clamped at zero. */
    public void changeHands(int amount) {
        handsRemaining = Math.max(0, handsRemaining + amount);
        notifyStateChanged();
    }
    /**
     * Performs the ge\1 \2arge\1 \2core operation.
     */
    public long getTargetScore() { return targetScore; }
    /**
     * Performs the ge\1 \2urren\1 \2core operation.
     */
    public long getCurrentScore() { return currentScore; }
    /**
     * Performs the ge\1 \2a\1 \2an\1 \2ize operation.
     */
    public int getMaxHandSize() { return maxHandSize; }
    /**
     * Performs the se\1 \2a\1 \2an\1 \2ize operation.
     */
    public void setMaxHandSize(int maxHandSize) throws InvalidGameActionException {
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
        notifyStateChanged();
    }

    /** Removes score, clamped at zero, for negative occurrence outcomes. */
    public void loseScore(long points) {
        if (points < 0) {
            throw new IllegalArgumentException("Score loss cannot be negative");
        }
        currentScore = Math.max(0, currentScore - points);
        notifyStateChanged();
    }
    /**
     * Performs the i\1 \2oun\1 \2on operation.
     */
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
    /**
     * Returns an unmodifiable view of the current hand. Ordering changes go
     * through the sortHandByRank() and sortHandBySuit() domain methods.
     */
    public List<Card> getHand() { return Collections.unmodifiableList(hand); }

    /** Sorts the hand high-to-low by rank using Card's natural ordering. */
    public void sortHandByRank() {
        hand.sort(Comparator.naturalOrder());
        System.out.println("[STATE] Hand sorted by rank: " + hand);
    }

    /** Sorts the hand by suit, then by rank within each suit. */
    public void sortHandBySuit() {
        hand.sort((first, second) -> {
            int suitComparison = first.getSuit().compareTo(second.getSuit());
            return suitComparison != 0 ? suitComparison : first.compareTo(second);
        });
        System.out.println("[STATE] Hand sorted by suit: " + hand);
    }

    /**
     * Permanently removes a card from the run deck and the active hand.
     * Supports destruction effects such as Glass shatters and the Hanged Man.
     *
     * @return true when the card belonged to the run deck
     */
    public boolean removeCardFromRun(Card card) throws InvalidGameActionException {
        if (card == null) {
            throw new InvalidGameActionException("Cannot destroy a null card.");
        }
        boolean removedFromDeck = deck.removeCardFromRun(card);
        boolean removedFromHand = hand.remove(card);
        System.out.println("[STATE] Card removal: " + card + " | handRemoved="
                + removedFromHand + " | runDeckRemoved=" + removedFromDeck);
        return removedFromDeck;
    }
    /**
     * Performs the ge\1 \2eck operation.
     */
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

    /**
     * Performs the ge\1 \2wne\1 \2aro\1 \2ards operation.
     */
    public List<TarotCards> getOwnedTarotCards() {
        return Collections.unmodifiableList(ownedTarotCards);
    }

    /**
     * Performs the ad\1 \2aro\1 \2ard operation.
     */
    public boolean addTarotCard(TarotCards tarotCard) {
        if (tarotCard == null || ownedTarotCards.size() >= MAX_TAROT_CARDS) {
            return false;
        }
        ownedTarotCards.add(tarotCard);
        System.out.println("[TAROT] Added " + tarotCard.getName() + " to consumable slot "
                + ownedTarotCards.size() + "/" + MAX_TAROT_CARDS);
        notifyStateChanged();
        return true;
    }

    /**
     * Performs the us\1 \2aro\1 \2ard operation.
     */
    public boolean useTarotCard(TarotCards tarotCard, List<Card> selectedCards)
            throws InvalidGameActionException {
        if (tarotCard == null) {
            throw new InvalidGameActionException("Cannot use a null Tarot card.");
        }
        if (!ownedTarotCards.contains(tarotCard)) {
            throw new InvalidGameActionException(
                    "Tarot card is not owned and cannot be used: " + tarotCard.getName() + ".");
        }
        // TarotCards validates its targets and throws before applying any
        // mutation, so an invalid selection never consumes the consumable.
        System.out.println("[TAROT] Attempting " + tarotCard.getName() + " with "
                + (selectedCards == null ? 0 : selectedCards.size()) + " selected card(s).");
        tarotCard.use(this, selectedCards);
        ownedTarotCards.remove(tarotCard);
        System.out.println("[TAROT] Used " + tarotCard.getName() + ".");
        notifyStateChanged();
        return true;
    }

    /**
     * Performs the ad\1 \2oker operation.
     */
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

    /**
     * Registers a listener invoked whenever run state mutates (cash, chips,
     * Jokers, hand, or round resources) so bound views can update in real time.
     */
    public void addStateChangeListener(Runnable listener) {
        if (listener != null && !stateChangeListeners.contains(listener)) {
            stateChangeListeners.add(listener);
        }
    }

    /**
     * Unregisters a previously added state change listener.
     */
    public void removeStateChangeListener(Runnable listener) {
        stateChangeListeners.remove(listener);
    }

    private void notifyStateChanged() {
        for (Runnable listener : List.copyOf(stateChangeListeners)) {
            listener.run();
        }
    }
    /**
     * Performs the i\1 \2\1 \2pecia\1 \2hallenge operation.
     */
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
