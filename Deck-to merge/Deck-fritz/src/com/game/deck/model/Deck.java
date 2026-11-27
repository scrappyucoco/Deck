package com.game.deck.model;

/**
 * Contains the Deck source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Deck is the model-side draw/discard container for the standard 52-card set.
 * GameState owns one Deck, and the view never mutates this class directly;
 * draw/discard operations are coordinated by the round controller logic.
 */
public class Deck {
    private final List<Card> cards;
    private final List<Card> discardPile;

    /**
     * Performs the deck operation.
     */
    public Deck() {
        this.cards = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        resetToStandardDeck();
    }

    /**
     * Performs the rese\1 \2\1 \2tandar\1 \2eck operation.
     */
    public final void resetToStandardDeck() {
        // Rebuild every suit/rank combination so a new round starts cleanly.
        cards.clear();
        discardPile.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        shuffle();
    }

    /**
     * Performs the rese\1 \2ul\1 \2eck operation.
     */
    public final void resetFullDeck() {
        resetToStandardDeck();
    }

    /**
     * Performs the shuffle operation.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Performs the draw operation.
     */
    public List<Card> draw(int count) {
        // Recycle discarded cards only when the draw pile is exhausted.
        List<Card> drawn = new ArrayList<>();
        if (count <= 0) {
            return drawn;
        }

        for (int i = 0; i < count; i++) {
            if (cards.isEmpty()) {
                recycleDiscards();
            }
            if (cards.isEmpty()) {
                break;
            }
            drawn.add(cards.remove(0));
        }
        return drawn;
    }

    /**
     * Performs the discard operation.
     */
    public void discard(List<Card> discardedCards) {
        if (discardedCards == null) {
            return;
        }
        discardPile.addAll(discardedCards);
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
     * Performs the ge\1 \2ards operation.
     */
    public List<Card> getCards() { return Collections.unmodifiableList(cards); }

    private void recycleDiscards() {
        if (!discardPile.isEmpty()) {
            cards.addAll(discardPile);
            discardPile.clear();
            shuffle();
        }
    }
}
