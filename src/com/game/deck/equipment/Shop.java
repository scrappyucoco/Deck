package com.game.deck.equipment;

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

    public static boolean purchaseJoker(GameState state, Joker joker) {
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Pass any registered Joker instance; getShopCost() supplies its price.
        if (state == null || !state.isShopAccessible()) {
            System.out.println("[SHOP] Purchase rejected: shop is unavailable.");
            return false;
        }
        if (joker == null) {
            System.out.println("[SHOP] Purchase rejected: invalid Joker.");
            return false;
        }
        int cost = joker.getShopCost();
        if (state.getOwnedJokers().size() >= state.getMaxJokers()) {
            System.out.println("[SHOP] Purchase rejected: joker slots are full.");
            return false;
        }
        if (!state.deductCash(cost)) {
            System.out.println("[SHOP] Purchase rejected: insufficient cash.");
            return false;
        }

        if (!state.addJoker(joker)) {
            state.addCash(cost);
            System.out.println("[SHOP] Purchase rejected: Joker could not be added.");
            return false;
        }
        System.out.println("[SHOP] Purchased " + joker.getName() + " for $" + cost + ".");
        return true;
    }
}
