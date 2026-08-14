# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Fixed

- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.

### Added

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
