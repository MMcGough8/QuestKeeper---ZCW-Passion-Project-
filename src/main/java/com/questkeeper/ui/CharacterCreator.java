package com.questkeeper.ui;

import com.questkeeper.character.Character;
import com.questkeeper.character.Character.Ability;
import com.questkeeper.character.Character.CharacterClass;
import com.questkeeper.character.Character.Race;
import com.questkeeper.core.Dice;

import java.util.*;

import static com.questkeeper.ui.Display.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 * Enhanced D&D Character Creator with multiple ability score methods.
 * 
 * Supports:
 * - Point Buy (27 points, official 5e method)
 * - Standard Array (15, 14, 13, 12, 10, 8)
 * - Roll for Stats (4d6 drop lowest)
 * - Random Everything (let fate decide)
 * 
 * @author Marc McGough
 * @version 1.0
 * Work in progress
 */
public class CharacterCreator {

    private static final Scanner scanner = new Scanner(System.in);
    private static final int POINT_BUY_TOTAL = 27;
    private static final int[] POINT_BUY_COSTS = {0, 1, 2, 3, 4, 5, 7, 9}; // Cost to reach score 8 through 15

    public static Character createCharacter() {
        Display.init();
        clearScreen();

        printBox("QUESTKEEPER", 70, YELLOW);
        printBox("A D&D 5th Edition Adventure", 70, WHITE);
        println();
        println(bold(colorize("Welcome, adventurer. Your story begins now...", CYAN)));
        println();
        pressEnterToContinue();

        // Step 1: Name
        clearScreen();
        printBox("STEP 1: NAME YOUR HERO", 70, MAGENTA);
        String name = promptForString("What is your character's name?", "Aelar");
        if (name.isEmpty()) {
            name = "Aelar";
        }
        println(colorize("Welcome to the world, " + bold(name) + "!", GREEN));
        pressEnterToContinue();

        // Step 2: Race
        clearScreen();
        printBox("STEP 2: CHOOSE YOUR RACE", 70, MAGENTA);
        println(bold("Your ancestry shapes your abilities and place in the world.\n"));
        for (int i = 0; i < Race.values().length; i++) {
            Race r = Race.values()[i];
            String bonus = getRaceBonusString(r);
            println(String.format("%s) %s — %s, %d ft speed",
                    colorize(String.valueOf(i + 1), YELLOW),
                    bold(r.getDisplayName()),
                    bonus,
                    r.getSpeed()));
        }
        println();
        Race race = promptForEnum(Race.values(), "Select your race (number): ");
        printBox("You are a proud " + race.getDisplayName() + "!", 70, GREEN);
        pressEnterToContinue();

        // Step 3: Class
        clearScreen();
        printBox("STEP 3: CHOOSE YOUR CLASS", 70, MAGENTA);
        println(bold("Your class defines your path and power.\n"));
        for (int i = 0; i < CharacterClass.values().length; i++) {
            CharacterClass cc = CharacterClass.values()[i];
            println(String.format("%s) %s — Hit Die: d%d",
                    colorize(String.valueOf(i + 1), YELLOW),
                    bold(cc.getDisplayName()),
                    cc.getHitDie()));
        }
        println();
        CharacterClass characterClass = promptForEnum(CharacterClass.values(), "Select your class (number): ");
        printBox("You are now a Level 1 " + characterClass.getDisplayName() + "!", 70, GREEN);
        pressEnterToContinue();

        // Step 4: Ability Score Method Selection
        clearScreen();
        printBox("STEP 4: DETERMINE ABILITY SCORES", 70, MAGENTA);
        println(bold("Choose how to generate your six ability scores:\n"));
        println("1) " + bold("Point Buy") + "         — Spend 27 points (official 5e method)");
        println("2) " + bold("Standard Array") + "   — Assign 15, 14, 13, 12, 10, 8");
        println("3) " + bold("Roll for Stats") + "   — 4d6 drop lowest ×6 (classic randomness)");
        println("4) " + bold("Random Everything") + " — Let fate decide race, class, and stats too!");
        println();

        int method = promptForInt("Choose method (1-4): ", 1, 4);

        // Handle random everything - overrides previous choices
        if (method == 4) {
            Object[] randomResult = randomFullCharacter(name);
            name = (String) randomResult[0];
            race = (Race) randomResult[1];
            characterClass = (CharacterClass) randomResult[2];
            @SuppressWarnings("unchecked")
            Map<Ability, Integer> randomScores = (Map<Ability, Integer>) randomResult[3];
            
            // Create character with random choices
            Character character = new Character(name, race, characterClass);
            for (Map.Entry<Ability, Integer> entry : randomScores.entrySet()) {
                character.setAbilityScore(entry.getKey(), entry.getValue());
            }
            
            printBox("Ability scores locked in!", 70, GREEN);
            pressEnterToContinue();
            showFinalCharacterSheet(character);
            Display.shutdown();
            return character;
        }

        // For other methods, collect base scores first
        Map<Ability, Integer> baseScores = new EnumMap<>(Ability.class);
        
        switch (method) {
            case 1 -> pointBuySystem(race, baseScores);
            case 2 -> standardArrayAssignment(race, baseScores);
            case 3 -> rollForStats(race, baseScores);
        }

        Character character = new Character(name, race, characterClass);
        
        for (Map.Entry<Ability, Integer> entry : baseScores.entrySet()) {
            character.setAbilityScore(entry.getKey(), entry.getValue());
        }

        printBox("Ability scores locked in!", 70, GREEN);
        pressEnterToContinue();

        showFinalCharacterSheet(character);

        Display.shutdown();
        return character;
    }

    private static String getRaceBonusString(Race race) {
        StringBuilder bonus = new StringBuilder();
        
        // Human special case - +1 to all
        if (race == Race.HUMAN) {
            return "+1 to all abilities";
        }
        
        // Check each ability for bonuses
        for (Ability a : Ability.values()) {
            int b = race.getAbilityBonus(a);
            if (b > 0) {
                if (bonus.length() > 0) {
                    bonus.append(", ");
                }
                bonus.append("+").append(b).append(" ").append(a.getAbbreviation());
            }
        }
        
        return bonus.toString();
    }

    private static void pointBuySystem(Race race, Map<Ability, Integer> scores) {
        // Initialize all scores to 8
        for (Ability a : Ability.values()) {
            scores.put(a, 8);
        }
        
        int pointsLeft = POINT_BUY_TOTAL;

        while (pointsLeft > 0) {
            clearScreen();
            printBox("POINT BUY — " + pointsLeft + " points remaining", 70, YELLOW);
            println("Costs: 8→9 (1pt), 9→10 (1pt), ... 13→14 (2pt), 14→15 (2pt)\n");
            println(bold("Current Scores (before racial bonuses):\n"));

            for (Ability a : Ability.values()) {
                int score = scores.get(a);
                int costToNext = score < 15 ? getPointCost(score + 1) : 0;
                int racialBonus = getRacialBonus(race, a);
                String racial = racialBonus > 0 ? colorize(" +" + racialBonus, GREEN) : "";
                String costStr = costToNext > 0 && costToNext <= pointsLeft 
                        ? colorize(" (next: " + costToNext + "pt)", MAGENTA) 
                        : "";
                        
                println(String.format("  %s: %2d%s%s",
                        padRight(a.getFullName(), 12),
                        score,
                        racial,
                        costStr));
            }
            println();
            println(colorize("Enter 'done' to finish early, or select an ability to increase.", WHITE));
            println();

            String input = promptForString("Increase which ability?", "").toLowerCase();
            
            if (input.equals("done")) {
                break;
            }

            Ability ability = parseAbility(input);
            if (ability == null) {
                println(colorize("Invalid ability. Try again.", RED));
                pressEnterToContinue();
                continue;
            }

            int current = scores.get(ability);

            if (current >= 15) {
                println(colorize("Cannot exceed 15 before racial bonuses!", RED));
                pressEnterToContinue();
                continue;
            }

            int cost = getPointCost(current + 1);
            if (cost > pointsLeft) {
                println(colorize("Not enough points! Need " + cost + ", have " + pointsLeft, RED));
                pressEnterToContinue();
                continue;
            }

            scores.put(ability, current + 1);
            pointsLeft -= cost;
            println(colorize("✓ " + ability.getFullName() + " → " + (current + 1) + " (-" + cost + " pts)", GREEN));
            pressEnterToContinue();
        }
    }

    /**
     * Standard array assignment - assign 15, 14, 13, 12, 10, 8 to abilities.
     */
    private static void standardArrayAssignment(Race race, Map<Ability, Integer> scores) {
        clearScreen();
        printBox("STANDARD ARRAY", 70, YELLOW);
        println("Assign the values: " + bold("15, 14, 13, 12, 10, 8") + "\n");
        pressEnterToContinue();

        int[] array = {15, 14, 13, 12, 10, 8};
        Set<Ability> assigned = EnumSet.noneOf(Ability.class);

        for (int value : array) {
            clearScreen();
            printBox("Assign " + bold(String.valueOf(value)) + " to which ability?", 70, CYAN);
            println("Current assignments:\n");

            for (Ability a : Ability.values()) {
                int curr = scores.getOrDefault(a, 0);
                int racialBonus = getRacialBonus(race, a);
                String racial = racialBonus > 0 ? colorize(" +" + racialBonus, GREEN) : "";
                String status = assigned.contains(a) ? colorize(" ✓", GREEN) : "";
                
                println(String.format("  %d) %s: %s%s%s",
                        a.ordinal() + 1,
                        padRight(a.getFullName(), 12),
                        curr > 0 ? String.valueOf(curr) : "--",
                        racial,
                        status));
            }
            println();

            Ability ability;
            do {
                ability = promptForEnum(Ability.values(), "Choose ability (number): ");
                if (assigned.contains(ability)) {
                    println(colorize("Already assigned! Choose another.", RED));
                }
            } while (assigned.contains(ability));

            scores.put(ability, value);
            assigned.add(ability);
            println(colorize("✓ Assigned " + value + " to " + ability.getFullName(), GREEN));
            pressEnterToContinue();
        }
    }

    /**
     * Roll for stats - 4d6 drop lowest, six times.
     */
    private static void rollForStats(Race race, Map<Ability, Integer> scores) {
        clearScreen();
        printBox("ROLLING FOR STATS — 4d6 drop lowest", 70, YELLOW);
        println("Rolling six times...\n");
        pressEnterToContinue();

        List<Integer> rolls = new ArrayList<>();
        Random rand = new Random();

        for (int i = 1; i <= 6; i++) {
            int[] dice = new int[4];
            for (int j = 0; j < 4; j++) {
                dice[j] = rand.nextInt(6) + 1;
            }
            Arrays.sort(dice);
            int dropped = dice[0];
            int total = dice[1] + dice[2] + dice[3];

            rolls.add(total);
            println(String.format("Roll %d: [%d, %d, %d, %d] → drop %d → %s",
                    i, dice[0], dice[1], dice[2], dice[3], dropped,
                    colorize(String.valueOf(total), GREEN)));
            sleep(800);
        }

        rolls.sort(Collections.reverseOrder());
        println("\nYour final rolled stats: " + bold(rolls.toString()));
        pressEnterToContinue();

        Set<Ability> assigned = EnumSet.noneOf(Ability.class);

        for (int value : rolls) {
            clearScreen();
            printBox("Assign " + bold(String.valueOf(value)) + " to which ability?", 70, CYAN);
            println("Current assignments:\n");

            for (Ability a : Ability.values()) {
                int curr = scores.getOrDefault(a, 0);
                int racialBonus = getRacialBonus(race, a);
                String racial = racialBonus > 0 ? colorize(" +" + racialBonus, GREEN) : "";
                String status = assigned.contains(a) ? colorize(" ✓", GREEN) : "";
                
                println(String.format("  %d) %s: %s%s%s",
                        a.ordinal() + 1,
                        padRight(a.getFullName(), 12),
                        curr > 0 ? String.valueOf(curr) : "--",
                        racial,
                        status));
            }
            println();

            Ability ability;
            do {
                ability = promptForEnum(Ability.values(), "Choose ability (number): ");
                if (assigned.contains(ability)) {
                    println(colorize("Already assigned! Choose another.", RED));
                }
            } while (assigned.contains(ability));

            scores.put(ability, value);
            assigned.add(ability);
            println(colorize("✓ Assigned " + value + " to " + ability.getFullName(), GREEN));
            pressEnterToContinue();
        }
    }

    /**
     * Randomizes everything - race, class, and stats.
     * Returns an array: [name, race, characterClass, scores map]
     */
    private static Object[] randomFullCharacter(String name) {
        clearScreen();
        printBox("FATE TAKES THE REINS...", 70, RED);
        println("Randomizing everything except your name...\n");
        sleep(1500);

        Random rand = new Random();

        Race[] races = Race.values();
        Race randomRace = races[rand.nextInt(races.length)];
        println("Race: " + bold(randomRace.getDisplayName()));
        sleep(1000);

        CharacterClass[] classes = CharacterClass.values();
        CharacterClass randomClass = classes[rand.nextInt(classes.length)];
        println("Class: " + bold(randomClass.getDisplayName()));
        sleep(1000);

        // Roll stats
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int[] d = new int[4];
            for (int j = 0; j < 4; j++) {
                d[j] = rand.nextInt(6) + 1;
            }
            Arrays.sort(d);
            rolls.add(d[1] + d[2] + d[3]);
        }
        rolls.sort(Collections.reverseOrder());

        // Assign highest to lowest across abilities
        Map<Ability, Integer> scores = new EnumMap<>(Ability.class);
        Ability[] abilities = Ability.values();
        for (int i = 0; i < 6; i++) {
            scores.put(abilities[i], rolls.get(i));
        }

        println("\nStats: " + rolls);
        println("Assigned highest to lowest: STR, DEX, CON, INT, WIS, CHA");
        sleep(1500);
        println(colorize("\nYour destiny has been forged!", GREEN));
        pressEnterToContinue();

        return new Object[] { name, randomRace, randomClass, scores };
    }

    /**
     * Displays the final character sheet.
     */
    private static void showFinalCharacterSheet(Character character) {
        clearScreen();
        printBox("YOUR HERO IS READY!", 80, GREEN);
        println();
        println(bold(centerText(character.getName(), 80)));
        println(centerText("Level " + character.getLevel() + " " + 
                character.getRace().getDisplayName() + " " + 
                character.getCharacterClass().getDisplayName(), 80));
        println();
        printDivider('─', 80, WHITE);

        println(bold("\nABILITY SCORES"));
        println(character.getAbilityScoresString());
        println();

        println(bold("COMBAT"));
        println(String.format("  Hit Points: %s%d/%d%s", 
                colorize("", GREEN), 
                character.getCurrentHitPoints(), 
                character.getMaxHitPoints(), 
                colorize("", WHITE)));
        println(String.format("  Armor Class: %d", character.getArmorClass()));
        println(String.format("  Initiative: %+d", character.getAbilityModifier(Ability.DEXTERITY)));
        println(String.format("  Speed: %d ft", character.getRace().getSpeed()));
        println(String.format("  Proficiency Bonus: +%d", character.getProficiencyBonus()));
        println();

        println(bold("RACIAL TRAITS"));
        println("  " + character.getRace().getDisplayName() + " traits applied");
        println();

        printDivider('─', 80, WHITE);
        printBox("Press Enter to begin your adventure as " + character.getName() + "...", 80, YELLOW);
        scanner.nextLine();
    }

    private static int getRacialBonus(Race race, Ability ability) {
        // Human gets +1 to all
        if (race == Race.HUMAN) {
            return 1;
        }
        return race.getAbilityBonus(ability);
    }

    private static int getPointCost(int targetScore) {
        if (targetScore <= 8) return 0;
        if (targetScore > 15) return 999; // Can't go above 15
        
        // Cost to go from (score-1) to score
        return switch (targetScore) {
            case 9, 10, 11, 12, 13 -> 1;
            case 14, 15 -> 2;
            default -> 0;
        };
    }

    private static Ability parseAbility(String input) {
        if (input == null || input.isEmpty()) return null;
        
        input = input.toLowerCase().trim();
        
        for (Ability a : Ability.values()) {
            if (a.getFullName().toLowerCase().startsWith(input) ||
                a.getAbbreviation().toLowerCase().equals(input)) {
                return a;
            }
        }
        
        // Try number
        try {
            int num = Integer.parseInt(input);
            if (num >= 1 && num <= 6) {
                return Ability.values()[num - 1];
            }
        } catch (NumberFormatException ignored) {}
        
        return null;
    }

    private static String padRight(String text, int width) {
        if (text.length() >= width) return text;
        return text + " ".repeat(width - text.length());
    }

    private static String promptForString(String prompt, String defaultValue) {
        print(colorize(prompt + " ", CYAN));
        if (!defaultValue.isEmpty()) {
            print(colorize("(default: " + defaultValue + ") ", MAGENTA));
        }
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
  
    private static <T extends Enum<T>> T promptForEnum(T[] values, String prompt) {
        while (true) {
            print(colorize(prompt, CYAN));
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n >= 1 && n <= values.length) {
                    return values[n - 1];
                }
            } catch (NumberFormatException ignored) {}
            println(colorize("Invalid input. Enter a number from 1 to " + values.length + ".", RED));
        }
    }

    private static int promptForInt(String prompt, int min, int max) {
        while (true) {
            print(colorize(prompt, CYAN));
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException ignored) {}
            println(colorize("Please enter a number between " + min + " and " + max + ".", RED));
        }
    }

    private static void pressEnterToContinue() {
        println();
        print(colorize("Press Enter to continue...", MAGENTA));
        scanner.nextLine();
    }

    private static String centerText(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Character player = CharacterCreator.createCharacter();
        println(bold("\nThe adventure begins for " + player.getName() + "!"));
    }
}