package com.game.deck.model;

/**
 * Contains the Deck source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.exception.InvalidGameActionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * Deck is the model-side container for every card owned during a run.
 * masterDeck is the single in-memory source of truth: it persists permanent
 * suit, rank, and enchantment changes across rounds and antes. cards is the
 * active draw pile and discardPile holds spent cards; both are rebuilt from
 * masterDeck at the start of each round without creating new Card objects.
 */
public class Deck {
    private final List<Card> masterDeck = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private final List<Card> discardPile = new ArrayList<>();

    /**
     * Creates a new run deck and prepares its first active draw pile.
     */
    public Deck() {
        resetToStandardDeck();
    }

    /**
     * Rebuilds the run deck for a brand-new run.
     * Only a new-run transition should call this method; round transitions use
     * prepareForRound() so permanent card modifications are preserved.
     */
    public final void resetToStandardDeck() {
        masterDeck.clear();
        cards.clear();
        discardPile.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                masterDeck.add(new Card(rank, suit));
            }
        }
        System.out.println("[DECK] Brand-new run deck created with " + masterDeck.size() + " cards.");
        prepareForRound();
    }

    /**
     * Prepares the active draw pile for a new round without replacing cards.
     * Clears transient per-round state (debuffs) but keeps the permanent suit,
     * rank, and enchantment changes stored on the master deck instances.
     */
    public final void prepareForRound() {
        cards.clear();
        discardPile.clear();
        int clearedDebuffs = 0;
        for (Card card : masterDeck) {
            if (card.isDebuffed()) {
                clearedDebuffs++;
            }
            card.setDebuffed(false);
            cards.add(card);
        }
        shuffle();
        System.out.println("[DECK] Round prepared from master deck: " + cards.size()
                + " cards loaded, " + clearedDebuffs + " transient debuff(s) cleared.");
    }

    /**
     * Performs the shuffle operation.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws cards from the active pile, recycling discards when necessary.
     *
     * @throws InvalidGameActionException when the requested count is invalid
     *         or both the draw and discard piles are empty
     */
    public List<Card> draw(int count) throws InvalidGameActionException {
        if (count <= 0) {
            throw new InvalidGameActionException(
                    "Cannot draw cards: the requested count must be at least 1 (requested " + count + ").");
        }
        if (cards.isEmpty() && discardPile.isEmpty()) {
            throw new InvalidGameActionException(
                    "Cannot draw from an empty deck: no cards remain in the draw pile or discard pile.");
        }

        // Recycle discarded cards only when the draw pile is exhausted.
        List<Card> drawn = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (cards.isEmpty()) {
                recycleDiscards();
            }
            if (cards.isEmpty()) {
                // The run deck ran out mid-draw; return the cards already drawn.
                break;
            }
            drawn.add(cards.remove(0));
        }
        return drawn;
    }

    /**
     * Discards cards rejected if the list, an entry, or a duplicate is invalid.
     *
     * @throws InvalidGameActionException when a null or duplicate card is discarded
     */
    public void discard(List<Card> discardedCards) throws InvalidGameActionException {
        if (discardedCards == null) {
            throw new InvalidGameActionException("Cannot discard a null card list.");
        }
        Set<Card> newDiscards = new LinkedHashSet<>();
        for (Card card : discardedCards) {
            if (card == null) {
                throw new InvalidGameActionException("Cannot discard a null card.");
            }
            if (!newDiscards.add(card)) {
                throw new InvalidGameActionException(
                        "Cannot discard the same card more than once: " + card + ".");
            }
            if (discardPile.contains(card)) {
                throw new InvalidGameActionException(
                        "Card is already in the discard pile: " + card + ".");
            }
        }
        discardPile.addAll(newDiscards);
        System.out.println("[DECK] Discarded " + newDiscards.size()
                + " card(s). Discard pile=" + discardPile.size() + ".");
    }

    /**
     * Adds a new card to the run permanently and makes it drawable this round.
     *
     * @return true when the card was added
     * @throws InvalidGameActionException when the card is null or already owned
     */
    public boolean addCardToRun(Card card) throws InvalidGameActionException {
        if (card == null) {
            throw new InvalidGameActionException("Cannot add a null card to the run deck.");
        }
        if (masterDeck.contains(card)) {
            throw new InvalidGameActionException(
                    "Card is already part of the run deck: " + card + ".");
        }
        masterDeck.add(card);
        cards.add(card);
        System.out.println("[DECK] Added " + card + " to the run deck. Total=" + masterDeck.size() + ".");
        return true;
    }

    /**
     * Permanently removes a card from the run: master deck, draw pile, and
     * discard pile. The active hand is cleared separately by GameState.
     *
     * @return true when the card was removed
     * @throws InvalidGameActionException when the card is null or not owned
     */
    public boolean removeCardFromRun(Card card) throws InvalidGameActionException {
        if (card == null) {
            throw new InvalidGameActionException("Cannot remove a null card from the run deck.");
        }
        if (!masterDeck.contains(card)) {
            throw new InvalidGameActionException(
                    "Card does not belong to the run deck and cannot be destroyed: " + card + ".");
        }
        cards.remove(card);
        discardPile.remove(card);
        masterDeck.remove(card);
        System.out.println("[DECK] Permanently removed " + card
                + " from the run deck. Total=" + masterDeck.size() + ".");
        return true;
    }

    /**
     * Performs the remainin\1 \2ards operation.
     */
    public int remainingCards() { return cards.size(); }
    /**
     * Performs the discarde\1 \2ard\1 \2ount operation.
     */
    public int discardedCardsCount() { return discardPile.size(); }
    /**
     * Returns an unmodifiable view of the active draw pile.
     */
    public List<Card> getCards() { return Collections.unmodifiableList(cards); }
    /**
     * Returns an unmodifiable view of every card owned during this run.
     */
    public List<Card> getMasterDeck() { return Collections.unmodifiableList(masterDeck); }
    /**
     * Returns the total number of cards in the persistent run deck.
     */
    public int totalCards() { return masterDeck.size(); }

    private void recycleDiscards() {
        if (!discardPile.isEmpty()) {
            int recycled = discardPile.size();
            cards.addAll(discardPile);
            discardPile.clear();
            shuffle();
            System.out.println("[DECK] Recycled " + recycled + " discarded card(s) into the draw pile.");
        }
    }
}
