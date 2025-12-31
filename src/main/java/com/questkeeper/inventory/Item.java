package com.questkeeper.inventory;

import java.util.Objects;

/**
 * Base class for all items in the game.
 * 
 * Items have a name, description, weight, and gold value.
 * Subclasses add specific functionality for weapons, armor, consumables, etc.
 * 
 * @author Marc McGough
 * @version 1.0
 */

public class Item {

    /**
     * Categories of items for sorting and filtering.
     */

    public enum ItemType {
        WEAPON("Weapon"),
        ARMOR("Armor"),
        SHIELD("Shield"),
        CONSUMABLE("Consumable"),
        MAGIC_ITEM("Magic Item"),
        TOOL("Tool"),
        TREASURE("Treasure"),
        QUEST_ITEM("Quest Item"),
        MISCELLANEOUS("Miscellaneous");
        
        private final String displayName;
        
        ItemType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }

    /**
     * Rarity levels for items (affects value and power).
     */
    
    public enum Rarity {
        COMMON("Common", "gray"),
        UNCOMMON("Uncommon", "green"),
        RARE("Rare", "blue"),
        VERY_RARE("Very Rare", "purple"),
        LEGENDARY("Legendary", "orange"),
        ARTIFACT("Artifact", "red");
        
        private final String displayName;
        private final String color;
        
        Rarity(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }