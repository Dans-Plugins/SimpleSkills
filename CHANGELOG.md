# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- Unit tests for `PlayerRecord` (skill levels, experience, overall level, save/load) and `ExperienceCalculator`

### Fixed
- Broken `Ponder` dependency coordinates in `pom.xml` that made the project (and CI) fail to build: the pinned tag `v0.14-alpha-2` no longer exists upstream, and the `groupId`/`artifactId` combination was never resolvable via jitpack for this repository

## [2.4.1]

### Added
- 20 skills: Athlete, Boating, Breeding, Cardio, Crafting, Digging, Dueling, Enchanting, Farming, Fishing, Floriculture, Gliding, Hardiness, Mining, Monster Hunting, Pyromaniac, Quarrying, Riding, Strength, Lumberjack
- Per-skill activation toggles in `config.yml`
- Configurable max level, base XP requirement, and XP increase factor
- Level-up and benefit alerts (configurable)
- `/ss stats`, `/ss top`, `/ss info`, `/ss skill` player commands
- `/ss force`, `/ss force wipe`, `/ss force activate`, `/ss force deactivate`, `/ss reload` operator commands
- Docker Compose development server setup
