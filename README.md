# SimpleSkills

## Description

SimpleSkills is an open source Minecraft plugin that systematically introduces skills into the game. As players perform in-game activities, they gain experience in corresponding skills and unlock beneficial effects. There are both useful and situational skills, and server owners can configure which skills are active and adjust experience progression to suit their server.

## Supported Minecraft Versions
This plugin is supported on the Minecraft versions listed in [`minecraft-versions.json`](minecraft-versions.json): currently **1.19.4**, **1.21.11**, **26.2** and **26.3** (Spigot and its forks). Every stable release is booted on a real server of each of these versions before it is published, and every build checks that the plugin only uses Bukkit API that exists on all of them. Other versions from 1.19.4 onwards are expected to work but are not tested. To support another version, add it to the file: both checks pick it up.

## Installation

1. You can download the plugin from [this page](https://www.spigotmc.org/resources/simpleskills.98039/).
2. Once downloaded, place the jar in the plugins folder of your server files.
3. Restart your server.

## Works Well With
SimpleSkills is part of the **survival flavour** set of Dan's Plugins. These are companion plugins that suit the same kind of server and run side by side; SimpleSkills does not depend on or call into any of them.

- [Food Spoilage](https://github.com/Dans-Plugins/FoodSpoilage) ([SpigotMC](https://www.spigotmc.org/resources/food-spoilage.81507/), `/dpm get foodspoilage`): food goes bad over time.
- [Wild Pets](https://github.com/Dans-Plugins/Wild-Pets) ([SpigotMC](https://www.spigotmc.org/resources/wild-pets.95800/), `/dpm get wildpets`): players tame any entity and keep it as a pet.
- [More Recipes](https://github.com/Dans-Plugins/More-Recipes) ([SpigotMC](https://www.spigotmc.org/resources/more-recipes.81832/), `/dpm get morerecipes`): recipes for items that cannot be crafted in vanilla.
- [Medieval Cookery](https://github.com/Dans-Plugins/Medieval-Cookery) (no SpigotMC page, no stable release yet): cooking recipes for custom foods, defined by the server owner.

Running a medieval roleplay server? The [Medieval Roleplay Engine](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine#works-well-with) set lists the plugins for that.

Every plugin above is listed on [dansplugins.com](https://dansplugins.com). SimpleSkills is listed at [dansplugins.com/resources/simple-skills](https://dansplugins.com/resources/simple-skills) and can be installed in game with [Dan's Plugin Manager](https://github.com/Dans-Plugins/Dans-Plugin-Manager): `/dpm get simpleskills`.

## Usage

### Documentation

- [User Guide](USER_GUIDE.md) - Getting started and common scenarios
- [Commands Reference](COMMANDS.md) - Complete list of all commands
- [Configuration Guide](CONFIG.md) - Detailed config options

### Wiki & Additional Resources

- [FAQ](https://github.com/Dans-Plugins/SimpleSkills/wiki/FAQ)

## Support

You can find the support discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/Dans-Plugins/SimpleSkills/issues/new/choose).

- [Known Bugs](https://github.com/Dans-Plugins/SimpleSkills/issues?q=is%3Aopen+is%3Aissue+label%3Abug)

## Contributing

- [Notes for Developers](https://github.com/Dans-Plugins/SimpleSkills/wiki/Developer-Notes)

## Development

### Test Server

For development purposes, a Docker-based test server is available.

#### Setup

1. Copy `sample.env` to `.env` and configure as needed.
2. Install the Ponder dependency (required once per machine):
   ```bash
   mvn install:install-file -Dfile=dependencies/ponder-v0.14-alpha-2.jar -DgroupId=preponderous -DartifactId=ponder -Dversion=v0.14-alpha-2 -Dpackaging=jar
   ```
3. Build the plugin: `mvn clean package`
4. Start the test server: `./up.sh`

#### Stopping the Test Server

```
./down.sh
```

## Authors and acknowledgement

| Name | Main Contributions |
|---|---|
| Daniel Stephenson | Creator |
| VoChiDanh | NMS utilization & other improvements |
| Callum | Massively improved the plugin in many ways |
| Deej | Renamed a skill |

## License

This project is licensed under the [MIT License](LICENSE).

## Project Status

This project is in active development.

### bStats

You can view the bStats page for the plugin [here](https://bstats.org/plugin/bukkit/SimpleSkills/13470).

## Usage reporting

SimpleSkills reports its usage by default: when the plugin is enabled, and each time one of its commands is used, it sends its name, its version and the command's name to https://trace.danielstephenson.dev, so it is known which plugins are actually in use. Nothing about players, worlds or IP addresses is sent, and neither is anything typed after a command.

Each event also carries a random server ID (the `server-id` line in `plugins/trace/config.yml`) so
servers can be counted rather than events. It identifies no person, account or IP address; delete
the line to get a new one.

To turn it off:

- for this plugin only: set `usage-reporting.enabled: false` in `plugins/SimpleSkills/config.yml`;
- for every plugin on the server that reports to trace: set `enabled: false` in `plugins/trace/config.yml` (created on the first start);
- for the whole server process: set the environment variable `TRACE_USAGE_REPORTING=off` or `DO_NOT_TRACK=1`.

Details: https://danielstephenson.dev/usage-reporting
