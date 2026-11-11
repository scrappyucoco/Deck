package com.game.deck.equipment;

import com.game.deck.model.GameState;

/**
 * Base contract for Balatro-inspired Tarot consumables.
 * Concrete cards provide their own effect against the active run state.
 */
public abstract class TarotCards {
    public static final String ASSET_DIRECTORY = "assets/tarotCards/";

    private final String name;
    private final String description;
    private final String assetPath;
    private final int baseShopCost;
    private final int baseSellValue;
    private int shopCost;
    private int sellValue;

    /**
     * Defines the display metadata shared by all Tarot cards.
     * assetName is the filename stem under resources/assets/tarotCards/.
     */
    protected TarotCards(String name, String description, String assetName, int shopCost, int sellValue) {
        this.name = name;
        this.description = description;
        this.assetPath = ASSET_DIRECTORY + assetName + ".png";
        this.baseShopCost = shopCost;
        this.baseSellValue = sellValue;
        this.shopCost = shopCost;
        this.sellValue = sellValue;
    }

    /**
     * Applies this Tarot card's effect to the active run.
     */
    public final void use(GameState state) {
        applyEffect(state);
    }

    protected abstract void applyEffect(GameState state);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAssetPath() { return assetPath; }
    public int getShopCost() { return shopCost; }
    public int getSellValue() { return sellValue; }
    protected final int getBaseShopCost() { return baseShopCost; }
    protected final int getBaseSellValue() { return baseSellValue; }
}
