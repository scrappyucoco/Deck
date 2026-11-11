package com.game.deck.views;

/**
 * Contains the GameView source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.equipment.Joker;
import com.game.deck.equipment.Shop;
import com.game.deck.equipment.TarotCards;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 * GameView is the primary JavaFX View/controller surface for one run.
 * It binds GameState model data to CardNode/JokerNode controls, delegates
 * scoring to engine.ScoringEngine, and delegates purchases to equipment.Shop.
 */
public class GameView extends BorderPane {
    private static final int MAX_SHOP_OFFERS = 3;

    private final GameState gameState;
    private final List<CardNode> activeCardNodes = new ArrayList<>();
    private final List<Joker> shopOffers = new ArrayList<>();
    private final List<TarotCards> tarotShopOffers = new ArrayList<>();
    private final Map<Joker, Button> shopBuyButtons = new LinkedHashMap<>();
    private final Map<TarotCards, Button> tarotBuyButtons = new LinkedHashMap<>();
    private boolean firstRoundStarted;
    private boolean shopOpen = true;
    private StackPane centerLayer;

    private final Label scoreLabel = createHeaderLabel("");
    private final Label targetLabel = createHeaderLabel("");
    private final Label cashLabel = createHeaderLabel("");
    private final Label handsLabel = createStatLabel("");
    private final Label discardsLabel = createStatLabel("");
    private final Label anteLabel = createHeaderLabel("");
    private final Label handTypeLabel = new Label();
    private final Label handScorePreviewLabel = new Label();
    private final Label shopMessageLabel = new Label();
    private final Label tarotShopMessageLabel = new Label();
    private final HBox handContainer = new HBox(10);
    private final HBox ownedJokersContainer = new HBox(15);
    private final HBox ownedTarotCardsContainer = new HBox(12);
    private final HBox shopOfferContainer = new HBox(12);
    private final HBox tarotShopOfferContainer = new HBox(12);
    private final VBox shopPanel = new VBox(8);
    private final Button shopButton = new Button("Shop");
    private final Button startRoundButton = new Button("Start Round");
    private final Button playButton = new Button("Play Hand");
    private final Button discardButton = new Button("Discard");
    private final Button sortRankButton = new Button("Sort Rank");
    private final Button sortSuitButton = new Button("Sort Suit");

    /**
     * Performs the gam\1 \2iew operation.
     */
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
        // --- SCOREBOARD SIDEBAR ---
        shopButton.getStyleClass().add("scoreboard-shop-button");
        shopButton.setOnAction(event -> toggleShop());


        VBox anteBox = new VBox(4, anteLabel);
        anteBox.getStyleClass().add("scoreboard-card");

        VBox targetBox = createScoreCard("Target Score", targetLabel, "assets/chips/Blue_Side.png",
                "score-target");
        VBox currentBox = createScoreCard("Current Score", scoreLabel, "assets/chips/Green.png",
                "score-current");

        Label cashCaption = new Label("Economy");
        cashCaption.getStyleClass().add("scoreboard-caption");
        HBox cashRow = new HBox(40, cashLabel, shopButton);
        cashRow.setAlignment(Pos.CENTER_LEFT);
        VBox cashBox = new VBox(cashCaption, cashRow);
        cashBox.getStyleClass().addAll("scoreboard-card", "cash-card");

        Label roundCaption = new Label("ROUND");
        roundCaption.getStyleClass().add("scoreboard-caption");
        HBox resources = new HBox(8, createResourceBadge(handsLabel, "Hands"),
                createResourceBadge(discardsLabel, "Discards"));
        VBox resourceBox = new VBox(6, roundCaption, resources);
        resourceBox.getStyleClass().add("scoreboard-card");

        VBox previewCard = new VBox(5, handTypeLabel, handScorePreviewLabel);
        previewCard.getStyleClass().add("score-preview-card");
        handTypeLabel.getStyleClass().add("hand-type-label");
        handScorePreviewLabel.getStyleClass().add("hand-score-label");

        startRoundButton.getStyleClass().add("btn-primary");
        startRoundButton.setOnAction(event -> startRound());
        VBox scoreboard = new VBox(15, anteBox, targetBox, currentBox, previewCard,
                resourceBox, cashBox, startRoundButton);
        scoreboard.setAlignment(Pos.TOP_CENTER);
        scoreboard.getStyleClass().add("scoreboard-panel");
        setLeft(scoreboard);
    }

    private VBox createScoreCard(String caption, Label value, String iconPath, String valueClass) {
        Label title = new Label(caption);
        title.getStyleClass().add("scoreboard-caption");
        Image iconImage = loadScoreIcon(iconPath);
        ImageView icon = new ImageView(iconImage);
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        value.getStyleClass().add(valueClass);
        HBox row = new HBox(8, icon, value);
        row.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(4, title, row);
        card.getStyleClass().add("scoreboard-card");
        return card;
    }

    private Image loadScoreIcon(String path) {
        var url = getClass().getResource("/" + path);
        if (url == null) {
            throw new IllegalStateException("Missing scoreboard asset: " + path);
        }
        return new Image(url.toExternalForm());
    }

    private VBox createResourceBadge(Label value, String caption) {
        Label title = new Label(caption);
        title.getStyleClass().add("resource-caption");
        value.getStyleClass().add("resource-value");
        VBox badge = new VBox(3, title, value);
        badge.getStyleClass().add("resource-badge");
        return badge;
    }

    private void buildCenterArea() {
        // --- CENTER PLAY AREA ---
        Label jokerTitle = new Label("Jokers");
        jokerTitle.getStyleClass().add("section-title");
        ownedJokersContainer.setAlignment(Pos.CENTER);
        configureEquipmentSlot(ownedJokersContainer, 3);

        Label tarotTitle = new Label("Tarots");
        tarotTitle.getStyleClass().add("section-title");
        ownedTarotCardsContainer.setAlignment(Pos.CENTER);
        configureEquipmentSlot(ownedTarotCardsContainer, 2);

        handContainer.setAlignment(Pos.CENTER);
        handContainer.setSpacing(-15);
        handContainer.setMinHeight(CardNode.CARD_HEIGHT + 20);

        shopPanel.getStyleClass().add("shop-panel");
        buildShopPanel();

        VBox jokerColumn = new VBox(8, jokerTitle, ownedJokersContainer);
        VBox tarotColumn = new VBox(8, tarotTitle, ownedTarotCardsContainer);
        HBox equipmentRow = new HBox(24, jokerColumn, tarotColumn);
        equipmentRow.getStyleClass().add("equipment-row");
        equipmentRow.setTranslateY(-35);
        VBox playArea = new VBox(30, equipmentRow, handContainer);
        playArea.setAlignment(Pos.CENTER);

        centerLayer = new StackPane(playArea, shopPanel);
        centerLayer.setAlignment(Pos.CENTER);
        shopPanel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setCenter(centerLayer);
    }

    private void configureEquipmentSlot(HBox slot, int capacity) {
        double width = capacity * CardNode.CARD_WIDTH + (capacity - 1) * 12;
        slot.setMinSize(width, CardNode.CARD_HEIGHT);
        slot.setPrefSize(width, CardNode.CARD_HEIGHT);
        slot.setMaxSize(width, CardNode.CARD_HEIGHT);
        slot.setSpacing(12);
        slot.setAlignment(Pos.CENTER);
        slot.getStyleClass().add("equipment-slot");
    }

    private void buildShopPanel() {
        // --- SHOP OFFER RENDERING ---
        Label title = new Label("SHOP");
        title.getStyleClass().add("shop-title");
        shopMessageLabel.getStyleClass().add("shop-message");
        Label tarotTitle = new Label("TAROT CONSUMABLES");
        tarotTitle.getStyleClass().add("shop-title");
        tarotShopMessageLabel.getStyleClass().add("shop-message");

        shopOfferContainer.setAlignment(Pos.CENTER);
        tarotShopOfferContainer.setAlignment(Pos.CENTER);
        rerollShopOffers();
        shopPanel.getChildren().addAll(title, shopMessageLabel, shopOfferContainer,
                tarotTitle, tarotShopMessageLabel, tarotShopOfferContainer);
    }

    private void buildBottomArea() {
        // --- LOWER HAND ACTION BAR ---
        playButton.getStyleClass().add("btn-primary");
        playButton.getStyleClass().add("play-hand-button");
        discardButton.getStyleClass().add("btn-danger");
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

        VBox actionBar = new VBox(10);
        actionBar.setAlignment(Pos.CENTER);
        HBox rightActions = new HBox(8, discardButton, sortRankButton, sortSuitButton);
        rightActions.setAlignment(Pos.CENTER);
        actionBar.setAlignment(Pos.CENTER);
        actionBar.getChildren().addAll(playButton, rightActions);
        actionBar.getStyleClass().add("hand-action-bar");
        setBottom(actionBar);
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
            handScorePreviewLabel.setText("0 Chips  x  0 Mult = 0 ");
            System.out.println("[PREVIEW] selected=0 | chips=0 | mult=0.0 | score=0");
            return;
        }
        if (selected.size() > 5) {
            handTypeLabel.setText("Too many cards selected! (Max 5)");
            handScorePreviewLabel.setText("[ - ]");
            return;
        }

        BasePokerEngine.ScoreResult result =
                BasePokerEngine.evaluateAndScore(selected, gameState);
        handTypeLabel.setText(result.handType.getDisplayName());
        handScorePreviewLabel.setText(String.format(
                "[ %d Chips  x  %.1f Mult = %d ]",
                result.totalChips, result.mult, result.finalScore));
        System.out.printf("[PREVIEW] selected=%d | hand=%s | baseChips=%d | cardChips=%d"
                        + " | totalChips=%d | mult=%.2f | score=%d%n",
                selected.size(), result.handType.getDisplayName(), result.baseChips,
                result.cardChips, result.totalChips, result.mult, result.finalScore);
    }

    /**
     * Performs the refres\1 \2i operation.
     */
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
        renderTarotCards();
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

    private void renderTarotCards() {
        ownedTarotCardsContainer.getChildren().clear();
        if (gameState.getOwnedTarotCards().isEmpty()) {
            Label empty = new Label("No Tarot cards | "
                    + gameState.getOwnedTarotCards().size() + "/" + gameState.getMaxTarotCards() + " slots");
            empty.getStyleClass().add("empty-jokers");
            ownedTarotCardsContainer.getChildren().add(empty);
            return;
        }

        for (TarotCards tarotCard : gameState.getOwnedTarotCards()) {
            ownedTarotCardsContainer.getChildren().add(
                    new TarotNode(tarotCard, () -> useTarotCard(tarotCard)));
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
        if (centerLayer.getChildren().stream().anyMatch(node -> node instanceof OccurrenceSelectionView)) {
            return;
        }
        OccurrenceSelectionView occurrenceView = new OccurrenceSelectionView(gameState, () -> {
            centerLayer.getChildren().removeIf(node -> node instanceof OccurrenceSelectionView);
            beginRoundAfterOccurrence();
        });
        centerLayer.getChildren().add(occurrenceView);
        occurrenceView.toFront();
        startRoundButton.setDisable(true);
        shopPanel.setVisible(false);
        shopPanel.setManaged(false);
        System.out.println("[UI] Pre-round occurrence selection opened.");
    }

    private void beginRoundAfterOccurrence() {
        if (!firstRoundStarted) {
            gameState.startRound(300, 4, 3);
            firstRoundStarted = true;
        } else {
            gameState.startNextRound();
        }
        shopOpen = false;
        startRoundButton.setDisable(false);
        System.out.println("[UI] Start Round clicked after occurrence.");
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
        boolean visible = accessible && shopOpen;
        shopPanel.setVisible(visible);
        shopPanel.setManaged(visible);
        shopButton.setDisable(!accessible);
        for (Map.Entry<Joker, Button> entry : shopBuyButtons.entrySet()) {
            Joker offer = entry.getKey();
            int offerCost = offer.getShopCost();
            Button buyButton = entry.getValue();
            buyButton.setText("Buy " + offer.getName() + " ($" + offerCost + ")");
            buyButton.setDisable(
                    !accessible
                            || gameState.getCash() < offerCost
                            || gameState.getOwnedJokers().size() >= gameState.getMaxJokers());
        }
        for (Map.Entry<TarotCards, Button> entry : tarotBuyButtons.entrySet()) {
            TarotCards offer = entry.getKey();
            int offerCost = offer.getShopCost();
            Button buyButton = entry.getValue();
            buyButton.setText("Buy " + offer.getName() + " ($" + offerCost + ")");
            buyButton.setDisable(
                    !accessible
                            || gameState.getCash() < offerCost
                            || gameState.getOwnedTarotCards().size() >= gameState.getMaxTarotCards());
        }
        shopMessageLabel.setText("Choose an offer | "
                + gameState.getOwnedJokers().size() + "/" + gameState.getMaxJokers() + " slots used");
        tarotShopMessageLabel.setText(tarotShopOffers.isEmpty()
                ? "No Tarot cards are registered yet."
                : "Choose an offer | " + gameState.getOwnedTarotCards().size()
                        + "/" + gameState.getMaxTarotCards() + " slots used");
    }

    private void rerollShopOffers() {
        shopOffers.clear();
        shopOffers.addAll(Joker.createShopOffers(MAX_SHOP_OFFERS));
        tarotShopOffers.clear();
        tarotShopOffers.addAll(TarotCards.createShopOffers(MAX_SHOP_OFFERS));
        renderShopOffers();
    }

    private void renderShopOffers() {
        // Rebuild the offer cards from the registered equipment data.
        shopOfferContainer.getChildren().clear();
        shopBuyButtons.clear();
        for (Joker offer : shopOffers) {
            Label description = new Label(offer.getDescription());
            description.getStyleClass().add("shop-message");
            description.setWrapText(true);
            description.setMaxWidth(150);

            Button buyButton = new Button();
            buyButton.getStyleClass().add("btn-primary");
            buyButton.setOnAction(event -> buyShopOffer(offer));
            shopBuyButtons.put(offer, buyButton);

            VBox offerCard = new VBox(6, new JokerNode(offer), description, buyButton);
            offerCard.getStyleClass().add("shop-offer");
            offerCard.setAlignment(Pos.CENTER);
            shopOfferContainer.getChildren().add(offerCard);
        }

        tarotShopOfferContainer.getChildren().clear();
        tarotBuyButtons.clear();
        for (TarotCards offer : tarotShopOffers) {
            Label description = new Label(offer.getDescription());
            description.getStyleClass().add("shop-message");
            description.setWrapText(true);
            description.setMaxWidth(150);

            Button buyButton = new Button();
            buyButton.getStyleClass().add("btn-primary");
            buyButton.setOnAction(event -> buyTarotOffer(offer));
            tarotBuyButtons.put(offer, buyButton);

            VBox offerCard = new VBox(6, new TarotNode(offer), description, buyButton);
            offerCard.getStyleClass().add("shop-offer");
            offerCard.setAlignment(Pos.CENTER);
            tarotShopOfferContainer.getChildren().add(offerCard);
        }
    }

    private void buyShopOffer(Joker offer) {
        System.out.println("[UI] Buy " + offer.getName() + " clicked.");
        if (Shop.purchaseJoker(gameState, offer)) {
            rerollShopOffers();
            refreshUI();
        }
    }

    private void buyTarotOffer(TarotCards offer) {
        System.out.println("[UI] Buy Tarot " + offer.getName() + " clicked.");
        if (Shop.purchaseTarotCard(gameState, offer)) {
            rerollShopOffers();
            refreshUI();
        }
    }

    private void useTarotCard(TarotCards tarotCard) {
        System.out.println("[UI] Use Tarot " + tarotCard.getName() + " clicked.");
        if (gameState.useTarotCard(tarotCard, getSelectedCards())) {
            refreshUI();
        }
    }

    private void sellJoker(Joker joker) {
        if (gameState.sellJoker(joker)) {
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
