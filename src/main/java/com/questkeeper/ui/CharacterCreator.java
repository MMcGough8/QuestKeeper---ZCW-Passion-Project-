package com.questkeeper.ui;

import com.questkeeper.character.Character;
import com.questkeeper.character.Character.Ability;
import com.questkeeper.character.Character.CharacterClass;
import com.questkeeper.character.Character.Race;
import com.questkeeper.core.Dice;

import java.util.*;
import static com.questkeeper.ui.Display.*;

/**
 * Enhanced D&D Character Creator with multiple ability score methods.
 * Work in progress!
 */
public class CharacterCreator {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final int POINT_BUY_TOTAL = 27;
    private static final int[] POINT_BUY_COSTS = {0, 1, 2, 3, 4, 5, 7, 9}; // Cost to reach score 8 through 15
}

public static Character createCharacter() {
        Display.init();
        clearScreen();

        printBox("QUESTKEEPER", 70, YELLOW);
        printBox("A D&D 5th Edition Adventure", 70, WHITE);
        println();
        println(bold(colorize("Welcome, adventurer. Your story begins now...", CYAN)));
        println();
        pressEnterToContinue();

        Character character = new Character();

        // Step 1: Name
        clearScreen();
        printBox("STEP 1: NAME YOUR HERO", 70, MAGENTA);
        String name = promptForString("What is your character's name?", "Aelar");
        character.setName(name.isEmpty() ? "Aelar" : name);
        println(colorize("Welcome to the world, " + bold(character.getName()) + "!", GREEN));
        pressEnterToContinue();

        // Step 2: Race
        clearScreen();
        printBox("STEP 2: CHOOSE YOUR RACE", 70, MAGENTA);
        println(bold("Your ancestry shapes your abilities and place in the world.\n"));
        for (int i = 0; i < Race.values().length; i++) {
            Race race = Race.values()[i];
            String bonus = "+" + race.getAbilityBonusAmount() + " " + race.getAbilityBonus().getAbbreviation();
            if (race.getSecondaryBonus() != null) {
                bonus += " and +" + race.getSecondaryBonusAmount() + " " + race.getSecondaryBonus().getAbbreviation();
            }
            println(String.format("%s%d) %s%s — %s, %d ft speed",
                    colorize(String.valueOf(i + 1), YELLOW),
                    colorize(") ", WHITE),
                    bold(race.getDisplayName()),
                    colorize(" (" + race.getSubraceDisplay() + ")", MAGENTA),
                    bonus,
                    race.getSpeed()));
        }
        println();
        Race race = promptForEnum(Race.values(), "Select your race (number): ");
        character.setRace(race);
        printBox("You are a proud " + race.getDisplayName() + "!", 70, GREEN);
        pressEnterToContinue();

        // Step 3: Class
        clearScreen();
        printBox("STEP 3: CHOOSE YOUR CLASS", 70, MAGENTA);
        println(bold("Your class defines your path and power.\n"));
        for (int i = 0; i < CharacterClass.values().length; i++) {
            CharacterClass cc = CharacterClass.values()[i];
            println(String.format("%s%d) %s%s — Hit Die: d%d",
                    colorize(String.valueOf(i + 1), YELLOW),
                    colorize(") ", WHITE),
                    bold(cc.getDisplayName()),
                    colorize(" (Level 1)", MAGENTA),
                    cc.getHitDie()));
        }
        println();
        CharacterClass characterClass = promptForEnum(CharacterClass.values(), "Select your class (number): ");
        character.setCharacterClass(characterClass);
        character.setLevel(1);
        printBox("You are now a Level 1 " + characterClass.getDisplayName() + "!", 70, GREEN);
        pressEnterToContinue();

        // Step 4: Ability Score Method Selection
        clearScreen();
        printBox("STEP 4: DETERMINE ABILITY SCORES", 70, MAGENTA);
        println(bold("Choose how to generate your six ability scores:\n"));
        println("1) " + bold("Point Buy") + "        — Spend 27 points (official 5e method)");
        println("2) " + bold("Standard Array") + "  — Assign 15, 14, 13, 12, 10, 8");
        println("3) " + bold("Roll for Stats") + "  — 4d6 drop lowest ×6 (classic randomness)");
        println("4) " + bold("Random Everything") + " — Let fate decide race, class, and stats too!");
        println();

        int method = promptForInt("Choose method (1-4): ", 1, 4);

        if (method == 4) {
            randomFullCharacter(character);
        } else {
            Map<Ability, Integer> baseScores = new EnumMap<>(Ability.class);
            for (Ability a : Ability.values()) {
                baseScores.put(a, method == 1 ? 8 : 0); // Point buy starts at 8
            }

            switch (method) {
                case 1 -> pointBuySystem(character, baseScores);
                case 2 -> standardArrayAssignment(character, baseScores);
                case 3 -> rollForStats(character, baseScores);
            }

            // Apply final base scores
            for (Map.Entry<Ability, Integer> entry : baseScores.entrySet()) {
                character.setRawAbilityScore(entry.getKey(), entry.getValue());
            }
        }

        printBox("Ability scores locked in!", 70, GREEN);
        pressEnterToContinue();

        // Final Character Sheet
        showFinalCharacterSheet(character);

        Display.shutdown();
        return character;
    }

    
