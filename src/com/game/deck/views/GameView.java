package com.game.deck.views;

import com.game.deck.equipment.Joker;
import com.game.deck.equipment.RationalistJoker;
import com.game.deck.equipment.Shop;
import com.game.deck.engine.BasePokerEngine;
import com.game.deck.engine.ScoringEngine;
import com.game.deck.model.Card;
import com.game.deck.model.GameState;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*
 * GameView is the primary JavaFX View/controller surface for one run.
 * It binds GameState model data to CardNode/JokerNode controls, delegates
 * scoring to engine.ScoringEngine, and delegates purchases to equipment.Shop.
 */
public class GameView extends BorderPane {
    private final GameState gameState;
    private final List<CardNode> activeCardNodes = new ArrayList<>();
    private boolean firstRoundStarted;
    private boolean shopOpen = true;

    private final Label scoreLabel = createHeaderLabel("");
    private final Label targetLabel = createHeaderLabel("");
    private final Label cashLabel = createHeaderLabel("");
    private final Label handsLabel = createStatLabel("");
    private final Label discardsLabel = createStatLabel("");
    private final Label anteLabel = createHeaderLabel("");
    private final Label handTypeLabel = new Label();
    private final Label handScorePreviewLabel = new Label();
    private final Label shopMessageLabel = new Label();
    private final HBox handContainer = new HBox(10);
    private final HBox ownedJokersContainer = new HBox(15);
    private final VBox shopPanel = new VBox(8);
    private final Button shopButton = new Button("Shop");
    private final Button startRoundButton = new Button("Start Round");
    private final Button buyJokerButton = new Button();
    private final Button playButton = new Button("Play Hand");
    private final Button discardButton = new Button("Discard");
    private final Button sortRankButton = new Button("Sort Rank");
    private final Button sortSuitButton = new Button("Sort Suit");

    public GameView(GameState gameState) {
        // --- VIEW INITIALIZATION ---
        this.gameState = Objects.requireNonNull(gameState, "gameState");
        getStyleClass().add("poker-table");
        setPadding(new Insets(15));

        buildTopBar();
        buildCenterArea();
        buildBottomArea();
        refreshUI();
    }

    private void buildTopBar() {
        // --- STATUS BAR ---
        shopButton.getStyleClass().add("btn-secondary");
        shopButton.setOnAction(event -> toggleShop());

        HBox scoreBox = new HBox(18, anteLabel, targetLabel, scoreLabel, cashLabel, shopButton);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.getStyleClass().add("status-bar");
        scoreBox.setPadding(new Insets(10));
        setTop(scoreBox);
    }

    private void buildCenterArea() {
        // --- CENTER PLAY AREA ---
        Label jokerTitle = new Label("Jokers (drag to reorder)");
        jokerTitle.getStyleClass().add("section-title");
        ownedJokersContainer.setAlignment(Pos.CENTER);
        ownedJokersContainer.setMinHeight(135);

        handTypeLabel.getStyleClass().add("hand-type-label");
        handScorePreviewLabel.getStyleClass().add("hand-score-label");
        VBox preview = new VBox(6, handTypeLabel, handScorePreviewLabel);
        preview.setAlignment(Pos.CENTER);

        handContainer.setAlignment(Pos.CENTER);
        handContainer.setMinHeight(160);

        shopPanel.getStyleClass().add("shop-panel");
        buildShopPanel();

        VBox center = new VBox(10, jokerTitle, ownedJokersContainer, preview, handContainer, shopPanel);
        center.setAlignment(Pos.CENTER);
        setCenter(center);
    }

    private void buildShopPanel() {
        // --- SHOP OFFER REGISTRATION ---
        // [CUSTOMIZATION: ADD NEW JOKERS HERE]
        // Add another JokerNode(new MyJoker()) and a matching purchase handler
        // to expose a new concrete Joker in the shop UI.
        Label title = new Label("SHOP");
        title.getStyleClass().add("shop-title");
        shopMessageLabel.getStyleClass().add("shop-message");

        JokerNode offer = new JokerNode(new RationalistJoker());
        buyJokerButton.getStyleClass().add("btn-primary");
        buyJokerButton.setOnAction(event -> buyRationalist());

        HBox offerRow = new HBox(12, offer, buyJokerButton);
        offerRow.setAlignment(Pos.CENTER);
        shopPanel.getChildren().addAll(title, shopMessageLabel, offerRow);
    }

    private void buildBottomArea() {
        // --- ACTION BAR ---
        HBox stats = new HBox(20, handsLabel, discardsLabel);
        stats.setAlignment(Pos.CENTER);

        playButton.getStyleClass().add("btn-primary");
        discardButton.getStyleClass().add("btn-secondary");
        sortRankButton.getStyleClass().add("btn-secondary");
        sortSuitButton.getStyleClass().add("btn-secondary");
        playButton.setOnAction(event -> handlePlayHand());
        discardButton.setOnAction(event -> handleDiscard());

        sortRankButton.setOnAction(event -> {
            System.out.println("[UI] Sort Rank clicked.");
            Collections.sort(gameState.getHand());
            renderHand();
        });
        sortSuitButton.setOnAction(event -> {
            System.out.println("[UI] Sort Suit clicked.");
            gameState.getHand().sort((first, second) -> {
                int suitComparison = first.getSuit().compareTo(second.getSuit());
                return suitComparison != 0 ? suitComparison : first.compareTo(second);
            });
            renderHand();
        });

        startRoundButton.getStyleClass().add("btn-primary");
        startRoundButton.setOnAction(event -> startRound());

        HBox actions = new HBox(15, startRoundButton, playButton, discardButton, sortRankButton, sortSuitButton);
        actions.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(12, stats, actions);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10));
        setBottom(bottom);
    }

    private void handleCardSelection(CardNode node) {
        // Enforce the poker hand maximum before toggling a new selection on.
        if (!node.isSelected() && getSelectedCards().size() >= 5) {
            System.out.println("[UI] Selection rejected: maximum of 5 cards is already selected.");
            return;
        }

        node.toggleSelection();
        List<Card> selected = getSelectedCards();
        System.out.println("[UI] Card selection changed: selected=" + selected.size() + "/5");
        updateHandPreview();
    }

    private void handlePlayHand() {
        // --- PLAY HAND EVENT ---
        List<Card> selected = getSelectedCards();
        if (!gameState.isRoundActive() || selected.isEmpty() || selected.size() > 5) {
            System.out.println("[UI] Play rejected: select between 1 and 5 cards during a round.");
            return;
        }

        List<Card> held = new ArrayList<>(gameState.getHand());
        held.removeAll(selected);
        System.out.println("[UI] Play clicked: selected=" + selected.size() + ", held=" + held.size());
        long score = ScoringEngine.calculateScore(
                selected,
                held,
                gameState.getOwnedJokers(),
                gameState);
        gameState.addScore(score);
        gameState.playSelectedCards(selected);
        gameState.refillHand();
        System.out.println("[UI] Score applied: +" + score + " | total=" + gameState.getCurrentScore());

        if (gameState.isRoundWon() || gameState.isGameOver()) {
            gameState.endRound();
            // [HOOK: ADD CUSTOM OCCURRENCES/EVENTS]
            // Invoke new Occurrence implementations here after endRound().
        }
        refreshUI();
    }

    private void handleDiscard() {
        List<Card> selected = getSelectedCards();
        if (!gameState.isRoundActive() || selected.isEmpty() || selected.size() > 5) {
            System.out.println("[UI] Discard rejected: select between 1 and 5 cards during a round.");
            return;
        }
        System.out.println("[UI] Discard clicked: selected=" + selected.size());
        gameState.discardSelectedCards(selected);
        refreshUI();
    }

    private void updateHandPreview() {
        // Preview uses BasePokerEngine so UI inspection never consumes sockets.
        List<Card> selected = getSelectedCards();
        if (selected.isEmpty()) {
            handTypeLabel.setText(gameState.isRoundActive() ? "Select 1 to 5 cards" : "Start a round to play");
            handScorePreviewLabel.setText("[ 0 Chips  x  0 Mult = 0 ]");
            System.out.println("[PREVIEW] selected=0 | chips=0 | mult=0.0 | score=0");
            return;
        }
        if (selected.size() > 5) {
            handTypeLabel.setText("Too many cards selected! (Max 5)");
            handScorePreviewLabel.setText("[ - ]");
            return;
        }

        BasePokerEngine.ScoreResult result =
                BasePokerEngine.evaluateAndScore(selected, gameState.getOwnedJokers());
        handTypeLabel.setText(result.handType.getDisplayName());
        handScorePreviewLabel.setText(String.format(
                "[ %d Chips  x  %.1f Mult = %d ]",
                result.totalChips, result.mult, result.finalScore));
        System.out.printf("[PREVIEW] selected=%d | hand=%s | baseChips=%d | cardChips=%d"
                        + " | totalChips=%d | mult=%.2f | score=%d%n",
                selected.size(), result.handType.getDisplayName(), result.baseChips,
                result.cardChips, result.totalChips, result.mult, result.finalScore);
    }

    public void refreshUI() {
        // --- VIEW STATE SYNCHRONIZATION ---
        anteLabel.setText("Ante: " + gameState.getAnte());
        targetLabel.setText("Target: " + gameState.getTargetScore());
        scoreLabel.setText("Score: " + gameState.getCurrentScore());
        cashLabel.setText("Cash: $" + gameState.getCash());
        handsLabel.setText("Hands: " + gameState.getHandsRemaining());
        discardsLabel.setText("Discards: " + gameState.getDiscardsRemaining());
        renderHand();
        renderJokers();
        updateHandPreview();
        updateShop();
        updateRoundState();
    }

    private void renderHand() {
        // Recreate nodes so the rendered order mirrors the model hand order.
        handContainer.getChildren().clear();
        activeCardNodes.clear();
        for (Card card : gameState.getHand()) {
            CardNode node = new CardNode(card);
            node.setOnMouseClicked(event -> handleCardSelection(node));
            activeCardNodes.add(node);
            handContainer.getChildren().add(node);
        }
    }

    private void renderJokers() {
        // --- OWNED JOKER SLOT RENDERING ---
        ownedJokersContainer.getChildren().clear();
        if (gameState.getOwnedJokers().isEmpty()) {
            Label empty = new Label("No active jokers. Buy one in the shop.");
            empty.getStyleClass().add("empty-jokers");
            ownedJokersContainer.getChildren().add(empty);
            return;
        }

        for (Joker joker : gameState.getOwnedJokers()) {
            JokerNode node = new JokerNode(joker,
                    () -> sellJoker(joker));
            node.setOnDragOver(event -> {
                if (event.getGestureSource() instanceof JokerNode source && source != node) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });
            node.setOnDragDropped(event -> {
                if (event.getGestureSource() instanceof JokerNode source && source != node) {
                    int targetIndex = ownedJokersContainer.getChildren().indexOf(node);
                    gameState.moveJoker(source.getJoker(), targetIndex);
                    System.out.println("[UI] Joker dragged to slot " + (targetIndex + 1));
                    renderJokers();
                    event.setDropCompleted(true);
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            });
            ownedJokersContainer.getChildren().add(node);
        }
    }

    private List<Card> getSelectedCards() {
        List<Card> selected = new ArrayList<>();
        for (CardNode node : activeCardNodes) {
            if (node.isSelected()) {
                selected.add(node.getCard());
            }
        }
        return selected;
    }

    private void startRound() {
        // --- ROUND LIFECYCLE EVENTS ---
        if (gameState.isRoundActive()) {
            return;
        }
        if (!firstRoundStarted) {
            gameState.startRound(300, 4, 3);
            firstRoundStarted = true;
        } else {
            gameState.startNextRound();
        }
        shopOpen = false;
        System.out.println("[UI] Start Round clicked.");
        refreshUI();
    }

    private void toggleShop() {
        if (!gameState.isShopAccessible()) {
            System.out.println("[UI] Shop rejected: shop is only available before or after a round.");
            return;
        }
        shopOpen = !shopOpen;
        System.out.println("[UI] Shop toggled: open=" + shopOpen);
        updateShop();
    }

    private void updateShop() {
        // Shop visibility is gated by model round state, not just a UI toggle.
        boolean accessible = gameState.isShopAccessible();
        shopPanel.setVisible(accessible && shopOpen);
        shopPanel.setManaged(accessible && shopOpen);
        shopButton.setDisable(!accessible);
        buyJokerButton.setText("Buy The Rationalist ($" + Shop.RATIONALIST_JOKER_COST + ")");
        buyJokerButton.setDisable(
                !accessible
                        || gameState.getCash() < Shop.RATIONALIST_JOKER_COST
                        || gameState.getOwnedJokers().size() >= gameState.getMaxJokers());
        shopMessageLabel.setText("The Rationalist: +4 Mult per played hand | "
                + gameState.getOwnedJokers().size() + "/" + gameState.getMaxJokers() + " slots used");
    }

    private void buyRationalist() {
        System.out.println("[UI] Buy The Rationalist clicked.");
        if (Shop.purchaseRationalist(gameState)) {
            refreshUI();
        }
    }

    private void sellJoker(Joker joker) {
        if (gameState.sellJoker(joker, Shop.JOKER_SELL_VALUE)) {
            refreshUI();
        }
    }

    private void updateRoundState() {
        boolean active = gameState.isRoundActive();
        playButton.setDisable(!active || gameState.getHandsRemaining() <= 0);
        discardButton.setDisable(!active || gameState.getDiscardsRemaining() <= 0);
        startRoundButton.setDisable(active);
        startRoundButton.setText(firstRoundStarted ? "Start Next Round" : "Start Round");

        if (!active) {
            if (gameState.isRoundWon()) {
                handTypeLabel.setText("ROUND CLEARED - SHOP OPEN");
            } else if (firstRoundStarted) {
                handTypeLabel.setText("ROUND OVER - SHOP OPEN");
            } else {
                handTypeLabel.setText("SHOP OPEN BEFORE ROUND");
            }
        }
    }

    private static Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("header-stat");
        return label;
    }

    private static Label createStatLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("stat-label");
        return label;
    }
}
