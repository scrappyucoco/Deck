package com.game.deck.model;

/*
 * PokerHand is the model catalog of recognized poker combinations.
 * PokerEvaluator selects an entry; scoring engines read its base chips/mult;
 * the level field provides a future Planet/Tome progression hook.
 */
public enum PokerHand {
    // ==========================================
    // [CONFIG: TWEAK SCORE VALUES HERE]
    // Each entry is NAME("display name", base chips, base multiplier); level
    // scaling is implemented by getChips() and getMult() below.
    HIGH_CARD("High Card", 3, 1),
    PAIR("Pair", 4, 2),
    TWO_PAIR("Two Pair", 5, 2),
    THREE_OF_A_KIND("Three of a Kind", 6, 3),
    STRAIGHT("Straight", 8, 4),
    FLUSH("Flush", 9, 4),
    FULL_HOUSE("Full House", 10, 4),
    FOUR_OF_A_KIND("Four of a Kind", 12, 5),
    STRAIGHT_FLUSH("Straight Flush", 12, 5),
    FIVE_OF_A_KIND("Five of a Kind", 13, 6),
    FLUSH_HOUSE("Flush House", 14, 6),
    FLUSH_FIVE("Flush Five", 16, 8);

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

    // [CONFIG: TWEAK SCORE VALUES HERE]
    // Change the per-level chip/multiplier increments here to tune progression.
    public int getChips() { return baseChips + ((level - 1) * 15); }
    public int getMult() { return baseMult + ((level - 1) * 2); }
    public void levelUp() { this.level++; }
    public int getLevel() { return level; }
    public String getDisplayName() { return displayName; }
}