package com.questkeeper.ui;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Display utility class for CLI presentation.
 * 
 * Handles all text output formatting for the QuestKeeper game,
 * including locations, characters, combat, dialogue, rolls, and system messages.
 * Uses Jansi for ANSI color support in terminals.
 * 
 * @author Marc McGough
 * @version 1.0
 */
public class Display {

    /** Default width for boxes and dividers */
    private static final int DEFAULT_WIDTH = 60;
    
    /** Characters for box drawing */
    private static final char BOX_HORIZONTAL = '═';
    private static final char BOX_VERTICAL = '║';
    private static final char BOX_TOP_LEFT = '╔';
    private static final char BOX_TOP_RIGHT = '╗';
    private static final char BOX_BOTTOM_LEFT = '╚';
    private static final char BOX_BOTTOM_RIGHT = '╝';
    
    /** Characters for simple dividers */
    private static final char DIVIDER_CHAR = '─';
    private static final char DIVIDER_ACCENT = '═';

     /** Whether Jansi has been installed */
    private static boolean jansiInstalled = false;
    
    /** Whether colors are enabled (can be disabled for testing or plain terminals) */
    private static boolean colorsEnabled = true;

    /**
     * Should be called once at application startup.
     */
    public static void init() {
        if (!jansiInstalled) {
            AnsiConsole.systemInstall();
            jansiInstalled = true;
        }
    }

    /**
     * Should be called at application shutdown.
     */
    public static void shutdown() {
        if (jansiInstalled) {
            AnsiConsole.systemUninstall();
            jansiInstalled = false;
        }
    }

     /**
     * Enables or disables color output.
     * 
     * @param enabled true to enable colors, false for plain text
     */
    public static void setColorsEnabled(boolean enabled) {
        colorsEnabled = enabled;
    }
    
    /**
     * Checks if colors are currently enabled.
     * 
     * @return true if colors are enabled
     */
    public static boolean areColorsEnabled() {
        return colorsEnabled;
    }

    public static void showLocation(String name, String description, String[] exits) {
        println();
        printBox(name, DEFAULT_WIDTH, CYAN);
        println();
        printWrapped(description, DEFAULT_WIDTH);
        println();

        if (exits != null && exits.length > 0) {
            print(colorize("Exits: ", YELLOW));
            println(String.join(", ", exits));
        }
        println();
    }

    public static void showCharacter(String name, String race, String charClass, int level, int hp, int maxHp, int ac) {
        println();
        printBox(name + " - " + race + " " + charClass, DEFAULT_WIDTH, GREEN);
        println();

        printHealthBar(hp, maxHp);

        print(colorize("Level: ", WHITE));
        print(colorize(String.valueOf(level), YELLOW));
        print("  ");
        print(colorize("AC: ", WHITE));
        println(colorize(String.valueOf(ac), YELLOW));
        println();
    }

    public static void printHealthBar(int current, int max) {
        int barWidth = 20;
        int filled = (int) ((double) current / max * barWidth);
        filled = Math.max(0, Math.min(barWidth, filled));
        
        print(colorize("HP: ", WHITE));
        print("[");
        
        double percentage = (double) current / max;
        Ansi.Color barColor = percentage > 0.5 ? GREEN : (percentage > 0.25 ? YELLOW : RED);
        
        print(colorize("█".repeat(filled), barColor));
        print(colorize("░".repeat(barWidth - filled), DEFAULT));
        print("] ");
        
        print(colorize(current + "/" + max, barColor));
        println();
    }

    public static void showCombat(String enemyName, int enemyHp, int enemyMaxHp, int playerHp, int playerMaxHp, int round) {
        println();
        printDivider(DIVIDER_ACCENT, DEFAULT_WIDTH);
        println(colorize(" COMBAT - Round " + round, RED));
        printDivider(DIVIDER_ACCENT, DEFAULT_WIDTH);
        println();

        print(colorize(enemyName + ": ", RED));
        printHealthBar(enemyHp, enemyMaxHp);
        
        print(colorize("You: ", GREEN));
        printHealthBar(playerHp, playerMaxHp);
        
        println();
        printDivider(DIVIDER_CHAR, DEFAULT_WIDTH);
        println();
    }

    public static void showDialogue(String speaker, String text) {
        println();
        print(colorize(speaker, CYAN));
        println(colorize(": ", WHITE));
        print(colorize("\"", YELLOW));
        print(colorize(text, WHITE));
        println(colorize("\"", YELLOW));
        println();
    }

    public static void showDialogue(String speaker, String text, String voiceTag) {
        println();
        print(colorize(speaker, CYAN));
        print(colorize(" (" + voiceTag + ")", DEFAULT));
        println(colorize(": ", WHITE));
        print(colorize("\"", YELLOW));
        print(colorize(text, WHITE));
        println(colorize("\"", YELLOW));
        println();
    }

    public static void showRoll(String formula, int result, Boolean success) {
        print(colorize("[ROLL] ", CYAN));
        print(colorize(formula + ": ", WHITE));
        print(colorize(String.valueOf(result), YELLOW));
        
        if (success != null) {
            if (success) {
                println(colorize(" - SUCCESS!", GREEN));
            } else {
                println(colorize(" - FAILURE", RED));
            }
        } else {
            println();
        }
    }

    public static void showSkillCheck (String skillName, int roll, int modifier, int dc, boolean success) {
        int total = roll + modifier;
        String modStr = modifier >= 0 ? "+" + modifier : String.valueOf(modifier);

        print(colorize("[" + skillName.toUpperCase() + " CHECK] ", CYAN));
        print(colorize("d20: " + roll + " " + modStr + " = ", WHITE));
        print(colorize(String.valueOf(total), YELLOW));
        print(colorize(" vs DC " + dc + " - ", WHITE));

        if (success) {
            println(colorize("SUCCESS!", GREEN));
        } else {
            println(colorize("FAILURE", RED));
        }

        if (roll == 20) {
            print(colorize(" NATURAL 20! ", YELLOW));
        } else if (roll == 1) {
            println(colorize(" X Natural 1...", RED));
        }
    }

    public static void showDamage (String notation, int total, String damageType) {
        print(colorize("[DAMAGE] ", RED));
        print(colorize(notation + " = ", WHITE));
        print(colorize(String.valueOf(total), YELLOW));
        if (damageType != null && !damageType.isEmpty()) {
            print(colorize(" " + damageType, MAGENTA));
        }
        println();
    }

    public static void showError(String message) {
        println();
        print(colorize("[ERROR] ", RED));
        println(colorize(message, WHITE));
        println();
    }

    public static void showWarning(String message) {
        print(colorize("[WARNING] ", YELLOW));
        println(colorize(message, WHITE));
    }

    public static void showInfo(String message) {
        print(colorize("[INFO] ", CYAN));
        println(colorize(message, WHITE));
    }

    public static void showSuccess(String message) {
        println(colorize("✦ " + message, GREEN));
    }

    public static void showHelp() {
        println();
        printBox("AVAILABLE COMMANDS", DEFAULT_WIDTH, YELLOW);
        println();
        
        showHelpCommand("look / examine", "Examine your surroundings or an object");
        showHelpCommand("go <direction>", "Move in a direction (north, south, east, west)");
        showHelpCommand("talk <npc>", "Talk to a character");
        showHelpCommand("take <item>", "Pick up an item");
        showHelpCommand("use <item>", "Use an item from your inventory");
        showHelpCommand("inventory / i", "View your inventory");
        showHelpCommand("stats / character", "View your character stats");
        showHelpCommand("attack <target>", "Attack a target in combat");
        showHelpCommand("cast <spell>", "Cast a spell");
        showHelpCommand("rest", "Take a short or long rest");
        showHelpCommand("save", "Save your game");
        showHelpCommand("load", "Load a saved game");
        showHelpCommand("quit / exit", "Exit the game");
        showHelpCommand("help", "Show this help menu");
        
        println();
    }

    private static void showHelpCommand(String command, String description) {
        print("  ");
        print(colorize(String.format("%-20s", command), CYAN));
        println(colorize(description, WHITE));
    }

    public static void showNarrative(String text) {
        println();
        printWrapped(colorize(text, WHITE), DEFAULT_WIDTH);
        println();
    }

    public static void showPause() {
        println(colorize("...", DEFAULT));
    }

    public static void showSceneTransition(String sceneName) {
        println();
        printDivider(DIVIDER_ACCENT, DEFAULT_WIDTH);
        if (sceneName != null && !sceneName.isEmpty()) {
            int padding = (DEFAULT_WIDTH - sceneName.length() - 4) / 2;
            println(colorize(" ".repeat(Math.max(0, padding)) + "« " + sceneName + " »", MAGENTA));
        }
        printDivider(DIVIDER_ACCENT, DEFAULT_WIDTH);
        println();
    }

    public static void showChoices(String prompt, String[] choices) {
        println();
        println(colorize(prompt, WHITE));
        println();
        
        for (int i = 0; i < choices.length; i++) {
            print(colorize("  [" + (i + 1) + "] ", YELLOW));
            println(colorize(choices[i], WHITE));
        }
        println();
    }

    // ========================================================================
    // MUDDLEBROOK-SPECIFIC DISPLAYS
    // ========================================================================

    public static void showTrialHeader(String trialName, int trialNumber) {
        println();
        printBox("TRIAL #" + trialNumber + ": " + trialName, DEFAULT_WIDTH, MAGENTA);
        println();
    }

    public static void showMiniGameResult(boolean success, String reward, String consequence) {
        println();
        if (success) {
            println(colorize("═══ SUCCESS! ═══", GREEN));
            if (reward != null && !reward.isEmpty()) {
                println(colorize("✦ REWARD: " + reward, YELLOW));
            }
        } else {
            println(colorize("═══ FAILED ═══", RED));
            if (consequence != null && !consequence.isEmpty()) {
                println(colorize("✗ " + consequence, RED));
            }
        }
        println();
    }

    public static void showVillainMessage(String message) {
        println();
        printDivider('~', DEFAULT_WIDTH);
        println(colorize("  🎭 The Harlequin Machinist:", MAGENTA));
        println();
        print(colorize("  \"", RED));
        print(colorize(message, WHITE));
        println(colorize("\"", RED));
        println();
        printDivider('~', DEFAULT_WIDTH);
        println();
    }

    public static void showItemGained(String itemName, String itemDescription) {
        println();
        println(colorize("╔══════════════════════════════════════╗", YELLOW));
        println(colorize("║       ✦ NEW ITEM ACQUIRED ✦         ║", YELLOW));
        println(colorize("╚══════════════════════════════════════╝", YELLOW));
        println();
        println(colorize("  " + itemName, GREEN));
        if (itemDescription != null && !itemDescription.isEmpty()) {
            println(colorize("  " + itemDescription, WHITE));
        }
        println();
    }

    public static void printBox(String title, int width, Ansi.Color color) {
        // Top border
        print(colorize(String.valueOf(BOX_TOP_LEFT), color));
        print(colorize(String.valueOf(BOX_HORIZONTAL).repeat(width - 2), color));
        println(colorize(String.valueOf(BOX_TOP_RIGHT), color));
        
        // Title line (centered)
        int padding = (width - 2 - title.length()) / 2;
        int extraPadding = (width - 2 - title.length()) % 2;
        
        print(colorize(String.valueOf(BOX_VERTICAL), color));
        print(" ".repeat(padding));
        print(colorize(title, color));
        print(" ".repeat(padding + extraPadding));
        println(colorize(String.valueOf(BOX_VERTICAL), color));
        
        // Bottom border
        print(colorize(String.valueOf(BOX_BOTTOM_LEFT), color));
        print(colorize(String.valueOf(BOX_HORIZONTAL).repeat(width - 2), color));
        println(colorize(String.valueOf(BOX_BOTTOM_RIGHT), color));
    }

    public static void printDivider(char character, int width) {
        println(String.valueOf(character).repeat(width));
    }

    public static void printDivider(char character, int width, Ansi.Color color) {
        println(colorize(String.valueOf(character).repeat(width), color));
    }

    public static void printWrapped(String text, int width) {
        if (text == null || text.isEmpty()) return;
        
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            if (line.length() + word.length() + 1 > width) {
                println(line.toString());
                line = new StringBuilder();
            }
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
        }
        
        if (line.length() > 0) {
            println(line.toString());
        }
    }

    public static String colorize(String text, Ansi.Color color) {
        if (!colorsEnabled || color == null) {
            return text;
        }
        return ansi().fg(color).a(text).reset().toString();
    }

    public static String bold(String text) {
        if (!colorsEnabled) {
            return text;
        }
        return ansi().bold().a(text).reset().toString();
    }

    public static void clearScreen() {
        if (colorsEnabled) {
            System.out.print(ansi().eraseScreen().cursor(1, 1));
        } else {
            for (int i = 0; i < 50; i++) {
                println();
            }
        }
    }

    public static void print(String text) {
        System.out.print(text);
    }

    public static void println(String text) {
        System.out.println(text);
    }

    public static void println() {
        System.out.println();
    }

    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public static void showPrompt() {
        print(colorize("> ", GREEN));
    }

    public static void showPrompt(String prompt) {
        print(colorize(prompt + " ", GREEN));
    }
}