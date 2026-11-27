package com.game.deck.equipment;

/**
 * Contains the TarotCards source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.GameState;
import com.game.deck.model.Card;
import com.game.deck.exception.InvalidGameActionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
        StarCard::new,
        StrengthCard::new,
        WeaknessCard::new,
        WheelOfFortuneCard::new
            // Register concrete Tarot cards here, for example: MyTarot::new
    );
    private static final Random SHOP_RANDOM = new Random();
    public static final int DEFAULT_SHOP_COST = 3;
    public static final int DEFAULT_SELL_VALUE = 1;
    public static final int DEFAULT_MAX_TARGET_CARDS = 5;

    private final String name;
    private final String description;
    private final String assetPath;
    private final int baseShopCost;
    private final int baseSellValue;
    private final int maxTargetCards;
    private int shopCost;
    private int sellValue;

    /**
     * Defines the display metadata shared by all Tarot cards.
     * assetName is the filename stem under resources/assets/tarotCards/.
     */
    protected TarotCards(String name, String description, String assetName) {
        this(name, description, assetName, DEFAULT_SHOP_COST, DEFAULT_SELL_VALUE,
                DEFAULT_MAX_TARGET_CARDS);
    }

    /**
     * Defines display metadata and economy values with the default target limit.
     */
    protected TarotCards(String name, String description, String assetName, int shopCost, int sellValue) {
        this(name, description, assetName, shopCost, sellValue, DEFAULT_MAX_TARGET_CARDS);
    }

    /**
     * Defines display metadata, economy values, and this card's target limit.
     */
    protected TarotCards(String name, String description, String assetName,
            int shopCost, int sellValue, int maxTargetCards) {
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
        if (maxTargetCards < 1) {
            throw new IllegalArgumentException("Tarot target limit must be positive");
        }
        this.baseShopCost = shopCost;
        this.baseSellValue = sellValue;
        this.shopCost = shopCost;
        this.sellValue = sellValue;
        this.maxTargetCards = maxTargetCards;
    }

    /**
     * Applies this Tarot card's effect to the active run.
     * Target validation runs before applyEffect(), so an invalid selection
     * throws without mutating the run or consuming this consumable.
     *
     * @throws InvalidGameActionException when the target selection is invalid
     */
    public final void use(GameState state, List<Card> selectedCards) throws InvalidGameActionException {
        System.out.println("[TAROT] " + name + " validating "
                + (selectedCards == null ? 0 : selectedCards.size()) + " target(s).");
        validateTargets(state, selectedCards);
        applyEffect(state, selectedCards);
        System.out.println("[TAROT] " + name + " resolved successfully.");
    }

    /**
     * Applies this Tarot card's concrete effect after target validation.
     *
     * @throws InvalidGameActionException when a card-specific rule is violated
     */
    protected abstract void applyEffect(GameState state, List<Card> selectedCards)
            throws InvalidGameActionException;

    /**
     * Validates that the targets are non-null, unique, within this card's
     * limit, and currently held in the player's hand.
     */
    private void validateTargets(GameState state, List<Card> selectedCards)
            throws InvalidGameActionException {
        if (state == null) {
            throw reject("Use requires an active game state.");
        }
        if (selectedCards == null || selectedCards.isEmpty()) {
            throw reject("Select at least one target card from your hand.");
        }
        if (selectedCards.size() > maxTargetCards) {
            throw reject("Select at most " + maxTargetCards + " target card(s).");
        }
        Set<Card> uniqueTargets = new HashSet<>();
        for (Card card : selectedCards) {
            if (card == null) {
                throw reject("Null cards cannot be targeted.");
            }
            if (!uniqueTargets.add(card)) {
                throw reject("The same card cannot be selected more than once.");
            }
            if (!state.getHand().contains(card)) {
                throw reject("Target cards must currently be in your hand.");
            }
        }
    }

    /**
     * Logs and builds a rejected-usage exception without mutating state.
     */
    private InvalidGameActionException reject(String reason) {
        System.out.println("[TAROT] " + name + " rejected: " + reason);
        return new InvalidGameActionException(name + ": " + reason);
    }
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
     * Returns the maximum number of hand cards this Tarot can target.
     */
    public int getMaxTargetCards() { return maxTargetCards; }
    /**
     * Performs the ge\1 \2as\1 \2ho\1 \2ost operation.
     */
    protected final int getBaseShopCost() { return baseShopCost; }
    /**
     * Performs the ge\1 \2as\1 \2el\1 \2alue operation.
     */
    protected final int getBaseSellValue() { return baseSellValue; }
}
