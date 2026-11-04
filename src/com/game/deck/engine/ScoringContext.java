package com.game.deck.engine;

public class ScoringContext {
    private long chips;
    private double mult;

    public ScoringContext(long baseChips, double baseMult) {
        this.chips = baseChips;
        this.mult = baseMult;
    }

    public void addChips(long amount) { this.chips += amount; }
    public void addMult(double amount) { this.mult += amount; }
    public void multiplyMult(double factor) { this.mult *= factor; }
    public void multiplyChips(double factor) { this.chips = (long)(this.chips * factor); }

    public long calculateTotal() { return Math.max(0, (long)(this.chips * this.mult)); }

    public long getChips() { return chips; }
    public double getMult() { return mult; }
}