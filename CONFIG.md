# SimpleSkills - Configuration Guide

This document provides detailed information about all configuration options available in SimpleSkills.

## Table of Contents

- [config.yml](#configyml)
  - [General Settings](#general-settings)
  - [Alert Settings](#alert-settings)
  - [Skill Activation](#skill-activation)
- [message.yml](#messageyml)

---

## config.yml

The `config.yml` file is located in `plugins/SimpleSkills/config.yml` on your server.

### General Settings

#### `config-version`

**Type:** String  
**Default:** `0.1`  
**Description:** Internal version identifier for the config file. Do not modify this manually. If this does not match the expected version, you will see an error in the server console prompting you to update your config.

---

#### `debugMode`

**Type:** Boolean  
**Default:** `false`  
**Description:** When `true`, enables additional debug logging to help diagnose issues.

---

#### `defaultMaxLevel`

**Type:** Integer  
**Default:** `100`  
**Description:** The maximum level a skill can reach.  
**Range:** Any positive integer

---

#### `defaultBaseExperienceRequirement`

**Type:** Integer  
**Default:** `10`  
**Description:** The amount of experience required to reach level 2. Each subsequent level requires more experience based on the `defaultExperienceIncreaseFactor`.  
**Range:** Any positive integer

---

#### `defaultExperienceIncreaseFactor`

**Type:** Float  
**Default:** `1.2`  
**Description:** The multiplier applied to the experience requirement at each level. For example, with a base requirement of 10 and a factor of 1.2, level 2 requires 10 XP, level 3 requires 12 XP, level 4 requires ~14.4 XP, and so on.  
**Range:** Any value greater than 1.0

---

### Alert Settings

#### `levelUpAlert`

**Type:** Boolean  
**Default:** `true`  
**Description:** When `true`, players receive a chat message when they level up a skill.

---

#### `benefitAlert`

**Type:** Boolean  
**Default:** `true`  
**Description:** When `true`, players receive a chat message when a skill benefit triggers.

---

### Skill Activation

Each skill can be individually activated or deactivated. Skills that are deactivated will not grant experience and will not trigger their benefits.

Skill activation states are stored under the `skills` key in `config.yml`:

```yaml
skills:
  Mining:
    active: true
  Farming:
    active: false
```

**Available skills and their config keys:**

| Skill | Config Key |
|---|---|
| Athlete | `skills.Athlete.active` |
| Boating | `skills.Boating.active` |
| Breeding | `skills.Breeding.active` |
| Cardio | `skills.Cardio.active` |
| Crafting | `skills.Crafting.active` |
| Digging | `skills.Digging.active` |
| Dueling | `skills.Dueling.active` |
| Enchanting | `skills.Enchanting.active` |
| Farming | `skills.Farming.active` |
| Fishing | `skills.Fishing.active` |
| Floriculture | `skills.Floriculture.active` |
| Gliding | `skills.Gliding.active` |
| Hardiness | `skills.Hardiness.active` |
| Mining | `skills.Mining.active` |
| Monster Hunting | `skills."Monster Hunting".active` |
| Pyromaniac | `skills.Pyromaniac.active` |
| Quarrying | `skills.Quarrying.active` |
| Riding | `skills.Riding.active` |
| Strength | `skills.Strength.active` |
| Woodcutting | `skills.Lumberjack.active` |

> **Note:** Skill names with spaces (e.g., Monster Hunting) must be quoted in YAML:
> ```yaml
> skills:
>   "Monster Hunting":
>     active: false
> ```

> **Note:** The Woodcutting skill uses `Lumberjack` as its internal config key. Use `Lumberjack` when referencing it in `config.yml` or with `/ss force activate`/`/ss force deactivate`.

Skills can also be toggled at runtime using the admin commands `/ss force activate <skillName>` and `/ss force deactivate <skillName>` without restarting the server.

---

## message.yml

The `message.yml` file is located in `plugins/SimpleSkills/message.yml` on your server. It controls all user-facing messages displayed by the plugin.

### `message-version`

**Type:** String  
**Default:** `0.2`  
**Description:** Internal version identifier for the message file. Do not modify this manually.

---

### Message Keys

All message strings support Minecraft color codes using the `&` prefix (e.g., `&a` for green, `&b` for aqua, `&c` for red).

The following placeholders are available in specific messages:

| Placeholder | Used In | Description |
|---|---|---|
| `%version%` | `Default-Command` | The plugin version |
| `%author%` | `Default-Command` | The plugin author |
| `%nos%` | `Stats` | Number of skills |
| `%nopr%` | `Stats` | Number of player records |
| `%uns%` | `Stats` | Number of unknown skills |
| `%skill%` | `NoTop`, `Top-Header` | Skill name |
| `%rank%` | `Top-Body` | Player rank position |
| `%player%` | `Top-Body`, `SendInfo-Header` | Player name |
| `%top%` | `Top-Body` | Top player's level |
| `%skill%` | `SendInfo-Body` | Skill name |
| `%level%` | `SendInfo-Body`, `LevelUp` | Current skill level |
| `%min%` | `SendInfo-Body` | Current experience |
| `%max%` | `SendInfo-Body` | Experience required for next level |
| `%skillname%` | `Skill-Info` | Skill name |
| `%active%` | `Skill-Info` | Whether the skill is active |
| `%mlevel%` | `Skill-Info` | Max level of the skill |
| `%ber%` | `Skill-Info` | Base experience requirement |
| `%eif%` | `Skill-Info` | Experience increase factor |
