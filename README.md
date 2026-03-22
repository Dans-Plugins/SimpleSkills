# SimpleSkills

## Description

SimpleSkills is an open source Minecraft plugin that systematically introduces skills into the game. As players perform in-game activities, they gain experience in corresponding skills and unlock beneficial effects. There are both useful and situational skills, and server owners can configure which skills are active and adjust experience progression to suit their server.

## Installation

1. You can download the plugin from [this page](https://www.spigotmc.org/resources/simpleskills.98039/).
2. Once downloaded, place the jar in the plugins folder of your server files.
3. Restart your server.

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
