package com.game.deck.equipment;

/**
 * Contains the TarotCards source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.GameState;
import com.game.deck.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Base contract for Balatro-inspired Tarot consumables.
 * Concrete cards provide their own effect against the active run state.
 */
public abstract class TarotCards {
    public static final String ASSET_DIRECTORY = "assets/tarotCards/";
    private static final List<Supplier<? extends TarotCards>> AVAILABLE_TAROT_FACTORIES = List.of(
        Star::new
            // Register concrete Tarot cards here, for example: MyTarot::new
    );
    private static final Random SHOP_RANDOM = new Random();
    public static final int DEFAULT_SHOP_COST = 3;
    public static final int DEFAULT_SELL_VALUE = 1;

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
    protected TarotCards(String name, String description, String assetName) {
        this(name, description, assetName, DEFAULT_SHOP_COST, DEFAULT_SELL_VALUE);
    }

    /**
     * Performs the taro\1 \2ards operation.
     */
    protected TarotCards(String name, String description, String assetName, int shopCost, int sellValue) {
        this.name = name;
        this.description = description;
        this.assetPath = ASSET_DIRECTORY + assetName + ".png";
        if (shopCost < 0 || sellValue < 0) {
            throw new IllegalArgumentException("Tarot card prices cannot be negative");
        }
        this.baseShopCost = shopCost;
        this.baseSellValue = sellValue;
        this.shopCost = shopCost;
        this.sellValue = sellValue;
    }

    /**
     * Applies this Tarot card's effect to the active run.
     */
    public final void use(GameState state, List<Card> selecteCards) {
        applyEffect(state, selecteCards);
    }

    /**
     * Performs the appl\1 \2ffect operation.
     */
    protected abstract void applyEffect(GameState state, List<Card> selecteCards);

    /**
     * Creates up to maxOffers distinct instances from the registered Tarot pool.
     * An empty pool is valid until concrete Tarot cards are registered.
     */
    public static List<TarotCards> createShopOffers(int maxOffers) {
        if (maxOffers < 1) {
            throw new IllegalArgumentException("The shop must request at least one offer.");
        }
        if (AVAILABLE_TAROT_FACTORIES.isEmpty()) {
            return List.of();
        }

        List<Supplier<? extends TarotCards>> shuffledFactories =
                new ArrayList<>(AVAILABLE_TAROT_FACTORIES);
        Collections.shuffle(shuffledFactories, SHOP_RANDOM);

        int offerCount = Math.min(maxOffers, shuffledFactories.size());
        List<TarotCards> offers = new ArrayList<>(offerCount);
        for (int i = 0; i < offerCount; i++) {
            offers.add(shuffledFactories.get(i).get());
        }
        return List.copyOf(offers);
    }

    /**
     * Performs the ge\1 \2ame operation.
     */
    public String getName() { return name; }
    /**
     * Performs the ge\1 \2escription operation.
     */
    public String getDescription() { return description; }
    /**
     * Performs the ge\1 \2sse\1 \2ath operation.
     */
    public String getAssetPath() { return assetPath; }
    /**
     * Performs the ge\1 \2ho\1 \2ost operation.
     */
    public int getShopCost() { return shopCost; }
    /**
     * Performs the ge\1 \2el\1 \2alue operation.
     */
    public int getSellValue() { return sellValue; }
    /**
     * Performs the ge\1 \2as\1 \2ho\1 \2ost operation.
     */
    protected final int getBaseShopCost() { return baseShopCost; }
    /**
     * Performs the ge\1 \2as\1 \2el\1 \2alue operation.
     */
    protected final int getBaseSellValue() { return baseSellValue; }
}
