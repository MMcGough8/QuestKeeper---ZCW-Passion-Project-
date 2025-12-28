package com.questkeeper.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Display utility class.
 * 
 * Tests output formatting without relying on specific ANSI codes.
 * Colors are disabled during testing for predictable output.
 * 
 * @author Marc McGough
 */
class DisplayTest {
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outputStream));
        // Disable colors for predictable testing
        Display.setColorsEnabled(false);
    }
    
    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
        // Re-enable colors
        Display.setColorsEnabled(true);
    }
    
    /**
     * Gets the captured output as a string.
     */
    private String getOutput() {
        return outputStream.toString();
    }
    
    /**
     * Clears the captured output.
     */
    private void clearOutput() {
        outputStream.reset();
    }
    
    // ========================================================================
    // INITIALIZATION TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {
        
        @Test
        @DisplayName("Colors can be enabled and disabled")
        void colorsToggle() {
            Display.setColorsEnabled(true);
            assertTrue(Display.areColorsEnabled());
            
            Display.setColorsEnabled(false);
            assertFalse(Display.areColorsEnabled());
        }
    }
    
    // ========================================================================
    // LOCATION DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Location Display Tests")
    class LocationDisplayTests {
        
        @Test
        @DisplayName("showLocation displays name, description, and exits")
        void showLocationDisplaysAllInfo() {
            Display.showLocation(
                "Drunken Dragon Inn",
                "A cozy tavern with warm lantern light and the smell of fresh bread.",
                new String[]{"north", "east", "west"}
            );
            
            String output = getOutput();
            assertTrue(output.contains("Drunken Dragon Inn"), "Should contain location name");
            assertTrue(output.contains("cozy tavern"), "Should contain description");
            assertTrue(output.contains("Exits:"), "Should show exits label");
            assertTrue(output.contains("north"), "Should list exits");
        }
        
        @Test
        @DisplayName("showLocation handles null exits gracefully")
        void showLocationNullExits() {
            assertDoesNotThrow(() -> {
                Display.showLocation("Test Room", "A test room.", null);
            });
            
            String output = getOutput();
            assertTrue(output.contains("Test Room"));
            assertFalse(output.contains("Exits:"), "Should not show exits when null");
        }
        
        @Test
        @DisplayName("showLocation handles empty exits array")
        void showLocationEmptyExits() {
            Display.showLocation("Dead End", "No way out.", new String[]{});
            
            String output = getOutput();
            assertFalse(output.contains("Exits:"), "Should not show exits when empty");
        }
    }
    
    // ========================================================================
    // CHARACTER DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Character Display Tests")
    class CharacterDisplayTests {
        
        @Test
        @DisplayName("showCharacter displays all character info")
        void showCharacterDisplaysAllInfo() {
            Display.showCharacter("Thorin", "Dwarf", "Fighter", 3, 25, 30, 16);
            
            String output = getOutput();
            assertTrue(output.contains("Thorin"), "Should contain character name");
            assertTrue(output.contains("Dwarf"), "Should contain race");
            assertTrue(output.contains("Fighter"), "Should contain class");
            assertTrue(output.contains("Level:"), "Should show level label");
            assertTrue(output.contains("3"), "Should show level value");
            assertTrue(output.contains("AC:"), "Should show AC label");
            assertTrue(output.contains("16"), "Should show AC value");
        }
        
        @Test
        @DisplayName("printHealthBar shows HP values")
        void printHealthBarShowsValues() {
            Display.printHealthBar(15, 20);
            
            String output = getOutput();
            assertTrue(output.contains("HP:"), "Should contain HP label");
            assertTrue(output.contains("15/20"), "Should show current/max HP");
        }
        
        @Test
        @DisplayName("printHealthBar handles zero HP")
        void printHealthBarZeroHP() {
            assertDoesNotThrow(() -> {
                Display.printHealthBar(0, 20);
            });
            
            String output = getOutput();
            assertTrue(output.contains("0/20"));
        }
        
        @Test
        @DisplayName("printHealthBar handles full HP")
        void printHealthBarFullHP() {
            assertDoesNotThrow(() -> {
                Display.printHealthBar(20, 20);
            });
            
            String output = getOutput();
            assertTrue(output.contains("20/20"));
        }
    }
    
    // ========================================================================
    // COMBAT DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Combat Display Tests")
    class CombatDisplayTests {
        
        @Test
        @DisplayName("showCombat displays enemy and player HP")
        void showCombatDisplaysHPBars() {
            Display.showCombat("Clockwork Critter", 8, 11, 20, 25, 1);
            
            String output = getOutput();
            assertTrue(output.contains("COMBAT"), "Should show combat header");
            assertTrue(output.contains("Round 1"), "Should show round number");
            assertTrue(output.contains("Clockwork Critter"), "Should show enemy name");
            assertTrue(output.contains("You:"), "Should show player label");
        }
    }
    
    // ========================================================================
    // DIALOGUE DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Dialogue Display Tests")
    class DialogueDisplayTests {
        
        @Test
        @DisplayName("showDialogue displays speaker and text")
        void showDialogueBasic() {
            Display.showDialogue("Norrin the Bard", "Ah! New faces—new verses!");
            
            String output = getOutput();
            assertTrue(output.contains("Norrin the Bard"), "Should contain speaker name");
            assertTrue(output.contains("New faces"), "Should contain dialogue text");
        }
        
        @Test
        @DisplayName("showDialogue with voice tag includes tag")
        void showDialogueWithVoiceTag() {
            Display.showDialogue("Mara", "If you're not buying, make it quick.", "no-nonsense");
            
            String output = getOutput();
            assertTrue(output.contains("Mara"), "Should contain speaker name");
            assertTrue(output.contains("no-nonsense"), "Should contain voice tag");
            assertTrue(output.contains("not buying"), "Should contain dialogue");
        }
    }
    
    // ========================================================================
    // ROLL DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Roll Display Tests")
    class RollDisplayTests {
        
        @Test
        @DisplayName("showRoll displays formula and result")
        void showRollBasic() {
            Display.showRoll("d20+5", 18, null);
            
            String output = getOutput();
            assertTrue(output.contains("[ROLL]"), "Should have roll prefix");
            assertTrue(output.contains("d20+5"), "Should show formula");
            assertTrue(output.contains("18"), "Should show result");
        }
        
        @Test
        @DisplayName("showRoll shows SUCCESS when success is true")
        void showRollSuccess() {
            Display.showRoll("d20+5", 18, true);
            
            String output = getOutput();
            assertTrue(output.contains("SUCCESS"), "Should show success");
        }
        
        @Test
        @DisplayName("showRoll shows FAILURE when success is false")
        void showRollFailure() {
            Display.showRoll("d20+5", 8, false);
            
            String output = getOutput();
            assertTrue(output.contains("FAILURE"), "Should show failure");
        }
        
        @Test
        @DisplayName("showSkillCheck displays full check information")
        void showSkillCheckDisplaysAll() {
            Display.showSkillCheck("Investigation", 15, 3, 12, true);
            
            String output = getOutput();
            assertTrue(output.contains("INVESTIGATION CHECK"), "Should show skill name");
            assertTrue(output.contains("d20: 15"), "Should show raw roll");
            assertTrue(output.contains("+3"), "Should show modifier");
            assertTrue(output.contains("= 18"), "Should show total");
            assertTrue(output.contains("DC 12"), "Should show DC");
            assertTrue(output.contains("SUCCESS"), "Should show result");
        }
        
        @Test
        @DisplayName("showSkillCheck highlights natural 20")
        void showSkillCheckNat20() {
            Display.showSkillCheck("Athletics", 20, 2, 15, true);
            
            String output = getOutput();
            assertTrue(output.contains("NATURAL 20"), "Should highlight nat 20");
        }
        
        @Test
        @DisplayName("showSkillCheck highlights natural 1")
        void showSkillCheckNat1() {
            Display.showSkillCheck("Stealth", 1, 5, 10, false);
            
            String output = getOutput();
            assertTrue(output.contains("Natural 1"), "Should highlight nat 1");
        }
        
        @Test
        @DisplayName("showDamage displays damage information")
        void showDamageBasic() {
            Display.showDamage("2d6+3", 11, "slashing");
            
            String output = getOutput();
            assertTrue(output.contains("[DAMAGE]"), "Should have damage prefix");
            assertTrue(output.contains("2d6+3"), "Should show notation");
            assertTrue(output.contains("11"), "Should show total");
            assertTrue(output.contains("slashing"), "Should show damage type");
        }
        
        @Test
        @DisplayName("showDamage works without damage type")
        void showDamageNoDamageType() {
            Display.showDamage("1d8", 5, null);
            
            String output = getOutput();
            assertTrue(output.contains("1d8"));
            assertTrue(output.contains("5"));
        }
    }
    
    // ========================================================================
    // MESSAGE DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Message Display Tests")
    class MessageDisplayTests {
        
        @Test
        @DisplayName("showError displays error message")
        void showErrorDisplays() {
            Display.showError("Invalid command");
            
            String output = getOutput();
            assertTrue(output.contains("[ERROR]"), "Should have error prefix");
            assertTrue(output.contains("Invalid command"), "Should contain message");
        }
        
        @Test
        @DisplayName("showWarning displays warning message")
        void showWarningDisplays() {
            Display.showWarning("Low on health!");
            
            String output = getOutput();
            assertTrue(output.contains("[WARNING]"), "Should have warning prefix");
            assertTrue(output.contains("Low on health"), "Should contain message");
        }
        
        @Test
        @DisplayName("showInfo displays info message")
        void showInfoDisplays() {
            Display.showInfo("Game saved.");
            
            String output = getOutput();
            assertTrue(output.contains("[INFO]"), "Should have info prefix");
            assertTrue(output.contains("Game saved"), "Should contain message");
        }
        
        @Test
        @DisplayName("showSuccess displays success message")
        void showSuccessDisplays() {
            Display.showSuccess("QUEST STARTED: The Harlequin Trials");
            
            String output = getOutput();
            assertTrue(output.contains("QUEST STARTED"), "Should contain message");
        }
    }
    
    // ========================================================================
    // HELP DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Help Display Tests")
    class HelpDisplayTests {
        
        @Test
        @DisplayName("showHelp displays command list")
        void showHelpDisplaysCommands() {
            Display.showHelp();
            
            String output = getOutput();
            assertTrue(output.contains("AVAILABLE COMMANDS"), "Should have header");
            assertTrue(output.contains("look"), "Should list look command");
            assertTrue(output.contains("go"), "Should list go command");
            assertTrue(output.contains("talk"), "Should list talk command");
            assertTrue(output.contains("inventory"), "Should list inventory command");
            assertTrue(output.contains("help"), "Should list help command");
        }
    }
    
    // ========================================================================
    // NARRATIVE DISPLAY TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Narrative Display Tests")
    class NarrativeDisplayTests {
        
        @Test
        @DisplayName("showNarrative displays text")
        void showNarrativeDisplays() {
            Display.showNarrative("The tavern door creaks open, revealing warm firelight within.");
            
            String output = getOutput();
            assertTrue(output.contains("tavern door creaks"), "Should contain narrative text");
        }
        
        @Test
        @DisplayName("showChoices displays prompt and options")
        void showChoicesDisplays() {
            Display.showChoices(
                "What do you do?",
                new String[]{"Approach the bar", "Find a quiet corner", "Leave the tavern"}
            );
            
            String output = getOutput();
            assertTrue(output.contains("What do you do?"), "Should show prompt");
            assertTrue(output.contains("[1]"), "Should number first choice");
            assertTrue(output.contains("[2]"), "Should number second choice");
            assertTrue(output.contains("[3]"), "Should number third choice");
            assertTrue(output.contains("Approach the bar"), "Should list first choice");
            assertTrue(output.contains("quiet corner"), "Should list second choice");
        }
        
        @Test
        @DisplayName("showSceneTransition displays transition marker")
        void showSceneTransitionDisplays() {
            Display.showSceneTransition("Town Hall");
            
            String output = getOutput();
            assertTrue(output.contains("Town Hall"), "Should contain scene name");
        }
    }
    
    // ========================================================================
    // MUDDLEBROOK-SPECIFIC TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Muddlebrook-Specific Display Tests")
    class MuddlebrookDisplayTests {
        
        @Test
        @DisplayName("showTrialHeader displays trial info")
        void showTrialHeaderDisplays() {
            Display.showTrialHeader("Backwards Clock", 1);
            
            String output = getOutput();
            assertTrue(output.contains("TRIAL #1"), "Should show trial number");
            assertTrue(output.contains("Backwards Clock"), "Should show trial name");
        }
        
        @Test
        @DisplayName("showMiniGameResult displays success with reward")
        void showMiniGameResultSuccess() {
            Display.showMiniGameResult(true, "Blinkstep Spark (teleport 10 ft 1/long rest)", null);
            
            String output = getOutput();
            assertTrue(output.contains("SUCCESS"), "Should show success");
            assertTrue(output.contains("REWARD"), "Should show reward label");
            assertTrue(output.contains("Blinkstep Spark"), "Should show reward item");
        }
        
        @Test
        @DisplayName("showMiniGameResult displays failure with consequence")
        void showMiniGameResultFailure() {
            Display.showMiniGameResult(false, null, "1 lightning damage + hair stands up");
            
            String output = getOutput();
            assertTrue(output.contains("FAILED"), "Should show failed");
            assertTrue(output.contains("lightning damage"), "Should show consequence");
        }
        
        @Test
        @DisplayName("showVillainMessage displays Machinist message")
        void showVillainMessageDisplays() {
            Display.showVillainMessage("Welcome to the show! Let's see if you're worth my time...");
            
            String output = getOutput();
            assertTrue(output.contains("Harlequin Machinist"), "Should identify the villain");
            assertTrue(output.contains("Welcome to the show"), "Should contain message");
        }
        
        @Test
        @DisplayName("showItemGained displays item notification")
        void showItemGainedDisplays() {
            Display.showItemGained("Jester's Lucky Coin", "1/day +1d4, tails = 1 psychic damage");
            
            String output = getOutput();
            assertTrue(output.contains("NEW ITEM ACQUIRED"), "Should show acquisition header");
            assertTrue(output.contains("Jester's Lucky Coin"), "Should show item name");
            assertTrue(output.contains("1/day +1d4"), "Should show description");
        }
    }
    
    // ========================================================================
    // FORMATTING HELPER TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Formatting Helper Tests")
    class FormattingHelperTests {
        
        @Test
        @DisplayName("printBox creates bordered box")
        void printBoxCreatesBox() {
            Display.printBox("TEST TITLE", 40, null);
            
            String output = getOutput();
            assertTrue(output.contains("╔"), "Should have top-left corner");
            assertTrue(output.contains("╗"), "Should have top-right corner");
            assertTrue(output.contains("╚"), "Should have bottom-left corner");
            assertTrue(output.contains("╝"), "Should have bottom-right corner");
            assertTrue(output.contains("TEST TITLE"), "Should contain title");
        }
        
        @Test
        @DisplayName("printDivider creates divider line")
        void printDividerCreatesLine() {
            Display.printDivider('─', 20);
            
            String output = getOutput();
            assertTrue(output.contains("────────────────────"), "Should have divider of correct length");
        }
        
        @Test
        @DisplayName("printWrapped wraps text at specified width")
        void printWrappedWrapsText() {
            String longText = "This is a very long sentence that should be wrapped " +
                              "across multiple lines when displayed in the terminal.";
            Display.printWrapped(longText, 30);
            
            String output = getOutput();
            String[] lines = output.split("\n");
            
            // Each line should be approximately 30 chars or less
            for (String line : lines) {
                assertTrue(line.length() <= 35, // Allow some flexibility for word boundaries
                    "Line should not exceed width significantly: " + line);
            }
        }
        
        @Test
        @DisplayName("printWrapped handles empty text")
        void printWrappedEmptyText() {
            assertDoesNotThrow(() -> {
                Display.printWrapped("", 40);
            });
        }
        
        @Test
        @DisplayName("printWrapped handles null text")
        void printWrappedNullText() {
            assertDoesNotThrow(() -> {
                Display.printWrapped(null, 40);
            });
        }
        
        @Test
        @DisplayName("colorize returns plain text when colors disabled")
        void colorizeReturnsPlainWhenDisabled() {
            Display.setColorsEnabled(false);
            String result = Display.colorize("test", null);
            assertEquals("test", result, "Should return plain text");
        }
    }
    
    // ========================================================================
    // PROMPT TESTS
    // ========================================================================
    
    @Nested
    @DisplayName("Prompt Tests")
    class PromptTests {
        
        @Test
        @DisplayName("showPrompt displays default prompt")
        void showPromptDefault() {
            Display.showPrompt();
            
            String output = getOutput();
            assertTrue(output.contains(">"), "Should contain prompt character");
        }
        
        @Test
        @DisplayName("showPrompt displays custom prompt")
        void showPromptCustom() {
            Display.showPrompt("Enter command:");
            
            String output = getOutput();
            assertTrue(output.contains("Enter command:"), "Should contain custom prompt");
        }
    }
}