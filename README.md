# QuestKeeper: Auto-DM

A CLI-based D&D 5e adventure game that automates the Dungeon Master role, bringing tabletop roleplaying to life through intelligent narrative generation and rules management.

## Overview

QuestKeeper is a Java-based application designed to serve as an automated Dungeon Master for D&D 5th Edition gameplay. The project emphasizes beginner-friendly mechanics while maintaining compatibility with core 5e rules, featuring natural language processing for enhanced player interaction.

## Features

- **Automated DM System** — Handles narrative progression, NPC interactions, combat encounters, and puzzle resolution
- **Character Creation** — Full D&D 5e character generation with race, class, and background selection
- **Dynamic Encounters** — Scalable combat and puzzle systems that adapt to party composition
- **State Management** — Persistent game state tracking with YAML-based data storage
- **Campaign Support** — Modular campaign architecture supporting multiple adventure settings

## Included Campaigns

### Muddlebrook: Harlequin Trials

A comedic, chaotic mystery campaign featuring puzzle rooms, mini-games, and a theatrical mastermind villain.

**Tone:** Scooby-Doo mystery meets Monty Python absurdity with dark theatrical traps (cartoon menace, never gory)

**Core Loop:**
1. Safe Hub Scene (tavern/shop/town square)
2. Lead (rumor, witness, strange event, villain taunt)
3. Trial Location (puzzle room with 3–6 mini-games)
4. Encounter (clockwork minions, time-ripple hazards, rival contestants)
5. Reward (magical item/boon)
6. Stinger (Machinist message, mayor hint, new location unlocked)

**Key Locations:**
- Drunken Dragon Inn (hub)
- Town Hall (Trial #1)
- Clocktower Hill (Trial #2+)
- Old Market Row (shops, gossip, side quests)
- The Fogline Docks (smuggling, watery side plots)
- The Whisperwood Edge (forest, time oddities)

**Notable NPCs:**
- **Norrin the Bard** — Rumors, comedic relief, foreshadowing through song
- **Mara Ember** — Grounded bartender, practical advice
- **Darius** — Dark recluse, mentor figure with warnings
- **Elara** — Clockwork Curios shopkeeper, upgrades and rewards
- **The Harlequin Machinist** — Theatrical villain who never fights fair

**Unique Monsters:**
- Clockwork Critters (CR 1/4) — Item-stealing constructs
- Confetti Ooze (CR 1/2) — Glitter-bursting ooze with humiliation damage
- Time Ripple Hazard — Environmental puzzle encounter

---

### Eberron: Shards of the Fallen Sky

A myth-heavy, puzzle-forward campaign exploring cosmic origins and ancient powers.

**Core Myth:**
- **Eberron** — The Guardian dragon who became the world itself
- **Fybris** — The Devourer, imprisoned within Eberron's body
- **Astrael** — The Fallen Sister whose shattered body became the dragonshards and magic

**Core Theme:** *The deeper you go, the darker it gets.*

**Campaign Start:** The Olympic Convergence Games — a competition that secretly scouts for heroes with dormant potential and shard resonance.

**Dragonshards (Reward System):**
| Shard | Passive Bonus | Active Ability |
|-------|---------------|----------------|
| Might | +1 Strength | +1d6 damage (1/long rest) |
| Insight | +1 Investigation/Perception | Ask DM one truthful hint (1/long rest) |
| Inspiration | Auto-gain Inspiration per fight | Give Inspiration to all allies (1/day) |
| Speed | +5 ft movement | Dash as bonus action (1/long rest) |
| Resolve | +1 to saving throws | Turn failed save to success (1/day) |
| Shadow | Advantage on Stealth in dim light | Invisibility until end of next turn (1/day) |

**Major Factions:**
- **The Shardwardens** — Ancient order keeping shards separate
- **The Depthbound** — Cultists seeking to free Fybris
- **The Skybound Observers** — Scholars tracking Astrael's remains (the stars)

---

## Technical Architecture

### Tech Stack
- **Language:** Java 17+
- **Build Tool:** Maven
- **Data Persistence:** SnakeYAML
- **Testing:** JUnit 5

### Data Model Components
- **Locations** — Description, NPCs, encounter tables, puzzle lists
- **NPCs** — Voice, personality tags, goals, secrets, sample dialogue
- **Mini-games** — Trigger → required roll → success reward → fail consequence
- **Encounters** — Creatures/hazards with scaling parameters
- **State Flags** — Boolean tracking (e.g., `met_norrin`, `solved_trial1_clock`, `has_blinkstep_spark`)
- **Scene Scripts** — Read-aloud intros and villain stingers

### Project Structure
```
questkeeper/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/questkeeper/
│   │   │       ├── core/           # Game engine and state management
│   │   │       ├── character/      # Character creation and management
│   │   │       ├── combat/         # Combat system and encounters
│   │   │       ├── narrative/      # Story and dialogue systems
│   │   │       ├── puzzle/         # Mini-game and puzzle mechanics
│   │   │       └── data/           # YAML loaders and data models
│   │   └── resources/
│   │       ├── campaigns/          # Campaign data files
│   │       ├── creatures/          # Monster stat blocks
│   │       └── items/              # Magic item definitions
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## AI DM Guidelines

The Auto-DM system follows these principles:

1. **Always ask for rolls** on uncertain actions
2. **Keep scenes cinematic**, not mechanical dumps
3. **Don't reveal hidden structure** to players
4. **Use comedic consequences** over lethal outcomes early
5. **Reward curiosity** and creative problem-solving
6. Every trial can be **won without violence**
7. Every trial can be **failed without death** (unless reckless)

## Difficulty Scaling

| Factor | Adjustment |
|--------|------------|
| Boss stats | +2 AC / +15 HP per tier |
| Minions | +1–2 extra per additional player |
| DCs | 10–12 (early) → 13–15 (mid) → 16–18 (late) |
| Failures | Add conditions (slowed, blinded) instead of raw damage |

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation
```bash
git clone https://github.com/yourusername/questkeeper.git
cd questkeeper
mvn clean install
```

### Running the Game
```bash
mvn exec:java -Dexec.mainClass="com.questkeeper.Main"
```

## Roadmap

- [ ] Core character creation system
- [ ] Basic combat engine
- [ ] Muddlebrook Trial #1 implementation
- [ ] NPC dialogue system
- [ ] State persistence
- [ ] Eberron Olympic Games trials
- [ ] Natural language command parsing
- [ ] AI-enhanced narrative generation
