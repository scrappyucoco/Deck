package com.game.deck.model;

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

    public Card(Rank rank, Suit suit) {
        this(rank, suit, Enchantment.NONE);
    }

    public Card(Rank rank, Suit suit, Enchantment enchantment) {
        this.rank = Objects.requireNonNull(rank, "rank");
        this.suit = Objects.requireNonNull(suit, "suit");
        this.enchantment = Objects.requireNonNull(enchantment, "enchantment");
        this.isDebuffed = false;
    }

    public int getChipValue() {
        // Debuffs suppress scoring; Stone replaces the normal rank chip value.
        if (isDebuffed) return 0;
        if (enchantment == Enchantment.STONE) return 50;
        return rank.getBaseChips();
    }

    public String getAssetPath() {
        return "assets/cards/" + suit.getAssetName() + "_" + rank.getAssetName() + ".png";
    }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public Enchantment getEnchantment() { return enchantment; }
    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = Objects.requireNonNull(enchantment, "enchantment");
    }
    public void setSuit(Suit suit){
        this.suit = suit;
    }
    public void setRank(Rank rank){
        this.rank = rank;
    }
    public boolean isDebuffed() { return isDebuffed; }
    public void setDebuffed(boolean debuffed) { isDebuffed = debuffed; }

    @Override
    public int compareTo(Card other) {
        // Sort high-to-low by rank, then use enum order for a stable suit order.
        Objects.requireNonNull(other, "other");
        int rankComparison = Integer.compare(other.rank.getValue(), rank.getValue());
        return rankComparison != 0 ? rankComparison : suit.compareTo(other.suit);
    }

    @Override
    public String toString() {
        String enhancement = enchantment != Enchantment.NONE ? " [" + enchantment.name() + "]" : "";
        return rank.getDisplayName() + " of " + suit.getDisplayName() + enhancement;
    }
}