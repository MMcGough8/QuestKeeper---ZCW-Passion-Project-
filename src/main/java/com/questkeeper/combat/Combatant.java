package com.questkeeper.combat;

/**
 * Interface for any entity that can participate in combat.
 * 
 * Implemented by Character (player) and Monster (enemy) classes.
 * Provides common combat operations for the Combat controller.
 * 
 * @author Marc McGough
 * @version 1.0
 */

public interface Combatant {

    String getName();

    int getCurrentHitPoints();

    int getMaxHitPoints();

    int getArmorClass();

    int takeDamage(int amount);

    int heal(int amount); 

    default boolean isAlive() {
        return getCurrentHitPoints() > 0;
    }

    default boolean isUnconscious() {
        return getCurrentHitPoints() <= 0;
    }

    default boolean isBloodied() {
        return getCurrentHitPoints() <= getMaxHitPoints() / 2;
    }

    int getInitiativeModifier();

    int rollInitiative();

    default String getCombatStatus() {
        String status = isBloodied() ? " [BLOODIED]" : "";
        if (isUnconscious()) {
            status = " [DOWN]";
        }
        return String.format("%s (HP: %d/%d)%s", 
                getName(), getCurrentHitPoints(), getMaxHitPoints(), status);
    }

    default int getHpPercentage() { 
        if (getMaxHitPoints() <= 0) return 0;
        return (int) ((double) getCurrentHitPoints() / getMaxHitPoints() * 100);
    }
}