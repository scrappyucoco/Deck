package com.game.deck.equipment;

/**
 * Contains the Joker source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.engine.ScoringContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/*
 * Joker is the abstract Equipment/Engine contract for all active Joker cards.
 * GameState stores concrete instances, GameView renders them, and
 * ScoringEngine invokes evaluate() in their current left-to-right order.
 */
public abstract class Joker {
    public static final String ASSET_DIRECTORY = "assets/jokers/";
    private static final String DEFAULT_ASSET_NAME = "joker_1";
    public static final String DEFAULT_ASSET_PATH = ASSET_DIRECTORY + DEFAULT_ASSET_NAME + ".png";

    // ==========================================
    // [CUSTOMIZATION: ADD NEW JOKERS HERE]
    // Register every shop Joker exactly once as MyJoker::new. The shop samples
    // this complete hardcoded list without replacement, so every offer is equal.
    private static final List<Supplier<? extends Joker>> AVAILABLE_JOKER_FACTORIES = List.of(
            RationalistJoker::new,
            GreedyJoker::new
    );
    private static final Random SHOP_RANDOM = new Random();

    private final String name;
    private final String description;
    private final List<Item> sockets;
    private static final int MAX_SOCKETS = 2;
    private final String assetPath;
    private final int baseShopCost;
    private final int baseSellValue;
    private int shopCost;
    private int sellValue;

    /**
     * Compatibility constructor for utility/test Jokers that are not shop offers.
     * Shop-facing concrete Jokers should use the five-argument constructor.
     */
    protected Joker(String name, String description, String assetName) {
        this(name, description, assetName, 0, 0);
    }

    /**
     * Defines the display metadata and individual economy values for a Joker.
     * assetName is the filename stem under resources/assets/jokers/, such as
     * "joker_1" for resources/assets/jokers/joker_1.png.
     */
    protected Joker(String name, String description, String assetName, int shopCost, int sellValue) {
        if (assetName == null || assetName.isBlank()) {
            throw new IllegalArgumentException("Joker asset name cannot be blank");
        }
        if (shopCost < 0 || sellValue < 0) {
            throw new IllegalArgumentException("Joker prices cannot be negative");
        }
        this.name = name;
        this.description = description;
        this.sockets = new ArrayList<>();
        this.assetPath = ASSET_DIRECTORY + assetName + ".png";
        this.baseShopCost = shopCost;
        this.baseSellValue = sellValue;
        this.shopCost = shopCost;
        this.sellValue = sellValue;
    }

    /**
     * Performs the socke\1 \2tem operation.
     */
    public boolean socketItem(Item item) {
        // --- SOCKET MANAGEMENT ---
        if (sockets.size() < MAX_SOCKETS) {
            sockets.add(item);
            System.out.println("[EQUIPMENT] Socketed " + item.getName() + " into " + this.name + " [Slot " + sockets.size() + "/2]");
            return true;
        }
        System.out.println("[EQUIPMENT] Sockets full for " + this.name);
        return false;
    }

    /**
     * Performs the evaluate operation.
     */
    public void evaluate(ScoringContext ctx) {
        // --- AUTHORITATIVE TRIGGER ---
        int repeatTimes = 1;
        for (Item item : sockets) {
            repeatTimes *= item.getTriggerMultiplier();
        }

        for (int i = 0; i < repeatTimes; i++) {
            applyBaseEffect(ctx);
            applyRuntimeEffects(ctx);
            for (Item item : sockets) {
                item.onJokerTrigger(ctx);
            }
        }

        sockets.removeIf(Item::isConsumable);
    }

    /**
     * Applies only the joker's own effect for a non-mutating score preview.
     * Socket effects and consumable removal belong exclusively to evaluate().
     */
    public void preview(ScoringContext ctx) {
        // Preview score effects only; economy changes belong to the live run.
        applyBaseEffect(ctx);
    }

    /**
     * Applies changes to the recognized hand before cards and enchantments score.
     * Override this for effects such as changing Flush base chips or multiplier.
     */
    public void modifyHand(ScoringContext ctx) {
        // Default Jokers do not alter the hand's base values.
    }

    /**
     * Applies live-run effects that must not execute during score previews.
     * Use this for money changes or persistent price adjustments.
     */
    protected void applyRuntimeEffects(ScoringContext ctx) {
        // Default Jokers have no economy-side effects.
    }

    /**
     * Creates up to maxOffers distinct shop instances from the registered pool.
     * Shuffling the complete pool gives every registered factory equal odds.
     */
    public static List<Joker> createShopOffers(int maxOffers) {
        if (AVAILABLE_JOKER_FACTORIES.isEmpty()) {
            throw new IllegalStateException("The Joker shop pool cannot be empty.");
        }
        if (maxOffers < 1) {
            throw new IllegalArgumentException("The shop must request at least one offer.");
        }

        List<Supplier<? extends Joker>> shuffledFactories =
                new ArrayList<>(AVAILABLE_JOKER_FACTORIES);
        Collections.shuffle(shuffledFactories, SHOP_RANDOM);

        int offerCount = Math.min(maxOffers, shuffledFactories.size());
        List<Joker> offers = new ArrayList<>(offerCount);
        for (int i = 0; i < offerCount; i++) {
            Joker offer = shuffledFactories.get(i).get();
            offers.add(offer);
            System.out.println("[SHOP] RNG offer " + (i + 1) + "/" + offerCount
                    + ": " + offer.getName());
        }
        return List.copyOf(offers);
    }

    /**
     * Compatibility helper for callers that need one random registered Joker.
     */
    public static Joker createRandomJoker() {
        return createShopOffers(1).get(0);
    }

    /**
     * Returns this Joker's individual purchase price.
     */
    public int getShopCost() {
        return shopCost;
    }

    /**
     * Returns this Joker's individual resale value.
     */
    public int getSellValue() {
        return sellValue;
    }

    /**
     * Returns the immutable constructor price for custom price calculations.
     */
    protected final int getBaseShopCost() {
        return baseShopCost;
    }

    /**
     * Returns the immutable constructor resale value for custom calculations.
     */
    protected final int getBaseSellValue() {
        return baseSellValue;
    }

    /**
     * Adjusts this instance's future shop price; use from a concrete Joker hook.
     */
    protected final void adjustShopCost(int amount) {
        shopCost = requireNonNegativePrice("shop cost", shopCost + amount);
    }

    /**
     * Adjusts this instance's future resale value; use from a concrete Joker hook.
     */
    protected final void adjustSellValue(int amount) {
        sellValue = requireNonNegativePrice("sell value", sellValue + amount);
    }

    private static int requireNonNegativePrice(String label, int value) {
        if (value < 0) {
            throw new IllegalArgumentException(label + " cannot be negative");
        }
        return value;
    }

    /**
     * Performs the appl\1 \2as\1 \2ffect operation.
     */
    protected abstract void applyBaseEffect(ScoringContext ctx);

    /**
     * Performs the ge\1 \2ame operation.
     */
    public String getName() { return name; }
    /**
     * Performs the ge\1 \2escription operation.
     */
    public String getDescription() { return description; }
    /**
     * Performs the ge\1 \2ockets operation.
     */
    public List<Item> getSockets() { return Collections.unmodifiableList(sockets); }
    /**
     * Performs the ge\1 \2sse\1 \2ath operation.
     */
    public String getAssetPath() { return assetPath; }
}
