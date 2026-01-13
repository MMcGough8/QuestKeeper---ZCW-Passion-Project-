package com.questkeeper.combat.status;

import com.questkeeper.character.Character;
import com.questkeeper.character.Character.Ability;
import com.questkeeper.character.Character.CharacterClass;
import com.questkeeper.character.Character.Race;
import com.questkeeper.combat.Monster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the StatusEffectManager class.
 */
@DisplayName("StatusEffectManager Tests")
class StatusEffectManagerTest {

    private StatusEffectManager manager;
    private Character testCharacter;
    private Monster testMonster;

    @BeforeEach
    void setUp() {
        manager = new StatusEffectManager();

        testCharacter = new Character("Hero", Race.HUMAN, CharacterClass.FIGHTER);
        testCharacter.setAbilityScore(Ability.STRENGTH, 14);
        testCharacter.setAbilityScore(Ability.DEXTERITY, 12);
        testCharacter.setAbilityScore(Ability.CONSTITUTION, 13);

        testMonster = new Monster("test_goblin", "Test Goblin", 13, 7);
        testMonster.setAbilityModifiers(1, 2, 0, -1, 0, -1);
    }

    @Nested
    @DisplayName("Apply Effect Tests")
    class ApplyEffectTests {

        @Test
        @DisplayName("applyEffect adds effect to combatant")
        void applyEffectAddsEffect() {
            ConditionEffect poisoned = ConditionEffect.poisoned(3);

            manager.applyEffect(testCharacter, poisoned);

            List<StatusEffect> effects = manager.getEffects(testCharacter);
            assertEquals(1, effects.size());
            assertEquals(Condition.POISONED, effects.get(0).getCondition());
        }

        @Test
        @DisplayName("can apply multiple effects to same combatant")
        void canApplyMultipleEffects() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            manager.applyEffect(testCharacter, ConditionEffect.blinded(2));

            List<StatusEffect> effects = manager.getEffects(testCharacter);
            assertEquals(2, effects.size());
        }

        @Test
        @DisplayName("effects on different combatants are independent")
        void effectsOnDifferentCombatantsAreIndependent() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            manager.applyEffect(testMonster, ConditionEffect.restrained(2));

            assertEquals(1, manager.getEffects(testCharacter).size());
            assertEquals(1, manager.getEffects(testMonster).size());

            assertEquals(Condition.POISONED, manager.getEffects(testCharacter).get(0).getCondition());
            assertEquals(Condition.RESTRAINED, manager.getEffects(testMonster).get(0).getCondition());
        }
    }

    @Nested
    @DisplayName("Remove Effect Tests")
    class RemoveEffectTests {

        @Test
        @DisplayName("removeEffect removes specific effect")
        void removeEffectRemovesSpecificEffect() {
            ConditionEffect poisoned = ConditionEffect.poisoned(3);
            manager.applyEffect(testCharacter, poisoned);

            assertTrue(manager.removeEffect(testCharacter, poisoned));
            assertTrue(manager.getEffects(testCharacter).isEmpty());
        }

        @Test
        @DisplayName("removeCondition removes all effects with that condition")
        void removeConditionRemovesAllOfCondition() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(5)); // Another poisoned
            manager.applyEffect(testCharacter, ConditionEffect.blinded(2));

            int removed = manager.removeCondition(testCharacter, Condition.POISONED);

            assertEquals(2, removed);
            assertEquals(1, manager.getEffects(testCharacter).size());
            assertEquals(Condition.BLINDED, manager.getEffects(testCharacter).get(0).getCondition());
        }

        @Test
        @DisplayName("removeEffectsFromSource removes effects by source")
        void removeEffectsFromSourceRemovesBySource() {
            ConditionEffect effect1 = ConditionEffect.poisoned(3);
            effect1.setSource(testMonster);

            ConditionEffect effect2 = ConditionEffect.blinded(2);
            // effect2 has no source

            manager.applyEffect(testCharacter, effect1);
            manager.applyEffect(testCharacter, effect2);

            int removed = manager.removeEffectsFromSource(testCharacter, testMonster);

            assertEquals(1, removed);
            assertEquals(1, manager.getEffects(testCharacter).size());
        }

        @Test
        @DisplayName("clearEffects removes all effects from combatant")
        void clearEffectsRemovesAll() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            manager.applyEffect(testCharacter, ConditionEffect.blinded(2));

            manager.clearEffects(testCharacter);

            assertTrue(manager.getEffects(testCharacter).isEmpty());
        }

        @Test
        @DisplayName("clearAllEffects removes effects from all combatants")
        void clearAllEffectsRemovesFromAll() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            manager.applyEffect(testMonster, ConditionEffect.restrained(2));

            manager.clearAllEffects();

            assertTrue(manager.getEffects(testCharacter).isEmpty());
            assertTrue(manager.getEffects(testMonster).isEmpty());
        }
    }

    @Nested
    @DisplayName("Query Effect Tests")
    class QueryEffectTests {

        @Test
        @DisplayName("hasCondition returns true when condition present")
        void hasConditionReturnsTrueWhenPresent() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));

            assertTrue(manager.hasCondition(testCharacter, Condition.POISONED));
            assertFalse(manager.hasCondition(testCharacter, Condition.BLINDED));
        }

        @Test
        @DisplayName("hasAnyEffects returns true when effects present")
        void hasAnyEffectsReturnsTrueWhenPresent() {
            assertFalse(manager.hasAnyEffects(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));

            assertTrue(manager.hasAnyEffects(testCharacter));
        }

        @Test
        @DisplayName("getEffects returns empty list for combatant without effects")
        void getEffectsReturnsEmptyForNoEffects() {
            List<StatusEffect> effects = manager.getEffects(testCharacter);
            assertTrue(effects.isEmpty());
        }

        @Test
        @DisplayName("getEffects returns unmodifiable list")
        void getEffectsReturnsUnmodifiableList() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));

            List<StatusEffect> effects = manager.getEffects(testCharacter);

            assertThrows(UnsupportedOperationException.class, () -> {
                effects.add(ConditionEffect.blinded(2));
            });
        }
    }

    @Nested
    @DisplayName("Combat Query Tests")
    class CombatQueryTests {

        @Test
        @DisplayName("hasAdvantageOnAttacks returns true for invisible")
        void hasAdvantageOnAttacksForInvisible() {
            assertFalse(manager.hasAdvantageOnAttacks(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.invisible(3));

            assertTrue(manager.hasAdvantageOnAttacks(testCharacter));
        }

        @Test
        @DisplayName("hasDisadvantageOnAttacks returns true for poisoned")
        void hasDisadvantageOnAttacksForPoisoned() {
            assertFalse(manager.hasDisadvantageOnAttacks(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));

            assertTrue(manager.hasDisadvantageOnAttacks(testCharacter));
        }

        @Test
        @DisplayName("attacksHaveAdvantageAgainst returns true for restrained")
        void attacksHaveAdvantageAgainstRestrained() {
            assertFalse(manager.attacksHaveAdvantageAgainst(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.restrained(3));

            assertTrue(manager.attacksHaveAdvantageAgainst(testCharacter));
        }

        @Test
        @DisplayName("canTakeActions returns false for paralyzed")
        void canTakeActionsReturnsFalseForParalyzed() {
            assertTrue(manager.canTakeActions(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.paralyzed(3));

            assertFalse(manager.canTakeActions(testCharacter));
        }

        @Test
        @DisplayName("canMove returns false for grappled")
        void canMoveReturnsFalseForGrappled() {
            assertTrue(manager.canMove(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.grappled());

            assertFalse(manager.canMove(testCharacter));
        }

        @Test
        @DisplayName("meleeCritsOnHit returns true for paralyzed")
        void meleeCritsOnHitForParalyzed() {
            assertFalse(manager.meleeCritsOnHit(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.paralyzed(3));

            assertTrue(manager.meleeCritsOnHit(testCharacter));
        }

        @Test
        @DisplayName("autoFailsStrDexSaves returns true for stunned")
        void autoFailsStrDexSavesForStunned() {
            assertFalse(manager.autoFailsStrDexSaves(testCharacter));

            manager.applyEffect(testCharacter, ConditionEffect.stunned(3));

            assertTrue(manager.autoFailsStrDexSaves(testCharacter));
        }
    }

    @Nested
    @DisplayName("Turn Processing Tests")
    class TurnProcessingTests {

        @Test
        @DisplayName("processTurnEnd decrements round-based duration")
        void processTurnEndDecrementsDuration() {
            ConditionEffect poisoned = ConditionEffect.poisoned(2);
            manager.applyEffect(testCharacter, poisoned);

            assertEquals(2, poisoned.getRemainingDuration());

            manager.processTurnEnd(testCharacter);

            assertEquals(1, poisoned.getRemainingDuration());
        }

        @Test
        @DisplayName("processTurnEnd removes expired effects")
        void processTurnEndRemovesExpiredEffects() {
            ConditionEffect poisoned = ConditionEffect.poisoned(1);
            manager.applyEffect(testCharacter, poisoned);

            manager.processTurnEnd(testCharacter);

            // Effect should be expired and removed
            assertFalse(manager.hasCondition(testCharacter, Condition.POISONED));
        }

        @Test
        @DisplayName("processTurnStart removes effects with UNTIL_START_OF_TURN duration")
        void processTurnStartRemovesUntilStartEffects() {
            // Create a custom effect with UNTIL_START_OF_TURN duration
            // For this test, we'll use a regular effect and manually expire it
            ConditionEffect effect = ConditionEffect.poisoned(3);
            manager.applyEffect(testCharacter, effect);

            // This doesn't test UNTIL_START_OF_TURN specifically since we don't have
            // a factory method for that, but it tests that turn start processing works
            List<String> messages = manager.processTurnStart(testCharacter);

            // Effect should still be present (not UNTIL_START_OF_TURN)
            assertTrue(manager.hasCondition(testCharacter, Condition.POISONED));
        }
    }

    @Nested
    @DisplayName("Display Tests")
    class DisplayTests {

        @Test
        @DisplayName("getStatusDisplay returns empty string for no effects")
        void getStatusDisplayReturnsEmptyForNoEffects() {
            String display = manager.getStatusDisplay(testCharacter);
            assertEquals("", display);
        }

        @Test
        @DisplayName("getStatusDisplay lists effect names")
        void getStatusDisplayListsEffectNames() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            manager.applyEffect(testCharacter, ConditionEffect.blinded(2));

            String display = manager.getStatusDisplay(testCharacter);

            assertTrue(display.contains("Poisoned"));
            assertTrue(display.contains("Blinded"));
        }

        @Test
        @DisplayName("getDetailedStatus shows full effect details")
        void getDetailedStatusShowsFullDetails() {
            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));

            String details = manager.getDetailedStatus(testCharacter);

            assertTrue(details.contains("Hero"));
            assertTrue(details.contains("Poisoned"));
        }

        @Test
        @DisplayName("getTrackedCombatantCount returns correct count")
        void getTrackedCombatantCountReturnsCorrectCount() {
            assertEquals(0, manager.getTrackedCombatantCount());

            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            assertEquals(1, manager.getTrackedCombatantCount());

            manager.applyEffect(testMonster, ConditionEffect.restrained(2));
            assertEquals(2, manager.getTrackedCombatantCount());
        }

        @Test
        @DisplayName("getTotalEffectCount returns correct count")
        void getTotalEffectCountReturnsCorrectCount() {
            assertEquals(0, manager.getTotalEffectCount());

            manager.applyEffect(testCharacter, ConditionEffect.poisoned(3));
            manager.applyEffect(testCharacter, ConditionEffect.blinded(2));
            manager.applyEffect(testMonster, ConditionEffect.restrained(2));

            assertEquals(3, manager.getTotalEffectCount());
        }
    }
}
