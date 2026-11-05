package com.game.deck.equipment;

import com.game.deck.model.GameState;

public final class Shop {
    public static final int RATIONALIST_JOKER_COST = 4;
    public static final int JOKER_SELL_VALUE = 3;

    private Shop() {
    }

    public static boolean purchaseRationalist(GameState state) {
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
