package com.game.deck.occurrences;

/**
 * Contains the MonologueTrial source type for the Deck game.
 * This source file participates in the modular Deck application.
 */

import com.game.deck.model.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

import com.game.deck.equipment.Joker;
import com.game.deck.model.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.Random;

/**
 * MonologueTrial is a multi-phase "Push Your Luck" pre-round dialogue event.
 * Players interact with a shadowy Jester through escalating gamble phases
 * inside a single dynamically updating JavaFX view.
 */
public class MonologueTrial extends Occurrence {
    /**
     * Dialogue phases for the Push Your Luck state machine.
     */
    public enum Phase {
        PHASE_1_TEMPTATION,
        WALKED_AWAY,
        PHASE_2A_SCAM,
        PHASE_2B_SECOND_TEMPTATION,
        DECLINED,
        PHASE_3_JACKPOT,
        PHASE_3_BUST
    }

    public static final String PHASE_1_TEXT =
            "A shadowy Jester whispers from the deck: 'Why settle for honest hands when fortune "
                    + "favors the bold? Step into the dark, friend... let us test your fate.'";
    public static final String WALK_AWAY_TEXT =
            "You pocket $2 and walk away. The Jester sneers in the background.";
    public static final String PHASE_2A_SCAM_TEXT =
            "The Jester cackles hysterically! 'A fool and his chips are soon parted!'";
    public static final String PHASE_2B_TEMPTATION_TEXT =
            "The Jester smiles warmly: 'You have courage! But courage is cheap, do you dare push "
                    + "your luck further? Double down for glory, or leave with empty pockets!'";
    public static final String DECLINE_TEXT =
            "You refuse to risk any further. The Jester vanishes into thin air.";
    public static final String PHASE_3_BUST_TEXT =
            "The cards turn cold... The Jester shrugs: 'Luck is a fickle mistress. Better luck next hand!'";

    private static final String ROOT_STYLE =
            "-fx-background-color: #1e1e2f;"
                    + " -fx-background-radius: 12px;"
                    + " -fx-border-color: #ffd700;"
                    + " -fx-border-width: 2px;"
                    + " -fx-border-radius: 12px;";
    private static final String DIALOGUE_BOX_STYLE =
            "-fx-background-color: #141422;"
                    + " -fx-background-radius: 10px;"
                    + " -fx-border-color: rgba(255, 215, 0, 0.35);"
                    + " -fx-border-width: 1px;"
                    + " -fx-border-radius: 10px;";
    private static final String HEADER_STYLE =
            "-fx-text-fill: #ffd700; -fx-font-size: 20px; -fx-font-weight: bold;";
    private static final String SUBHEADER_STYLE =
            "-fx-text-fill: #ffd700; -fx-font-size: 13px; -fx-font-weight: bold;";
    private static final String MONOLOGUE_TEXT_STYLE =
            "-fx-text-fill: #f1f5f9; -fx-font-size: 15px; -fx-line-spacing: 3px;";
    private static final String OUTCOME_STYLE_NEUTRAL =
            "-fx-text-fill: #cbd5e1; -fx-font-size: 13px; -fx-font-weight: bold;";
    private static final String OUTCOME_STYLE_POSITIVE =
            "-fx-text-fill: #ffd700; -fx-font-size: 13px; -fx-font-weight: bold;";
    private static final String OUTCOME_STYLE_NEGATIVE =
            "-fx-text-fill: #f87171; -fx-font-size: 13px; -fx-font-weight: bold;";
    private static final String BUTTON_BASE_STYLE =
            "-fx-background-color: #4a5568;"
                    + " -fx-text-fill: #f8fafc;"
                    + " -fx-font-size: 14px;"
                    + " -fx-font-weight: bold;"
                    + " -fx-padding: 10px 20px;"
                    + " -fx-background-radius: 8px;"
                    + " -fx-border-color: #718096;"
                    + " -fx-border-width: 1px;"
                    + " -fx-border-radius: 8px;";
    private static final String BUTTON_HOVER_STYLE =
            "-fx-background-color: #5a677d;"
                    + " -fx-text-fill: #ffd700;"
                    + " -fx-font-size: 14px;"
                    + " -fx-font-weight: bold;"
                    + " -fx-padding: 10px 20px;"
                    + " -fx-background-radius: 8px;"
                    + " -fx-border-color: #ffd700;"
                    + " -fx-border-width: 1px;"
                    + " -fx-border-radius: 8px;";

    private final Random random;
    private Phase currentPhase = Phase.PHASE_1_TEMPTATION;
    private String currentMonologueText = PHASE_1_TEXT;
    private Joker lastRewardedJoker;

    /**
     * Creates a Push Your Luck trial with standard randomness.
     */
    public MonologueTrial() {
        this(new Random());
    }

    /**
     * Creates a Push Your Luck trial using the provided random source.
     */
    public MonologueTrial(Random random) {
        super("The Monologue Trial", "Push your luck in a shadowy multi-step bargain.");
        this.random = Objects.requireNonNull(random, "random");
    /**
     * Performs the monologu\1 \2rial operation.
     */
    public MonologueTrial() {
        this(
                "When probability fails, how do you respond?",
                List.of("I adapt my hand", "through calculated discards", "I accept defeat"),
                "I adapt my hand through calculated discards",
                false
        );
    }

    /**
     * Performs the monologu\1 \2rial operation.
     */
    public MonologueTrial(String prompt, List<String> fragments, String targetPattern, boolean isHighRisk) {
        super("Inner Monologue Trial", "Debate identity and probability to earn rewards.");
        this.promptQuestion = prompt;
        this.availableFragments = fragments;
        this.targetAlignedPattern = targetPattern;
        this.isHighRiskDire = isHighRisk;
    }

    @Override
    /**
     * Performs the execute operation.
     */
    public void execute(GameState state) {
        System.out.println("\n=== [PRE-ROUND OCCURRENCE: THE MONOLOGUE TRIAL] ===");
        System.out.println("PHASE 1: \"" + PHASE_1_TEXT + "\"");
    }

    @Override
    public boolean isInteractive() {
        return true;
    }
    /**
     * Performs the i\1 \2nteractive operation.
     */
    public boolean isInteractive() { return true; }

    @Override
    /**
     * Performs the creat\1 \2iew operation.
     */
    public Node createView(GameState state, Runnable onComplete) {
        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("occurrence-trial");
        Label prompt = new Label(promptQuestion);
        prompt.getStyleClass().add("occurrence-prompt");
        prompt.setWrapText(true);
        Label sentence = new Label("Select fragments in order.");
        sentence.getStyleClass().add("occurrence-result");
        List<String> selected = new ArrayList<>();
        FlowPane fragments = new FlowPane(8, 8);
        fragments.setAlignment(Pos.CENTER);
        for (String fragment : availableFragments) {
            Button button = new Button(fragment);
            button.getStyleClass().add("occurrence-fragment");
            button.setOnAction(event -> {
                selected.add(fragment);
                button.setDisable(true);
                sentence.setText(String.join(" ", selected));
            });
            fragments.getChildren().add(button);
        }
        Button submit = new Button("Submit Trial");
        submit.getStyleClass().add("btn-primary");
        submit.setOnAction(event -> {
            boolean passed = submitSentence(selected, state);
            sentence.setText(passed ? "Trial passed. Reward granted." : "Trial failed.");
            submit.setDisable(true);
            Button continueButton = new Button("Continue to Round");
            continueButton.getStyleClass().add("btn-primary");
            continueButton.setOnAction(ignored -> onComplete.run());
            root.getChildren().add(continueButton);
        });
        root.getChildren().addAll(prompt, sentence, fragments, submit);
        return root;
    }

    /**
     * Performs the submi\1 \2entence operation.
     */
    public boolean submitSentence(List<String> selectedFragments, GameState state) {
        // Join fragments in player order before comparing against the target.
        String builtSentence = String.join(" ", selectedFragments);
        System.out.println("[MONOLOGUE] Built Sentence: \"" + builtSentence + "\"");

    /**
     * Creates the multi-phase interactive view for callers without a completion callback.
     */
    public Node createView(GameState state) {
        return createView(state, null);
    }

    @Override
    public Node createView(GameState state, Runnable onComplete) {
        Objects.requireNonNull(state, "state");

        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        root.setPrefWidth(560);
        root.setMaxWidth(560);
        root.getStyleClass().add("monologue-trial-root");
        root.setStyle(ROOT_STYLE);

        Label titleLabel = new Label("THE MONOLOGUE TRIAL");
        titleLabel.getStyleClass().add("monologue-trial-header");
        titleLabel.setStyle(HEADER_STYLE);

        Label phaseLabel = new Label();
        phaseLabel.getStyleClass().add("monologue-trial-subheader");
        phaseLabel.setStyle(SUBHEADER_STYLE);

        Label monologueLabel = new Label();
        monologueLabel.getStyleClass().add("monologue-trial-text");
        monologueLabel.setStyle(MONOLOGUE_TEXT_STYLE);
        monologueLabel.setWrapText(true);
        monologueLabel.setMaxWidth(480);
        monologueLabel.setPrefWidth(480);

        Label effectLabel = new Label();
        effectLabel.setWrapText(true);
        effectLabel.setMaxWidth(480);
        effectLabel.setVisible(false);
        effectLabel.setManaged(false);

        VBox dialogueBox = new VBox(10, monologueLabel, effectLabel);
        dialogueBox.setAlignment(Pos.CENTER_LEFT);
        dialogueBox.setPadding(new Insets(16, 20, 16, 20));
        dialogueBox.setMaxWidth(512);
        dialogueBox.getStyleClass().add("monologue-dialogue-box");
        dialogueBox.setStyle(DIALOGUE_BOX_STYLE);
        VBox.setVgrow(dialogueBox, Priority.NEVER);

        HBox actionsBox = new HBox(12);
        actionsBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, phaseLabel, dialogueBox, actionsBox);

        renderPhase1(state, root, phaseLabel, monologueLabel, effectLabel, actionsBox, onComplete);
        return root;
    }

    private void renderPhase1(
            GameState state,
            VBox root,
            Label phaseLabel,
            Label monologueLabel,
            Label effectLabel,
            HBox actionsBox,
            Runnable onComplete
    ) {
        currentPhase = Phase.PHASE_1_TEMPTATION;
        currentMonologueText = PHASE_1_TEXT;
        phaseLabel.setText("Phase I — The Initial Temptation");
        monologueLabel.setText(PHASE_1_TEXT);
        hideEffect(effectLabel);

        Button walkAwayButton = createStyledButton("Walk Away");
        walkAwayButton.setOnAction(event -> {
            state.addCash(2);
            currentPhase = Phase.WALKED_AWAY;
            currentMonologueText = WALK_AWAY_TEXT;
            phaseLabel.setText("Outcome — Safe Retreat");
            monologueLabel.setText(WALK_AWAY_TEXT);
            showEffect(effectLabel, "Effect: +$2 Cash (Current Cash: $" + state.getCash() + ")",
                    OUTCOME_STYLE_POSITIVE);
            System.out.println("[MONOLOGUE TRIAL] Player walked away: +$2 Cash.");
            showCloseButton(root, actionsBox, onComplete);
        });

        Button listenButton = createStyledButton("Listen & Stay");
        listenButton.setOnAction(event -> {
            if (random.nextBoolean()) {
                renderPhase2A(state, root, phaseLabel, monologueLabel, effectLabel, actionsBox, onComplete);
            } else {
                renderPhase2B(state, root, phaseLabel, monologueLabel, effectLabel, actionsBox, onComplete);
            }
        });

        actionsBox.getChildren().setAll(walkAwayButton, listenButton);
    }

    private void renderPhase2A(
            GameState state,
            VBox root,
            Label phaseLabel,
            Label monologueLabel,
            Label effectLabel,
            HBox actionsBox,
            Runnable onComplete
    ) {
        state.deductBaseChips(10);
        currentPhase = Phase.PHASE_2A_SCAM;
        currentMonologueText = PHASE_2A_SCAM_TEXT;

        phaseLabel.setText("Phase II — The Scam Trap");
        monologueLabel.setText(PHASE_2A_SCAM_TEXT);
        showEffect(effectLabel,
                "Effect: -10 Base Chips (Run Base Chip Modifier: " + state.getBaseChipsModifier() + ")",
                OUTCOME_STYLE_NEGATIVE);
        System.out.println("[MONOLOGUE TRIAL] Phase 2A Scam triggered: -10 Base Chips.");
        showCloseButton(root, actionsBox, onComplete);
    }

    private void renderPhase2B(
            GameState state,
            VBox root,
            Label phaseLabel,
            Label monologueLabel,
            Label effectLabel,
            HBox actionsBox,
            Runnable onComplete
    ) {
        currentPhase = Phase.PHASE_2B_SECOND_TEMPTATION;
        currentMonologueText = PHASE_2B_TEMPTATION_TEXT;

        phaseLabel.setText("Phase II — The Second Temptation");
        monologueLabel.setText(PHASE_2B_TEMPTATION_TEXT);
        hideEffect(effectLabel);
        System.out.println("[MONOLOGUE TRIAL] Phase 2B Second Temptation triggered.");

        Button pushLuckButton = createStyledButton("Push Your Luck!");
        pushLuckButton.setOnAction(event -> {
            if (random.nextBoolean()) {
                renderPhase3Jackpot(state, root, phaseLabel, monologueLabel, effectLabel, actionsBox, onComplete);
            } else {
                renderPhase3Bust(root, phaseLabel, monologueLabel, effectLabel, actionsBox, onComplete);
            }
        });

        Button declineButton = createStyledButton("Decline");
        declineButton.setOnAction(event -> {
            currentPhase = Phase.DECLINED;
            currentMonologueText = DECLINE_TEXT;
            phaseLabel.setText("Outcome — Empty Pockets");
            monologueLabel.setText(DECLINE_TEXT);
            showEffect(effectLabel, "Effect: $0 gained.", OUTCOME_STYLE_NEUTRAL);
            System.out.println("[MONOLOGUE TRIAL] Player declined in Phase 2B: $0.");
            showCloseButton(root, actionsBox, onComplete);
        });

        actionsBox.getChildren().setAll(pushLuckButton, declineButton);
    }

    private void renderPhase3Jackpot(
            GameState state,
            VBox root,
            Label phaseLabel,
            Label monologueLabel,
            Label effectLabel,
            HBox actionsBox,
            Runnable onComplete
    ) {
        state.addCash(10);
        Joker reward = state.getRandomShopJoker();
        lastRewardedJoker = reward;
        boolean jokerAdded = state.addJoker(reward);

        currentPhase = Phase.PHASE_3_JACKPOT;
        String jackpotText = "FORTUNE FAVORS THE BOLD! The Jester hands you a crisp $10 bill "
                + "and a shiny new Joker: " + reward.getName() + "!";
        if (!jokerAdded) {
            jackpotText += " (Your Joker slots are full, so the Joker could not be added.)";
        }
        currentMonologueText = jackpotText;

        phaseLabel.setText("Phase III — JACKPOT!");
        monologueLabel.setText(jackpotText);
        showEffect(effectLabel,
                jokerAdded
                        ? "Effect: +$10 Cash & Joker [" + reward.getName() + "] added!"
                        : "Effect: +$10 Cash (Joker slots full: "
                                + state.getOwnedJokers().size() + "/" + state.getMaxJokers() + ")",
                OUTCOME_STYLE_POSITIVE);
        System.out.println("[MONOLOGUE TRIAL] Phase 3 Jackpot: +$10 Cash, Joker=" + reward.getName()
                + ", added=" + jokerAdded);
        showCloseButton(root, actionsBox, onComplete);
    }

    private void renderPhase3Bust(
            VBox root,
            Label phaseLabel,
            Label monologueLabel,
            Label effectLabel,
            HBox actionsBox,
            Runnable onComplete
    ) {
        currentPhase = Phase.PHASE_3_BUST;
        currentMonologueText = PHASE_3_BUST_TEXT;

        phaseLabel.setText("Phase III — Bust");
        monologueLabel.setText(PHASE_3_BUST_TEXT);
        showEffect(effectLabel, "Effect: $0 gained.", OUTCOME_STYLE_NEGATIVE);
        System.out.println("[MONOLOGUE TRIAL] Phase 3 Bust: $0.");
        showCloseButton(root, actionsBox, onComplete);
    }

    private void showCloseButton(VBox root, HBox actionsBox, Runnable onComplete) {
        Button closeButton = createStyledButton("Close Trial");
        closeButton.setOnAction(event -> {
            if (onComplete != null) {
                onComplete.run();
            } else if (root.getParent() instanceof Pane parent) {
                parent.getChildren().remove(root);
            }
        });
        actionsBox.getChildren().setAll(closeButton);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("monologue-choice-btn");
        button.setStyle(BUTTON_BASE_STYLE);
        button.setCursor(Cursor.HAND);
        button.setWrapText(true);
        button.hoverProperty().addListener((obs, wasHovered, isHovered) ->
                button.setStyle(isHovered ? BUTTON_HOVER_STYLE : BUTTON_BASE_STYLE));
        return button;
    }

    private void showEffect(Label effectLabel, String text, String style) {
        effectLabel.setText(text);
        effectLabel.setStyle(style);
        effectLabel.setVisible(true);
        effectLabel.setManaged(true);
    }

    private void hideEffect(Label effectLabel) {
        effectLabel.setText("");
        effectLabel.setVisible(false);
        effectLabel.setManaged(false);
    }

    @Override
    /**
     * Performs the o\1 \2ass operation.
     */
    public void onPass(GameState state) {
        state.addCash(10);
        Joker reward = state.getRandomShopJoker();
        lastRewardedJoker = reward;
        state.addJoker(reward);
        System.out.println("[REWARD] Passed Monologue Trial! Awarded +$10 Cash and " + reward.getName() + ".");
    }

    @Override
    /**
     * Performs the o\1 \2ail operation.
     */
    public void onFail(GameState state) {
        state.deductBaseChips(10);
        System.out.println("[RESULT] Monologue Trial Scam! Deducted 10 Base Chips.");
    }

    /**
     * Returns the current dialogue phase of this trial instance.
     */
    public Phase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Returns the currently displayed monologue text.
     */
    public String getCurrentMonologueText() {
        return currentMonologueText;
    }

    /**
     * Returns the Joker pulled during a Phase 3 jackpot, or null otherwise.
     */
    public Joker getLastRewardedJoker() {
        return lastRewardedJoker;
    }
     * Performs the ge\1 \2romp\1 \2uestion operation.
     */
    public String getPromptQuestion() { return promptQuestion; }
    /**
     * Performs the ge\1 \2vailabl\1 \2ragments operation.
     */
    public List<String> getAvailableFragments() { return availableFragments; }
}
