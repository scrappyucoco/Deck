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