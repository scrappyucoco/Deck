package com.game.deck.equipment;

/*
 * ItemRarity is the equipment value tier used by shop pricing and item UI.
 * Item stores this enum; Shop or future inventory views can read its price.
 */
public enum ItemRarity {
    // [CONFIG: TWEAK SCORE VALUES HERE]
    // These are economy values, not score multipliers: adjust basePrice per tier.
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