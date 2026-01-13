package com.questkeeper.combat.status;

/**
 * D&D 5e conditions with their mechanical effects.
 *
 * Each condition has specific effects on combat that can be queried
 * through the boolean methods. This allows the combat system to
 * easily determine how conditions affect attacks, saves, and movement.
 *
 * @author Marc McGough
 * @version 1.0
 */
public enum Condition {

    /**
     * A blinded creature can't see and automatically fails any ability check
     * that requires sight. Attack rolls against the creature have advantage,
     * and the creature's attack rolls have disadvantage.
     */
    BLINDED,

    /**
     * A charmed creature can't attack the charmer or target the charmer with
     * harmful abilities or magical effects. The charmer has advantage on any
     * ability check to interact socially with the creature.
     */
    CHARMED,

    /**
     * A deafened creature can't hear and automatically fails any ability check
     * that requires hearing.
     */
    DEAFENED,

    /**
     * A frightened creature has disadvantage on ability checks and attack rolls
     * while the source of its fear is within line of sight. The creature can't
     * willingly move closer to the source of its fear.
     */
    FRIGHTENED,

    /**
     * A grappled creature's speed becomes 0, and it can't benefit from any
     * bonus to its speed. The condition ends if the grappler is incapacitated
     * or if an effect removes the grappled creature from the grappler's reach.
     */
    GRAPPLED,

    /**
     * An incapacitated creature can't take actions or reactions.
     */
    INCAPACITATED,

    /**
     * An invisible creature is impossible to see without magic or special sense.
     * Attack rolls against the creature have disadvantage, and the creature's
     * attack rolls have advantage.
     */
    INVISIBLE,

    /**
     * A paralyzed creature is incapacitated and can't move or speak. The creature
     * automatically fails Strength and Dexterity saving throws. Attack rolls
     * against the creature have advantage. Any attack that hits the creature is
     * a critical hit if the attacker is within 5 feet of the creature.
     */
    PARALYZED,

    /**
     * A petrified creature is transformed into a solid inanimate substance.
     * It is incapacitated, can't move or speak, and is unaware of its surroundings.
     * Attack rolls against the creature have advantage. The creature automatically
     * fails Strength and Dexterity saving throws. It has resistance to all damage.
     */
    PETRIFIED,

    /**
     * A poisoned creature has disadvantage on attack rolls and ability checks.
     */
    POISONED,

    /**
     * A prone creature's only movement option is to crawl. The creature has
     * disadvantage on attack rolls. An attack roll against the creature has
     * advantage if the attacker is within 5 feet; otherwise disadvantage.
     */
    PRONE,

    /**
     * A restrained creature's speed becomes 0. Attack rolls against the creature
     * have advantage, and the creature's attack rolls have disadvantage.
     * The creature has disadvantage on Dexterity saving throws.
     */
    RESTRAINED,

    /**
     * A stunned creature is incapacitated, can't move, and can speak only falteringly.
     * The creature automatically fails Strength and Dexterity saving throws.
     * Attack rolls against the creature have advantage.
     */
    STUNNED,

    /**
     * An unconscious creature is incapacitated, can't move or speak, and is unaware
     * of its surroundings. The creature drops whatever it's holding and falls prone.
     * Attack rolls against the creature have advantage. Any attack that hits the
     * creature is a critical hit if the attacker is within 5 feet.
     */
    UNCONSCIOUS;

    // ==========================================
    // Mechanical Query Methods
    // ==========================================

    /**
     * Returns true if this condition causes the creature to be incapacitated.
     * Incapacitated creatures can't take actions or reactions.
     */
    public boolean causesIncapacitated() {
        return this == INCAPACITATED ||
               this == PARALYZED ||
               this == PETRIFIED ||
               this == STUNNED ||
               this == UNCONSCIOUS;
    }

    /**
     * Returns true if attacks against a creature with this condition have advantage.
     */
    public boolean grantsAdvantageOnAttacksAgainst() {
        return this == BLINDED ||
               this == PARALYZED ||
               this == PETRIFIED ||
               this == RESTRAINED ||
               this == STUNNED ||
               this == UNCONSCIOUS;
    }

    /**
     * Returns true if a creature with this condition has disadvantage on attacks.
     */
    public boolean causesDisadvantageOnAttacks() {
        return this == BLINDED ||
               this == FRIGHTENED ||
               this == POISONED ||
               this == PRONE ||
               this == RESTRAINED;
    }

    /**
     * Returns true if a creature with this condition has advantage on attacks.
     */
    public boolean grantsAdvantageOnAttacks() {
        return this == INVISIBLE;
    }

    /**
     * Returns true if this condition prevents movement.
     */
    public boolean preventsMovement() {
        return this == GRAPPLED ||
               this == PARALYZED ||
               this == PETRIFIED ||
               this == RESTRAINED ||
               this == STUNNED ||
               this == UNCONSCIOUS;
    }

    /**
     * Returns true if a creature with this condition automatically fails
     * Strength and Dexterity saving throws.
     */
    public boolean autoFailsStrDexSaves() {
        return this == PARALYZED ||
               this == PETRIFIED ||
               this == STUNNED ||
               this == UNCONSCIOUS;
    }

    /**
     * Returns true if melee attacks that hit a creature with this condition
     * are automatic critical hits.
     */
    public boolean meleeCritsOnHit() {
        return this == PARALYZED ||
               this == UNCONSCIOUS;
    }

    /**
     * Returns true if this condition prevents the creature from speaking.
     */
    public boolean preventsSpeech() {
        return this == PARALYZED ||
               this == PETRIFIED ||
               this == UNCONSCIOUS;
    }

    /**
     * Returns true if a creature with this condition has disadvantage on
     * Dexterity saving throws (beyond auto-fail).
     */
    public boolean causesDisadvantageOnDexSaves() {
        return this == RESTRAINED;
    }

    /**
     * Returns true if a creature with this condition has disadvantage on
     * ability checks.
     */
    public boolean causesDisadvantageOnAbilityChecks() {
        return this == FRIGHTENED ||
               this == POISONED;
    }

    /**
     * Returns a human-readable description of this condition's effects.
     */
    public String getDescription() {
        return switch (this) {
            case BLINDED -> "Can't see. Attacks against have advantage, attacks made have disadvantage.";
            case CHARMED -> "Can't attack or target the charmer with harmful effects.";
            case DEAFENED -> "Can't hear. Auto-fails hearing-based checks.";
            case FRIGHTENED -> "Disadvantage on attacks and ability checks while source is visible.";
            case GRAPPLED -> "Speed is 0. Can't benefit from speed bonuses.";
            case INCAPACITATED -> "Can't take actions or reactions.";
            case INVISIBLE -> "Can't be seen. Attacks against have disadvantage, attacks made have advantage.";
            case PARALYZED -> "Incapacitated, can't move or speak. Auto-fails STR/DEX saves. Attacks against have advantage, melee hits are crits.";
            case PETRIFIED -> "Transformed to stone. Incapacitated, can't move or speak. Auto-fails STR/DEX saves. Attacks against have advantage.";
            case POISONED -> "Disadvantage on attack rolls and ability checks.";
            case PRONE -> "Must crawl to move. Disadvantage on attacks. Melee attacks against have advantage.";
            case RESTRAINED -> "Speed is 0. Disadvantage on attacks and DEX saves. Attacks against have advantage.";
            case STUNNED -> "Incapacitated, can't move. Auto-fails STR/DEX saves. Attacks against have advantage.";
            case UNCONSCIOUS -> "Incapacitated, can't move or speak. Falls prone. Auto-fails STR/DEX saves. Attacks against have advantage, melee hits are crits.";
        };
    }

    /**
     * Returns the condition name in a display-friendly format.
     */
    public String getDisplayName() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
