package com.game.deck.model;

public class Card {
    private final Rank rank;
    private final Suit suit;
    private Enhancement enhancement;
    private boolean isDebuffed;

    public Card(Rank rank, Suit suit) {
        this(rank, suit, Enhancement.NONE);
    }

    public Card(Rank rank, Suit suit, Enhancement enhancement) {
        this.rank = rank;
        this.suit = suit;
        this.enhancement = enhancement;
        this.isDebuffed = false;
    }

    public int getChipValue() {
        if (isDebuffed) return 0;
        if (enhancement == Enhancement.STONE) return 50;
        return rank.getBaseChips();
    }

    // Asset Path Wiring: assets/cards/spade_ace.png
    public String getAssetPath() {
        return "assets/cards/" + suit.getAssetPrefix() + "_" + rank.getAssetName() + ".png";
    }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public Enhancement getEnhancement() { return enhancement; }
    public void setEnhancement(Enhancement enhancement) { this.enhancement = enhancement; }
    public boolean isDebuffed() { return isDebuffed; }
    public void setDebuffed(boolean debuffed) { isDebuffed = debuffed; }

    @Override
    public String toString() {
        String enhStr = (enhancement != Enhancement.NONE) ? " [" + enhancement.name() + "]" : "";
        return rank.name() + " of " + suit.name() + enhStr;
    }
}