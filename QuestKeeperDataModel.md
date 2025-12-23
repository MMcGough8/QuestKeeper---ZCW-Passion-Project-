# QuestKeeper - Data Model (CLI Implementation)

## Overview

This data model shows the core entities and their relationships for the QuestKeeper CLI application. Unlike the database ER diagram, this focuses on **in-memory Java objects** and **YAML file structure** for file-based persistence.

---

## Core Entities (Java Classes)

### **Character** (Player's Character)
```
Character
├── characterId: String
├── name: String
├── race: String
├── characterClass: String
├── level: int
├── experiencePoints: int
├── maxHp: int
├── currentHp: int
├── armorClass: int
├── strength: int
├── dexterity: int
├── constitution: int
├── intelligence: int
├── wisdom: int
├── charisma: int
├── skillProficiencies: List<String>
└── inventory: Inventory
```

**Owns:** Inventory  
**Implements:** Combatant interface

---

### **Inventory** (Character's Items)
```
Inventory
├── items: List<Item>
└── maxWeight: float
```

**Contains:** Items (aggregation - items exist independently)

---

### **Campaign** (Game Content Container)
```
Campaign
├── campaignId: String
├── name: String
├── description: String
├── locations: Map<String, Location>
├── npcs: Map<String, NPC>
├── trials: Map<String, Trial>
├── items: Map<String, Item>
├── monsters: Map<String, Monster>
└── quests: Map<String, Quest>
```

**Owns:** All game content (Locations, NPCs, Trials, Items, Monsters, Quests)  
**Loaded from:** YAML files at startup

---

### **Location** (Places to Visit)
```
Location
├── locationId: String
├── name: String
├── description: String
├── type: String
├── isSafeZone: boolean
├── exits: Map<String, String>          // direction → locationId
├── itemsOnGround: List<String>         // itemIds
└── npcsPresent: List<String>           // npcIds
```

**Has:** Items on ground, NPCs present (references by ID)

---

### **NPC** (Non-Player Character)
```
NPC
├── npcId: String
├── name: String
├── role: String
├── voiceDescription: String
├── greetingText: String
└── dialogueOptions: List<DialogueOption>
```

**Owns:** DialogueOptions

---

### **DialogueOption** (NPC Dialogue)
```
DialogueOption
├── topic: String
├── responseText: String
├── requiredFlag: String                // optional
└── setsFlag: String                    // optional
```

---

### **Trial** (Puzzle Challenge)
```
Trial
├── trialId: String
├── name: String
├── description: String
├── difficulty: int
└── miniGames: List<MiniGame>
```

**Owns:** MiniGames

---

### **MiniGame** (Individual Puzzle)
```
MiniGame
├── miniGameId: String
├── name: String
├── description: String
├── requiredSkill: String               // "Dexterity", "Investigation", etc.
├── difficultyClass: int                // DC for skill check
├── allowRetry: boolean
├── successRewardId: String             // itemId
├── successText: String
├── failureText: String
└── failureDamage: int
```

**Rewards:** Item (by ID reference)

---

### **Item** (Equipment/Consumables)
```
Item
├── itemId: String
├── name: String
├── description: String
├── type: String                        // "weapon", "potion", "magical", etc.
├── rarity: String                      // "common", "uncommon", "rare", etc.
├── isConsumable: boolean
├── goldValue: int
├── weight: float
└── effects: List<ItemEffect>
```

**Owns:** ItemEffects

---

### **ItemEffect** (Item Powers)
```
ItemEffect
├── effectType: String                  // "teleport", "damage_bonus", etc.
├── effectDescription: String
├── modifier: int
└── usesPerRest: String                // "1/day", "1/long rest", "unlimited"
```

---

### **Monster** (Enemy Creature)
```
Monster
├── monsterId: String
├── name: String
├── type: String
├── size: String
├── challengeRating: float
├── armorClass: int
├── maxHp: int
└── currentHp: int
```

**Implements:** Combatant interface

---

### **Encounter** (Combat Scenario)
```
Encounter
├── encounterId: String
├── name: String
├── monsterIds: List<String>            // references to Monster
├── monsterCounts: Map<String, Integer> // monsterId → count
├── experienceReward: int
└── goldReward: int
```

**References:** Monsters (by ID)

---

### **Quest** (Mission)
```
Quest
├── questId: String
├── name: String
├── description: String
├── type: String
├── objectives: List<QuestObjective>
├── experienceReward: int
└── goldReward: int
```

**Owns:** QuestObjectives

---

### **QuestObjective** (Quest Step)
```
QuestObjective
├── objectiveId: String
├── description: String
├── requiredFlag: String
└── isComplete: boolean
```

---

### **GameState** (Player Progress)
```
GameState
├── stateId: String
├── characterId: String                 // reference to Character
├── currentCampaignId: String
├── currentLocationId: String
├── flags: Set<String>                  // "met_norrin", "completed_trial_01", etc.
├── completedTrials: Set<String>        // trialIds
└── activeQuestIds: List<String>        // questIds
```

**References:** Character, Campaign, Location (by ID)  
**Tracks:** All game progress via flags

---

## Relationship Diagram

```
Campaign
├── owns Location (many)
├── owns NPC (many)
├── owns Trial (many)
├── owns Item (many)
├── owns Monster (many)
└── owns Quest (many)

Character
├── owns Inventory (one)
└── implements Combatant

Inventory
└── contains Item (many, by reference)

Location
├── has Item on ground (many, by ID reference)
└── has NPC present (many, by ID reference)

NPC
└── owns DialogueOption (many)

Trial
├── owns MiniGame (many)
└── may reference Encounter (optional, by ID)

MiniGame
└── rewards Item (one, by ID reference)

Encounter
└── references Monster (many, by ID)

Quest
└── owns QuestObjective (many)

GameState
├── references Character (by ID)
├── references Campaign (by ID)
├── references Location (by ID)
└── tracks progress via flags/sets
```

---

## Key Design Patterns

### **Composition (Owns) ◆**
Parent creates and manages child lifecycle:
- Campaign → Location, NPC, Trial, Item, Monster, Quest
- Character → Inventory
- Trial → MiniGame
- NPC → DialogueOption
- Item → ItemEffect
- Quest → QuestObjective

### **Aggregation (Contains) ◇**
Parent references child, but child exists independently:
- Inventory → Item (items can be dropped, exist elsewhere)
- Location → Item (items on ground)
- Location → NPC (NPCs can move)

### **Association (References) →**
One object knows about another via ID:
- MiniGame → Item (reward by ID)
- Encounter → Monster (by ID and count)
- GameState → Character, Campaign, Location (by ID)

### **Interface Implementation**
Both Character and Monster implement Combatant

---

## Data Storage Strategy

### **Runtime (In-Memory)**
All entities loaded into RAM when game starts:
```java
Campaign campaign = CampaignLoader.load("muddlebrook");
Character character = SaveLoadManager.loadCharacter("save_01.yaml");
GameState gameState = new GameState(character, campaign);
```

### **Persistence (YAML Files)**

#### **Campaign Content** (Read-Only)
```
resources/data/campaigns/muddlebrook/
├── campaign.yaml          # Campaign metadata
├── locations.yaml         # All locations with exits
├── npcs.yaml             # All NPCs with dialogue
├── trials.yaml           # All trials with minigames
├── items.yaml            # All items with effects
├── monsters.yaml         # All monster stats
└── quests.yaml           # All quests with objectives
```

#### **Character Saves** (Read-Write)
```
saves/
├── character_01.yaml     # Character + Inventory + GameState
├── character_02.yaml
└── character_03.yaml
```