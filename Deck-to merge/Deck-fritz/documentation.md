# Deck Project Documentation

## 1. What This Project Is

**Deck** is a Java 21 and JavaFX 21 single-player deckbuilder inspired by
Balatro. The player selects cards, the game identifies the poker hand, the
selected cards are scored, and Jokers modify the result.

The central scoring formula is:

```text
Final Score = Chips x Mult
```

The project is separated into four broad responsibilities:

| Area | Package | Responsibility |
|---|---|---|
| Model | `com.game.deck.model` | Represents cards, suits, ranks, hands, decks, and run state |
| Engine | `com.game.deck.engine` | Evaluates hands and calculates scores |
| Equipment | `com.game.deck.equipment` | Implements Jokers and socketable Items |
| View | `com.game.deck.views` | Displays the game and handles player interaction |

This separation means the scoring rules do not need to know how JavaFX draws a
card, and the JavaFX view does not need to implement poker mathematics.

---

## 2. Important Java Terminology

### Class

A **class** is a blueprint for objects. It defines data and behavior.

Example:

```java
public class Card {
    private final Rank rank;
    private final Suit suit;
}
```

`Card` is a class. A card object created from it might be the seven of
Diamonds.

### Object or Instance

An **object** is a concrete instance of a class.

```java
Card sevenOfDiamonds = new Card(Rank.SEVEN, Suit.DIAMONDS);
```

`sevenOfDiamonds` is an object, or instance, of the `Card` class.

### Field

A **field** is a variable stored inside an object.

```java
private final Rank rank;
private final Suit suit;
```

These fields store the identity of a `Card`.

### Method

A **method** is a named block of behavior belonging to a class. It may read
data, change data, perform a calculation, or return a result.

```java
public void addMult(double amount) {
    mult += amount;
}
```

`addMult` is a method. “Method” is the general term for all of the functions
listed below.

### Constructor

A **constructor** creates and initializes an object. It has the same name as
the class and has no return type.

```java
public GreedyJoker() {
    super("The Greedy", "+3 Mult when all played cards are Diamonds",
          "joker_2", 3, 2);
}
```

The `GreedyJoker` constructor supplies the Joker's display name, description,
asset, shop cost, and sell value.

### Getter or Accessor Method

A **getter**, also called an **accessor**, is a method that returns stored
information without being the main operation that changes it.

Examples:

```java
ctx.getPlayedCards();
ctx.getHandType();
ctx.getMult();
ctx.getChips();
```

The `get` prefix is a common naming convention, but the important idea is that
the method provides controlled access to internal data.

In this project:

- `getPlayedCards()` is a getter/accessor for the selected cards.
- `getHandType()` is a getter/accessor for the classified poker hand.
- `getMult()` and `getChips()` are getters/accessors for score state.

### Setter or Mutator Method

A **setter**, also called a **mutator**, changes stored data.

Examples:

```java
ctx.setMult(10);
ctx.setChips(100);
card.setEnchantment(Enchantment.BONUS);
```

Not every mutator begins with `set`. These are also mutator-style methods:

```java
ctx.addMult(3);
ctx.multiplyChips(2);
ctx.addCash(5);
```

The difference is that `setMult` replaces a value, while `addMult` changes it
relative to its current value.

### Query Method or Inspector Method

A **query method** answers a question about current data without changing it.
An **inspector method** is an informal but useful name for the same concept.

The card-inspection methods added to `ScoringContext` are query/inspector
methods:

```java
ctx.countPlayedCards(Suit.DIAMONDS);
ctx.hasPlayedSuit(Suit.DIAMONDS);
ctx.allPlayedCards(Suit.DIAMONDS);
ctx.countPlayedCards(Rank.ACE);
ctx.hasPlayedRank(Rank.ACE);
ctx.allPlayedCards(Rank.ACE);
```

They inspect the cards associated with the current scoring operation.

### Predicate Method

A **predicate** is a method that returns `true` or `false`. Predicate names
often begin with `is`, `has`, or `all`.

Examples:

```java
ctx.hasPlayedSuit(Suit.DIAMONDS);
ctx.hasPlayedRank(Rank.ACE);
ctx.allPlayedCards(Suit.DIAMONDS);
ctx.isPreview();
```

In particular:

```java
ctx.allPlayedCards(Suit.DIAMONDS)
```

means:

> “Is the current selection non-empty, and does every selected card have the
> Diamonds suit?”

It does **not** mean that the poker hand was classified as a Flush. A hand can
contain all Diamonds without being a Flush if fewer than five cards are
selected.

### Helper Method

A **helper method** hides a repeated or low-level operation behind a readable
name.

For example, `allPlayedCards(Suit.DIAMONDS)` internally relies on
`countPlayedCards(Suit.DIAMONDS)`. The Joker does not need to repeat the loop
that examines every card.

This keeps Joker code focused on game behavior instead of collection logic.

### Parameter and Argument

A **parameter** is the variable declared by a method.

```java
public int countPlayedCards(Suit suit)
```

Here, `suit` is the parameter.

An **argument** is the actual value supplied when calling the method.

```java
ctx.countPlayedCards(Suit.DIAMONDS);
```

`Suit.DIAMONDS` is the argument.

### Return Value

A method's **return value** is the result it gives back to its caller.

```java
int diamondCount = ctx.countPlayedCards(Suit.DIAMONDS);
boolean allDiamonds = ctx.allPlayedCards(Suit.DIAMONDS);
```

`countPlayedCards` returns an `int`; `allPlayedCards` returns a `boolean`.

### `void`

`void` means a method performs an action but does not return a value.

```java
public void addMult(double amount)
```

The method changes the scoring context and returns nothing.

### Enum

An **enum** is a fixed set of named values.

This project uses:

```java
Suit.DIAMONDS
Suit.HEARTS
Suit.CLUBS
Suit.SPADES
```

and:

```java
Rank.TWO
Rank.ACE
```

Enums prevent invalid values such as an unknown suit or rank from being passed
through the scoring system.

### Interface

An **interface** defines a contract that classes can implement. It describes
what behavior is available without prescribing the complete implementation.

The project primarily uses abstract base classes for shared equipment behavior,
but the same contract-oriented idea appears in the occurrence and equipment
design.

### Abstract Class

An **abstract class** is a partially implemented base class that is not meant
to be instantiated directly.

`Joker` is the shared base class for concrete Jokers. It provides common
metadata, pricing, sockets, and trigger behavior. A concrete Joker supplies
its unique effect by overriding `applyBaseEffect`.

### Inheritance

**Inheritance** allows one class to reuse and specialize another class.

```java
public class GreedyJoker extends Joker
```

`GreedyJoker` inherits the common Joker functionality from `Joker`.

### Method Overriding

**Overriding** occurs when a subclass supplies its own implementation of a
method declared by a superclass.

```java
@Override
protected void applyBaseEffect(ScoringContext ctx) {
    if (ctx.allPlayedCards(Suit.DIAMONDS)) {
        ctx.addMult(3.0);
    }
}
```

The `@Override` annotation tells Java and the reader that this method replaces
the inherited implementation for `GreedyJoker`.

### Method Overloading

**Overloading** means using the same method name with different parameter
types or parameter counts.

`ScoringContext` overloads `countPlayedCards`:

```java
countPlayedCards(Suit suit)
countPlayedCards(Rank rank)
```

Java selects the correct version based on the argument type.

---

## 3. The Card-Inspection API

### The Current Played-Card Snapshot

`ScoringContext` receives the cards involved in the current scoring operation.
The list is copied with `List.copyOf`, so the context keeps a stable list
reference for that score calculation.

```java
public List<Card> getPlayedCards()
```

This is a getter/accessor. It allows a future Joker to inspect the cards
directly, although most Jokers should prefer the more readable helper methods.

The list is passed into the context in two places:

1. `BasePokerEngine` passes the normalized selected cards for the UI preview.
2. `ScoringEngine` passes the selected cards for the authoritative score.

This is why the preview and the actual Play action can use the same Joker
condition.

### Counting by Suit

```java
public int countPlayedCards(Suit suit)
```

Returns how many selected cards have the requested suit.

Examples:

```java
ctx.countPlayedCards(Suit.DIAMONDS); // 3
ctx.countPlayedCards(Suit.HEARTS);   // 0
```

### Checking Whether a Suit Exists

```java
public boolean hasPlayedSuit(Suit suit)
```

Returns `true` when at least one selected card has that suit.

Example:

```java
if (ctx.hasPlayedSuit(Suit.DIAMONDS)) {
    ctx.addMult(3);
}
```

This is appropriate for a rule such as:

> “Gain Mult if at least one Diamond was played.”

### Checking Whether Every Card Has a Suit

```java
public boolean allPlayedCards(Suit suit)
```

Returns `true` only when the selection is not empty and every selected card
matches the suit.

Example:

```java
if (ctx.allPlayedCards(Suit.DIAMONDS)) {
    ctx.addMult(3);
}
```

This is the condition currently used by `GreedyJoker`.

### Counting by Rank

```java
public int countPlayedCards(Rank rank)
```

Returns how many selected cards have the requested rank.

Example:

```java
if (ctx.countPlayedCards(Rank.ACE) >= 2) {
    ctx.addMult(5);
}
```

### Checking Whether a Rank Exists

```java
public boolean hasPlayedRank(Rank rank)
```

Returns `true` when at least one selected card has the requested rank.

### Checking Whether Every Card Has a Rank

```java
public boolean allPlayedCards(Rank rank)
```

Returns `true` when every selected card has the requested rank.

Example:

```java
if (ctx.allPlayedCards(Rank.SEVEN)) {
    ctx.addChips(50);
}
```

---

## 4. GreedyJoker Explained

The current Joker is:

```java
public class GreedyJoker extends Joker
```

Its description is:

```text
+3 Mult when all played cards are Diamonds
```

Its behavior is:

```java
@Override
protected void applyBaseEffect(ScoringContext ctx) {
    if (ctx.allPlayedCards(Suit.DIAMONDS)) {
        ctx.addMult(3.0);
    }
}
```

Line-by-line explanation:

1. `@Override` confirms that this is the Joker-specific implementation of the
   shared effect hook.
2. `ScoringContext ctx` is the scoring context for the current selection.
3. `allPlayedCards(Suit.DIAMONDS)` inspects every selected card.
4. The `if` statement activates the effect only when all selected cards are
   Diamonds.
5. `addMult(3.0)` adds three to the current multiplier.

The Joker does not check whether the hand is called `FLUSH`, `PAIR`, or
`STRAIGHT`. It checks the actual card identities independently of poker-hand
classification.

---

## 5. Preview Versus Authoritative Scoring

### Preview

The preview runs when the player changes the current selection. It uses
`BasePokerEngine`.

Purpose:

- Show the predicted hand type.
- Show predicted chips and multiplier.
- Show whether score-based Joker effects will activate.
- Avoid consuming items or changing the player's cash.

### Authoritative Score

The authoritative calculation runs after the player clicks **Play Hand**. It
uses `ScoringEngine`.

Purpose:

- Calculate the real score.
- Apply Joker effects.
- Apply held-card effects.
- Apply socketed item effects.
- Apply permitted run-state changes.

### Why Both Engines Receive the Cards

If only the authoritative engine received the selected cards, Greedy would
activate after Play but the UI preview could not show the bonus beforehand.

If only the preview engine received them, the displayed score could be
different from the score actually awarded.

Passing the same selected-card list into both contexts keeps the preview and
the real calculation consistent.

---

## 6. Scoring Pipeline

`ScoringEngine.calculateScore` processes a played hand in four stages.

### Stage 1: Pre-Scoring

The engine identifies the poker hand and gives Jokers an opportunity to
change base hand values through `modifyHand`.

Examples:

- Change the base Chips of a Flush.
- Change the base Mult of a Pair.

### Stage 2: Played Cards

Each selected card contributes its rank Chips. Card enchantments can also
modify Chips or Mult.

Examples:

- Bonus card: additional Chips.
- Mult card: additional Mult.
- Glass card: multiplies Mult.

### Stage 3: Held Cards

Cards that remain in the player's hand can apply passive effects.

Examples:

- Steel cards multiply Mult.
- Gold cards add cash.

### Stage 4: Jokers and Socketed Items

Each owned Joker triggers in left-to-right order. Its socketed Items may
repeat or modify the Joker effect.

Greedy activates in this stage. The preview calls the non-mutating
`preview` path instead.

Finally:

```text
Final Score = Final Chips x Final Mult
```

---

## 7. Poker-Hand Classification Versus Card Identity

`PokerEvaluator` classifies a list of cards and returns a `PokerHand` enum.

Examples of classifications:

```text
PAIR
TWO_PAIR
FLUSH
STRAIGHT
FULL_HOUSE
```

The classification answers:

> “What poker pattern does this selection form?”

The card-inspection API answers:

> “What actual suits and ranks are present in this selection?”

These are intentionally different concepts.

For example, the following are both Flush hands:

```text
2 Diamonds, 5 Diamonds, 7 Diamonds, 9 Diamonds, Ace Diamonds
2 Hearts,   5 Hearts,   7 Hearts,   9 Hearts,   Ace Hearts
```

`PokerHand.FLUSH` alone cannot tell a Joker whether the Flush is Diamonds or
Hearts. `ScoringContext` preserves the selected cards so a Joker can make
that distinction.

---

## 8. How to Create Another Card-Condition Joker

Create a new class in:

```text
src/com/game/deck/equipment/
```

Example: a Joker that gives Chips when at least three Hearts are selected.

```java
package com.game.deck.equipment;

import com.game.deck.engine.ScoringContext;
import com.game.deck.model.Suit;

public class HeartCollectorJoker extends Joker {
    public HeartCollectorJoker() {
        super(
                "Heart Collector",
                "+20 Chips if at least 3 Hearts are played",
                "joker_1",
                4,
                2
        );
    }

    @Override
    protected void applyBaseEffect(ScoringContext ctx) {
        if (ctx.countPlayedCards(Suit.HEARTS) >= 3) {
            ctx.addChips(20);
        }
    }
}
```

Possible conditions:

```java
ctx.hasPlayedSuit(Suit.CLUBS)
ctx.allPlayedCards(Suit.SPADES)
ctx.countPlayedCards(Suit.HEARTS) >= 3
ctx.hasPlayedRank(Rank.ACE)
ctx.allPlayedCards(Rank.SEVEN)
ctx.countPlayedCards(Rank.QUEEN) == 2
```

To make the Joker appear in the shop, register its constructor in the Joker
factory list:

```java
HeartCollectorJoker::new
```

The shop then creates its display name, description, asset, cost, and sell
value from the Joker object.

---

## 9. Encapsulation and Data Safety

The scoring context uses private fields:

```java
private final List<Card> playedCards;
```

Other classes do not directly manipulate that field. They use public methods
such as `getPlayedCards`, `countPlayedCards`, and `allPlayedCards`.

This is **encapsulation**: the class controls how its internal data is read
and changed.

`List.copyOf(playedCards)` creates an unmodifiable list snapshot. This prevents
the list structure from being changed while a score is being calculated.
Individual `Card` objects still contain mutable gameplay state such as
enchantments and debuffs; the snapshot protects the selected-card collection,
not every property of every Card object.

---

## 10. Presentation-Ready Explanation

The project can be explained in this order:

1. **The model represents the game.**  
   `Card`, `Rank`, `Suit`, `PokerHand`, and `GameState` store the game's data.

2. **The view collects player input.**  
   `GameView` lets the player select cards, preview the score, play the hand,
   or discard it.

3. **The evaluator identifies the poker pattern.**  
   `PokerEvaluator` determines whether the cards form a Pair, Flush, Straight,
   or another `PokerHand`.

4. **The scoring context stores the current calculation.**  
   `ScoringContext` holds Chips, Mult, the classified hand, and the actual
   selected cards.

5. **The scoring engine applies rules in order.**  
   `ScoringEngine` processes base hand values, played cards, held cards, and
   Jokers.

6. **Jokers use polymorphism.**  
   Every Joker shares the same `Joker` base class, but each subclass overrides
   `applyBaseEffect` with its own rule.

7. **Greedy demonstrates data-driven card inspection.**  
   It does not care whether the hand is a Flush or Pair. It directly asks the
   context whether all selected cards have the Diamonds suit.

8. **Preview and real scoring share the same condition.**  
   Both engines receive the selected cards, so the preview reflects what will
   happen when the player clicks Play.

---

## 11. Useful Files

| File | Explanation |
|---|---|
| `src/com/game/deck/model/Card.java` | Defines one playable card |
| `src/com/game/deck/model/Rank.java` | Defines ranks and rank chip values |
| `src/com/game/deck/model/Suit.java` | Defines the four suits |
| `src/com/game/deck/model/PokerHand.java` | Defines poker-hand classifications and base values |
| `src/com/game/deck/engine/PokerEvaluator.java` | Classifies selected cards |
| `src/com/game/deck/engine/ScoringContext.java` | Stores score state and card-inspection helpers |
| `src/com/game/deck/engine/BasePokerEngine.java` | Calculates non-mutating UI previews |
| `src/com/game/deck/engine/ScoringEngine.java` | Performs authoritative scoring |
| `src/com/game/deck/equipment/Joker.java` | Shared Joker base class and trigger contract |
| `src/com/game/deck/equipment/GreedyJoker.java` | Diamond-only Joker example |
| `src/com/game/deck/views/GameView.java` | Handles selection, preview, Play, and Discard actions |

