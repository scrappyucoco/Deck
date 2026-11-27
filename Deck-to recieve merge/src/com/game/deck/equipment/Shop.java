package com.game.deck.equipment;

/**
 * Contains the Shop source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.exception.InvalidGameActionException;
import com.game.deck.model.GameState;

/*
 * Shop is the economy service for purchasing and pricing equipment.
 * It belongs to the Equipment layer and validates GameState shop access/cash.
 * Each Joker supplies its own purchase and resale values; this class does not
 * maintain a separate price table.
 */
public final class Shop {
    private Shop() {
    }

    /**
     * Performs the purchas\1 \2oker operation.
     */
    public static boolean purchaseJoker(GameState state, Joker joker)
            throws InvalidGameActionException {
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Pass any registered Joker instance; getShopCost() supplies its price.
        if (state == null) {
            throw new InvalidGameActionException("Cannot buy a Joker without an active game state.");
        }
        if (!state.isShopAccessible()) {
            throw new InvalidGameActionException("The shop is only available between rounds.");
        }
        if (joker == null) {
            throw new InvalidGameActionException("Cannot buy a null Joker.");
        }
        int cost = joker.getShopCost();
        if (state.getOwnedJokers().size() >= state.getMaxJokers()) {
            throw new InvalidGameActionException(
                    "Cannot buy " + joker.getName() + ": Joker slots are full ("
                            + state.getMaxJokers() + " maximum).");
        }
        state.spendCash(cost);

        if (!state.addJoker(joker)) {
            state.addCash(cost);
            throw new InvalidGameActionException(
                    "Could not add " + joker.getName() + " to the Joker slots; purchase refunded.");
        }
        System.out.println("[SHOP] Purchased " + joker.getName() + " for $" + cost + ".");
        return true;
    }

    /**
     * Performs the purchas\1 \2aro\1 \2ard operation.
     */
    public static boolean purchaseTarotCard(GameState state, TarotCards tarotCard)
            throws InvalidGameActionException {
        if (state == null) {
            throw new InvalidGameActionException(
                    "Cannot buy a Tarot card without an active game state.");
        }
        if (!state.isShopAccessible()) {
            throw new InvalidGameActionException("The shop is only available between rounds.");
        }
        if (tarotCard == null) {
            throw new InvalidGameActionException("Cannot buy a null Tarot card.");
        }
        if (state.getOwnedTarotCards().size() >= state.getMaxTarotCards()) {
            throw new InvalidGameActionException(
                    "Cannot buy " + tarotCard.getName() + ": consumable slots are full ("
                            + state.getMaxTarotCards() + " maximum).");
        }

        int cost = tarotCard.getShopCost();
        state.spendCash(cost);
        if (!state.addTarotCard(tarotCard)) {
            state.addCash(cost);
            throw new InvalidGameActionException(
                    "Could not add " + tarotCard.getName() + " to the consumable slots; purchase refunded.");
        }

        System.out.println("[SHOP] Purchased " + tarotCard.getName() + " for $" + cost + ".");
        return true;
    }
}
