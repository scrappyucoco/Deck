/**
 * Declares the Deck application module and the JavaFX/JDK modules it requires.
 * The exported packages form the public boundaries between the model, engine,
 * equipment, occurrence, and view layers.
 */
module com.game.deck {
    requires java.base;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;

    exports com.game.deck;
    exports com.game.deck.model;
    exports com.game.deck.engine;
    exports com.game.deck.equipment;
    exports com.game.deck.occurrences;
    exports com.game.deck.views;
}
