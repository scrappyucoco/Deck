package com.game.deck.model;

public enum PokerHand {
    HIGH_CARD("High Card", 5, 1),
    PAIR("Pair", 10, 2),
    TWO_PAIR("Two Pair", 20, 2),
    THREE_OF_A_KIND("Three of a Kind", 30, 3),
    STRAIGHT("Straight", 30, 4),
    FLUSH("Flush", 35, 4),
    FULL_HOUSE("Full House", 40, 4),
    FOUR_OF_A_KIND("Four of a Kind", 60, 7),
    STRAIGHT_FLUSH("Straight Flush", 100, 8),
    FIVE_OF_A_KIND("Five of a Kind", 120, 12),
    FLUSH_HOUSE("Flush House", 140, 14),
    FLUSH_FIVE("Flush Five", 160, 16);

    private final String displayName;
    private final int baseChips;
    private final int baseMult;
    private int level;

    PokerHand(String displayName, int baseChips, int baseMult) {
        this.displayName = displayName;
        this.baseChips = baseChips;
        this.baseMult = baseMult;
        this.level = 1;
    }

    public int getChips() { return baseChips + ((level - 1) * 15); }
    public int getMult() { return baseMult + ((level - 1) * 2); }
    public void levelUp() { this.level++; }
    public int getLevel() { return level; }
    public String getDisplayName() { return displayName; }
}