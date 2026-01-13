package com.questkeeper.combat.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Condition enum.
 */
@DisplayName("Condition Enum Tests")
class ConditionTest {

    @Nested
    @DisplayName("Incapacitation Tests")
    class IncapacitationTests {

        @Test
        @DisplayName("PARALYZED causes incapacitation")
        void paralyzedCausesIncapacitation() {
            assertTrue(Condition.PARALYZED.causesIncapacitated());
        }

        @Test
        @DisplayName("STUNNED causes incapacitation")
        void stunnedCausesIncapacitation() {
            assertTrue(Condition.STUNNED.causesIncapacitated());
        }

        @Test
        @DisplayName("UNCONSCIOUS causes incapacitation")
        void unconsciousCausesIncapacitation() {
            assertTrue(Condition.UNCONSCIOUS.causesIncapacitated());
        }

        @Test
        @DisplayName("PETRIFIED causes incapacitation")
        void petrifiedCausesIncapacitation() {
            assertTrue(Condition.PETRIFIED.causesIncapacitated());
        }

        @Test
        @DisplayName("INCAPACITATED causes incapacitation")
        void incapacitatedCausesIncapacitation() {
            assertTrue(Condition.INCAPACITATED.causesIncapacitated());
        }

        @Test
        @DisplayName("POISONED does not cause incapacitation")
        void poisonedDoesNotCauseIncapacitation() {
            assertFalse(Condition.POISONED.causesIncapacitated());
        }

        @Test
        @DisplayName("RESTRAINED does not cause incapacitation")
        void restrainedDoesNotCauseIncapacitation() {
            assertFalse(Condition.RESTRAINED.causesIncapacitated());
        }
    }

    @Nested
    @DisplayName("Attack Advantage/Disadvantage Tests")
    class AttackAdvantageTests {

        @Test
        @DisplayName("BLINDED grants advantage on attacks against")
        void blindedGrantsAdvantageAgainst() {
            assertTrue(Condition.BLINDED.grantsAdvantageOnAttacksAgainst());
        }

        @Test
        @DisplayName("BLINDED causes disadvantage on attacks")
        void blindedCausesDisadvantageOnAttacks() {
            assertTrue(Condition.BLINDED.causesDisadvantageOnAttacks());
        }

        @Test
        @DisplayName("INVISIBLE grants advantage on attacks")
        void invisibleGrantsAdvantageOnAttacks() {
            assertTrue(Condition.INVISIBLE.grantsAdvantageOnAttacks());
        }

        @Test
        @DisplayName("RESTRAINED grants advantage on attacks against")
        void restrainedGrantsAdvantageAgainst() {
            assertTrue(Condition.RESTRAINED.grantsAdvantageOnAttacksAgainst());
        }

        @Test
        @DisplayName("RESTRAINED causes disadvantage on attacks")
        void restrainedCausesDisadvantageOnAttacks() {
            assertTrue(Condition.RESTRAINED.causesDisadvantageOnAttacks());
        }

        @Test
        @DisplayName("POISONED causes disadvantage on attacks")
        void poisonedCausesDisadvantageOnAttacks() {
            assertTrue(Condition.POISONED.causesDisadvantageOnAttacks());
        }

        @Test
        @DisplayName("FRIGHTENED causes disadvantage on attacks")
        void frightenedCausesDisadvantageOnAttacks() {
            assertTrue(Condition.FRIGHTENED.causesDisadvantageOnAttacks());
        }

        @Test
        @DisplayName("PRONE causes disadvantage on attacks")
        void proneCausesDisadvantageOnAttacks() {
            assertTrue(Condition.PRONE.causesDisadvantageOnAttacks());
        }
    }

    @Nested
    @DisplayName("Movement Prevention Tests")
    class MovementTests {

        @Test
        @DisplayName("GRAPPLED prevents movement")
        void grappledPreventsMovement() {
            assertTrue(Condition.GRAPPLED.preventsMovement());
        }

        @Test
        @DisplayName("RESTRAINED prevents movement")
        void restrainedPreventsMovement() {
            assertTrue(Condition.RESTRAINED.preventsMovement());
        }

        @Test
        @DisplayName("PARALYZED prevents movement")
        void paralyzedPreventsMovement() {
            assertTrue(Condition.PARALYZED.preventsMovement());
        }

        @Test
        @DisplayName("STUNNED prevents movement")
        void stunnedPreventsMovement() {
            assertTrue(Condition.STUNNED.preventsMovement());
        }

        @Test
        @DisplayName("POISONED does not prevent movement")
        void poisonedDoesNotPreventMovement() {
            assertFalse(Condition.POISONED.preventsMovement());
        }

        @Test
        @DisplayName("BLINDED does not prevent movement")
        void blindedDoesNotPreventMovement() {
            assertFalse(Condition.BLINDED.preventsMovement());
        }
    }

    @Nested
    @DisplayName("Auto-Fail Saves Tests")
    class AutoFailSavesTests {

        @Test
        @DisplayName("PARALYZED auto-fails STR/DEX saves")
        void paralyzedAutoFailsSaves() {
            assertTrue(Condition.PARALYZED.autoFailsStrDexSaves());
        }

        @Test
        @DisplayName("STUNNED auto-fails STR/DEX saves")
        void stunnedAutoFailsSaves() {
            assertTrue(Condition.STUNNED.autoFailsStrDexSaves());
        }

        @Test
        @DisplayName("UNCONSCIOUS auto-fails STR/DEX saves")
        void unconsciousAutoFailsSaves() {
            assertTrue(Condition.UNCONSCIOUS.autoFailsStrDexSaves());
        }

        @Test
        @DisplayName("PETRIFIED auto-fails STR/DEX saves")
        void petrifiedAutoFailsSaves() {
            assertTrue(Condition.PETRIFIED.autoFailsStrDexSaves());
        }

        @Test
        @DisplayName("RESTRAINED does not auto-fail saves")
        void restrainedDoesNotAutoFailSaves() {
            assertFalse(Condition.RESTRAINED.autoFailsStrDexSaves());
        }
    }

    @Nested
    @DisplayName("Auto-Crit Tests")
    class AutoCritTests {

        @Test
        @DisplayName("PARALYZED causes melee crits on hit")
        void paralyzedCausesMeleeCrits() {
            assertTrue(Condition.PARALYZED.meleeCritsOnHit());
        }

        @Test
        @DisplayName("UNCONSCIOUS causes melee crits on hit")
        void unconsciousCausesMeleeCrits() {
            assertTrue(Condition.UNCONSCIOUS.meleeCritsOnHit());
        }

        @Test
        @DisplayName("STUNNED does not cause melee crits")
        void stunnedDoesNotCauseMeleeCrits() {
            assertFalse(Condition.STUNNED.meleeCritsOnHit());
        }

        @Test
        @DisplayName("RESTRAINED does not cause melee crits")
        void restrainedDoesNotCauseMeleeCrits() {
            assertFalse(Condition.RESTRAINED.meleeCritsOnHit());
        }
    }

    @Nested
    @DisplayName("Display Tests")
    class DisplayTests {

        @Test
        @DisplayName("getDisplayName formats condition name correctly")
        void displayNameFormatsCorrectly() {
            assertEquals("Poisoned", Condition.POISONED.getDisplayName());
            assertEquals("Paralyzed", Condition.PARALYZED.getDisplayName());
            assertEquals("Unconscious", Condition.UNCONSCIOUS.getDisplayName());
        }

        @Test
        @DisplayName("getDescription returns non-empty string for all conditions")
        void allConditionsHaveDescriptions() {
            for (Condition condition : Condition.values()) {
                assertNotNull(condition.getDescription());
                assertFalse(condition.getDescription().isEmpty());
            }
        }
    }
}
