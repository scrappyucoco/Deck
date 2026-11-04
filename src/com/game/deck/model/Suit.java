package com.game.deck.model;

public enum Suit {
    CLUBS("Clubs", "clubs"),
    DIAMONDS("Diamonds", "diamonds"),
    HEARTS("Hearts", "hearts"),
    SPADES("Spades", "spades");

    private final String displayName;
    private final String assetPrefix;

    Suit(String displayName, String assetPrefix) {
        this.displayName = displayName;
        this.assetPrefix = assetPrefix;
    }

    public String getDisplayName() { return displayName; }
    public String getAssetPrefix() { return assetPrefix; }
}