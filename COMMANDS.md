# SimpleSkills - Commands Reference

This document provides a comprehensive list of all commands available in the SimpleSkills plugin.

## Table of Contents

- [Command Aliases](#command-aliases)
- [General Commands](#general-commands)
- [Admin Commands](#admin-commands)

## Command Aliases

The main SimpleSkills command can be accessed using any of the following aliases:

- `/simpleskills`
- `/ss`
- `/skills`

---

## General Commands

### `/ss help`

**Permission:** `ss.help` (default: true)  
**Description:** Displays a list of helpful commands and usage information.  
**Usage:** `/ss help`

---

### `/ss info [player]`

**Permission:** `ss.info` (default: true)  
**Description:** Displays skill levels and experience for a player. If no player name is provided, shows your own skills.  
**Usage:**
- `/ss info` – View your own skills
- `/ss info <playerName>` – View another player's skills

---

### `/ss skill <skillName>`

**Permission:** `ss.skill` (default: true)  
**Description:** Displays detailed information about a specific skill, including its active status, max level, base experience requirement, and experience increase factor.  
**Usage:** `/ss skill <skillName>`  
**Example:** `/ss skill Mining`

---

### `/ss top [skillName]`

**Permission:** `ss.top` (default: true)  
**Description:** Displays the top players ranked by skill level. If a skill name is provided, shows the leaderboard for that skill specifically.  
**Usage:**
- `/ss top` – View top players overall
- `/ss top <skillName>` – View top players in a specific skill

**Example:** `/ss top Farming`

---

### `/ss stats`

**Permission:** `ss.stats` (default: true)  
**Description:** Displays server-wide statistics, including the total number of skills, player records, and unknown skills.  
**Usage:** `/ss stats`

---

## Admin Commands

### `/ss reload`

**Permission:** `ss.reload` (default: op)  
**Description:** Reloads the plugin configuration and language files without restarting the server.  
**Usage:** `/ss reload`

---

### `/ss force wipe`

**Permission:** `ss.force.wipe` (default: op, console only)  
**Description:** Clears all player skill records from the server. **This action is irreversible.**  
**Usage:** `/ss force wipe`  
**Notes:** This command can only be run from the console.

---

### `/ss force activate <skillName>`

**Permission:** `ss.force.activate` (default: op)  
**Description:** Activates a skill so it can be gained by players and its benefits can trigger.  
**Usage:** `/ss force activate <skillName>`  
**Example:** `/ss force activate Mining`

---

### `/ss force deactivate <skillName>`

**Permission:** `ss.force.deactivate` (default: op)  
**Description:** Deactivates a skill so players cannot gain experience in it and its benefits will not trigger.  
**Usage:** `/ss force deactivate <skillName>`  
**Example:** `/ss force deactivate MonsterHunting`

---

## Permissions Summary

| Permission | Default | Description |
|---|---|---|
| `ss.help` | Everyone | Use `/ss help` |
| `ss.info` | Everyone | Use `/ss info` |
| `ss.skill` | Everyone | Use `/ss skill` |
| `ss.top` | Everyone | Use `/ss top` |
| `ss.stats` | Everyone | Use `/ss stats` |
| `ss.reload` | Op | Use `/ss reload` |
| `ss.force` | Op | Use `/ss force` |
| `ss.force.wipe` | Op | Use `/ss force wipe` |
| `ss.force.activate` | Op | Use `/ss force activate` |
| `ss.force.deactivate` | Op | Use `/ss force deactivate` |
