package com.game.deck.engine;

public class ScoringContext {
    private long chips;
    private double mult;

    public ScoringContext(long baseChips, double baseMult) {
        this.chips = baseChips;
        this.mult = baseMult;
    }

    public void addChips(long amount) {
        long previous = chips;
        chips += amount;
        System.out.printf("[SCORING] Chips %d -> %d (%+d)%n", previous, chips, amount);
    }

    public void addMult(double amount) {
        double previous = mult;
        mult += amount;
        System.out.printf("[SCORING] Mult %.2f -> %.2f (%+.2f)%n", previous, mult, amount);
    }

    public void multiplyMult(double factor) {
        double previous = mult;
        mult *= factor;
        System.out.printf("[SCORING] Mult %.2f -> %.2f (x%.2f)%n", previous, mult, factor);
    }

    public void multiplyChips(double factor) {
        long previous = chips;
        chips = (long) (chips * factor);
        System.out.printf("[SCORING] Chips %d -> %d (x%.2f)%n", previous, chips, factor);
    }

    public long calculateTotal() {
        long total = Math.max(0, (long) (chips * mult));
        System.out.printf("[SCORING] Total = %d chips x %.2f mult = %d%n", chips, mult, total);
        return total;
    }

    public long getChips() { return chips; }
    public double getMult() { return mult; }
}