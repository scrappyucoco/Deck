package com.game.deck.model;

/**
 * Contains the Card source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import java.util.Objects;

/*
 * Card is the model entity for one playable card.
 * It owns immutable rank/suit identity plus mutable enchantment/debuff state.
 * The engine package reads its chip and modifier values, while views.CardNode
 * uses getAssetPath() and the display accessors to render the card.
 */
public class Card implements Comparable<Card> {
    private Rank rank;
    private Suit suit;
    private Enchantment enchantment;
    private boolean isDebuffed;

    /**
     * Performs the card operation.
     */
    public Card(Rank rank, Suit suit) {
        this(rank, suit, Enchantment.NONE);
    }

    /**
     * Performs the card operation.
     */
    public Card(Rank rank, Suit suit, Enchantment enchantment) {
        this.rank = Objects.requireNonNull(rank, "rank");
        this.suit = Objects.requireNonNull(suit, "suit");
        this.enchantment = Objects.requireNonNull(enchantment, "enchantment");
        this.isDebuffed = false;
    }

    /**
     * Performs the ge\1 \2hi\1 \2alue operation.
     */
    public int getChipValue() {
        // Debuffs suppress scoring; Stone replaces the normal rank chip value.
        if (isDebuffed) return 0;
        if (enchantment == Enchantment.STONE) return 50;
        return rank.getBaseChips();
    }

    /**
     * Performs the ge\1 \2sse\1 \2ath operation.
     */
    public String getAssetPath() {
        return "assets/cards/" + suit.getAssetName() + "_" + rank.getAssetName() + ".png";
    }

    /**
     * Performs the ge\1 \2ank operation.
     */
    public Rank getRank() { return rank; }
    /**
     * Performs the ge\1 \2uit operation.
     */
    public Suit getSuit() { return suit; }
    /**
     * Performs the ge\1 \2nchantment operation.
     */
    public Enchantment getEnchantment() { return enchantment; }
    /**
     * Performs the se\1 \2nchantment operation.
     */
    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = Objects.requireNonNull(enchantment, "enchantment");
    }
    /**
     * Performs the se\1 \2uit operation.
     */
    public void setSuit(Suit suit){
        this.suit = suit;
    }
    /**
     * Performs the se\1 \2ank operation.
     */
    public void setRank(Rank rank){
        this.rank = rank;
    }
    /**
     * Performs the i\1 \2ebuffed operation.
     */
    public boolean isDebuffed() { return isDebuffed; }
    /**
     * Performs the se\1 \2ebuffed operation.
     */
    public void setDebuffed(boolean debuffed) { isDebuffed = debuffed; }

    @Override
    /**
     * Performs the compar\1 \2o operation.
     */
    public int compareTo(Card other) {
        // Sort high-to-low by rank, then use enum order for a stable suit order.
        Objects.requireNonNull(other, "other");
        int rankComparison = Integer.compare(other.rank.getValue(), rank.getValue());
        return rankComparison != 0 ? rankComparison : suit.compareTo(other.suit);
    }

    @Override
    /**
     * Performs the t\1 \2tring operation.
     */
    public String toString() {
        String enhancement = enchantment != Enchantment.NONE ? " [" + enchantment.name() + "]" : "";
        return rank.getDisplayName() + " of " + suit.getDisplayName() + enhancement;
    }
}
