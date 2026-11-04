package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

public abstract class Item {
    private final String name;
    private final String description;
    private final ItemRarity rarity;
    private final boolean consumable;
    private final String assetPath;

    public Item(String name, String description, ItemRarity rarity, boolean consumable, String assetName) {
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.consumable = consumable;
        this.assetPath = "assets/items/" + assetName + ".png";
    }

    public abstract void onJokerTrigger(ScoringContext ctx);
    public int getTriggerMultiplier() { return 1; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemRarity getRarity() { return rarity; }
    public boolean isConsumable() { return consumable; }
    public String getAssetPath() { return assetPath; }
}