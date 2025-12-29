package com.questkeeper.character;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a player character in the game.
 * 
 * Handles ability scores, derived stats, class/race features,
 * and D&D 5e mechanics like proficiency bonuses and skill checks.
 * 
 * @author Marc McGough
 * @version 1.0
 */
public class Character {

    /**
     * The six core ability scores.
     */

    public enum Ability {
        STRENGTH("Strength", "STR"),
        DEXTERITY("Dexterity", "DEX"),
        CONSTITUTION("Constitution", "CON"),
        INTELLIGENCE("Intelligence", "INT"),
        WISDOM("Wisdom", "WIS"),
        CHARISMA("Charisma", "CHA");

        private final String fullName;
        private final String abbreviation;

        Ability(String fullName, String abbreviation) {
            this.fullName = fullName;
            this.abbreviation = abbreviation;
        }

        public String getFullName() { return fullName; }
        public String getAbbreviation() { return abbreviation; }
    }

    /**
     * Skills and their associated abilities.
     */

    public enum Skill {
        // Strength
        ATHLETICS(Ability.STRENGTH, "Athletics"),
        
        // Dexterity
        ACROBATICS(Ability.DEXTERITY, "Acrobatics"),
        SLEIGHT_OF_HAND(Ability.DEXTERITY, "Sleight of Hand"),
        STEALTH(Ability.DEXTERITY, "Stealth"),
        
        // Intelligence
        ARCANA(Ability.INTELLIGENCE, "Arcana"),
        HISTORY(Ability.INTELLIGENCE, "History"),
        INVESTIGATION(Ability.INTELLIGENCE, "Investigation"),
        NATURE(Ability.INTELLIGENCE, "Nature"),
        RELIGION(Ability.INTELLIGENCE, "Religion"),
        
        // Wisdom
        ANIMAL_HANDLING(Ability.WISDOM, "Animal Handling"),
        INSIGHT(Ability.WISDOM, "Insight"),
        MEDICINE(Ability.WISDOM, "Medicine"),
        PERCEPTION(Ability.WISDOM, "Perception"),
        SURVIVAL(Ability.WISDOM, "Survival"),
        
        // Charisma
        DECEPTION(Ability.CHARISMA, "Deception"),
        INTIMIDATION(Ability.CHARISMA, "Intimidation"),
        PERFORMANCE(Ability.CHARISMA, "Performance"),
        PERSUASION(Ability.CHARISMA, "Persuasion");
        
        private final Ability ability;
        private final String displayName;
        
        Skill(Ability ability, String displayName) {
            this.ability = ability;
            this.displayName = displayName;
        }
        
        public Ability getAbility() { return ability; }
        public String getDisplayName() { return displayName; }
    }

    /**
     * Available character races.
     */
    public enum Race {
        HUMAN( "Human", 0, 0, 0, 0, 0, 0, 30),              // +1 to all (handled separately)
        DWARF("Dwarf", 0, 0, 2, 0, 0, 0, 25),               // +2 CON
        Elf("Elf", 0, 2, 0, 0, 0, 0, 30),                   // +2 DEX
        HALFLING("Halfling", 0, 2, 0, 0, 0, 0, 25),         // +2 DEX
        DRAGONBORN("Dragonborn", 2, 0, 0, 0, 0, 1, 30),     // +2 STR, +1 CHA
        GNOME("Gnome", 0, 0, 0, 2, 0, 0, 25),               // +2 INT
        HALF_ELF("Half-Elf", 0, 0, 0, 0, 0, 0, 30),         // +2 CHA, +1 to two others
        HALF_ORC("Half-Orc", 2, 0, 1, 0, 0, 0, 30),         // +2 STR, +1 CON
        TIEFLING("Tiefling", 0, 0, 0, 1, 0, 2, 30);         // +2 CHA, +1 INT

        private final String displayName;
        private final int strBonus;
        private final int dexBonus;
        private final int conBonus;
        private final int intBonus;
        private final int wisBonus;
        private final int chaBonus;
        private final int speed;

        Race(String displayName, int str, int dex, int con, int intel, int wis, int cha, int speed) {
            this.displayName = displayName;
            this.strBonus = str;
            this.dexBonus = dex;
            this.conBonus = con;
            this.intBonus = intel;
            this.wisBonus = wis;
            this.chaBonus = cha;
            this.speed = speed;
        }

        public String getDisplayName() {
            return displayName; 
            
        public int getStrBonus() { 
            return strBonus; 
        }
        public int getDexBonus() { 
            return dexBonus; 
        }
        public int getConBonus() { 
            return conBonus; 
        }
        public int getIntBonus() { 
            return intBonus; 
        }
        public int getWisBonus() { 
            return wisBonus; 
        }
        public int getChaBonus() { 
            return chaBonus; 
        }
        public int getSpeed() { 
            return speed; 
        }

        public int getAbilityBonus(Ability ability) {
            return switch (ability) {
                case STRENGTH -> strBonus;
                case DEXTERITY -> dexBonus;
                case CONSTITUTION -> conBonus;
                case INTELLIGENCE -> intBonus;
                case WISDOM -> wisBonus;
                case CHARISMA -> chaBonus;
            };
        }
    }

    
}
