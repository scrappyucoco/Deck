package com.game.deck.model;

public enum Enchantment {
    NONE("Standard Card", null),
    BONUS("+30 Bonus Chips", "assets/enchantment/bonus_shield.png"),
    MULT("+4 Flat Mult", "assets/enchantment/mult_fire.png"),
    GLASS("x2 Mult (1/4 Shatter Risk)", "assets/enchantment/glass_border.png"),
    STEEL("x1.5 Mult Held in Hand", "assets/enchantment/steel_frame.png"),
    GOLD("+$3 End of Round", "assets/enchantment/gold_foil.png"),
    LUCKY("Chance for +20 Mult or $20", "assets/enchantment/lucky_clover.png"),
    STONE("+50 Chips (No Rank/Suit)", "assets/enchantment/stone_texture.png"),
    WILD("Counts as Any Suit", "assets/enchantment/wild_rainbow.png");

    private final String description;
    private final String overlayAssetPath;

    Enchantment(String description, String overlayAssetPath) {
        this.description = description;
        this.overlayAssetPath = overlayAssetPath;
    }

    public String getDescription() { return description; }
    public String getOverlayAssetPath() { return overlayAssetPath; }
}