package com.questkeeper.character;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.questkeeper.core.Dice;

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
        HUMAN("Human", 0, 0, 0, 0, 0, 0, 30),           // +1 to all (handled separately)
        DWARF("Dwarf", 0, 0, 2, 0, 0, 0, 25),           // +2 CON
        ELF("Elf", 0, 2, 0, 0, 0, 0, 30),               // +2 DEX
        HALFLING("Halfling", 0, 2, 0, 0, 0, 0, 25),     // +2 DEX
        DRAGONBORN("Dragonborn", 2, 0, 0, 0, 0, 1, 30), // +2 STR, +1 CHA
        GNOME("Gnome", 0, 0, 0, 2, 0, 0, 25),           // +2 INT
        HALF_ELF("Half-Elf", 0, 0, 0, 0, 0, 2, 30),     // +2 CHA, +1 to two others
        HALF_ORC("Half-Orc", 2, 0, 1, 0, 0, 0, 30),     // +2 STR, +1 CON
        TIEFLING("Tiefling", 0, 0, 0, 1, 0, 2, 30);     // +2 CHA, +1 INT
        
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
        
        public String getDisplayName() { return displayName; }
        public int getStrBonus() { return strBonus; }
        public int getDexBonus() { return dexBonus; }
        public int getConBonus() { return conBonus; }
        public int getIntBonus() { return intBonus; }
        public int getWisBonus() { return wisBonus; }
        public int getChaBonus() { return chaBonus; }
        public int getSpeed() { return speed; }
        
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
    
    /**
     * Available character classes.
     */
    public enum CharacterClass {
        BARBARIAN("Barbarian", 12, Ability.STRENGTH, Ability.CONSTITUTION),
        BARD("Bard", 8, Ability.DEXTERITY, Ability.CHARISMA),
        CLERIC("Cleric", 8, Ability.WISDOM, Ability.CHARISMA),
        DRUID("Druid", 8, Ability.INTELLIGENCE, Ability.WISDOM),
        FIGHTER("Fighter", 10, Ability.STRENGTH, Ability.CONSTITUTION),
        MONK("Monk", 8, Ability.STRENGTH, Ability.DEXTERITY),
        PALADIN("Paladin", 10, Ability.WISDOM, Ability.CHARISMA),
        RANGER("Ranger", 10, Ability.STRENGTH, Ability.DEXTERITY),
        ROGUE("Rogue", 8, Ability.DEXTERITY, Ability.INTELLIGENCE),
        SORCERER("Sorcerer", 6, Ability.CONSTITUTION, Ability.CHARISMA),
        WARLOCK("Warlock", 8, Ability.WISDOM, Ability.CHARISMA),
        WIZARD("Wizard", 6, Ability.INTELLIGENCE, Ability.WISDOM);
        
        private final String displayName;
        private final int hitDie;
        private final Ability primarySave;
        private final Ability secondarySave;
        
        CharacterClass(String displayName, int hitDie, Ability primary, Ability secondary) {
            this.displayName = displayName;
            this.hitDie = hitDie;
            this.primarySave = primary;
            this.secondarySave = secondary;
        }
        
        public String getDisplayName() { return displayName; }
        public int getHitDie() { return hitDie; }
        public Ability getPrimarySave() { return primarySave; }
        public Ability getSecondarySave() { return secondarySave; }
    }
   
    /** Minimum ability score */
    private static final int MIN_ABILITY_SCORE = 1;
    
    /** Maximum ability score (before racial bonuses) */
    private static final int MAX_ABILITY_SCORE = 20;
    
    /** Base AC when unarmored */
    private static final int BASE_AC = 10;
    
    /** Starting level */
    private static final int STARTING_LEVEL = 1;
    
    /** XP thresholds for each level (index = level - 1) */
    private static final int[] XP_THRESHOLDS = {
        0, 300, 900, 2700, 6500, 14000, 23000, 34000, 48000, 64000,
        85000, 100000, 120000, 140000, 165000, 195000, 225000, 265000, 305000, 355000
    };

    private String name;
    private Race race;
    private CharacterClass characterClass;
    private int level;
    private int experiencePoints;
    
    private final Map<Ability, Integer> baseAbilityScores;
    private final Set<Skill> proficientSkills;
    private final Set<Ability> savingThrowProficiencies;
    
    private int currentHitPoints;
    private int maxHitPoints;
    private int temporaryHitPoints;
    
    private int armorBonus;  // From equipped armor
    private int shieldBonus; // From equipped shield

    /**
     * Creates a new character with default stats.
     */
    public Character(String name, Race race, CharacterClass characterClass) {
        this.name = name;
        this.race = race;
        this.characterClass = characterClass;
        this.level = STARTING_LEVEL;
        this.experiencePoints = 0;
        
        this.baseAbilityScores = new EnumMap<>(Ability.class);
        this.proficientSkills = EnumSet.noneOf(Skill.class);
        this.savingThrowProficiencies = EnumSet.noneOf(Ability.class);
        
        // Initialize with default scores of 10
        for (Ability ability : Ability.values()) {
            baseAbilityScores.put(ability, 10);
        }
        
        // Add class saving throw proficiencies
        savingThrowProficiencies.add(characterClass.getPrimarySave());
        savingThrowProficiencies.add(characterClass.getSecondarySave());
        
        // Calculate initial HP
        this.maxHitPoints = calculateMaxHitPoints();
        this.currentHitPoints = maxHitPoints;
        this.temporaryHitPoints = 0;
        
        this.armorBonus = 0;
        this.shieldBonus = 0;
    }
    
    /**
     * Creates a character with specified ability scores.
     */
    public Character(String name, Race race, CharacterClass characterClass,
                     int str, int dex, int con, int intel, int wis, int cha) {
        this(name, race, characterClass);
        setAbilityScores(str, dex, con, intel, wis, cha);

        this.maxHitPoints = calculateMaxHitPoints();
        this.currentHitPoints = maxHitPoints;
    }
    
    public int getBaseAbilityScore(Ability ability) {
        return baseAbilityScores.get(ability);
    }

    public int getAbilityScore(Ability ability) {
        int base = baseAbilityScores.get(ability);
        int racialBonus = race.getAbilityBonus(ability);
        
        // Humans get +1 to all abilities
        if (race == Race.HUMAN) {
            racialBonus = 1;
        }
        
        return Math.min(base + racialBonus, MAX_ABILITY_SCORE);
    }
    
    /**
     * Gets the ability modifier for an ability.
     * Modifier = (score - 10) / 2, rounded down.
     */
    public int getAbilityModifier(Ability ability) {
        return (getAbilityScore(ability) - 10) / 2;
    }
    
    public void setAbilityScore(Ability ability, int score) {
        int clampedScore = Math.max(MIN_ABILITY_SCORE, Math.min(MAX_ABILITY_SCORE, score));
        baseAbilityScores.put(ability, clampedScore);
        
        // Recalculate HP if CON changed
        if (ability == Ability.CONSTITUTION) {
            int oldMax = maxHitPoints;
            maxHitPoints = calculateMaxHitPoints();
            // Adjust current HP proportionally
            if (oldMax > 0) {
                currentHitPoints = (int) ((double) currentHitPoints / oldMax * maxHitPoints);
            }
        }
    }
    
    public void setAbilityScores(int str, int dex, int con, int intel, int wis, int cha) {
        baseAbilityScores.put(Ability.STRENGTH, clampAbilityScore(str));
        baseAbilityScores.put(Ability.DEXTERITY, clampAbilityScore(dex));
        baseAbilityScores.put(Ability.CONSTITUTION, clampAbilityScore(con));
        baseAbilityScores.put(Ability.INTELLIGENCE, clampAbilityScore(intel));
        baseAbilityScores.put(Ability.WISDOM, clampAbilityScore(wis));
        baseAbilityScores.put(Ability.CHARISMA, clampAbilityScore(cha));
        
        // Recalculate HP
        maxHitPoints = calculateMaxHitPoints();
        if (currentHitPoints > maxHitPoints) {
            currentHitPoints = maxHitPoints;
        }
    }
    
    private int clampAbilityScore(int score) {
        return Math.max(MIN_ABILITY_SCORE, Math.min(MAX_ABILITY_SCORE, score));
    }

    public int getProficiencyBonus() {
        return (level - 1) / 4 + 2;
    }
    
    /**
     * Calculates maximum hit points.
     * Level 1: max hit die + CON modifier
     * Higher levels: add average hit die roll + CON modifier per level
     */
    private int calculateMaxHitPoints() {
        int conMod = getAbilityModifier(Ability.CONSTITUTION);
        int hitDie = characterClass.getHitDie();
        
        // Level 1: max hit die + CON mod
        int hp = hitDie + conMod;
        
        // Additional levels: average roll + CON mod
        // Average = (hitDie / 2) + 1
        for (int i = 2; i <= level; i++) {
            hp += (hitDie / 2) + 1 + conMod;
        }
        
        return Math.max(1, hp);
    }
    
    /**
     * Gets the armor class.
     * Base AC = 10 + DEX modifier + armor bonus + shield bonus
     */
    public int getArmorClass() {
        return BASE_AC + getAbilityModifier(Ability.DEXTERITY) + armorBonus + shieldBonus;
    }
    
    /**
     * Gets the initiative modifier (DEX modifier).
     */
    public int getInitiativeModifier() {
        return getAbilityModifier(Ability.DEXTERITY);
    }
    
    /**
     * Gets the movement speed.
     */
    public int getSpeed() {
        return race.getSpeed();
    }
    
    /**
     * Gets the passive perception score.
     */
    public int getPassivePerception() {
        return 10 + getSkillModifier(Skill.PERCEPTION);
    }

    public void addSkillProficiency(Skill skill) {
        proficientSkills.add(skill);
    }

    public void removeSkillProficiency(Skill skill) {
        proficientSkills.remove(skill);
    }

    public boolean isProficientIn(Skill skill) {
        return proficientSkills.contains(skill);
    }
    
    /**
     * Gets the modifier for a skill check.
     */
    public int getSkillModifier(Skill skill) {
        int modifier = getAbilityModifier(skill.getAbility());
        if (proficientSkills.contains(skill)) {
            modifier += getProficiencyBonus();
        }
        return modifier;
    }

    public Set<Skill> getProficientSkills() {
        return EnumSet.copyOf(proficientSkills);
    }
    
    /**
     * Checks if the character is proficient in a saving throw.
     */
    public boolean hasSavingThrowProficiency(Ability ability) {
        return savingThrowProficiencies.contains(ability);
    }

    public int getSavingThrowModifier(Ability ability) {
        int modifier = getAbilityModifier(ability);
        if (savingThrowProficiencies.contains(ability)) {
            modifier += getProficiencyBonus();
        }
        return modifier;
    }

    public int makeAbilityCheck(Ability ability) {
        return Dice.rollWithModifier(20, getAbilityModifier(ability));
    }

    public int makeSkillCheck(Skill skill) {
        return Dice.rollWithModifier(20, getSkillModifier(skill));
    }

    public int makeSavingThrow(Ability ability) {
        return Dice.rollWithModifier(20, getSavingThrowModifier(ability));
    }

    public boolean makeAbilityCheckAgainstDC(Ability ability, int dc) {
        return Dice.checkAgainstDC(getAbilityModifier(ability), dc);
    }

    public boolean makeSkillCheckAgainstDC(Skill skill, int dc) {
        return Dice.checkAgainstDC(getSkillModifier(skill), dc);
    }
 
    public boolean makeSavingThrowAgainstDC(Ability ability, int dc) {
        return Dice.checkAgainstDC(getSavingThrowModifier(ability), dc);
    }

    public int rollInitiative() {
        return Dice.rollWithModifier(20, getInitiativeModifier());
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public int getTemporaryHitPoints() {
        return temporaryHitPoints;
    }

    public int heal(int amount) {
        if (amount <= 0) return 0;
        
        int oldHp = currentHitPoints;
        currentHitPoints = Math.min(currentHitPoints + amount, maxHitPoints);
        return currentHitPoints - oldHp;
    }

    public int takeDamage(int amount) {
        if (amount <= 0) return 0;
        
        int remainingDamage = amount;
        
        // Deplete temp HP first
        if (temporaryHitPoints > 0) {
            if (temporaryHitPoints >= remainingDamage) {
                temporaryHitPoints -= remainingDamage;
                return 0;
            } else {
                remainingDamage -= temporaryHitPoints;
                temporaryHitPoints = 0;
            }
        }
        
        // Apply remaining damage to real HP
        int actualDamage = Math.min(remainingDamage, currentHitPoints);
        currentHitPoints -= actualDamage;
        return actualDamage;
    }
    
    /**
     * Sets temporary hit points.
     * Temp HP don't stack - only keeps the higher value.
     */
    public void setTemporaryHitPoints(int amount) {
        temporaryHitPoints = Math.max(temporaryHitPoints, amount);
    }

    public boolean isUnconscious() {
        return currentHitPoints <= 0;
    }

    public boolean isBloodied() {
        return currentHitPoints <= maxHitPoints / 2;
    }

    public void fullHeal() {
        currentHitPoints = maxHitPoints;
    }

    public int getLevel() {
        return level;
    }
 
    public int getExperiencePoints() {
        return experiencePoints;
    }

    public int getXpForNextLevel() {
        if (level >= XP_THRESHOLDS.length) {
            return -1; // Max level
        }
        return XP_THRESHOLDS[level];
    }

    public int addExperience(int xp) {
        if (xp <= 0) return 0;
        
        experiencePoints += xp;
        int levelsGained = 0;
        
        // Check for level ups
        while (level < XP_THRESHOLDS.length && experiencePoints >= XP_THRESHOLDS[level]) {
            levelUp();
            levelsGained++;
        }
        
        return levelsGained;
    }

    private void levelUp() {
        level++;
        
        // Recalculate max HP
        int oldMax = maxHitPoints;
        maxHitPoints = calculateMaxHitPoints();
        
        // Add the HP difference to current HP
        currentHitPoints += (maxHitPoints - oldMax);
    }
    
    public void setLevel(int newLevel) {
        if (newLevel < 1) newLevel = 1;
        if (newLevel > 20) newLevel = 20;
        
        this.level = newLevel;
        this.maxHitPoints = calculateMaxHitPoints();
        this.currentHitPoints = Math.min(currentHitPoints, maxHitPoints);
    }

    public void setArmorBonus(int bonus) {
        this.armorBonus = Math.max(0, bonus);
    }

    public void setShieldBonus(int bonus) {
        this.shieldBonus = Math.max(0, bonus);
    }

    public int getArmorBonus() {
        return armorBonus;
    }

    public int getShieldBonus() {
        return shieldBonus;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Race getRace() {
        return race;
    }
    
    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public String getAbilityScoresString() {
        StringBuilder sb = new StringBuilder();
        for (Ability ability : Ability.values()) {
            int score = getAbilityScore(ability);
            int mod = getAbilityModifier(ability);
            String modStr = mod >= 0 ? "+" + mod : String.valueOf(mod);
            sb.append(String.format("%s: %d (%s)  ", ability.getAbbreviation(), score, modStr));
        }
        return sb.toString().trim();
    }
    
    @Override
    public String toString() {
        return String.format("%s - Level %d %s %s (HP: %d/%d, AC: %d)",
                name, level, race.getDisplayName(), characterClass.getDisplayName(),
                currentHitPoints, maxHitPoints, getArmorClass());
    }
}