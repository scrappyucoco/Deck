package com.game.deck.model;

public enum Enhancement {
    NONE("Standard Card", null),
    BONUS("+30 Bonus Chips", "assets/enhancements/bonus_shield.png"),
    MULT("+4 Flat Mult", "assets/enhancements/mult_fire.png"),
    GLASS("x2 Mult (1/4 Shatter Risk)", "assets/enhancements/glass_border.png"),
    STEEL("x1.5 Mult Held in Hand", "assets/enhancements/steel_frame.png"),
    GOLD("+$3 End of Round", "assets/enhancements/gold_foil.png"),
    LUCKY("Chance for +20 Mult or $20", "assets/enhancements/lucky_clover.png"),
    STONE("+50 Chips (No Rank/Suit)", "assets/enhancements/stone_texture.png"),
    WILD("Counts as Any Suit", "assets/enhancements/wild_rainbow.png");

    private final String description;
    private final String overlayAssetPath;

    Enhancement(String description, String overlayAssetPath) {
        this.description = description;
        this.overlayAssetPath = overlayAssetPath;
    }

    public String getDescription() { return description; }
    public String getOverlayAssetPath() { return overlayAssetPath; }
}