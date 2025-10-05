# SimpleSkills - Minecraft Plugin Development Guide

This is a Java Minecraft Spigot plugin that adds skill systems to Minecraft servers. The plugin uses Maven for builds and Docker for testing.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites and Setup
- Install Java 8+ (project targets Java 8 but works with modern versions)
- Install Maven 3.6+
- Install Docker and Docker Compose for testing
- **CRITICAL**: Install the Ponder dependency first before any builds:
  ```bash
  mvn install:install-file -Dfile=dependencies/ponder-v0.14-alpha-2.jar -DgroupId=preponderous -DartifactId=ponder -Dversion=v0.14-alpha-2 -Dpackaging=jar
  ```

### Build Process
- **NETWORK DEPENDENCY**: Builds require internet access to download Spigot API and XSeries dependencies
- **IF BUILD FAILS** due to network restrictions (common in sandboxed environments):
  - Document that `mvn clean package` fails due to firewall/network limitations accessing hub.spigotmc.org and jitpack.io
  - The build normally takes 30-60 seconds with network access. NEVER CANCEL builds - set timeout to 3+ minutes.
  - Dependencies needed: `org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT`, `com.github.cryptomorin:XSeries:8.6.1`
  - Error messages will show: "Could not transfer metadata" and "No address associated with hostname"

### Standard Development Commands
1. **Install Ponder dependency** (required first):
   ```bash
   mvn install:install-file -Dfile=dependencies/ponder-v0.14-alpha-2.jar -DgroupId=preponderous -DartifactId=ponder -Dversion=v0.14-alpha-2 -Dpackaging=jar
   ```

2. **Build the plugin** (requires network access):
   ```bash
   mvn clean package
   ```
   - Takes 30-60 seconds with network access. NEVER CANCEL. Set timeout to 180+ seconds.
   - Creates `target/SimpleSkills-2.2.1-SNAPSHOT.jar`

3. **Quick compile script**:
   ```bash
   chmod +x compile.sh  # Make executable first
   ./compile.sh
   ```
   - Equivalent to `mvn clean package` 
   - **Also fails with network restrictions** - same errors as Maven

## Testing and Validation

### Docker-Based Testing (PRIMARY TEST METHOD)
This project uses Docker to test the plugin in a real Minecraft server environment:

1. **Build the plugin first** (creates JAR file needed by Docker)
2. **Setup environment file**:
   ```bash
   cp sample.env .env
   # Edit .env with your values - particularly OPERATOR_UUID and OPERATOR_NAME
   ```

3. **Start test server**:
   ```bash
   ./up.sh
   ```
   - **NETWORK DEPENDENCY**: Downloads and builds Spigot 1.21.4 server from hub.spigotmc.org
   - **IF FAILS** due to network restrictions: Will fail with "No address associated with hostname" for hub.spigotmc.org
   - Sets up test Minecraft server with the plugin installed
   - Server runs on port 25565
   - **FIRST RUN WARNING**: Downloads 100+ MB and builds Spigot server components
   - **TIMING**: Docker build takes 60+ seconds just to install Java, then fails at BuildTools download in restricted environments

4. **Stop test server**:
   ```bash
   ./down.sh
   ```

**DOCKER BUILD TIMES MEASURED:**
- Docker dependency installation (apt update, Java install): ~60 seconds
- BuildTools download and Spigot build: 15-30 minutes (when network works)
- **TOTAL FIRST BUILD**: 20-35 minutes. NEVER CANCEL. Set timeout to 45+ minutes.

### Manual Testing Scenarios
After starting the Docker test server, **ALWAYS** validate these scenarios:

1. **Connect to the test server** on `localhost:25565` with Minecraft client
2. **Test basic plugin functionality**:
   - Run `/ss help` to verify plugin loads
   - Run `/ss stats` to check skill system
   - Perform actions that trigger skills (mining, crafting, etc.)
   - Verify skill experience gain messages appear
3. **Test skill progression**:
   - Mine blocks to gain Mining skill experience  
   - Craft items to gain Crafting skill experience
   - Check `/ss stats` shows updated levels
4. **Test configuration**:
   - Verify `config.yml` settings are applied
   - Check message customization works via `message.yml`

### NO TRADITIONAL UNIT TESTS
- This project has 0 unit tests - it relies entirely on Docker-based integration testing
- Do not look for JUnit tests or similar - they do not exist
- **ALL TESTING** is done through the Docker container setup

## Project Structure and Navigation

### Key Directories
- `src/main/java/dansplugins/simpleskills/` - Main plugin code
  - `SimpleSkills.java` - Main plugin class (extends PonderBukkitPlugin)
  - `commands/` - All command implementations (`/ss`, `/skills`, etc.)
  - `skill/` - Skill system core
    - `skills/` - Individual skill implementations (20 skills: Athlete, Boating, Breeding, Cardio, Crafting, Digging, Dueling, Enchanting, Farming, Fishing, Floriculture, Gliding, Hardiness, Mining, MonsterHunting, Pyromaniac, Quarrying, Riding, Strength, Woodcutting)
    - `abs/` - Abstract skill base classes
  - `playerrecord/` - Player data management
  - `config/` - Configuration service
  - `message/` - Message/localization service
  - `chance/` - Skill chance calculations
  - `experience/` - Experience calculation system

### Configuration Files
- `src/main/resources/config.yml` - Main plugin configuration
- `src/main/resources/message.yml` - All user-facing messages
- `src/main/resources/plugin.yml` - Spigot plugin metadata

### Build Files
- `pom.xml` - Maven configuration (uses Java 8, Spigot API 1.18.1, Ponder framework)
- `Dockerfile` - Test server container definition
- `compose.yml` - Docker Compose configuration for test environment

## Dependencies and External Libraries

### Required Dependencies
1. **Ponder Framework** (`preponderous:ponder:v0.14-alpha-2`)
   - Custom framework for Bukkit plugins
   - **MUST** be manually installed to local Maven repository
   - Located in `dependencies/ponder-v0.14-alpha-2.jar`

2. **Spigot API** (`org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT`)
   - Minecraft server API for plugin development
   - Downloaded from Spigot repository (requires network access)

3. **XSeries** (`com.github.cryptomorin:XSeries:8.6.1`)
   - Cross-version Minecraft compatibility library
   - Downloaded from JitPack repository

## Common Development Tasks

### Adding New Skills
1. Create new skill class in `src/main/java/dansplugins/simpleskills/skill/skills/`
2. Extend appropriate abstract skill class (`AbstractSkill`, `AbstractBlockSkill`, etc.)
3. Register skill in `SimpleSkills.java` `initializeSkills()` method
4. Add skill-specific configuration to `config.yml` if needed

### Modifying Commands
- All commands in `src/main/java/dansplugins/simpleskills/commands/`
- Commands use Ponder framework command system
- Tab completion handled in `commands/tab/TabCommand.java`

### Configuration Changes
- Always test configuration changes using Docker environment
- Verify both `config.yml` and `message.yml` changes work correctly
- Version compatibility checked in `SimpleSkills.java` `checkFilesVersion()` method

## Validation Requirements
- **ALWAYS** build and test your changes using the Docker environment
- **NEVER** skip manual testing scenarios when making functional changes
- **ALWAYS** verify skill experience gain works after skill system modifications
- **ALWAYS** test command functionality after command modifications
- Run complete user scenarios, not just basic startup/shutdown tests

## Build Time Expectations
- **Ponder dependency installation**: 5-10 seconds
- **Maven clean package**: 30-60 seconds (with network), fails without network with "No address associated with hostname"
- **Docker environment setup**: 60+ seconds just for Java installation 
- **Docker first build**: 20-35 minutes (downloads and builds Spigot server). NEVER CANCEL.
- **Docker subsequent builds**: 2-5 minutes  
- **Server startup in Docker**: 30-60 seconds

NOTE: Always set appropriate timeouts (3+ minutes for builds, 45+ minutes for Docker first run) and include explicit "NEVER CANCEL" warnings for long-running operations.

## Troubleshooting Common Issues

### Build Failures Due to Network Restrictions
**Symptoms:**
- `mvn clean package` fails with "No address associated with hostname"
- `./compile.sh` fails with same error
- `docker compose build` fails downloading BuildTools.jar

**Error Messages to Expect:**
```
Could not transfer metadata org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT/maven-metadata.xml from/to spigotmc-repo
hub.spigotmc.org: No address associated with hostname
jitpack.io: No address associated with hostname
```

**Solution:**
- Document these network limitations in your implementation notes
- This is expected behavior in sandboxed/restricted environments
- Do not attempt workarounds - the instructions are correct, environment is restricted

### Permission Issues
**Symptoms:**
- `./compile.sh` fails with "Permission denied"

**Solution:**
```bash
chmod +x compile.sh
./up.sh  # Also make this executable
chmod +x down.sh
```

### Docker Issues
**Symptoms:**
- Docker compose fails after 60+ seconds at BuildTools download

**Expected Behavior:**
- This is normal in restricted environments
- Document as network limitation, not instruction error

## Network Dependency Issues
In restricted environments (sandboxes, firewalls), these commands will fail:
- `mvn clean package` - Cannot reach hub.spigotmc.org, jitpack.io, central Maven repositories
- `docker compose build` - Cannot download BuildTools.jar from hub.spigotmc.org
- **Expected error messages**: "No address associated with hostname", "Could not transfer metadata"
- **Workaround**: Document the network limitations in instructions when encountered