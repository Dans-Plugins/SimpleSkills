# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Fixed

- `/ss top <skillName>` now sends the `NoTop` message (`No one is very skilled at %skill%.`) when no player has the skill yet. The command checked the leaderboard for `null`, but `PlayerRecordRepository.getTopPlayerRecords` returns an empty list in that case, so the message was never sent and the sender saw only the `Top-Header` line.

- Players who are online when `/ss force wipe` runs now keep gaining experience. Records are created on join, so the wipe left online players without one, and the experience path logged `A player record wasn't found for <name> while attempting to increment experience.` and dropped the gain on every activity until the player rejoined. A skill now creates a missing record before adding experience to it, as the benefit roll already did. The wipe is also saved to `playerRecords.json` straight away instead of at the next autosave, world save or shutdown, so a crash in between can no longer bring the old records back.

## [2.5.0] – 2026-09-19

The first stable release on the AI-first line; it supersedes the `3.0.0-SNAPSHOT-8-8-2026` snapshot below and includes everything listed there.

### Removed

- The `dansplugins.simpleskills.enums.Triggers` enum, which listed twelve event classes a skill could be triggered by. Nothing has referenced it since each skill began naming its own trigger classes directly, and any Bukkit event class is a valid trigger, so the list neither constrained nor described what the skill system accepts. The rule it left implicit — that a trigger is matched on the event's exact runtime class, so declaring a base class does not catch its subclasses — is now stated on the `AbstractSkill` constructor a skill author already reads.

### Fixed

- The plugin now loads on Minecraft 26.x. The shaded XSeries `8.6.1` parsed the server version with a pattern that required a single-digit major version, so on a `26.1.2` or `26.2` server `XMaterial` failed to initialise and every skill that looks up a material through it — Pyromaniac, Farming, Floriculture, Digging, Gliding, Fishing and Quarrying — failed on every event it handled, with `NoClassDefFoundError: Could not initialize class cryptomorin.xseries.XMaterial` logged each time. XSeries is bumped to `13.7.1`, and the shade filter now names the classes `XMaterial` depends on rather than excluding a list that no longer matches its dependencies; the plugin jar grows from about 340 KB to about 537 KB as a result.

- The minimum-version check on startup now works. It read the server version by parsing the `org.bukkit.craftbukkit.vX_Y_RZ` package name through Ponder's `NMSAssistant`, which Spigot no longer versions, so on every modern server it threw `NumberFormatException` — logged as `Failed to determine NMS version` — and the check never ran. The version now comes from XSeries, the same detection every material lookup already relies on. One consequence: the check's "disable the plugin" branch was previously unreachable, so the plugin started on any server version. On a server older than 1.13, where the material names the skills look up do not exist, the plugin now disables itself instead of failing later.

- The `defaultExperienceIncreaseFactor` set in `config.yml` is now applied. Skills read the setting under the name `defaultDefaultExperienceIncreaseFactor`, which no config file contains, so the lookup always missed and the hardcoded fallback of `1.2` shaped every skill's experience curve no matter what a server owner configured. Servers that had changed the setting will see their configured curve take effect on this upgrade — including a steeper or shallower climb than players have been used to — while servers on the default value are unaffected. The same setting was already read under its correct name for the bStats chart, so the figure reported upstream had not matched the running behaviour either.

- `/ss skill <skillName>` now reports the configured `defaultMaxLevel` rather than a hardcoded `100`. The cap actually enforced has always come from the config, so a server that had changed the setting enforced one number while advertising another.

- `CONFIG.md` described the experience requirement as a multiplier compounded onto the previous level's cost, giving 10, 12 and ~14.4 as the first requirements under the defaults. The requirement is a power curve raised from the level a skill is currently at — `base × level ^ factor` — so those requirements are really 10, 22 and 37. The formula, a table of requirements under the shipped defaults, and the behaviour of the two settings that shape it are now documented, the stale `config-version` default of `0.1` has been corrected to `0.2`, and the `/ss info` sample output in `USER_GUIDE.md` no longer shows totals the curve cannot produce.

- Hardiness now triggers on damage dealt by a mob, another player, a projectile or a block. It declared only `EntityDamageEvent` as a trigger, and a trigger is matched on the event's exact runtime class, so damage from an attacker (an `EntityDamageByEntityEvent`) and damage from a block such as a cactus (an `EntityDamageByBlockEvent`) reached no handler. In game the skill therefore gained experience, and reduced or negated damage, only for damage with nothing behind it — falling, fire, drowning, suffocation, poison — and not for being hit, which is what it is most expected to cover. Note that the skill is now reached far more often than before; its 10% benefit roll and its negation weights are unchanged and are worth reviewing against live play.

- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.

- Growing memory use on long-running servers: every skill kept a reference to every event it had ever handled — and, through it, to that event's block, player and entities — for as long as the server ran. The events were remembered only so that a skill would not act on the same one twice, which is now prevented where the duplicate arose instead, by each skill listening only for the events its triggers accept and each listener taking only the event class it was registered for.

- The `Release` workflow now runs when a release is published rather than when it is created, so a draft that is published later gets its build, and it skips entirely when the published release already carries a `.jar` — the organisation's release automation attaches the exact jar that passed verification, and a rebuild would attach a second, unverified one beside it.

### Added

- The per-skill benefit toggles are now shipped in `config.yml` and documented in `CONFIG.md`. Every skill has checked a `<skill>BenefitEnabled` key — `miningBenefitEnabled`, `monsterHuntingBenefitEnabled`, `lumberjackBenefitEnabled` for Woodcutting, and so on — before granting its benefit, but the key appeared in neither the default config nor the documentation, so the toggle could not be discovered without reading the source. Setting one to `false` stops that skill's benefit while leaving its experience gain and levelling intact, which is what distinguishes it from deactivating the skill under `skills`. A missing key is still treated as `true`, so an existing `config.yml` — which is never rewritten on upgrade — behaves exactly as before until the keys are added by hand.

- The plugin now reports usage events — `startup` on enable, `command` on each of its commands — to the author's trace server so it is known which plugins are in use. Events carry the plugin name, the event name, and the plugin version or command name; nothing about players or the server. Reporting runs off the main thread, never delays a tick, drops silently when the server is unreachable, and is turned off with `usage-reporting.enabled: false` in `config.yml`. The default config carries the plugin's key, so reporting is active out of the box unless turned off — including on servers upgraded from a version before the `usage-reporting` block existed, whose `config.yml` is never rewritten: the plugin reads the bundled defaults for any key the file lacks.

- Usage reporting is disclosed and can be switched off server-wide. On every enable the console states whether reporting is on and how to turn it off; on the first start after upgrading, the `usage-reporting` block is written into an existing `config.yml` so the switch is visible where the console says it is. Beyond this plugin's own `usage-reporting.enabled`, reporting is also off when `plugins/trace/config.yml` sets `enabled: false` — which covers every plugin on the server that reports to trace — or when the environment variable `TRACE_USAGE_REPORTING=off` or `DO_NOT_TRACK=1` is set for the server process. The `README.md` and `CONFIG.md` describe what is sent and each way of turning it off.

- Player records are now written out whenever the server saves a world, in addition to the existing 5-minute autosave and the write on shutdown. A crash therefore costs at most the progress made since the server's own last save — previously up to five minutes of skill progress could be lost, since an unclean shutdown never reaches `onDisable()`. A server fires the event once per world, so consecutive saves less than 5 seconds apart are collapsed into one write rather than rewriting the same file once per world. Unlike the scheduled autosave, this write runs on the main thread, which is where the records are mutated, so it cannot iterate them mid-change. How progress is saved is now described in `USER_GUIDE.md`, where it was previously documented nowhere.

- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get simpleskills --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

## [3.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- SimpleSkills is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `3.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.
- `IllegalStateException` reported when a skill trigger fails now carries the underlying failure as its cause, and the failure is logged through the plugin logger instead of a bare stack trace

### Added
- Unit tests for `PlayerRecord` (skill levels, experience, overall level, save/load) and `ExperienceCalculator`
- Unit tests for `MessageService` (bundled-default fallback, on-disk overrides, saving) and `AbstractSkill` trigger failure reporting

### Fixed
- Broken `Ponder` dependency coordinates in `pom.xml` that made the project (and CI) fail to build: the pinned tag `v0.14-alpha-2` no longer exists upstream, and the `groupId`/`artifactId` combination was never resolvable via jitpack for this repository
- Silk Touch mining/quarrying/digging/woodcutting/floriculture/pyromaniac experience farming exploit: breaking a block that a player placed (e.g. a Silk Touch-harvested ore placed back down) no longer grants skill experience or rewards
- `IllegalArgumentException` thrown (and logged to console) by the Crafting skill's reward handler when `CraftItemEvent#getRecipe()` reports no recipe (e.g. certain merge/repair crafts); the reward is now skipped instead
- `IllegalStateException: Failed to trigger '<skill>' with event '<event>'!` raised on servers whose `message.yml` predates a message key the plugin now uses (the file on disk is never rewritten on upgrade): message lookups now fall back to the copy of `message.yml` bundled in the jar, while any value present on disk still takes precedence

## [2.4.1]

### Added
- 20 skills: Athlete, Boating, Breeding, Cardio, Crafting, Digging, Dueling, Enchanting, Farming, Fishing, Floriculture, Gliding, Hardiness, Mining, Monster Hunting, Pyromaniac, Quarrying, Riding, Strength, Lumberjack
- Per-skill activation toggles in `config.yml`
- Configurable max level, base XP requirement, and XP increase factor
- Level-up and benefit alerts (configurable)
- `/ss stats`, `/ss top`, `/ss info`, `/ss skill` player commands
- `/ss force`, `/ss force wipe`, `/ss force activate`, `/ss force deactivate`, `/ss reload` operator commands
- Docker Compose development server setup
