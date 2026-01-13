package com.questkeeper.combat;

import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a combat action.
 *
 * Contains attack rolls, damage dealt, and status information
 * for display by the UI layer. All rolls are shown transparently.
 *
 * @author Marc McGough
 * @version 1.0
 */
public class CombatResult {

    public enum Type {
        COMBAT_START,     // Combat initiated, initiative rolled
        TURN_START,       // A combatant's turn begins
        ATTACK_HIT,       // Attack landed
        ATTACK_MISS,      // Attack missed
        ENEMY_DEFEATED,   // Enemy HP <= 0
        PLAYER_DEFEATED,  // Player HP <= 0
        VICTORY,          // All enemies defeated
        FLED,             // Player escaped combat
        ERROR             // Invalid action or state
    }

    private final Type type;
    private final String message;
    private final Combatant attacker;
    private final Combatant defender;
    private final int attackRoll;
    private final int targetAC;
    private final int damageRoll;
    private final int xpGained;
    private final List<Combatant> turnOrder;

    private CombatResult(Type type, String message, Combatant attacker, Combatant defender,
                        int attackRoll, int targetAC, int damageRoll, int xpGained,
                        List<Combatant> turnOrder) {
        this.type = type;
        this.message = message;
        this.attacker = attacker;
        this.defender = defender;
        this.attackRoll = attackRoll;
        this.targetAC = targetAC;
        this.damageRoll = damageRoll;
        this.xpGained = xpGained;
        this.turnOrder = turnOrder != null ?
            Collections.unmodifiableList(turnOrder) : List.of();
    }

    // ==========================================
    // Factory Methods
    // ==========================================

    /**
     * Creates a combat start result with initiative order.
     */
    public static CombatResult combatStart(List<Combatant> turnOrder, String message) {
        return new CombatResult(Type.COMBAT_START, message, null, null,
            0, 0, 0, 0, turnOrder);
    }

    /**
     * Creates a turn start result.
     */
    public static CombatResult turnStart(Combatant combatant) {
        String message = String.format("%s's turn!", combatant.getName());
        return new CombatResult(Type.TURN_START, message, combatant, null,
            0, 0, 0, 0, null);
    }

    /**
     * Creates an attack hit result with full roll details.
     */
    public static CombatResult attackHit(Combatant attacker, Combatant defender,
                                         int attackRoll, int targetAC, int damage) {
        String message = String.format("%s attacks %s! [Roll: %d vs AC %d] HIT! [Damage: %d] %s",
            attacker.getName(), defender.getName(), attackRoll, targetAC, damage,
            defender.getCombatStatus());
        return new CombatResult(Type.ATTACK_HIT, message, attacker, defender,
            attackRoll, targetAC, damage, 0, null);
    }

    /**
     * Creates an attack miss result with roll details.
     */
    public static CombatResult attackMiss(Combatant attacker, Combatant defender,
                                          int attackRoll, int targetAC) {
        String message = String.format("%s attacks %s! [Roll: %d vs AC %d] MISS!",
            attacker.getName(), defender.getName(), attackRoll, targetAC);
        return new CombatResult(Type.ATTACK_MISS, message, attacker, defender,
            attackRoll, targetAC, 0, 0, null);
    }

    /**
     * Creates an enemy defeated result.
     */
    public static CombatResult enemyDefeated(Combatant enemy) {
        String message = String.format("%s has been defeated!", enemy.getName());
        return new CombatResult(Type.ENEMY_DEFEATED, message, null, enemy,
            0, 0, 0, 0, null);
    }

    /**
     * Creates a player defeated result.
     */
    public static CombatResult playerDefeated(Combatant player) {
        String message = String.format("%s has fallen...", player.getName());
        return new CombatResult(Type.PLAYER_DEFEATED, message, null, player,
            0, 0, 0, 0, null);
    }

    /**
     * Creates a victory result with XP gained.
     */
    public static CombatResult victory(int xpGained) {
        String message = String.format("Victory! You gained %d XP.", xpGained);
        return new CombatResult(Type.VICTORY, message, null, null,
            0, 0, 0, xpGained, null);
    }

    /**
     * Creates a fled result.
     */
    public static CombatResult fled() {
        return new CombatResult(Type.FLED, "You fled from combat!", null, null,
            0, 0, 0, 0, null);
    }

    /**
     * Creates an error result.
     */
    public static CombatResult error(String message) {
        return new CombatResult(Type.ERROR, message, null, null,
            0, 0, 0, 0, null);
    }

    // ==========================================
    // Accessors
    // ==========================================

    public Type getType() {
        return type;
    }

    public boolean isSuccess() {
        return type != Type.ERROR;
    }

    public boolean isError() {
        return type == Type.ERROR;
    }

    public boolean isCombatOver() {
        return type == Type.VICTORY || type == Type.PLAYER_DEFEATED || type == Type.FLED;
    }

    public String getMessage() {
        return message;
    }

    public Combatant getAttacker() {
        return attacker;
    }

    public Combatant getDefender() {
        return defender;
    }

    public int getAttackRoll() {
        return attackRoll;
    }

    public int getTargetAC() {
        return targetAC;
    }

    public int getDamageRoll() {
        return damageRoll;
    }

    public int getXpGained() {
        return xpGained;
    }

    public List<Combatant> getTurnOrder() {
        return turnOrder;
    }

    public boolean hasTurnOrder() {
        return !turnOrder.isEmpty();
    }

    // ==========================================
    // Display
    // ==========================================

    /**
     * Formats the result for display in the game UI.
     */
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(message);

        if (hasTurnOrder() && type == Type.COMBAT_START) {
            sb.append("\n\nInitiative Order:");
            for (int i = 0; i < turnOrder.size(); i++) {
                sb.append(String.format("\n  %d. %s", i + 1, turnOrder.get(i).getCombatStatus()));
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("CombatResult[type=%s, message=%s]",
            type,
            message != null ? message.substring(0, Math.min(50, message.length())) : "null");
    }
}
