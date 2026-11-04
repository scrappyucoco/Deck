package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if (sockets.size() < MAX_SOCKETS) {
            sockets.add(item);
            System.out.println("[EQUIPMENT] Socketed " + item.getName() + " into " + this.name + " [Slot " + sockets.size() + "/2]");
            return true;
        }
        System.out.println("[EQUIPMENT] Sockets full for " + this.name);
        return false;
    }

    public void evaluate(ScoringContext ctx) {
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

    protected abstract void applyBaseEffect(ScoringContext ctx);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Item> getSockets() { return Collections.unmodifiableList(sockets); }
    public String getAssetPath() { return assetPath; }
}