package com.questkeeper.campaign;

import com.questkeeper.character.Character;
import com.questkeeper.character.Character.CharacterClass;
import com.questkeeper.character.Character.Race;
import com.questkeeper.character.Character.Skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Trial and MiniGame classes.
 *
 * @author Marc McGough
 * @version 1.0
 */
@DisplayName("Trial System")
class TrialTest {

    // ==========================================
    // MiniGame Tests
    // ==========================================

    @Nested
    @DisplayName("MiniGame")
    class MiniGameTests {

        @Nested
        @DisplayName("Construction")
        class ConstructionTests {

            @Test
            @DisplayName("should create mini-game with valid parameters")
            void createsWithValidParams() {
                MiniGame game = new MiniGame("mg_001", "Search the Room", MiniGame.Type.SEARCH);

                assertEquals("mg_001", game.getId());
                assertEquals("Search the Room", game.getName());
                assertEquals(MiniGame.Type.SEARCH, game.getType());
                assertFalse(game.isCompleted());
            }

            @Test
            @DisplayName("should create mini-game with description")
            void createsWithDescription() {
                MiniGame game = new MiniGame("mg_001", "Search", MiniGame.Type.SEARCH,
                        "Find the hidden clue");

                assertEquals("Find the hidden clue", game.getDescription());
            }

            @Test
            @DisplayName("should throw for null ID")
            void throwsForNullId() {
                assertThrows(IllegalArgumentException.class,
                        () -> new MiniGame(null, "Test", MiniGame.Type.SEARCH));
            }

            @Test
            @DisplayName("should throw for empty ID")
            void throwsForEmptyId() {
                assertThrows(IllegalArgumentException.class,
                        () -> new MiniGame("", "Test", MiniGame.Type.SEARCH));
            }

            @Test
            @DisplayName("should throw for whitespace ID")
            void throwsForWhitespaceId() {
                assertThrows(IllegalArgumentException.class,
                        () -> new MiniGame("   ", "Test", MiniGame.Type.SEARCH));
            }

            @Test
            @DisplayName("should throw for null name")
            void throwsForNullName() {
                assertThrows(IllegalArgumentException.class,
                        () -> new MiniGame("mg_001", null, MiniGame.Type.SEARCH));
            }

            @Test
            @DisplayName("should throw for empty name")
            void throwsForEmptyName() {
                assertThrows(IllegalArgumentException.class,
                        () -> new MiniGame("mg_001", "", MiniGame.Type.SEARCH));
            }

            @Test
            @DisplayName("should default to SKILL_CHECK when type is null")
            void defaultsToSkillCheck() {
                MiniGame game = new MiniGame("mg_001", "Test", null);
                assertEquals(MiniGame.Type.SKILL_CHECK, game.getType());
            }

            @Test
            @DisplayName("should trim ID and name")
            void trimsIdAndName() {
                MiniGame game = new MiniGame("  mg_001  ", "  Test Game  ", MiniGame.Type.SEARCH);
                assertEquals("mg_001", game.getId());
                assertEquals("Test Game", game.getName());
            }
        }

        @Nested
        @DisplayName("Setters")
        class SetterTests {

            private MiniGame game;

            @BeforeEach
            void setUp() {
                game = new MiniGame("mg_001", "Original", MiniGame.Type.SEARCH);
            }

            @Test
            @DisplayName("should update name")
            void updatesName() {
                game.setName("Updated Name");
                assertEquals("Updated Name", game.getName());
            }

            @Test
            @DisplayName("should ignore null name")
            void ignoresNullName() {
                game.setName(null);
                assertEquals("Original", game.getName());
            }

            @Test
            @DisplayName("should ignore empty name")
            void ignoresEmptyName() {
                game.setName("   ");
                assertEquals("Original", game.getName());
            }

            @Test
            @DisplayName("should update description")
            void updatesDescription() {
                game.setDescription("New description");
                assertEquals("New description", game.getDescription());
            }

            @Test
            @DisplayName("should handle null description")
            void handlesNullDescription() {
                game.setDescription("Something");
                game.setDescription(null);
                assertEquals("", game.getDescription());
            }

            @Test
            @DisplayName("should update type")
            void updatesType() {
                game.setType(MiniGame.Type.DECODE);
                assertEquals(MiniGame.Type.DECODE, game.getType());
            }

            @Test
            @DisplayName("should default type to SKILL_CHECK when set to null")
            void defaultsTypeToSkillCheck() {
                game.setType(null);
                assertEquals(MiniGame.Type.SKILL_CHECK, game.getType());
            }

            @Test
            @DisplayName("should update completion text")
            void updatesCompletionText() {
                game.setCompletionText("You did it!");
                assertEquals("You did it!", game.getCompletionText());
            }

            @Test
            @DisplayName("should default completion text when set to null")
            void defaultsCompletionText() {
                game.setCompletionText(null);
                assertEquals("Challenge completed!", game.getCompletionText());
            }

            @Test
            @DisplayName("should update failure text")
            void updatesFailureText() {
                game.setFailureText("You failed miserably.");
                assertEquals("You failed miserably.", game.getFailureText());
            }

            @Test
            @DisplayName("should default failure text when set to null")
            void defaultsFailureText() {
                game.setFailureText(null);
                assertEquals("You failed the challenge.", game.getFailureText());
            }
        }

        @Nested
        @DisplayName("Game Actions")
        class GameActionTests {

            private MiniGame game;

            @BeforeEach
            void setUp() {
                game = new MiniGame("mg_001", "Test", MiniGame.Type.SEARCH);
                game.setCompletionText("Success!");
                game.setFailureText("Failure!");
            }

            @Test
            @DisplayName("should complete and return completion text")
            void completesAndReturnsText() {
                String result = game.complete();

                assertTrue(game.isCompleted());
                assertEquals("Success!", result);
            }

            @Test
            @DisplayName("should fail and return failure text")
            void failsAndReturnsText() {
                game.complete(); // First complete it
                String result = game.fail();

                assertFalse(game.isCompleted());
                assertEquals("Failure!", result);
            }

            @Test
            @DisplayName("should reset to incomplete state")
            void resetsToIncomplete() {
                game.complete();
                assertTrue(game.isCompleted());

                game.reset();
                assertFalse(game.isCompleted());
            }
        }

        @Nested
        @DisplayName("Display")
        class DisplayTests {

            @Test
            @DisplayName("should return formatted display text")
            void returnsFormattedDisplay() {
                MiniGame game = new MiniGame("mg_001", "Find the Key", MiniGame.Type.SEARCH,
                        "Search the room for a hidden key");

                String display = game.getDisplayText();

                assertTrue(display.contains("[Search]"));
                assertTrue(display.contains("Find the Key"));
                assertTrue(display.contains("Search the room"));
                assertTrue(display.contains("Incomplete"));
            }

            @Test
            @DisplayName("should show COMPLETE status when completed")
            void showsCompleteStatus() {
                MiniGame game = new MiniGame("mg_001", "Test", MiniGame.Type.SEARCH);
                game.complete();

                String display = game.getDisplayText();
                assertTrue(display.contains("COMPLETE"));
            }
        }

        @Nested
        @DisplayName("Equality")
        class EqualityTests {

            @Test
            @DisplayName("should be equal by ID")
            void equalById() {
                MiniGame game1 = new MiniGame("mg_001", "Name1", MiniGame.Type.SEARCH);
                MiniGame game2 = new MiniGame("mg_001", "Name2", MiniGame.Type.DECODE);

                assertEquals(game1, game2);
                assertEquals(game1.hashCode(), game2.hashCode());
            }

            @Test
            @DisplayName("should not be equal with different IDs")
            void notEqualDifferentIds() {
                MiniGame game1 = new MiniGame("mg_001", "Same", MiniGame.Type.SEARCH);
                MiniGame game2 = new MiniGame("mg_002", "Same", MiniGame.Type.SEARCH);

                assertNotEquals(game1, game2);
            }

            @Test
            @DisplayName("should not be equal to null")
            void notEqualToNull() {
                MiniGame game = new MiniGame("mg_001", "Test", MiniGame.Type.SEARCH);
                assertNotEquals(null, game);
            }

            @Test
            @DisplayName("should not be equal to different type")
            void notEqualToDifferentType() {
                MiniGame game = new MiniGame("mg_001", "Test", MiniGame.Type.SEARCH);
                assertNotEquals("mg_001", game);
            }
        }

        @Nested
        @DisplayName("Type Enum")
        class TypeEnumTests {

            @Test
            @DisplayName("all types have display names")
            void allHaveDisplayNames() {
                for (MiniGame.Type type : MiniGame.Type.values()) {
                    assertNotNull(type.getDisplayName());
                    assertFalse(type.getDisplayName().isEmpty());
                }
            }

            @Test
            @DisplayName("all types have descriptions")
            void allHaveDescriptions() {
                for (MiniGame.Type type : MiniGame.Type.values()) {
                    assertNotNull(type.getDescription());
                    assertFalse(type.getDescription().isEmpty());
                }
            }

            @Test
            @DisplayName("should have expected types")
            void hasExpectedTypes() {
                assertEquals(10, MiniGame.Type.values().length);
                assertNotNull(MiniGame.Type.SEARCH);
                assertNotNull(MiniGame.Type.EXAMINE);
                assertNotNull(MiniGame.Type.DECODE);
                assertNotNull(MiniGame.Type.ALIGNMENT);
                assertNotNull(MiniGame.Type.TIMING);
                assertNotNull(MiniGame.Type.DIALOGUE);
                assertNotNull(MiniGame.Type.MECHANISM);
                assertNotNull(MiniGame.Type.CHOICE);
                assertNotNull(MiniGame.Type.COMBAT);
                assertNotNull(MiniGame.Type.SKILL_CHECK);
            }
        }

        @Nested
        @DisplayName("D&D Skill Check Fields")
        class SkillCheckFieldTests {

            private MiniGame game;

            @BeforeEach
            void setUp() {
                game = new MiniGame("mg_001", "Pick Lock", MiniGame.Type.SKILL_CHECK);
            }

            @Test
            @DisplayName("should have default DC of 10")
            void hasDefaultDc() {
                assertEquals(10, game.getDc());
            }

            @Test
            @DisplayName("should set and get hint")
            void setsAndGetsHint() {
                game.setHint("Look for a hidden keyhole");
                assertEquals("Look for a hidden keyhole", game.getHint());
            }

            @Test
            @DisplayName("should handle null hint")
            void handlesNullHint() {
                game.setHint(null);
                assertEquals("", game.getHint());
            }

            @Test
            @DisplayName("should set and get required skill")
            void setsAndGetsRequiredSkill() {
                game.setRequiredSkill(Skill.SLEIGHT_OF_HAND);
                assertEquals(Skill.SLEIGHT_OF_HAND, game.getRequiredSkill());
            }

            @Test
            @DisplayName("should set and get alternate skill")
            void setsAndGetsAlternateSkill() {
                game.setAlternateSkill(Skill.ATHLETICS);
                assertEquals(Skill.ATHLETICS, game.getAlternateSkill());
            }

            @Test
            @DisplayName("should set and get DC")
            void setsAndGetsDc() {
                game.setDc(15);
                assertEquals(15, game.getDc());
            }

            @Test
            @DisplayName("should clamp DC to minimum of 1")
            void clampsDcToMinimum() {
                game.setDc(0);
                assertEquals(1, game.getDc());

                game.setDc(-5);
                assertEquals(1, game.getDc());
            }

            @Test
            @DisplayName("should set and get reward")
            void setsAndGetsReward() {
                game.setReward("lockpick_set");
                assertEquals("lockpick_set", game.getReward());
            }

            @Test
            @DisplayName("should handle null reward")
            void handlesNullReward() {
                game.setReward(null);
                assertEquals("", game.getReward());
            }

            @Test
            @DisplayName("should set and get fail consequence")
            void setsAndGetsFailConsequence() {
                game.setFailConsequence("1d6 poison damage from trap");
                assertEquals("1d6 poison damage from trap", game.getFailConsequence());
            }

            @Test
            @DisplayName("should handle null fail consequence")
            void handlesNullFailConsequence() {
                game.setFailConsequence(null);
                assertEquals("", game.getFailConsequence());
            }

            @Test
            @DisplayName("should check if skill is valid approach")
            void checksValidApproach() {
                game.setRequiredSkill(Skill.SLEIGHT_OF_HAND);
                game.setAlternateSkill(Skill.INVESTIGATION);

                assertTrue(game.isValidApproach(Skill.SLEIGHT_OF_HAND));
                assertTrue(game.isValidApproach(Skill.INVESTIGATION));
                assertFalse(game.isValidApproach(Skill.ATHLETICS));
            }

            @Test
            @DisplayName("should check for alternate approach")
            void checksAlternateApproach() {
                game.setRequiredSkill(Skill.SLEIGHT_OF_HAND);
                assertFalse(game.hasAlternateApproach());

                game.setAlternateSkill(Skill.INVESTIGATION);
                assertTrue(game.hasAlternateApproach());
            }
        }

        @Nested
        @DisplayName("D&D Skill Check Evaluation")
        class SkillCheckEvaluationTests {

            private MiniGame game;
            private Character rogue;
            private Character fighter;

            @BeforeEach
            void setUp() {
                game = new MiniGame("mg_001", "Pick Lock", MiniGame.Type.SKILL_CHECK);
                game.setRequiredSkill(Skill.SLEIGHT_OF_HAND);
                game.setAlternateSkill(Skill.ATHLETICS);
                game.setDc(12);
                game.setReward("gold_key");
                game.setFailConsequence("1d4 damage from trap");
                game.setCompletionText("The lock clicks open!");
                game.setFailureText("The lock remains stubbornly closed.");

                // Create a rogue with high DEX for sleight of hand
                rogue = new Character("Shade", Race.HALFLING, CharacterClass.ROGUE);
                rogue.addSkillProficiency(Skill.SLEIGHT_OF_HAND);

                // Create a fighter with high STR for athletics
                fighter = new Character("Bjorn", Race.HUMAN, CharacterClass.FIGHTER);
                fighter.addSkillProficiency(Skill.ATHLETICS);
            }

            @Test
            @DisplayName("should throw for null character")
            void throwsForNullCharacter() {
                assertThrows(IllegalArgumentException.class,
                        () -> game.evaluate(null, "SLEIGHT_OF_HAND"));
            }

            @Test
            @DisplayName("should throw for null approach")
            void throwsForNullApproach() {
                assertThrows(IllegalArgumentException.class,
                        () -> game.evaluate(rogue, null));
            }

            @Test
            @DisplayName("should throw for empty approach")
            void throwsForEmptyApproach() {
                assertThrows(IllegalArgumentException.class,
                        () -> game.evaluate(rogue, ""));
            }

            @Test
            @DisplayName("should throw when no required skill set")
            void throwsWhenNoRequiredSkill() {
                MiniGame noSkillGame = new MiniGame("mg_002", "Test", MiniGame.Type.SKILL_CHECK);

                assertThrows(IllegalStateException.class,
                        () -> noSkillGame.evaluate(rogue, "SLEIGHT_OF_HAND"));
            }

            @Test
            @DisplayName("should throw for invalid skill name")
            void throwsForInvalidSkillName() {
                assertThrows(IllegalArgumentException.class,
                        () -> game.evaluate(rogue, "INVALID_SKILL"));
            }

            @Test
            @DisplayName("should throw for non-allowed skill")
            void throwsForNonAllowedSkill() {
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                        () -> game.evaluate(rogue, "PERCEPTION"));

                assertTrue(ex.getMessage().contains("not valid"));
            }

            @Test
            @DisplayName("should accept required skill")
            void acceptsRequiredSkill() {
                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");
                assertNotNull(result);
            }

            @Test
            @DisplayName("should accept alternate skill")
            void acceptsAlternateSkill() {
                MiniGame.EvaluationResult result = game.evaluate(fighter, "ATHLETICS");
                assertNotNull(result);
            }

            @Test
            @DisplayName("should accept lowercase skill name")
            void acceptsLowercaseSkill() {
                MiniGame.EvaluationResult result = game.evaluate(rogue, "sleight_of_hand");
                assertNotNull(result);
            }

            @Test
            @DisplayName("should return result with roll details")
            void returnsRollDetails() {
                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertNotNull(result.rollDescription());
                assertTrue(result.rollDescription().contains("d20"));
                assertTrue(result.rollDescription().contains("DC 12"));
                assertTrue(result.naturalRoll() >= 1 && result.naturalRoll() <= 20);
            }

            @Test
            @DisplayName("should mark as completed on success")
            void marksCompletedOnSuccess() {
                // Use very low DC to ensure success
                game.setDc(1);

                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertTrue(result.success());
                assertTrue(game.isCompleted());
            }

            @Test
            @DisplayName("should not mark as completed on failure")
            void doesNotMarkCompletedOnFailure() {
                // Use very high DC to ensure failure
                game.setDc(30);

                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertFalse(result.success());
                assertFalse(game.isCompleted());
            }

            @Test
            @DisplayName("should return reward on success")
            void returnsRewardOnSuccess() {
                game.setDc(1); // Ensure success

                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertTrue(result.hasReward());
                assertEquals("gold_key", result.reward());
                assertFalse(result.hasConsequence());
            }

            @Test
            @DisplayName("should return consequence on failure")
            void returnsConsequenceOnFailure() {
                game.setDc(30); // Ensure failure

                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertTrue(result.hasConsequence());
                assertEquals("1d4 damage from trap", result.consequence());
                assertFalse(result.hasReward());
            }

            @Test
            @DisplayName("should return success message")
            void returnsSuccessMessage() {
                game.setDc(1);

                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertEquals("The lock clicks open!", result.message());
            }

            @Test
            @DisplayName("should return failure message")
            void returnsFailureMessage() {
                game.setDc(30);

                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertEquals("The lock remains stubbornly closed.", result.message());
            }

            @RepeatedTest(10)
            @DisplayName("should produce valid roll values")
            void producesValidRolls() {
                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");

                assertTrue(result.naturalRoll() >= 1 && result.naturalRoll() <= 20,
                        "Natural roll should be between 1 and 20");
                assertTrue(result.totalRoll() >= result.naturalRoll(),
                        "Total roll should include modifier");
            }

            @Test
            @DisplayName("formatted result contains all components")
            void formattedResultContainsComponents() {
                game.setDc(1); // Ensure success

                MiniGame.EvaluationResult result = game.evaluate(rogue, "SLEIGHT_OF_HAND");
                String formatted = result.getFormattedResult();

                assertTrue(formatted.contains("SKILL CHECK"));
                assertTrue(formatted.contains("d20"));
                assertTrue(formatted.contains("SUCCESS") || formatted.contains("FAILURE"));
            }
        }

        @Nested
        @DisplayName("EvaluationResult")
        class EvaluationResultTests {

            @Test
            @DisplayName("should check reward presence")
            void checksRewardPresence() {
                MiniGame.EvaluationResult withReward = new MiniGame.EvaluationResult(
                        true, "Success!", "gold_coin", null, "d20(15) = 15", 15, 15, false, false);
                MiniGame.EvaluationResult withoutReward = new MiniGame.EvaluationResult(
                        true, "Success!", "", null, "d20(15) = 15", 15, 15, false, false);

                assertTrue(withReward.hasReward());
                assertFalse(withoutReward.hasReward());
            }

            @Test
            @DisplayName("should check consequence presence")
            void checksConsequencePresence() {
                MiniGame.EvaluationResult withConsequence = new MiniGame.EvaluationResult(
                        false, "Failed!", null, "1d6 damage", "d20(5) = 5", 5, 5, false, false);
                MiniGame.EvaluationResult withoutConsequence = new MiniGame.EvaluationResult(
                        false, "Failed!", null, "", "d20(5) = 5", 5, 5, false, false);

                assertTrue(withConsequence.hasConsequence());
                assertFalse(withoutConsequence.hasConsequence());
            }

            @Test
            @DisplayName("should format natural 20 message")
            void formatsNatural20() {
                MiniGame.EvaluationResult nat20 = new MiniGame.EvaluationResult(
                        true, "Success!", "reward", null, "d20(20) = 20", 20, 20, true, false);

                String formatted = nat20.getFormattedResult();
                assertTrue(formatted.contains("NATURAL 20"));
            }

            @Test
            @DisplayName("should format natural 1 message")
            void formatsNatural1() {
                MiniGame.EvaluationResult nat1 = new MiniGame.EvaluationResult(
                        false, "Failed!", null, "consequence", "d20(1) = 1", 1, 1, false, true);

                String formatted = nat1.getFormattedResult();
                assertTrue(formatted.contains("NATURAL 1"));
            }
        }

        @Test
        @DisplayName("toString returns meaningful representation")
        void toStringRepresentation() {
            MiniGame game = new MiniGame("mg_001", "Test Game", MiniGame.Type.SEARCH);
            String str = game.toString();

            assertTrue(str.contains("MiniGame"));
            assertTrue(str.contains("mg_001"));
            assertTrue(str.contains("Test Game"));
            assertTrue(str.contains("SEARCH"));
        }
    }

    // ==========================================
    // Trial Tests
    // ==========================================

    @Nested
    @DisplayName("Trial")
    class TrialTests {

        @Nested
        @DisplayName("Construction")
        class ConstructionTests {

            @Test
            @DisplayName("should create trial with ID and name")
            void createsWithIdAndName() {
                Trial trial = new Trial("trial_01", "The Mayor's Office");

                assertEquals("trial_01", trial.getId());
                assertEquals("The Mayor's Office", trial.getName());
                assertFalse(trial.isStarted());
                assertFalse(trial.isCompleted());
            }

            @Test
            @DisplayName("should create trial with full details")
            void createsWithFullDetails() {
                Trial trial = new Trial("trial_01", "The Mayor's Office",
                        "mayors_office", "You enter the dusty office...");

                assertEquals("mayors_office", trial.getLocation());
                assertEquals("You enter the dusty office...", trial.getEntryNarrative());
            }

            @Test
            @DisplayName("should throw for null ID")
            void throwsForNullId() {
                assertThrows(IllegalArgumentException.class,
                        () -> new Trial(null, "Test"));
            }

            @Test
            @DisplayName("should throw for empty ID")
            void throwsForEmptyId() {
                assertThrows(IllegalArgumentException.class,
                        () -> new Trial("", "Test"));
            }

            @Test
            @DisplayName("should throw for whitespace ID")
            void throwsForWhitespaceId() {
                assertThrows(IllegalArgumentException.class,
                        () -> new Trial("   ", "Test"));
            }

            @Test
            @DisplayName("should throw for null name")
            void throwsForNullName() {
                assertThrows(IllegalArgumentException.class,
                        () -> new Trial("trial_01", null));
            }

            @Test
            @DisplayName("should throw for empty name")
            void throwsForEmptyName() {
                assertThrows(IllegalArgumentException.class,
                        () -> new Trial("trial_01", ""));
            }

            @Test
            @DisplayName("should trim ID and name")
            void trimsIdAndName() {
                Trial trial = new Trial("  trial_01  ", "  Test Trial  ");
                assertEquals("trial_01", trial.getId());
                assertEquals("Test Trial", trial.getName());
            }
        }

        @Nested
        @DisplayName("Setters")
        class SetterTests {

            private Trial trial;

            @BeforeEach
            void setUp() {
                trial = new Trial("trial_01", "Original");
            }

            @Test
            @DisplayName("should update name")
            void updatesName() {
                trial.setName("Updated Name");
                assertEquals("Updated Name", trial.getName());
            }

            @Test
            @DisplayName("should ignore null name")
            void ignoresNullName() {
                trial.setName(null);
                assertEquals("Original", trial.getName());
            }

            @Test
            @DisplayName("should ignore empty name")
            void ignoresEmptyName() {
                trial.setName("   ");
                assertEquals("Original", trial.getName());
            }

            @Test
            @DisplayName("should update location")
            void updatesLocation() {
                trial.setLocation("new_location");
                assertEquals("new_location", trial.getLocation());
            }

            @Test
            @DisplayName("should update entry narrative")
            void updatesEntryNarrative() {
                trial.setEntryNarrative("You enter a dark room...");
                assertEquals("You enter a dark room...", trial.getEntryNarrative());
            }

            @Test
            @DisplayName("should handle null entry narrative")
            void handlesNullEntryNarrative() {
                trial.setEntryNarrative(null);
                assertEquals("", trial.getEntryNarrative());
            }

            @Test
            @DisplayName("should update completion reward")
            void updatesCompletionReward() {
                trial.setCompletionReward("Favor Coin");
                assertEquals("Favor Coin", trial.getCompletionReward());
            }

            @Test
            @DisplayName("should handle null completion reward")
            void handlesNullCompletionReward() {
                trial.setCompletionReward(null);
                assertEquals("", trial.getCompletionReward());
            }

            @Test
            @DisplayName("should update stinger")
            void updatesStinger() {
                trial.setStinger("The villain laughs...");
                assertEquals("The villain laughs...", trial.getStinger());
            }

            @Test
            @DisplayName("should handle null stinger")
            void handlesNullStinger() {
                trial.setStinger(null);
                assertEquals("", trial.getStinger());
            }
        }

        @Nested
        @DisplayName("MiniGame Management")
        class MiniGameManagementTests {

            private Trial trial;
            private MiniGame game1;
            private MiniGame game2;

            @BeforeEach
            void setUp() {
                trial = new Trial("trial_01", "Test Trial");
                game1 = new MiniGame("mg_01", "Game 1", MiniGame.Type.SEARCH);
                game2 = new MiniGame("mg_02", "Game 2", MiniGame.Type.DECODE);
            }

            @Test
            @DisplayName("should add mini-games")
            void addsMiniGames() {
                trial.addMiniGame(game1);
                trial.addMiniGame(game2);

                assertEquals(2, trial.getMiniGameCount());
                assertTrue(trial.hasMiniGames());
            }

            @Test
            @DisplayName("should ignore null mini-game")
            void ignoresNullMiniGame() {
                trial.addMiniGame(null);
                assertEquals(0, trial.getMiniGameCount());
            }

            @Test
            @DisplayName("should get mini-game by ID")
            void getsMiniGameById() {
                trial.addMiniGame(game1);

                Optional<MiniGame> found = trial.getMiniGame("mg_01");

                assertTrue(found.isPresent());
                assertEquals(game1, found.get());
            }

            @Test
            @DisplayName("should return empty for unknown ID")
            void returnsEmptyForUnknownId() {
                Optional<MiniGame> found = trial.getMiniGame("unknown");
                assertTrue(found.isEmpty());
            }

            @Test
            @DisplayName("should remove mini-game")
            void removesMiniGame() {
                trial.addMiniGame(game1);
                trial.addMiniGame(game2);

                MiniGame removed = trial.removeMiniGame("mg_01");

                assertEquals(game1, removed);
                assertEquals(1, trial.getMiniGameCount());
            }

            @Test
            @DisplayName("should return null when removing unknown mini-game")
            void returnsNullForUnknownRemove() {
                MiniGame removed = trial.removeMiniGame("unknown");
                assertNull(removed);
            }

            @Test
            @DisplayName("should get all mini-games as unmodifiable list")
            void getsUnmodifiableList() {
                trial.addMiniGame(game1);
                trial.addMiniGame(game2);

                List<MiniGame> games = trial.getMiniGames();

                assertEquals(2, games.size());
                assertThrows(UnsupportedOperationException.class,
                        () -> games.add(new MiniGame("mg_03", "Game 3", MiniGame.Type.COMBAT)));
            }

            @Test
            @DisplayName("should report no mini-games when empty")
            void reportsNoMiniGames() {
                assertFalse(trial.hasMiniGames());
                assertEquals(0, trial.getMiniGameCount());
            }
        }

        @Nested
        @DisplayName("Trial Lifecycle")
        class TrialLifecycleTests {

            private Trial trial;
            private MiniGame game1;
            private MiniGame game2;

            @BeforeEach
            void setUp() {
                trial = new Trial("trial_01", "Test Trial");
                trial.setEntryNarrative("You enter the room...");
                trial.setCompletionReward("Gold Coin");
                trial.setStinger("The villain escapes!");

                game1 = new MiniGame("mg_01", "Game 1", MiniGame.Type.SEARCH);
                game2 = new MiniGame("mg_02", "Game 2", MiniGame.Type.DECODE);

                trial.addMiniGame(game1);
                trial.addMiniGame(game2);
            }

            @Test
            @DisplayName("should start trial and return entry narrative")
            void startsTrialReturnsNarrative() {
                String narrative = trial.start();

                assertTrue(trial.isStarted());
                assertEquals("You enter the room...", narrative);
            }

            @Test
            @DisplayName("should report incomplete when mini-games not finished")
            void reportsIncompleteWhenGamesNotFinished() {
                assertFalse(trial.checkComplete());
            }

            @Test
            @DisplayName("should report complete when all mini-games finished")
            void reportsCompleteWhenAllGamesFinished() {
                game1.complete();
                game2.complete();

                assertTrue(trial.checkComplete());
            }

            @Test
            @DisplayName("should report complete when no mini-games")
            void reportsCompleteWhenNoGames() {
                Trial emptyTrial = new Trial("empty", "Empty Trial");
                assertTrue(emptyTrial.checkComplete());
            }

            @Test
            @DisplayName("should complete trial and return result")
            void completesTrialReturnsResult() {
                trial.start();
                game1.complete();
                game2.complete();

                Trial.CompletionResult result = trial.complete();

                assertTrue(trial.isCompleted());
                assertEquals("Gold Coin", result.reward());
                assertEquals("The villain escapes!", result.stinger());
            }

            @Test
            @DisplayName("should throw when completing unstarted trial")
            void throwsWhenCompletingUnstartedTrial() {
                game1.complete();
                game2.complete();

                assertThrows(IllegalStateException.class, () -> trial.complete());
            }

            @Test
            @DisplayName("should throw when completing with unfinished mini-games")
            void throwsWhenCompletingWithUnfinishedGames() {
                trial.start();
                game1.complete();
                // game2 not completed

                assertThrows(IllegalStateException.class, () -> trial.complete());
            }

            @Test
            @DisplayName("should reset trial state")
            void resetsTrialState() {
                trial.start();
                game1.complete();
                game2.complete();
                trial.complete();

                trial.reset();

                assertFalse(trial.isStarted());
                assertFalse(trial.isCompleted());
                assertFalse(game1.isCompleted());
                assertFalse(game2.isCompleted());
            }
        }

        @Nested
        @DisplayName("CompletionResult")
        class CompletionResultTests {

            @Test
            @DisplayName("should check for reward presence")
            void checksRewardPresence() {
                Trial.CompletionResult withReward = new Trial.CompletionResult("Coin", "");
                Trial.CompletionResult withoutReward = new Trial.CompletionResult("", "");
                Trial.CompletionResult nullReward = new Trial.CompletionResult(null, "");

                assertTrue(withReward.hasReward());
                assertFalse(withoutReward.hasReward());
                assertFalse(nullReward.hasReward());
            }

            @Test
            @DisplayName("should check for stinger presence")
            void checksStingerPresence() {
                Trial.CompletionResult withStinger = new Trial.CompletionResult("", "Villain laughs");
                Trial.CompletionResult withoutStinger = new Trial.CompletionResult("", "");
                Trial.CompletionResult nullStinger = new Trial.CompletionResult("", null);

                assertTrue(withStinger.hasStinger());
                assertFalse(withoutStinger.hasStinger());
                assertFalse(nullStinger.hasStinger());
            }

            @Test
            @DisplayName("should format completion message with reward and stinger")
            void formatsCompleteMessage() {
                Trial.CompletionResult result = new Trial.CompletionResult("Gold Coin", "The villain laughs!");

                String message = result.getFormattedMessage();

                assertTrue(message.contains("TRIAL COMPLETE"));
                assertTrue(message.contains("Gold Coin"));
                assertTrue(message.contains("The villain laughs!"));
            }

            @Test
            @DisplayName("should format message without reward")
            void formatsMessageWithoutReward() {
                Trial.CompletionResult result = new Trial.CompletionResult("", "The villain laughs!");

                String message = result.getFormattedMessage();

                assertTrue(message.contains("TRIAL COMPLETE"));
                assertFalse(message.contains("received"));
                assertTrue(message.contains("The villain laughs!"));
            }
        }

        @Nested
        @DisplayName("Summary")
        class SummaryTests {

            @Test
            @DisplayName("should include trial name in summary")
            void includesTrialName() {
                Trial trial = new Trial("trial_01", "The Mayor's Office");

                String summary = trial.getSummary();

                assertTrue(summary.contains("The Mayor's Office"));
            }

            @Test
            @DisplayName("should include entry narrative in summary")
            void includesEntryNarrative() {
                Trial trial = new Trial("trial_01", "Test");
                trial.setEntryNarrative("You enter a dark room...");

                String summary = trial.getSummary();

                assertTrue(summary.contains("You enter a dark room..."));
            }

            @Test
            @DisplayName("should list mini-games with status")
            void listsMiniGamesWithStatus() {
                Trial trial = new Trial("trial_01", "Test");
                MiniGame game1 = new MiniGame("mg_01", "Search Desk", MiniGame.Type.SEARCH);
                MiniGame game2 = new MiniGame("mg_02", "Decode Message", MiniGame.Type.DECODE);
                game1.complete();

                trial.addMiniGame(game1);
                trial.addMiniGame(game2);

                String summary = trial.getSummary();

                assertTrue(summary.contains("Challenges:"));
                assertTrue(summary.contains("[X]") || summary.contains("Search Desk"));
                assertTrue(summary.contains("[ ]") || summary.contains("Decode Message"));
            }

            @Test
            @DisplayName("should include reward in summary")
            void includesReward() {
                Trial trial = new Trial("trial_01", "Test");
                trial.setCompletionReward("Harlequin's Favor Coin");

                String summary = trial.getSummary();

                assertTrue(summary.contains("Reward:"));
                assertTrue(summary.contains("Harlequin's Favor Coin"));
            }
        }

        @Nested
        @DisplayName("Equality")
        class EqualityTests {

            @Test
            @DisplayName("should be equal by ID")
            void equalById() {
                Trial trial1 = new Trial("trial_01", "Name1");
                Trial trial2 = new Trial("trial_01", "Name2");

                assertEquals(trial1, trial2);
                assertEquals(trial1.hashCode(), trial2.hashCode());
            }

            @Test
            @DisplayName("should not be equal with different IDs")
            void notEqualDifferentIds() {
                Trial trial1 = new Trial("trial_01", "Same Name");
                Trial trial2 = new Trial("trial_02", "Same Name");

                assertNotEquals(trial1, trial2);
            }

            @Test
            @DisplayName("should not be equal to null")
            void notEqualToNull() {
                Trial trial = new Trial("trial_01", "Test");
                assertNotEquals(null, trial);
            }

            @Test
            @DisplayName("should not be equal to different type")
            void notEqualToDifferentType() {
                Trial trial = new Trial("trial_01", "Test");
                assertNotEquals("trial_01", trial);
            }
        }

        @Test
        @DisplayName("toString returns meaningful representation")
        void toStringRepresentation() {
            Trial trial = new Trial("trial_01", "Test Trial");
            trial.addMiniGame(new MiniGame("mg_01", "Game", MiniGame.Type.SEARCH));

            String str = trial.toString();

            assertTrue(str.contains("Trial"));
            assertTrue(str.contains("trial_01"));
            assertTrue(str.contains("Test Trial"));
            assertTrue(str.contains("miniGames=1"));
        }
    }
}
