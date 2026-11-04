package com.game.deck.model;

public class GameState {
    private int ante;
    private int cash;
    private int handsRemaining;
    private int discardsRemaining;
    private long currentBlindTarget;
    private boolean inSpecialChallenge;

    public GameState() {
        this.ante = 1;
        this.cash = 4;
        this.handsRemaining = 4;
        this.discardsRemaining = 3;
        this.currentBlindTarget = 300;
        this.inSpecialChallenge = false;
    }

    public void addCash(int amount) {
        this.cash += amount;
        System.out.println("[STATE] Cash updated: +$" + amount + " | Current Balance: $" + this.cash);
    }

    public boolean deductCash(int amount) {
        if (this.cash >= amount) {
            this.cash -= amount;
            System.out.println("[STATE] Cash deducted: -$" + amount + " | Remaining Balance: $" + this.cash);
            return true;
        }
        System.out.println("[STATE] Insufficient funds! Needed $" + amount + " but only have $" + this.cash);
        return false;
    }

    public int getAnte() { return ante; }
    public void setAnte(int ante) { this.ante = ante; }
    public int getCash() { return cash; }
    public int getHandsRemaining() { return handsRemaining; }
    public void useHand() { this.handsRemaining--; }
    public void resetHands() { this.handsRemaining = 4; }
    public int getDiscardsRemaining() { return discardsRemaining; }
    public void useDiscard() { this.discardsRemaining--; }
    public void resetDiscards() { this.discardsRemaining = 3; }
    public void addExtraDiscard(int count) { this.discardsRemaining += count; }
    public long getCurrentBlindTarget() { return currentBlindTarget; }
    public void setCurrentBlindTarget(long target) { this.currentBlindTarget = target; }
    public boolean isInSpecialChallenge() { return inSpecialChallenge; }
    public void setInSpecialChallenge(boolean active) { this.inSpecialChallenge = active; }
}