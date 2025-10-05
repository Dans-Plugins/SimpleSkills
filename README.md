# SimpleSkills

## Description
This open source Minecraft plugin is intended to systematically introduce skills into the game. There are both useful and useless skills, and the qualification depends on the number of benefits that a skill has. An external API is planned in the future which would allow developers to create expansions or integrate this plugin with another. It is also planned to allow for the addition and configuration of skills and benefits through config files and in-game commands.

## Design Philosophy
The general design philosophy for this project is to create a systematic, easy to use, expandable and configurable plugin that server owners can utilize to add flavor to their servers. An external API is planned and will be able to be utilized for expansion and integration purposes. 

## Installation
1) You can download the plugin from [this page](https://www.spigotmc.org/resources/simpleskills.98039/).

2) Once downloaded, place the jar in the plugins folder of your server files.

3) Restart your server.

## Usage
- [User Guide](https://github.com/dmccoystephenson/SimpleSkills/wiki/Guide) (coming soon)
- [List of Commands](https://github.com/dmccoystephenson/SimpleSkills/wiki/Commands)
- [FAQ](https://github.com/dmccoystephenson/SimpleSkills/wiki/FAQ) (coming soon)

## Support
You can find the support discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?
Please fill out a bug report [here](https://github.com/dmccoystephenson/SimpleSkills/issues?q=is%3Aissue+is%3Aopen+label%3Abug).

## Roadmap
- [Known Bugs](https://github.com/dmccoystephenson/SimpleSkills/issues?q=is%3Aopen+is%3Aissue+label%3Abug)
- [Planned Features](https://github.com/dmccoystephenson/SimpleSkills/issues?q=is%3Aopen+is%3Aissue+label%3AEpic)
- [Planned Improvements](https://github.com/dmccoystephenson/SimpleSkills/issues?q=is%3Aopen+is%3Aissue+label%3Aenhancement)

## Contributing
- [Notes for Developers](https://github.com/dmccoystephenson/SimpleSkills/wiki/Developer-Notes) (coming soon)

### Development Setup

To build the project locally, you'll need:

1. **Java 8+** and **Maven 3.6+**
2. **GitHub Personal Access Token** (for accessing Ponder dependency from GitHub Packages)
   - Create a token at https://github.com/settings/tokens
   - Grant the `read:packages` scope
   - Set environment variables:
     ```bash
     export GITHUB_ACTOR=your-github-username
     export GITHUB_TOKEN=your-personal-access-token
     ```

3. **Build the project:**
   ```bash
   mvn clean package
   ```

The Ponder framework dependency is automatically downloaded from GitHub Packages during the build process.

## Authors and acknowledgement
| Name              | Main Contributions                         |
|-------------------|--------------------------------------------|
| Daniel Stephenson | Creator                                    |
| VoChiDanh         | NMS utilization & other improvements       |
| Callum            | Massively improved the plugin in many ways |
| Deej              | Renamed a skill.                           |

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

**Why MIT?**  
The MIT License is short, permissive, and widely understood. It allows anyone to use, modify, distribute, and even commercially exploit the software with minimal restrictions, as long as they include the original license and copyright notice.  
We chose MIT to **maximize adoption and flexibility**, making it easy for individuals and organizations to integrate this project into their own work without legal complexity.

## Project Status
This project is in active development.

### bStats
You can view the bStats page for the plugin [here](https://bstats.org/plugin/bukkit/SimpleSkills/13470).
