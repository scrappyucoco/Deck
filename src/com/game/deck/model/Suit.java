package com.game.deck.model;

/**
 * Contains the Suit source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

/*
 * Suit is the model registry for display symbols, resource prefixes, and color.
 * Card uses assetName for paths; PokerEvaluator uses the enum as a flush key;
 * CardNode uses isRed()/getSymbol() for fallback rendering.
 */
public enum Suit {
    SPADES("Spades", "♠", "spade", false),
    HEARTS("Hearts", "♥", "heart", true),
    DIAMONDS("Diamonds", "♦", "diamond", true),
    CLUBS("Clubs", "♣", "club", false);

    private final String displayName;
    private final String symbol;
    private final String assetName;
    private final boolean red;

    Suit(String displayName, String symbol, String assetName, boolean red) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.assetName = assetName;
        this.red = red;
    }

    /**
     * Performs the ge\1 \2ispla\1 \2ame operation.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Performs the ge\1 \2ymbol operation.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Performs the ge\1 \2sse\1 \2ame operation.
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Performs the i\1 \2ed operation.
     */
    public boolean isRed() {
        return red;
    }

    /** Compatibility alias for the original equipment and UI code. */
    public String getAssetPrefix() {
        return assetName;
    }
}
