package com.game.deck.model;

public enum Rank {
    TWO(2, 2, "2"),
    THREE(3, 3, "3"),
    FOUR(4, 4, "4"),
    FIVE(5, 5, "5"),
    SIX(6, 6, "6"),
    SEVEN(7, 7, "7"),
    EIGHT(8, 8, "8"),
    NINE(9, 9, "9"),
    TEN(10, 10, "10"),
    JACK(11, 10, "jack"),
    QUEEN(12, 10, "queen"),
    KING(13, 10, "king"),
    ACE(14, 11, "ace");

    private final int value;
    private final int baseChips;
    private final String assetName;

    Rank(int value, int baseChips, String assetName) {
        this.value = value;
        this.baseChips = baseChips;
        this.assetName = assetName;
    }

    public int getValue() { return value; }
    public int getBaseChips() { return baseChips; }
    public String getAssetName() { return assetName; }
}