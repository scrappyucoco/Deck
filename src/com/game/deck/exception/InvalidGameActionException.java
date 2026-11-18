package com.game.deck.exception;

/**
 * Checked business exception for illegal actions in a Deck run.
 * Model, equipment, and view layers use this single exception type to report
 * invalid plays, invalid Tarot targeting, empty-deck draws, and rejected
 * purchases without partially changing run state.
 */
public class InvalidGameActionException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a player-facing explanation.
     */
    public InvalidGameActionException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a player-facing explanation and root cause.
     */
    public InvalidGameActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
