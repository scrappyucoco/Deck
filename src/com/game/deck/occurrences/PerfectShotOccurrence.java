package com.game.deck.occurrences;

import com.game.deck.equipment.GreedyJoker;
import com.game.deck.model.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * PerfectShotOccurrence is a poker-rules quiz occurrence.
 * The player answers one multiple-choice question and earns a Greedy Joker
 * when the answer is correct. The view uses the same occurrence styling as
 * MonologueTrial and presents the question and feedback in dialogue boxes.
 */
public class PerfectShotOccurrence extends Occurrence {
    private static final Random RANDOM = new Random();

    private static final List<QuizQuestion> QUESTION_POOL = List.of(
            new QuizQuestion(
                    "What cards does a Full House contain?",
                    List.of(
                            "Three cards of one rank and two of another rank",
                            "Five cards of the same suit",
                            "Four cards of one rank and one other card",
                            "Five cards in consecutive rank order"),
                    "Three cards of one rank and two of another rank",
                    "A Full House combines three of a kind with a pair."),
            new QuizQuestion(
                    "Which hand is made of five cards of the same suit?",
                    List.of("Flush", "Straight", "Full House", "Two Pair"),
                    "Flush",
                    "A Flush contains five cards of one suit, regardless of rank."),
            new QuizQuestion(
                    "Which hand contains four cards of the same rank?",
                    List.of("Four of a Kind", "Full House", "Flush", "Three of a Kind"),
                    "Four of a Kind",
                    "Four of a Kind uses all four suits of one rank."),
            new QuizQuestion(
                    "A Straight is made of what?",
                    List.of(
                            "Five cards with consecutive ranks",
                            "Five cards of the same rank",
                            "Five cards of the same suit",
                            "Two pairs plus a kicker"),
                    "Five cards with consecutive ranks",
                    "A Straight runs five consecutive ranks in mixed suits."),
            new QuizQuestion(
                    "What is the highest standard poker hand?",
                    List.of("Royal Flush", "Straight Flush", "Four of a Kind", "Full House"),
                    "Royal Flush",
                    "A Royal Flush is an Ace-high Straight Flush."),
            new QuizQuestion(
                    "What makes a Two Pair hand?",
                    List.of(
                            "Two cards of one rank, two of another, plus a kicker",
                            "Two cards of the same rank",
                            "Three cards of one rank and two of another",
                            "Five cards of the same suit"),
                    "Two cards of one rank, two of another, plus a kicker",
                    "Two Pair contains two different pairs and one kicker."));

    private final QuizQuestion question;
    private final List<String> choices;

    /**
     * Creates the occurrence with a random poker-rules question.
     */
    public PerfectShotOccurrence() {
        this(randomQuestion());
    }

    private PerfectShotOccurrence(QuizQuestion question) {
        super("Perfect Shot", "Answer a poker question correctly to earn a Greedy Joker.");
        this.question = question;
        List<String> shuffledChoices = new ArrayList<>(question.getChoices());
        Collections.shuffle(shuffledChoices, RANDOM);
        this.choices = List.copyOf(shuffledChoices);
    }

    private static QuizQuestion randomQuestion() {
        return QUESTION_POOL.get(RANDOM.nextInt(QUESTION_POOL.size()));
    }

    @Override
    public void execute(GameState state) {
        System.out.println("\n=== [PRE-ROUND OCCURRENCE: PERFECT SHOT] ===");
        System.out.println("PROMPT: \"" + question.getPrompt() + "\"");
        System.out.println("OPTIONS: " + choices);
        System.out.println("REWARD: " + new GreedyJoker().getName());
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public Node createView(GameState state, Runnable onComplete) {
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setPrefWidth(560);
        root.setMaxWidth(560);
        root.getStyleClass().add("occurrence-trial");

        Label promptText = createDialogueText(question.getPrompt());
        VBox promptBox = createDialogueBox("THE DEALER", promptText);
        root.getChildren().add(promptBox);

        Label instruction = new Label("Choose one answer. A correct answer awards a Greedy Joker.");
        instruction.getStyleClass().add("occurrence-description");
        instruction.setWrapText(true);
        instruction.setMaxWidth(500);
        root.getChildren().add(instruction);

        VBox choicesBox = new VBox(8);
        choicesBox.setAlignment(Pos.CENTER);
        List<Button> choiceButtons = new ArrayList<>();

        Label feedbackLabel = createDialogueText("");
        VBox feedbackBox = createDialogueBox("RESULT", feedbackLabel);
        feedbackBox.setVisible(false);
        feedbackBox.setManaged(false);

        Button continueButton = new Button("Continue to Round");
        continueButton.getStyleClass().add("btn-primary");
        continueButton.setOnAction(event -> onComplete.run());

        for (String choice : choices) {
            Button choiceButton = new Button(choice);
            choiceButton.getStyleClass().add("occurrence-fragment");
            choiceButton.setWrapText(true);
            choiceButton.setMaxWidth(500);
            choiceButton.setPrefWidth(500);
            choiceButtons.add(choiceButton);

            choiceButton.setOnAction(event -> {
                // Lock every answer after the first click so the reward can
                // never be claimed more than once.
                for (Button button : choiceButtons) {
                    button.setDisable(true);
                }

                boolean correct = question.isCorrect(choice);
                boolean rewardGranted = false;
                if (correct) {
                    rewardGranted = claimReward(state);
                } else {
                    onFail(state);
                }

                setFeedback(feedbackBox, feedbackLabel, correct, rewardGranted);
                feedbackBox.setVisible(true);
                feedbackBox.setManaged(true);
                root.getChildren().add(continueButton);
            });

            choicesBox.getChildren().add(choiceButton);
        }

        root.getChildren().addAll(choicesBox, feedbackBox);
        return root;
    }

    private VBox createDialogueBox(String speakerText, Label dialogueText) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(14, 18, 14, 18));
        box.setMaxWidth(500);
        box.getStyleClass().add("occurrence-dialogue");

        Label speaker = new Label(speakerText);
        speaker.getStyleClass().add("occurrence-dialogue-speaker");

        box.getChildren().addAll(speaker, dialogueText);
        return box;
    }

    private Label createDialogueText(String text) {
        Label dialogue = new Label(text);
        dialogue.getStyleClass().add("occurrence-dialogue-text");
        dialogue.setWrapText(true);
        dialogue.setMaxWidth(460);
        return dialogue;
    }

    private void setFeedback(VBox feedbackBox, Label feedbackLabel,
            boolean correct, boolean rewardGranted) {
        feedbackBox.getStyleClass().removeAll(
                "occurrence-dialogue-success", "occurrence-dialogue-failure");
        feedbackBox.getStyleClass().add(
                correct ? "occurrence-dialogue-success" : "occurrence-dialogue-failure");

        if (correct) {
            feedbackLabel.setText("Correct! " + question.getExplanation()
                    + (rewardGranted
                            ? " A Greedy Joker has joined your collection."
                            : " Your Joker slots are full, so the reward could not be added."));
        } else {
            feedbackLabel.setText("Not quite. " + question.getExplanation());
        }
    }

    /**
     * Grants the Perfect Shot reward: one Greedy Joker.
     *
     * @return true when the Joker was added to a free slot
     */
    public boolean claimReward(GameState state) {
        GreedyJoker reward = new GreedyJoker();
        boolean added = state.addJoker(reward);
        System.out.println(added
                ? "[PERFECT SHOT] Correct answer. Reward granted: " + reward.getName() + "."
                : "[PERFECT SHOT] Correct answer, but Joker slots are full. Reward skipped.");
        return added;
    }

    @Override
    public void onPass(GameState state) {
        claimReward(state);
    }

    @Override
    public void onFail(GameState state) {
        System.out.println("[PERFECT SHOT] Incorrect answer. No Joker reward granted.");
    }

    /**
     * Returns the prompt shown for this occurrence instance.
     */
    public String getPromptQuestion() {
        return question.getPrompt();
    }

    /**
     * Returns the shuffled answer choices for this occurrence instance.
     */
    public List<String> getChoices() {
        return choices;
    }

    /**
     * Returns the correct answer text for this occurrence instance.
     */
    public String getCorrectAnswer() {
        return question.getCorrectAnswer();
    }

    /**
     * Returns the explanation shown after the player answers.
     */
    public String getExplanation() {
        return question.getExplanation();
    }

    /**
     * Immutable quiz data for one Perfect Shot question.
     */
    private static final class QuizQuestion {
        private final String prompt;
        private final List<String> choices;
        private final String correctAnswer;
        private final String explanation;

        private QuizQuestion(String prompt, List<String> choices,
                String correctAnswer, String explanation) {
            this.prompt = prompt;
            this.choices = List.copyOf(choices);
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
        }

        private boolean isCorrect(String answer) {
            return correctAnswer.equals(answer);
        }

        private String getPrompt() {
            return prompt;
        }

        private List<String> getChoices() {
            return choices;
        }

        private String getCorrectAnswer() {
            return correctAnswer;
        }

        private String getExplanation() {
            return explanation;
        }
    }
}
