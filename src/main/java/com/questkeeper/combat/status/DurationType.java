package com.questkeeper.combat.status;

/**
 * Defines how a status effect's duration is tracked and when it expires.
 *
 * Different abilities and spells have different duration mechanics in D&D 5e.
 * This enum captures the various ways an effect can be timed.
 *
 * @author Marc McGough
 * @version 1.0
 */
public enum DurationType {

    /**
     * Effect lasts for a fixed number of rounds.
     * Duration decrements at the end of each of the affected creature's turns.
     */
    ROUNDS,

    /**
     * Effect ends at the end of the affected creature's current turn.
     * Common for effects that last "until the end of your turn".
     */
    UNTIL_END_OF_TURN,

    /**
     * Effect ends at the start of the affected creature's next turn.
     * Common for effects that last "until the start of your next turn".
     */
    UNTIL_START_OF_TURN,

    /**
     * Effect persists until the affected creature succeeds on a saving throw.
     * The save is typically attempted at the end of each of the creature's turns.
     */
    UNTIL_SAVE,

    /**
     * Effect is permanent until removed by specific means (dispel magic, etc.).
     * The duration counter is not used for permanent effects.
     */
    PERMANENT,

    /**
     * Effect lasts indefinitely until explicitly removed.
     * Similar to PERMANENT but typically for effects that can be ended
     * by simple actions (standing up from prone, releasing a grapple).
     */
    INDEFINITE;

    /**
     * Returns true if this duration type uses a round counter.
     */
    public boolean usesRoundCounter() {
        return this == ROUNDS;
    }

    /**
     * Returns true if this duration type allows saving throws to end the effect.
     */
    public boolean allowsSavingThrow() {
        return this == UNTIL_SAVE;
    }

    /**
     * Returns true if this effect should be processed at turn start.
     */
    public boolean checksAtTurnStart() {
        return this == UNTIL_START_OF_TURN;
    }

    /**
     * Returns true if this effect should be processed at turn end.
     */
    public boolean checksAtTurnEnd() {
        return this == ROUNDS || this == UNTIL_END_OF_TURN || this == UNTIL_SAVE;
    }

    /**
     * Returns a human-readable description of this duration type.
     */
    public String getDescription() {
        return switch (this) {
            case ROUNDS -> "Lasts for a number of rounds";
            case UNTIL_END_OF_TURN -> "Lasts until end of turn";
            case UNTIL_START_OF_TURN -> "Lasts until start of next turn";
            case UNTIL_SAVE -> "Lasts until successful saving throw";
            case PERMANENT -> "Permanent until dispelled";
            case INDEFINITE -> "Lasts until removed";
        };
    }
}
