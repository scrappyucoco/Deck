package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;

/*
 * Item is the equipment extension base for Joker socket effects.
 * Joker owns and triggers Items; concrete Items mutate ScoringContext through
 * onJokerTrigger(), keeping equipment behavior separate from model entities.
 */
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

    // --- ITEM EXTENSION CONTRACT ---
    // Socket a concrete Item with joker.socketItem(new MyItem()); implement
    // onJokerTrigger(ScoringContext) to define its effect.
    public abstract void onJokerTrigger(ScoringContext ctx);
    public int getTriggerMultiplier() { return 1; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemRarity getRarity() { return rarity; }
    public boolean isConsumable() { return consumable; }
    public String getAssetPath() { return assetPath; }
}