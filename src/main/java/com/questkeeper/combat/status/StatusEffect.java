package com.questkeeper.combat.status;

import com.questkeeper.character.Character.Ability;
import com.questkeeper.combat.Combatant;

/**
 * Interface defining the contract for status effects in combat.
 *
 * Status effects represent conditions, debuffs, and other temporary
 * modifiers that affect combatants. Implementations handle their own
 * duration tracking, saving throws, and mechanical effects.
 *
 * @author Marc McGough
 * @version 1.0
 */
public interface StatusEffect {

    // ==========================================
    // Identity
    // ==========================================

    /**
     * Returns a unique identifier for this effect.
     * Used for tracking and removing specific effects.
     */
    String getId();

    /**
     * Returns the display name of this effect.
     */
    String getName();

    /**
     * Returns a description of what this effect does.
     */
    String getDescription();

    // ==========================================
    // Duration
    // ==========================================

    /**
     * Returns how this effect's duration is tracked.
     */
    DurationType getDurationType();

    /**
     * Returns the remaining duration in rounds (if applicable).
     * Returns -1 for effects that don't use round counters.
     */
    int getRemainingDuration();

    /**
     * Returns true if this effect has expired and should be removed.
     */
    boolean isExpired();

    /**
     * Decrements the duration counter by one round.
     * Only meaningful for effects with ROUNDS duration type.
     */
    void decrementDuration();

    /**
     * Marks this effect as expired, causing it to be removed.
     */
    void expire();

    // ==========================================
    // Turn Processing
    // ==========================================

    /**
     * Called at the start of the affected combatant's turn.
     * Can apply ongoing damage, check for expiration, etc.
     *
     * @param affected the combatant with this effect
     * @return a message describing any effects that occurred, or null
     */
    String onTurnStart(Combatant affected);

    /**
     * Called at the end of the affected combatant's turn.
     * Typically where saving throws are attempted and durations tick down.
     *
     * @param affected the combatant with this effect
     * @return a message describing any effects that occurred, or null
     */
    String onTurnEnd(Combatant affected);

    // ==========================================
    // Saving Throws
    // ==========================================

    /**
     * Returns true if this effect allows saving throws to end it.
     */
    boolean allowsSavingThrow();

    /**
     * Returns the ability used for saving throws against this effect.
     * Returns null if no saving throw is allowed.
     */
    Ability getSavingThrowAbility();

    /**
     * Returns the DC for saving throws against this effect.
     * Returns 0 if no saving throw is allowed.
     */
    int getSaveDC();

    /**
     * Attempts a saving throw to end this effect.
     *
     * @param affected the combatant attempting to save
     * @return true if the save succeeded and the effect should end
     */
    boolean attemptSave(Combatant affected);

    // ==========================================
    // Mechanical Effects
    // ==========================================

    /**
     * Returns true if a creature with this effect has advantage on attacks.
     */
    boolean grantsAdvantageOnAttacks();

    /**
     * Returns true if a creature with this effect has disadvantage on attacks.
     */
    boolean causesDisadvantageOnAttacks();

    /**
     * Returns true if attacks against a creature with this effect have advantage.
     */
    boolean grantsAdvantageAgainst();

    /**
     * Returns true if this effect prevents the creature from taking actions.
     */
    boolean preventsActions();

    /**
     * Returns true if this effect prevents the creature from moving.
     */
    boolean preventsMovement();

    /**
     * Returns true if melee attacks that hit are automatic critical hits.
     */
    boolean meleeCritsOnHit();

    /**
     * Returns true if the affected creature automatically fails STR/DEX saves.
     */
    boolean autoFailsStrDexSaves();

    // ==========================================
    // Source Tracking
    // ==========================================

    /**
     * Returns the combatant that applied this effect, if any.
     * May be null for environmental or trap effects.
     */
    Combatant getSource();

    /**
     * Sets the source of this effect.
     */
    void setSource(Combatant source);

    // ==========================================
    // Condition Query
    // ==========================================

    /**
     * Returns the D&D 5e condition this effect applies, if any.
     * Returns null for effects that don't map to standard conditions.
     */
    Condition getCondition();
}
