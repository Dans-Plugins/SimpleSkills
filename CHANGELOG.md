# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Removed

- The `dansplugins.simpleskills.enums.Triggers` enum, which listed twelve event classes a skill could be triggered by. Nothing has referenced it since each skill began naming its own trigger classes directly, and any Bukkit event class is a valid trigger, so the list neither constrained nor described what the skill system accepts. The rule it left implicit — that a trigger is matched on the event's exact runtime class, so declaring a base class does not catch its subclasses — is now stated on the `AbstractSkill` constructor a skill author already reads.

### Fixed

- The `defaultExperienceIncreaseFactor` set in `config.yml` is now applied. Skills read the setting under the name `defaultDefaultExperienceIncreaseFactor`, which no config file contains, so the lookup always missed and the hardcoded fallback of `1.2` shaped every skill's experience curve no matter what a server owner configured. Servers that had changed the setting will see their configured curve take effect on this upgrade — including a steeper or shallower climb than players have been used to — while servers on the default value are unaffected. The same setting was already read under its correct name for the bStats chart, so the figure reported upstream had not matched the running behaviour either.

- `/ss skill <skillName>` now reports the configured `defaultMaxLevel` rather than a hardcoded `100`. The cap actually enforced has always come from the config, so a server that had changed the setting enforced one number while advertising another.

- `CONFIG.md` described the experience requirement as a multiplier compounded onto the previous level's cost, giving 10, 12 and ~14.4 as the first requirements under the defaults. The requirement is a power curve raised from the level a skill is currently at — `base × level ^ factor` — so those requirements are really 10, 22 and 37. The formula, a table of requirements under the shipped defaults, and the behaviour of the two settings that shape it are now documented, the stale `config-version` default of `0.1` has been corrected to `0.2`, and the `/ss info` sample output in `USER_GUIDE.md` no longer shows totals the curve cannot produce.

- Hardiness now triggers on damage dealt by a mob, another player, a projectile or a block. It declared only `EntityDamageEvent` as a trigger, and a trigger is matched on the event's exact runtime class, so damage from an attacker (an `EntityDamageByEntityEvent`) and damage from a block such as a cactus (an `EntityDamageByBlockEvent`) reached no handler. In game the skill therefore gained experience, and reduced or negated damage, only for damage with nothing behind it — falling, fire, drowning, suffocation, poison — and not for being hit, which is what it is most expected to cover. Note that the skill is now reached far more often than before; its 10% benefit roll and its negation weights are unchanged and are worth reviewing against live play.

- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.

### Added

- Player records are now written out whenever the server saves a world, in addition to the existing 5-minute autosave and the write on shutdown. A crash therefore costs at most the progress made since the server's own last save — previously up to five minutes of skill progress could be lost, since an unclean shutdown never reaches `onDisable()`. A server fires the event once per world, so consecutive saves less than 5 seconds apart are collapsed into one write rather than rewriting the same file once per world. Unlike the scheduled autosave, this write runs on the main thread, which is where the records are mutated, so it cannot iterate them mid-change. How progress is saved is now described in `USER_GUIDE.md`, where it was previously documented nowhere.

- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get simpleskills --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

### Fixed

- Growing memory use on long-running servers: every skill kept a reference to every event it had ever handled — and, through it, to that event's block, player and entities — for as long as the server ran. The events were remembered only so that a skill would not act on the same one twice, which is now prevented where the duplicate arose instead, by each skill listening only for the events its triggers accept and each listener taking only the event class it was registered for.

## [3.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- SimpleSkills is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `3.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.

### Added
- Unit tests for `PlayerRecord` (skill levels, experience, overall level, save/load) and `ExperienceCalculator`
- Unit tests for `MessageService` (bundled-default fallback, on-disk overrides, saving) and `AbstractSkill` trigger failure reporting

### Fixed
- Broken `Ponder` dependency coordinates in `pom.xml` that made the project (and CI) fail to build: the pinned tag `v0.14-alpha-2` no longer exists upstream, and the `groupId`/`artifactId` combination was never resolvable via jitpack for this repository
- Silk Touch mining/quarrying/digging/woodcutting/floriculture/pyromaniac experience farming exploit: breaking a block that a player placed (e.g. a Silk Touch-harvested ore placed back down) no longer grants skill experience or rewards
- `IllegalArgumentException` thrown (and logged to console) by the Crafting skill's reward handler when `CraftItemEvent#getRecipe()` reports no recipe (e.g. certain merge/repair crafts); the reward is now skipped instead
- `IllegalStateException: Failed to trigger '<skill>' with event '<event>'!` raised on servers whose `message.yml` predates a message key the plugin now uses (the file on disk is never rewritten on upgrade): message lookups now fall back to the copy of `message.yml` bundled in the jar, while any value present on disk still takes precedence

### Changed
- `IllegalStateException` reported when a skill trigger fails now carries the underlying failure as its cause, and the failure is logged through the plugin logger instead of a bare stack trace

## [2.4.1]

### Added
- 20 skills: Athlete, Boating, Breeding, Cardio, Crafting, Digging, Dueling, Enchanting, Farming, Fishing, Floriculture, Gliding, Hardiness, Mining, Monster Hunting, Pyromaniac, Quarrying, Riding, Strength, Lumberjack
- Per-skill activation toggles in `config.yml`
- Configurable max level, base XP requirement, and XP increase factor
- Level-up and benefit alerts (configurable)
- `/ss stats`, `/ss top`, `/ss info`, `/ss skill` player commands
- `/ss force`, `/ss force wipe`, `/ss force activate`, `/ss force deactivate`, `/ss reload` operator commands
- Docker Compose development server setup
