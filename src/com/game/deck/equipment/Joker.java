package com.game.deck.equipment;

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
    // ==========================================
    // [CUSTOMIZATION: ADD NEW JOKERS HERE]
    // Register each concrete class as MyJoker::new; the class's constructor
    // supplies its name, description, asset, purchase price, and sell value.
    private static final List<Supplier<? extends Joker>> AVAILABLE_JOKER_FACTORIES = List.of(
            RationalistJoker::new
    );
    private static final Random SHOP_RANDOM = new Random();

    private final String name;
    private final String description;
    private final List<Item> sockets;
    private static final int MAX_SOCKETS = 2;
    private final String assetPath;
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
     */
    protected Joker(String name, String description, String assetName, int shopCost, int sellValue) {
        if (shopCost < 0 || sellValue < 0) {
            throw new IllegalArgumentException("Joker prices cannot be negative");
        }
        this.name = name;
        this.description = description;
        this.sockets = new ArrayList<>();
        this.assetPath = "assets/jokers/" + assetName + ".png";
        this.shopCost = shopCost;
        this.sellValue = sellValue;
    }

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
     * Selects and constructs one available Joker for the current shop offer.
     * The random index is still generated when the pool contains only one entry.
     */
    public static Joker createRandomJoker() {
        if (AVAILABLE_JOKER_FACTORIES.isEmpty()) {
            throw new IllegalStateException("The Joker shop pool cannot be empty.");
        }

        int selectedIndex = SHOP_RANDOM.nextInt(AVAILABLE_JOKER_FACTORIES.size());
        Joker selectedJoker = AVAILABLE_JOKER_FACTORIES.get(selectedIndex).get();
        System.out.println("[SHOP] RNG selected Joker " + (selectedIndex + 1) + "/"
                + AVAILABLE_JOKER_FACTORIES.size() + ": " + selectedJoker.getName());
        return selectedJoker;
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
        return shopCost;
    }

    /**
     * Returns the immutable constructor resale value for custom calculations.
     */
    protected final int getBaseSellValue() {
        return sellValue;
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

    protected abstract void applyBaseEffect(ScoringContext ctx);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Item> getSockets() { return Collections.unmodifiableList(sockets); }
    public String getAssetPath() { return assetPath; }
}