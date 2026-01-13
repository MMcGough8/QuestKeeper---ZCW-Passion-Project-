package com.questkeeper.combat.status;

import com.questkeeper.character.Character;
import com.questkeeper.character.Character.Ability;
import com.questkeeper.combat.Combatant;
import com.questkeeper.combat.Monster;
import com.questkeeper.core.Dice;

/**
 * Base implementation of StatusEffect with common duration and save handling.
 *
 * Subclasses should override the mechanical effect methods to define
 * what the effect actually does. This class handles the bookkeeping
 * of duration tracking and saving throw mechanics.
 *
 * @author Marc McGough
 * @version 1.0
 */
public abstract class AbstractStatusEffect implements StatusEffect {

    private final String id;
    private final String name;
    private final String description;
    private final DurationType durationType;

    private int remainingDuration;
    private boolean expired;
    private Combatant source;

    // Saving throw properties
    private final Ability savingThrowAbility;
    private final int saveDC;

    /**
     * Creates a status effect with a fixed duration in rounds.
     */
    protected AbstractStatusEffect(String id, String name, String description, int durationRounds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationType = DurationType.ROUNDS;
        this.remainingDuration = durationRounds;
        this.expired = false;
        this.savingThrowAbility = null;
        this.saveDC = 0;
    }

    /**
     * Creates a status effect that lasts until a saving throw succeeds.
     */
    protected AbstractStatusEffect(String id, String name, String description,
                                   Ability savingThrowAbility, int saveDC) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationType = DurationType.UNTIL_SAVE;
        this.remainingDuration = -1;
        this.expired = false;
        this.savingThrowAbility = savingThrowAbility;
        this.saveDC = saveDC;
    }

    /**
     * Creates a status effect with a specific duration type.
     */
    protected AbstractStatusEffect(String id, String name, String description,
                                   DurationType durationType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationType = durationType;
        this.remainingDuration = durationType == DurationType.ROUNDS ? 1 : -1;
        this.expired = false;
        this.savingThrowAbility = null;
        this.saveDC = 0;
    }

    /**
     * Creates a status effect with a specific duration type and saving throw.
     */
    protected AbstractStatusEffect(String id, String name, String description,
                                   DurationType durationType, Ability savingThrowAbility, int saveDC) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationType = durationType;
        this.remainingDuration = durationType == DurationType.ROUNDS ? 1 : -1;
        this.expired = false;
        this.savingThrowAbility = savingThrowAbility;
        this.saveDC = saveDC;
    }

    // ==========================================
    // Identity
    // ==========================================

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    // ==========================================
    // Duration
    // ==========================================

    @Override
    public DurationType getDurationType() {
        return durationType;
    }

    @Override
    public int getRemainingDuration() {
        return remainingDuration;
    }

    /**
     * Sets the remaining duration. Used for effects with ROUNDS duration type.
     */
    protected void setRemainingDuration(int rounds) {
        this.remainingDuration = rounds;
    }

    @Override
    public boolean isExpired() {
        if (expired) {
            return true;
        }
        if (durationType == DurationType.ROUNDS && remainingDuration <= 0) {
            return true;
        }
        return false;
    }

    @Override
    public void decrementDuration() {
        if (durationType == DurationType.ROUNDS && remainingDuration > 0) {
            remainingDuration--;
        }
    }

    @Override
    public void expire() {
        this.expired = true;
    }

    // ==========================================
    // Turn Processing
    // ==========================================

    @Override
    public String onTurnStart(Combatant affected) {
        if (durationType == DurationType.UNTIL_START_OF_TURN) {
            expire();
            return String.format("%s effect on %s has ended.", name, affected.getName());
        }
        return null;
    }

    @Override
    public String onTurnEnd(Combatant affected) {
        StringBuilder message = new StringBuilder();

        // Try saving throw if applicable
        if (allowsSavingThrow()) {
            boolean saved = attemptSave(affected);
            if (saved) {
                expire();
                message.append(String.format("%s makes their %s save and shakes off %s!",
                    affected.getName(), savingThrowAbility.getAbbreviation(), name));
                return message.toString();
            } else {
                message.append(String.format("%s fails their %s save against %s.",
                    affected.getName(), savingThrowAbility.getAbbreviation(), name));
            }
        }

        // Decrement duration for round-based effects
        if (durationType == DurationType.ROUNDS) {
            decrementDuration();
            if (isExpired()) {
                if (message.length() > 0) message.append(" ");
                message.append(String.format("%s effect on %s has worn off.", name, affected.getName()));
            }
        }

        // Handle end-of-turn expiration
        if (durationType == DurationType.UNTIL_END_OF_TURN) {
            expire();
            if (message.length() > 0) message.append(" ");
            message.append(String.format("%s effect on %s has ended.", name, affected.getName()));
        }

        return message.length() > 0 ? message.toString() : null;
    }

    // ==========================================
    // Saving Throws
    // ==========================================

    @Override
    public boolean allowsSavingThrow() {
        return savingThrowAbility != null && saveDC > 0;
    }

    @Override
    public Ability getSavingThrowAbility() {
        return savingThrowAbility;
    }

    @Override
    public int getSaveDC() {
        return saveDC;
    }

    @Override
    public boolean attemptSave(Combatant affected) {
        if (!allowsSavingThrow()) {
            return false;
        }

        int saveModifier = getSaveModifier(affected, savingThrowAbility);
        return Dice.checkAgainstDC(saveModifier, saveDC);
    }

    /**
     * Gets the saving throw modifier for a combatant.
     * Handles both Character and Monster combatants.
     */
    protected int getSaveModifier(Combatant combatant, Ability ability) {
        if (combatant instanceof Character character) {
            return character.getSavingThrowModifier(ability);
        } else if (combatant instanceof Monster monster) {
            // Monsters use their ability modifier directly
            return switch (ability) {
                case STRENGTH -> monster.getStrengthMod();
                case DEXTERITY -> monster.getDexterityMod();
                case CONSTITUTION -> monster.getConstitutionMod();
                case INTELLIGENCE -> monster.getIntelligenceMod();
                case WISDOM -> monster.getWisdomMod();
                case CHARISMA -> monster.getCharismaMod();
            };
        }
        return 0;
    }

    // ==========================================
    // Mechanical Effects (default implementations)
    // ==========================================

    @Override
    public boolean grantsAdvantageOnAttacks() {
        return false;
    }

    @Override
    public boolean causesDisadvantageOnAttacks() {
        return false;
    }

    @Override
    public boolean grantsAdvantageAgainst() {
        return false;
    }

    @Override
    public boolean preventsActions() {
        return false;
    }

    @Override
    public boolean preventsMovement() {
        return false;
    }

    @Override
    public boolean meleeCritsOnHit() {
        return false;
    }

    @Override
    public boolean autoFailsStrDexSaves() {
        return false;
    }

    // ==========================================
    // Source Tracking
    // ==========================================

    @Override
    public Combatant getSource() {
        return source;
    }

    @Override
    public void setSource(Combatant source) {
        this.source = source;
    }

    // ==========================================
    // Condition Query
    // ==========================================

    @Override
    public Condition getCondition() {
        return null; // Override in ConditionEffect
    }

    // ==========================================
    // Display
    // ==========================================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);

        if (durationType == DurationType.ROUNDS && remainingDuration > 0) {
            sb.append(String.format(" (%d rounds)", remainingDuration));
        } else if (durationType == DurationType.UNTIL_SAVE) {
            sb.append(String.format(" (DC %d %s)", saveDC, savingThrowAbility.getAbbreviation()));
        }

        return sb.toString();
    }
}
