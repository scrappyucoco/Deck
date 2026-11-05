package com.game.deck.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;
    private final List<Card> discardPile;

    public Deck() {
        this.cards = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        resetToStandardDeck();
    }

    public final void resetToStandardDeck() {
        cards.clear();
        discardPile.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        shuffle();
    }

    public final void resetFullDeck() {
        resetToStandardDeck();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> draw(int count) {
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

    public void discard(List<Card> discardedCards) {
        if (discardedCards == null) {
            return;
        }
        discardPile.addAll(discardedCards);
    }

    public int remainingCards() { return cards.size(); }
    public int discardedCardsCount() { return discardPile.size(); }
    public List<Card> getCards() { return Collections.unmodifiableList(cards); }

    private void recycleDiscards() {
        if (!discardPile.isEmpty()) {
            cards.addAll(discardPile);
            discardPile.clear();
            shuffle();
        }
    }
}