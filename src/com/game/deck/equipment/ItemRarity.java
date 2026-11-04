package com.game.deck.equipment;

public enum ItemRarity {
    COMMON("Common", 4),
    RARE("Rare", 7),
    GOLD("Gold", 12);

    private final String displayName;
    private final int basePrice;

    ItemRarity(String displayName, int basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() { return displayName; }
    public int getBasePrice() { return basePrice; }
}