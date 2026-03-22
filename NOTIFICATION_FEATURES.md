# Notification Features

This document describes the enhanced notification system for SimpleSkills, making level-ups and skill progression more obvious and engaging for players.

## Overview

The plugin now provides multiple types of visual and audio feedback when players gain experience, level up, or learn new skills. These notifications can be individually toggled in the configuration.

## Notification Types

### 1. Title Notifications (Level Up)
When a player levels up a skill, they receive a prominent title notification that appears in the center of their screen:

- **Main Title**: `"Level Up!"` (displayed in bold gold text)
- **Subtitle**: Shows the skill name and new level (e.g., `"Mining - Level 5"`)
- **Duration**: Appears for 3.5 seconds (10 ticks fade-in, 70 ticks display, 20 ticks fade-out)

**Configuration**: `titleNotifications: true/false`

### 2. Title Notifications (New Skill)
When a player learns a new skill for the first time:

- **Main Title**: `"New Skill!"` (displayed in bold gold text)
- **Subtitle**: Shows the skill name (e.g., `"Mining"`)
- **Duration**: Same as level-up notifications

**Configuration**: `titleNotifications: true/false`

### 3. Action Bar Notifications (Experience Gain)
When a player gains experience in a skill, a subtle notification appears above their hotbar:

- **Message**: `"+1 Mining XP"` (shown in green text)
- **Location**: Action bar (above the hotbar, doesn't interrupt gameplay)
- **Duration**: Brief appearance, automatically fades

**Configuration**: `actionBarNotifications: true/false`

### 4. Sound Effects
When players level up or learn new skills, they hear:

- **Sound**: `ENTITY_PLAYER_LEVELUP` (the standard Minecraft level-up sound)
- **Volume**: 1.0 (normal volume)
- **Pitch**: 1.0 (normal pitch)

**Configuration**: `soundNotifications: true/false`

### 5. Chat Messages
Traditional chat messages are still sent alongside the new notifications:

**Level Up:**
```
You've leveled up your Mining skill to 5
Type /ss help for more info
```

**New Skill:**
```
You've learned the Mining skill. Type /ss info to view your skills.
```

These messages provide helpful reminders about available commands.

## Configuration

All notification types can be enabled or disabled in `config.yml`:

```yaml
# Notification settings
titleNotifications: true        # Show title popup on level up/new skill
actionBarNotifications: true    # Show action bar for experience gains
soundNotifications: true        # Play sound on level up/new skill
levelUpAlert: true             # Master toggle for level-up alerts (existing)
```

## Message Customization

All notification messages can be customized in `message.yml`:

```yaml
# Title notifications
LearnedSkillTitle: "&6&lNew Skill!"
LearnedSkillSubtitle: "&a%skill%"
LevelUpTitle: "&6&lLevel Up!"
LevelUpSubtitle: "&a%skill% &7- &bLevel %level%"

# Chat messages
LearnedSkill: "&aYou've learned the %skill% skill. Type /ss info to view your skills."
LevelUp: "&aYou've leveled up your %skill% skill to %level%"
LevelUpTip: "&7Type &e/ss help &7for more info"

# Action bar
ExperienceGain: "&a+1 %skill% XP"
```

### Color Codes
- `&6` = Gold
- `&l` = Bold
- `&a` = Green
- `&7` = Gray
- `&b` = Aqua
- `&e` = Yellow

## Implementation Details

### When Notifications Trigger

1. **Learning a Skill** (`learnSkill()` method):
   - Chat message with command tip
   - Title notification (if enabled)
   - Level-up sound (if enabled)

2. **Leveling Up** (`levelUp()` method):
   - Chat message with skill and level
   - Helpful tip about `/ss help`
   - Title notification (if enabled)
   - Level-up sound (if enabled)

3. **Gaining Experience** (`incrementExperience()` method):
   - Action bar notification (if enabled)
   - No chat spam or sounds (designed to be non-intrusive)

### Technical Notes

- All notifications respect the `levelUpAlert` master configuration setting
- Title notifications use the Spigot 1.18+ `Player.sendTitle()` API
- Action bar notifications use the Spigot 1.18+ `Player.sendActionBar()` API
- Default values are provided if configuration keys are missing
- Null checks prevent crashes if message keys are undefined

## Player Experience

### Before (Old System)
- Small chat message that could be easily missed
- No visual distinction between experience gains and level-ups
- Easy to miss important progression milestones

### After (New System)
- Impossible to miss level-ups with large title popups
- Satisfying audio feedback reinforces achievement
- Action bar keeps you informed of XP gains without spam
- Helpful command reminders encourage exploration of features
- All feedback types work together for a cohesive experience

## Backward Compatibility

- All new features are **opt-in by default** (enabled in config.yml)
- Existing chat messages are preserved
- Server owners can disable any notification type independently
- Works with Minecraft/Spigot 1.18.1+ (uses modern Bukkit APIs)
- Compatible with existing skill system and player records
