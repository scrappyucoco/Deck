package com.game.deck.equipment;

/**
 * Contains the Item source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

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

    /**
     * Performs the item operation.
     */
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
    /**
     * Performs the o\1 \2oke\1 \2rigger operation.
     */
    public abstract void onJokerTrigger(ScoringContext ctx);
    /**
     * Performs the ge\1 \2rigge\1 \2ultiplier operation.
     */
    public int getTriggerMultiplier() { return 1; }

    /**
     * Performs the ge\1 \2ame operation.
     */
    public String getName() { return name; }
    /**
     * Performs the ge\1 \2escription operation.
     */
    public String getDescription() { return description; }
    /**
     * Performs the ge\1 \2arity operation.
     */
    public ItemRarity getRarity() { return rarity; }
    /**
     * Performs the i\1 \2onsumable operation.
     */
    public boolean isConsumable() { return consumable; }
    /**
     * Performs the ge\1 \2sse\1 \2ath operation.
     */
    public String getAssetPath() { return assetPath; }
}
