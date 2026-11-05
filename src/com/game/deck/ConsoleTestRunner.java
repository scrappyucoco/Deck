package com.game.deck;

import com.game.deck.engine.ScoringEngine;
import com.game.deck.equipment.*;

import com.game.deck.model.*;
import com.game.deck.occurrences.MonologueTrial;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTestRunner {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("      PHILOSOPHICAL DECKBUILDER ENGINE TEST HARNESS         ");
        System.out.println("============================================================\n");

        // 1. Initialize Game State & Deck
        GameState state = new GameState();
        Deck deck = new Deck();
        System.out.println("[DECK LOG] Standard 52-card deck instantiated & shuffled.");

        // 2. Draw Cards
        List<Card> drawnHand = deck.draw(8);
        System.out.println("[DECK LOG] Drawn Hand (8 Cards): " + drawnHand);

        // 3. Select 5 Cards to Form a Flush
        List<Card> playedHand = new ArrayList<>();
        playedHand.add(new Card(Rank.ACE, Suit.SPADES, Enchantment.BONUS));
        playedHand.add(new Card(Rank.KING, Suit.SPADES));
        playedHand.add(new Card(Rank.QUEEN, Suit.SPADES));
        playedHand.add(new Card(Rank.JACK, Suit.SPADES));
        playedHand.add(new Card(Rank.TEN, Suit.SPADES, Enchantment.GLASS));

        List<Card> heldInHand = new ArrayList<>();
        heldInHand.add(new Card(Rank.EIGHT, Suit.HEARTS, Enchantment.STEEL));
        heldInHand.add(new Card(Rank.SEVEN, Suit.DIAMONDS, Enchantment.GOLD));

        // 4. Create Jokers and Socket Items
        List<Joker> activeJokers = new ArrayList<>();

        Joker flatJoker = new Joker("Solar Rune", "+4 Flat Mult", "joker_flat") {
            @Override
            protected void applyBaseEffect(com.game.deck.engine.ScoringContext ctx) {
                ctx.addMult(4.0);
            }
        };
        flatJoker.socketItem(new PolishedGem()); // Socket Slot 1
        flatJoker.socketItem(new ShatteredTablet()); // Socket Slot 2 (Consumable)

        Joker xJoker = new Joker("Void Prism", "x1.5 Mult", "joker_xmult") {
            @Override
            protected void applyBaseEffect(com.game.deck.engine.ScoringContext ctx) {
                ctx.multiplyMult(1.5);
            }
        };
        xJoker.socketItem(new EgoRelic()); // Socket Slot 1

        activeJokers.add(flatJoker);
        activeJokers.add(xJoker);

        // 5. Execute Scoring Pipeline
        long score = ScoringEngine.calculateScore(playedHand, heldInHand, activeJokers, state);

        if (score >= state.getCurrentBlindTarget()) {
            System.out.println("[RESULT] SUCCESS! Score " + score + " exceeded Target " + state.getCurrentBlindTarget());
        } else {
            System.out.println("[RESULT] DEFEAT! Score " + score + " fell short of Target " + state.getCurrentBlindTarget());
        }

        // 6. Test Post-Round Monologue Trial
        List<String> fragments = List.of("I adapt my hand", "through calculated discards", "I accept defeat");
        MonologueTrial trial = new MonologueTrial(
                "When probability fails, how do you respond?",
                fragments,
                "I adapt my hand through calculated discards",
                true
        );
        trial.execute(state);
        trial.submitSentence(List.of("I adapt my hand", "through calculated discards"), state);

        System.out.println("\n[ENGINE TEST COMPLETE] Ready for JavaFX Frontend Integration!");
    }
}