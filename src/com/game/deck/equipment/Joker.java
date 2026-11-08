package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Joker is the abstract Equipment/Engine contract for all active Joker cards.
 * GameState stores concrete instances, GameView renders them, and
 * ScoringEngine invokes evaluate() in their current left-to-right order.
 */
public abstract class Joker {
    private final String name;
    private final String description;
    private final List<Item> sockets;
    private static final int MAX_SOCKETS = 2;
    private final String assetPath;

    public Joker(String name, String description, String assetName) {
        this.name = name;
        this.description = description;
        this.sockets = new ArrayList<>();
        this.assetPath = "assets/jokers/" + assetName + ".png";
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
        // Preview only the base effect; consumable sockets must remain untouched.
        applyBaseEffect(ctx);
    }

    protected abstract void applyBaseEffect(ScoringContext ctx);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Item> getSockets() { return Collections.unmodifiableList(sockets); }
    public String getAssetPath() { return assetPath; }
}