# SimpleSkills - User Guide

This guide provides step-by-step instructions for getting started with SimpleSkills and covers common usage scenarios.

## Table of Contents

- [Getting Started](#getting-started)
- [Skills Overview](#skills-overview)
- [Common Scenarios](#common-scenarios)
  - [Checking Your Skills](#checking-your-skills)
  - [Leveling Up a Skill](#leveling-up-a-skill)
  - [Viewing Skill Details](#viewing-skill-details)
  - [Checking the Leaderboard](#checking-the-leaderboard)
- [Tips and Best Practices](#tips-and-best-practices)

## Getting Started

### Understanding the Basics

SimpleSkills adds a skill progression system to Minecraft. As you perform in-game activities, your corresponding skills gain experience and level up. Higher skill levels unlock beneficial effects that enhance gameplay.

**Key Concepts:**

- **Skills** – Each skill is tied to a specific activity (e.g., Mining, Farming, Fishing).
- **Experience (EXP)** – Performing the activity associated with a skill grants experience.
- **Levels** – Accumulating enough experience causes a skill to level up.
- **Benefits** – At higher levels, skills can trigger special rewards (e.g., auto-smelting ores, double drops, speed boosts).

### First Steps

1. Join a server with SimpleSkills installed.
2. Perform any in-game activity (mining, farming, fishing, etc.) to start gaining experience.
3. Run `/ss info` to see your current skill levels.
4. Run `/ss help` to see a full list of available commands.

---

## Skills Overview

SimpleSkills includes 20 skills, each tied to a specific activity. Benefits trigger with a chance based on skill level.

| Skill | Activity | Benefit |
|---|---|---|
| **Athlete** | Swimming | Dolphin's Grace effect |
| **Boating** | Riding boats | Increased boat speed |
| **Breeding** | Breeding mobs | Double breeding experience |
| **Cardio** | Sprinting | Speed boost |
| **Crafting** | Crafting items | Bonus crafting materials |
| **Digging** | Digging with a shovel | Double drops, special items, or bonus XP |
| **Dueling** | Killing players | Absorption effect |
| **Enchanting** | Enchanting items | Enhanced or bonus enchantments |
| **Farming** | Harvesting crops | Auto-replant, bonus XP, or double crop drops |
| **Fishing** | Fishing | Golden Apple reward |
| **Floriculture** | Breaking or planting flowers | Bone meal drop |
| **Gliding** | Flying with elytra | Firework rocket boost |
| **Hardiness** | Taking damage | Damage reduction or negation |
| **Mining** | Mining ores with a pickaxe | Auto-smelt, double drops, or bonus XP |
| **Monster Hunting** | Killing monsters | Nearby monsters of the same type are revealed |
| **Pyromaniac** | Placing fire | Fire resistance and speed buffs |
| **Quarrying** | Breaking stone with a pickaxe | Double drops, special materials, or bonus XP |
| **Riding** | Riding animals | Speed boost for mount |
| **Strength** | Hitting entities | Strength boost |
| **Woodcutting** | Chopping wood with an axe | Double wood drops or bonus XP |

---

## Common Scenarios

### Checking Your Skills

**Goal:** View your current skill levels and experience.

**Steps:**

1. Run `/ss info` to see all your skills, their current level, and experience progress.
2. To view another player's skills, run `/ss info <playerName>`.

**Example output:**
```
=== Skills of Steve ===
Mining - LVL: 15 - EXP: 230/276
Farming - LVL: 8 - EXP: 45/99
Woodcutting - LVL: 3 - EXP: 10/17
```

---

### Leveling Up a Skill

**Goal:** Increase your level in a specific skill.

**Steps:**

1. Identify the activity associated with the skill you want to level (see [Skills Overview](#skills-overview)).
2. Perform that activity repeatedly to accumulate experience.
3. When you have enough experience, you will level up and see a level-up message in chat.
4. Run `/ss info` to confirm your new level.

**Example:** To level up **Mining**, mine ore blocks using a pickaxe.

---

### Viewing Skill Details

**Goal:** Learn more about a specific skill's configuration.

**Steps:**

1. Run `/ss skill <skillName>` to see the skill's current status, max level, base experience requirement, and experience increase factor.

**Example:**
```
/ss skill Mining
```

**Example output:**
```
=== Mining ===
Active: true
Max Level: 100
Base Experience Requirement: 10
Experience Increase Factor: 1.2
```

---

### Checking the Leaderboard

**Goal:** See who the top players are in a skill.

**Steps:**

1. Run `/ss top <skillName>` to see the top players in that skill.
2. Run `/ss top` to see overall top players.

**Example:**
```
/ss top Mining
```

**Example output:**
```
Top Players in Mining
1: Steve - LVL: 42
2: Alex - LVL: 37
3: Notch - LVL: 25
```

---

## Tips and Best Practices

- **Focus on one skill at a time** to reach higher levels faster and unlock benefits sooner.
- **Mining** and **Woodcutting** are great starter skills since those activities are very common in early gameplay.
- **Farming** with auto-replant makes sustained farming much more efficient at higher levels.
- **Hardiness** is useful for survival servers since it can reduce or negate incoming damage.
- Run `/ss stats` to see server-wide statistics, including the total number of skills and player records.
