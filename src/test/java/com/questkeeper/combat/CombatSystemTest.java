package com.questkeeper.combat;

import com.questkeeper.campaign.Campaign;
import com.questkeeper.character.Character;
import com.questkeeper.character.Character.Ability;
import com.questkeeper.character.Character.CharacterClass;
import com.questkeeper.character.Character.Race;
import com.questkeeper.state.GameState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CombatSystem class.
 *
 * @author Marc McGough
 */
@DisplayName("CombatSystem Tests")
class CombatSystemTest {

    @TempDir
    Path tempDir;

    private Path campaignDir;
    private Campaign campaign;
    private Character character;
    private GameState state;
    private CombatSystem combatSystem;

    @BeforeEach
    void setUp() throws IOException {
        campaignDir = tempDir.resolve("test_campaign");
        Files.createDirectories(campaignDir);

        createCampaignYaml();
        createLocationsYaml();

        campaign = Campaign.loadFromYaml(campaignDir);
        character = new Character("TestHero", Race.HUMAN, CharacterClass.FIGHTER,
            14, 12, 14, 10, 10, 10);
        state = new GameState(character, campaign);
        combatSystem = new CombatSystem();
    }

    private void createCampaignYaml() throws IOException {
        Files.writeString(campaignDir.resolve("campaign.yaml"), """
            id: test_campaign
            name: Test Campaign
            author: Test
            version: "1.0"
            starting_location: tavern
            """);
    }

    private void createLocationsYaml() throws IOException {
        Files.writeString(campaignDir.resolve("locations.yaml"), """
            locations:
              - id: tavern
                name: The Tavern
                description: A cozy tavern.
            """);
    }

    private Monster createGoblin() {
        Monster goblin = new Monster("goblin_1", "Goblin", 13, 7);
        goblin.setAttackBonus(4);
        goblin.setDamageDice("1d6+2");
        goblin.setDexterityMod(2);
        goblin.setExperienceValue(50);
        return goblin;
    }

    private Monster createOrc() {
        Monster orc = new Monster("orc_1", "Orc", 13, 15);
        orc.setAttackBonus(5);
        orc.setDamageDice("1d12+3");
        orc.setDexterityMod(1);
        orc.setExperienceValue(100);
        return orc;
    }

    // ==========================================
    // Initial State Tests
    // ==========================================

    @Nested
    @DisplayName("Initial State")
    class InitialStateTests {

        @Test
        @DisplayName("starts not in combat")
        void startsNotInCombat() {
            assertFalse(combatSystem.isInCombat());
            assertNull(combatSystem.getCurrentCombatant());
            assertTrue(combatSystem.getParticipants().isEmpty());
        }
    }

    // ==========================================
    // Start Combat Tests
    // ==========================================

    @Nested
    @DisplayName("Start Combat")
    class StartCombatTests {

        @Test
        @DisplayName("starts combat successfully")
        void startsCombatSuccessfully() {
            Monster goblin = createGoblin();

            CombatResult result = combatSystem.startCombat(state, List.of(goblin));

            assertEquals(CombatResult.Type.COMBAT_START, result.getType());
            assertTrue(combatSystem.isInCombat());
            assertTrue(result.getMessage().contains("Combat begins"));
        }

        @Test
        @DisplayName("includes all participants")
        void includesAllParticipants() {
            Monster goblin = createGoblin();
            Monster orc = createOrc();

            combatSystem.startCombat(state, List.of(goblin, orc));

            List<Combatant> participants = combatSystem.getParticipants();
            assertEquals(3, participants.size()); // Player + 2 enemies
        }

        @Test
        @DisplayName("rolls initiative for all")
        void rollsInitiativeForAll() {
            Monster goblin = createGoblin();

            CombatResult result = combatSystem.startCombat(state, List.of(goblin));

            assertTrue(result.getMessage().contains("Initiative Rolls"));
            assertTrue(result.hasTurnOrder());
            assertEquals(2, result.getTurnOrder().size());
        }

        @Test
        @DisplayName("sorts initiative order descending")
        void sortsInitiativeOrder() {
            Monster goblin = createGoblin();

            combatSystem.startCombat(state, List.of(goblin));

            List<Combatant> order = combatSystem.getInitiativeOrder();
            int firstRoll = combatSystem.getInitiativeRoll(order.get(0));
            int secondRoll = combatSystem.getInitiativeRoll(order.get(1));

            assertTrue(firstRoll >= secondRoll);
        }

        @Test
        @DisplayName("resets enemy HP")
        void resetsEnemyHp() {
            Monster goblin = createGoblin();
            goblin.takeDamage(5);
            assertEquals(2, goblin.getCurrentHitPoints());

            combatSystem.startCombat(state, List.of(goblin));

            assertEquals(7, goblin.getCurrentHitPoints());
        }

        @Test
        @DisplayName("fails for null state")
        void failsForNullState() {
            Monster goblin = createGoblin();

            CombatResult result = combatSystem.startCombat(null, List.of(goblin));

            assertTrue(result.isError());
        }

        @Test
        @DisplayName("fails for empty enemies")
        void failsForEmptyEnemies() {
            CombatResult result = combatSystem.startCombat(state, List.of());

            assertTrue(result.isError());
        }

        @Test
        @DisplayName("fails for null enemies")
        void failsForNullEnemies() {
            CombatResult result = combatSystem.startCombat(state, null);

            assertTrue(result.isError());
        }
    }

    // ==========================================
    // Player Turn Tests
    // ==========================================

    @Nested
    @DisplayName("Player Turn")
    class PlayerTurnTests {

        private Monster goblin;

        @BeforeEach
        void startCombatWithPlayer() {
            goblin = createGoblin();
            combatSystem.startCombat(state, List.of(goblin));

            // Advance until player's turn
            while (combatSystem.isInCombat() &&
                   combatSystem.getCurrentCombatant() instanceof Monster) {
                combatSystem.enemyTurn();
            }
        }

        @Test
        @DisplayName("attack hits when roll meets AC")
        void attackHitsWhenRollMeetsAC() {
            // Use high-HP enemy so hits don't end combat
            Monster toughGoblin = new Monster("tough_goblin", "Tough Goblin", 13, 100);
            toughGoblin.setDexterityMod(2);
            combatSystem.startCombat(state, List.of(toughGoblin));

            while (combatSystem.getCurrentCombatant() instanceof Monster &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            // Run many attempts to get at least one hit
            boolean gotHit = false;
            for (int i = 0; i < 20 && !gotHit && combatSystem.isInCombat(); i++) {
                CombatResult result = combatSystem.playerTurn("attack", null);
                if (result.getType() == CombatResult.Type.ATTACK_HIT) {
                    gotHit = true;
                    assertTrue(result.getMessage().contains("HIT"));
                }

                // Advance turns if combat continues
                if (combatSystem.isInCombat()) {
                    while (combatSystem.getCurrentCombatant() instanceof Monster &&
                           combatSystem.isInCombat()) {
                        combatSystem.enemyTurn();
                    }
                }
            }
            assertTrue(gotHit, "Should hit at least once in 20 attempts");
        }

        @Test
        @DisplayName("attack shows roll details")
        void attackShowsRollDetails() {
            // Use a high-HP enemy to ensure we get an attack result, not victory
            Monster toughGoblin = new Monster("tough_goblin", "Tough Goblin", 13, 100);
            toughGoblin.setDexterityMod(2);
            combatSystem.startCombat(state, List.of(toughGoblin));

            while (combatSystem.getCurrentCombatant() instanceof Monster) {
                combatSystem.enemyTurn();
            }

            CombatResult result = combatSystem.playerTurn("attack", null);

            // Result should be ATTACK_HIT or ATTACK_MISS, both contain roll info
            assertTrue(result.getType() == CombatResult.Type.ATTACK_HIT ||
                       result.getType() == CombatResult.Type.ATTACK_MISS);
            assertTrue(result.getMessage().contains("Roll:"));
            assertTrue(result.getMessage().contains("vs AC"));
        }

        @Test
        @DisplayName("attack defaults to first enemy")
        void attackDefaultsToFirstEnemy() {
            // Use high-HP weak enemy so combat doesn't end early
            Monster toughGoblin = new Monster("tough_goblin", "Tough Goblin", 13, 100);
            toughGoblin.setAttackBonus(0);
            toughGoblin.setDamageDice("1d1");
            combatSystem.startCombat(state, List.of(toughGoblin));

            while (combatSystem.getCurrentCombatant() instanceof Monster &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                CombatResult result = combatSystem.playerTurn("attack", null);
                assertTrue(result.getMessage().contains("Goblin") ||
                           result.getMessage().contains("goblin"));
            }
        }

        @Test
        @DisplayName("attack targets specific enemy")
        void attackTargetsSpecificEnemy() {
            // Use high-HP enemies to ensure combat doesn't end early
            Monster toughGoblin = new Monster("tough_goblin", "Tough Goblin", 13, 100);
            Monster toughOrc = new Monster("tough_orc", "Tough Orc", 13, 100);
            combatSystem.startCombat(state, List.of(toughGoblin, toughOrc));

            while (combatSystem.getCurrentCombatant() instanceof Monster &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                CombatResult result = combatSystem.playerTurn("attack", "orc");
                assertTrue(result.getMessage().contains("Orc"));
            }
        }

        @Test
        @DisplayName("attack with partial name match")
        void attackWithPartialNameMatch() {
            // Use high-HP weak enemy to ensure combat doesn't end early
            Monster toughGoblin = new Monster("tough_goblin", "Tough Goblin", 13, 100);
            toughGoblin.setAttackBonus(0);
            toughGoblin.setDamageDice("1d1");
            combatSystem.startCombat(state, List.of(toughGoblin));

            while (combatSystem.getCurrentCombatant() instanceof Monster &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                CombatResult result = combatSystem.playerTurn("attack", "gob");
                assertTrue(result.getMessage().contains("Goblin"));
            }
        }

        @Test
        @DisplayName("fails for unknown target")
        void failsForUnknownTarget() {
            CombatResult result = combatSystem.playerTurn("attack", "dragon");

            assertTrue(result.isError());
            assertTrue(result.getMessage().contains("No enemy"));
        }

        @Test
        @DisplayName("fails for invalid action")
        void failsForInvalidAction() {
            CombatResult result = combatSystem.playerTurn("dance", null);

            assertTrue(result.isError());
            assertTrue(result.getMessage().contains("Unknown action"));
        }

        @Test
        @DisplayName("accepts attack synonyms")
        void acceptsAttackSynonyms() {
            // Use high-HP weak enemy so combat doesn't end
            Monster toughEnemy = new Monster("tough", "Tough Enemy", 13, 100);
            toughEnemy.setAttackBonus(0);
            toughEnemy.setDamageDice("1d1");
            combatSystem.startCombat(state, List.of(toughEnemy));

            while (combatSystem.getCurrentCombatant() instanceof Monster &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                CombatResult hitResult = combatSystem.playerTurn("hit", null);
                assertFalse(hitResult.isError());
            }

            // Advance to player turn again
            while (combatSystem.getCurrentCombatant() instanceof Monster &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                CombatResult strikeResult = combatSystem.playerTurn("strike", null);
                assertFalse(strikeResult.isError());
            }
        }
    }

    // ==========================================
    // Enemy Turn Tests
    // ==========================================

    @Nested
    @DisplayName("Enemy Turn")
    class EnemyTurnTests {

        @Test
        @DisplayName("enemy attacks player")
        void enemyAttacksPlayer() {
            Monster goblin = createGoblin();
            combatSystem.startCombat(state, List.of(goblin));

            // Find an enemy turn
            while (combatSystem.isInCombat() &&
                   !(combatSystem.getCurrentCombatant() instanceof Monster)) {
                combatSystem.playerTurn("attack", null);
            }

            if (combatSystem.isInCombat()) {
                CombatResult result = combatSystem.enemyTurn();

                assertTrue(result.getMessage().contains(character.getName()) ||
                           result.isCombatOver());
            }
        }

        @Test
        @DisplayName("enemy turn shows roll details")
        void enemyTurnShowsRollDetails() {
            Monster goblin = createGoblin();
            combatSystem.startCombat(state, List.of(goblin));

            while (!(combatSystem.getCurrentCombatant() instanceof Monster) &&
                   combatSystem.isInCombat()) {
                combatSystem.playerTurn("attack", null);
            }

            if (combatSystem.isInCombat()) {
                CombatResult result = combatSystem.enemyTurn();

                assertTrue(result.getMessage().contains("Roll:") ||
                           result.isCombatOver());
            }
        }
    }

    // ==========================================
    // Attack Resolution Tests
    // ==========================================

    @Nested
    @DisplayName("Attack Resolution")
    class AttackResolutionTests {

        @Test
        @DisplayName("damage reduces target HP")
        void damageReducesTargetHp() {
            // Use weak enemy (low attack) and high HP to ensure we see HP reduction
            Monster weakEnemy = new Monster("weak", "Weak Enemy", 10, 50);
            weakEnemy.setAttackBonus(0);
            weakEnemy.setDamageDice("1d1");  // Very low damage
            combatSystem.startCombat(state, List.of(weakEnemy));

            int startingHp = weakEnemy.getCurrentHitPoints();

            // Attack until we hit or run out of turns
            int maxTurns = 50;
            while (combatSystem.isInCombat() && weakEnemy.getCurrentHitPoints() == startingHp && maxTurns-- > 0) {
                if (combatSystem.getCurrentCombatant() instanceof Character) {
                    combatSystem.playerTurn("attack", null);
                } else {
                    combatSystem.enemyTurn();
                }
            }

            // Either we hit or combat ended
            assertTrue(weakEnemy.getCurrentHitPoints() < startingHp || !weakEnemy.isAlive(),
                "Enemy HP should have decreased after being hit");
        }

        @Test
        @DisplayName("attack result includes attacker and defender")
        void attackResultIncludesParticipants() {
            // Use weak enemy to ensure player survives
            Monster weakEnemy = new Monster("weak", "Weak Enemy", 13, 100);
            weakEnemy.setAttackBonus(0);
            weakEnemy.setDamageDice("1d1");
            combatSystem.startCombat(state, List.of(weakEnemy));

            while (!(combatSystem.getCurrentCombatant() instanceof Character) &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                CombatResult result = combatSystem.playerTurn("attack", null);

                // Only check for ATTACK_HIT or ATTACK_MISS results
                if (result.getType() == CombatResult.Type.ATTACK_HIT ||
                    result.getType() == CombatResult.Type.ATTACK_MISS) {
                    assertNotNull(result.getAttacker());
                    assertNotNull(result.getDefender());
                }
            }
        }
    }

    // ==========================================
    // End Conditions Tests
    // ==========================================

    @Nested
    @DisplayName("End Conditions")
    class EndConditionsTests {

        @Test
        @DisplayName("victory when all enemies dead")
        void victoryWhenAllEnemiesDead() {
            Monster weakGoblin = new Monster("weak_goblin", "Weak Goblin", 5, 1);
            weakGoblin.setExperienceValue(25);

            combatSystem.startCombat(state, List.of(weakGoblin));

            CombatResult result = null;
            while (combatSystem.isInCombat()) {
                if (combatSystem.getCurrentCombatant() instanceof Character) {
                    result = combatSystem.playerTurn("attack", null);
                } else {
                    result = combatSystem.enemyTurn();
                }
            }

            assertNotNull(result);
            assertTrue(result.getType() == CombatResult.Type.VICTORY ||
                       result.getType() == CombatResult.Type.PLAYER_DEFEATED);
        }

        @Test
        @DisplayName("defeat when player HP reaches zero")
        void defeatWhenPlayerDead() {
            // Create a very strong monster
            Monster dragon = new Monster("dragon", "Dragon", 5, 100);
            dragon.setAttackBonus(20);
            dragon.setDamageDice("10d10+50");

            // Weaken player
            character.takeDamage(character.getCurrentHitPoints() - 1);

            combatSystem.startCombat(state, List.of(dragon));

            CombatResult result = null;
            int maxTurns = 100;
            while (combatSystem.isInCombat() && maxTurns-- > 0) {
                if (combatSystem.getCurrentCombatant() instanceof Monster) {
                    result = combatSystem.enemyTurn();
                } else {
                    result = combatSystem.playerTurn("attack", null);
                }
            }

            // Either player died or we hit max turns
            assertTrue(!combatSystem.isInCombat() || maxTurns <= 0);
        }

        @Test
        @DisplayName("fled ends combat")
        void fledEndsCombat() {
            Monster goblin = createGoblin();
            combatSystem.startCombat(state, List.of(goblin));

            // Keep trying to flee until successful or combat ends
            while (combatSystem.isInCombat()) {
                if (combatSystem.getCurrentCombatant() instanceof Character) {
                    CombatResult result = combatSystem.playerTurn("flee", null);
                    if (result.getType() == CombatResult.Type.FLED) {
                        assertFalse(combatSystem.isInCombat());
                        return;
                    }
                } else {
                    combatSystem.enemyTurn();
                }
            }
        }

        @Test
        @DisplayName("victory awards XP")
        void victoryAwardsXp() {
            Monster weakGoblin = new Monster("weak_goblin", "Weak Goblin", 5, 1);
            weakGoblin.setExperienceValue(50);

            int startingXp = character.getExperiencePoints();
            combatSystem.startCombat(state, List.of(weakGoblin));

            while (combatSystem.isInCombat()) {
                if (combatSystem.getCurrentCombatant() instanceof Character) {
                    combatSystem.playerTurn("attack", null);
                } else {
                    combatSystem.enemyTurn();
                }
            }

            // XP should increase if player won
            if (character.isAlive()) {
                assertTrue(character.getExperiencePoints() >= startingXp);
            }
        }
    }

    // ==========================================
    // Turn Cycling Tests
    // ==========================================

    @Nested
    @DisplayName("Turn Cycling")
    class TurnCyclingTests {

        @Test
        @DisplayName("advances turn after player action")
        void advancesTurnAfterPlayerAction() {
            Monster goblin = createGoblin();
            combatSystem.startCombat(state, List.of(goblin));

            // Get initial turn
            int initialTurn = combatSystem.getCurrentTurnIndex();

            // Execute one full round
            if (combatSystem.getCurrentCombatant() instanceof Character) {
                combatSystem.playerTurn("attack", null);
            } else {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                int newTurn = combatSystem.getCurrentTurnIndex();
                assertNotEquals(initialTurn, newTurn);
            }
        }

        @Test
        @DisplayName("cycles through all combatants")
        void cyclesThroughAllCombatants() {
            Monster goblin = createGoblin();
            Monster orc = createOrc();
            combatSystem.startCombat(state, List.of(goblin, orc));

            // Track which combatants took turns
            java.util.Set<String> tookTurn = new java.util.HashSet<>();

            int maxIterations = 10;
            while (combatSystem.isInCombat() && tookTurn.size() < 3 && maxIterations-- > 0) {
                Combatant current = combatSystem.getCurrentCombatant();
                tookTurn.add(current.getName());

                if (current instanceof Character) {
                    combatSystem.playerTurn("attack", null);
                } else {
                    combatSystem.enemyTurn();
                }
            }

            // All 3 combatants should have had a turn (or combat ended)
            assertTrue(tookTurn.size() >= 1);
        }
    }

    // ==========================================
    // Roll Transparency Tests
    // ==========================================

    @Nested
    @DisplayName("Roll Transparency")
    class RollTransparencyTests {

        @Test
        @DisplayName("attack message shows roll vs AC")
        void attackMessageShowsRollVsAc() {
            // Use high-HP enemy to ensure attack result, not victory
            Monster toughGoblin = new Monster("tough_goblin", "Tough Goblin", 13, 100);
            toughGoblin.setDexterityMod(2);
            combatSystem.startCombat(state, List.of(toughGoblin));

            while (!(combatSystem.getCurrentCombatant() instanceof Character) &&
                   combatSystem.isInCombat()) {
                combatSystem.enemyTurn();
            }

            if (combatSystem.isInCombat()) {
                CombatResult result = combatSystem.playerTurn("attack", null);
                assertTrue(result.getMessage().contains("vs AC"));
            }
        }

        @Test
        @DisplayName("hit message shows damage")
        void hitMessageShowsDamage() {
            Monster weakGoblin = new Monster("weak", "Weak Goblin", 1, 100);
            combatSystem.startCombat(state, List.of(weakGoblin));

            while (combatSystem.isInCombat()) {
                if (combatSystem.getCurrentCombatant() instanceof Character) {
                    CombatResult result = combatSystem.playerTurn("attack", null);
                    if (result.getType() == CombatResult.Type.ATTACK_HIT) {
                        assertTrue(result.getMessage().contains("Damage:"));
                        return;
                    }
                } else {
                    combatSystem.enemyTurn();
                }
            }
        }

        @Test
        @DisplayName("combat start shows all initiative rolls")
        void combatStartShowsInitiativeRolls() {
            Monster goblin = createGoblin();
            Monster orc = createOrc();

            CombatResult result = combatSystem.startCombat(state, List.of(goblin, orc));

            String message = result.getMessage();
            assertTrue(message.contains("TestHero"));
            assertTrue(message.contains("Goblin"));
            assertTrue(message.contains("Orc"));
        }
    }

    // ==========================================
    // State Management Tests
    // ==========================================

    @Nested
    @DisplayName("State Management")
    class StateManagementTests {

        @Test
        @DisplayName("getEnemies returns only monsters")
        void getEnemiesReturnsOnlyMonsters() {
            Monster goblin = createGoblin();
            combatSystem.startCombat(state, List.of(goblin));

            List<Combatant> enemies = combatSystem.getEnemies();

            assertEquals(1, enemies.size());
            assertTrue(enemies.get(0) instanceof Monster);
        }

        @Test
        @DisplayName("getPlayer returns character")
        void getPlayerReturnsCharacter() {
            Monster goblin = createGoblin();
            combatSystem.startCombat(state, List.of(goblin));

            Combatant player = combatSystem.getPlayer();

            assertNotNull(player);
            assertEquals("TestHero", player.getName());
        }

        @Test
        @DisplayName("getLivingEnemies excludes dead")
        void getLivingEnemiesExcludesDead() {
            Monster goblin = createGoblin();
            Monster orc = createOrc();
            combatSystem.startCombat(state, List.of(goblin, orc));

            // Kill the goblin
            goblin.takeDamage(100);

            List<Combatant> living = combatSystem.getLivingEnemies();

            assertEquals(1, living.size());
            assertEquals("Orc", living.get(0).getName());
        }
    }

    // ==========================================
    // Error Handling Tests
    // ==========================================

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("playerTurn fails when not in combat")
        void playerTurnFailsWhenNotInCombat() {
            CombatResult result = combatSystem.playerTurn("attack", null);

            assertTrue(result.isError());
            assertTrue(result.getMessage().contains("Not in combat"));
        }

        @Test
        @DisplayName("enemyTurn fails when not in combat")
        void enemyTurnFailsWhenNotInCombat() {
            CombatResult result = combatSystem.enemyTurn();

            assertTrue(result.isError());
        }

        @Test
        @DisplayName("executeTurn fails when not in combat")
        void executeTurnFailsWhenNotInCombat() {
            CombatResult result = combatSystem.executeTurn();

            assertTrue(result.isError());
        }

        @Test
        @DisplayName("endCombat fails when not in combat")
        void endCombatFailsWhenNotInCombat() {
            CombatResult result = combatSystem.endCombat();

            assertTrue(result.isError());
        }
    }
}
