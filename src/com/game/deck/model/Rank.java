package com.game.deck.model;

public enum Rank {
    TWO("2", 2, 2, "2"),
    THREE("3", 3, 3, "3"),
    FOUR("4", 4, 4, "4"),
    FIVE("5", 5, 5, "5"),
    SIX("6", 6, 6, "6"),
    SEVEN("7", 7, 7, "7"),
    EIGHT("8", 8, 8, "8"),
    NINE("9", 9, 9, "9"),
    TEN("10", 10, 10, "10"),
    JACK("Jack", 11, 10, "J"),
    QUEEN("Queen", 12, 10, "Q"),
    KING("King", 13, 10, "K"),
    ACE("Ace", 14, 11, "A");

    private final String displayName;
    private final int value;
    private final int baseChips;
    private final String assetName;

    Rank(String displayName, int value, int baseChips, String assetName) {
        this.displayName = displayName;
        this.value = value;
        this.baseChips = baseChips;
        this.assetName = assetName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValue() {
        return value;
    }

    public int getBaseChips() {
        return baseChips;
    }

    public String getAssetName() {
        return assetName;
    }
}