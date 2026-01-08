package com.questkeeper.save;

import com.questkeeper.character.Character;
import com.questkeeper.character.Character.Ability;
import com.questkeeper.character.Character.CharacterClass;
import com.questkeeper.character.Character.Race;
import com.questkeeper.character.Character.Skill;

import java.util.*;

/**
 * Serializable representation of Character data for save/load operations.
 * 
 * Separates persistence concerns from the Character class itself.
 * Handles conversion between Character objects and YAML-friendly Maps.
 * 
 * @author Marc McGough
 * @version 1.0
 */
public class CharacterData {

    // Identity
    private String name;
    private String race;        
    private String characterClass;
    
    // Progression
    private int level;
    private int experiencePoints;
    
    // Ability scores (base, before racial bonuses)
    private Map<String, Integer> baseAbilityScores;
    
    // Proficiencies
    private Set<String> skillProficiencies;
    private Set<String> savingThrowProficiencies;
    
    // Combat state
    private int currentHitPoints;
    private int maxHitPoints;
    private int temporaryHitPoints;
    private int armorBonus;
    private int shieldBonus;


    //creates empty CharacterData
    public CharacterData() {
        this.baseAbilityScores = new HashMap<>();
        this.skillProficiencies = new HashSet<>();
        this.savingThrowProficiencies = new HashSet<>();
    }

    //creates CharacterData from existing Character
    public static CharacterData fromCharacter(Character character) {
        CharacterData data = new CharacterData();
        
        // Identity
        data.name = character.getName();
        data.race = character.getRace().name();
        data.characterClass = character.getCharacterClass().name();
        
        // Progression
        data.level = character.getLevel();
        data.experiencePoints = character.getExperiencePoints();
        
        // Ability scores (store BASE scores, not with racial bonuses)
        for (Ability ability : Ability.values()) {
            data.baseAbilityScores.put(ability.name(), character.getBaseAbilityScore(ability));
        }
        
        // Skill proficiencies
        for (Skill skill : character.getProficientSkills()) {
            data.skillProficiencies.add(skill.name());
        }
        
        // Saving throw proficiencies
        for (Ability ability : Ability.values()) {
            if (character.hasSavingThrowProficiency(ability)) {
                data.savingThrowProficiencies.add(ability.name());
            }
        }
        
        // Combat state
        data.currentHitPoints = character.getCurrentHitPoints();
        data.maxHitPoints = character.getMaxHitPoints();
        data.temporaryHitPoints = character.getTemporaryHitPoints();
        data.armorBonus = character.getArmorBonus();
        data.shieldBonus = character.getShieldBonus();
        
        return data;
    }

}