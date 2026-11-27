package com.game.deck.model;

/**
 * Contains the Rank source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

/*
 * Rank is the model registry for ordering, display labels, chip values, and
 * asset suffixes. Card, Deck, PokerEvaluator, and CardNode all consume it.
 */
public enum Rank {
    // [CONFIG: TWEAK SCORE VALUES HERE]
    // Use NAME("display", sort value, base chips, asset suffix); face-card
    // assets must keep the J/Q/K/A naming convention used by resources/assets.
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

    /**
     * Performs the ge\1 \2ispla\1 \2ame operation.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Performs the ge\1 \2alue operation.
     */
    public int getValue() {
        return value;
    }

    /**
     * Performs the ge\1 \2as\1 \2hips operation.
     */
    public int getBaseChips() {
        return baseChips;
    }

    /**
     * Performs the ge\1 \2sse\1 \2ame operation.
     */
    public String getAssetName() {
        return assetName;
    }
}
