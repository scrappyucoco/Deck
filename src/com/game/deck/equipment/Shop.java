package com.game.deck.equipment;

import com.game.deck.model.GameState;

/*
 * Shop is the economy service for purchasing and pricing equipment.
 * It belongs to the Equipment layer, validates GameState shop access/cash,
 * and registers concrete Jokers without depending on JavaFX views.
 */
public final class Shop {
    // [CONFIG: TWEAK SCORE VALUES HERE]
    // These constants configure shop economy; purchase/sell methods use them.
    public static final int RATIONALIST_JOKER_COST = 4;
    public static final int JOKER_SELL_VALUE = 3;

    private Shop() {
    }

    public static boolean purchaseRationalist(GameState state) {
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Add another purchase method that constructs new MyJoker() and passes
        // it to state.addJoker() after applying the same access/cash checks.
        if (state == null || !state.isShopAccessible()) {
            System.out.println("[SHOP] Purchase rejected: shop is unavailable.");
            return false;
        }
        if (state.getOwnedJokers().size() >= state.getMaxJokers()) {
            System.out.println("[SHOP] Purchase rejected: joker slots are full.");
            return false;
        }
        if (!state.deductCash(RATIONALIST_JOKER_COST)) {
            System.out.println("[SHOP] Purchase rejected: insufficient cash.");
            return false;
        }

        state.addJoker(new RationalistJoker());
        System.out.println("[SHOP] Purchased The Rationalist for $" + RATIONALIST_JOKER_COST + ".");
        return true;
    }
}
