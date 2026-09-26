package dansplugins.simpleskills;

import dansplugins.simpleskills.bstats.Metrics;
import dansplugins.simpleskills.commands.*;
import dansplugins.simpleskills.commands.tab.TabCommand;
import dansplugins.simpleskills.listeners.PlacedBlockListener;
import dansplugins.simpleskills.listeners.PlayerJoinEventListener;
import dansplugins.simpleskills.listeners.WorldSaveEventListener;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.services.StorageService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.experience.ExperienceCalculator;

import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.skill.skills.*;
import dansplugins.simpleskills.trace.TraceClient;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.spigot.PonderMC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class SimpleSkills extends JavaPlugin {
    /**
     * The oldest Minecraft version this plugin will run on: 1.13, the "flattening" release.
     * Below it the material names the skills look up do not exist.
     */
    private static final int MINIMUM_MAJOR_VERSION = 1;
    private static final int MINIMUM_MINOR_VERSION = 13;

    private final String pluginVersion = "v" + getDescription().getVersion();
    private PonderMC ponder;

    private final ConfigService configService = new ConfigService(this);
    private final Log log = new Log(this, configService);
    private final MessageService messageService = new MessageService(this);
    private final ExperienceCalculator experienceCalculator = new ExperienceCalculator();
    private final SkillRepository skillRepository = new SkillRepository();
    private final PlayerRecordRepository playerRecordRepository = new PlayerRecordRepository(log, messageService, skillRepository, configService, experienceCalculator);
    private final StorageService storageService = new StorageService(playerRecordRepository, skillRepository, messageService, configService, experienceCalculator, log);
    private final ChanceCalculator chanceCalculator = new ChanceCalculator(playerRecordRepository, configService, skillRepository, messageService, experienceCalculator, log);
    private final PlayerJoinEventListener playerJoinEventListener = new PlayerJoinEventListener(playerRecordRepository, log);
    private final PlacedBlockListener placedBlockListener = new PlacedBlockListener(this);
    private final WorldSaveEventListener worldSaveEventListener = new WorldSaveEventListener(storageService, log);

    // A no-op until the config has been read, so a command arriving before
    // onEnable() finishes has something safe to report to.
    private TraceClient trace = TraceClient.disabled();

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        this.ponder = new PonderMC(this);
        configService.createConfig();
        performNMSChecks();
        setTabCompleterForCoreCommands();
        setupMetrics();
        log.debug("Loading files.");
        storageService.load();
        log.debug("Creating language files.");
        messageService.createlang();
        initializeSkills();
        registerEventListeners();
        initializeCommandService();
        checkFilesVersion();
        scheduleAutoSave();
        setupUsageReporting();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        trace.close();
        log.debug("Saving files.");
        storageService.save();
        log.debug("Saving language files.");
        messageService.savelang();
        log.debug("Saving config files.");
        configService.saveConfig();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     *
     * @param sender The sender of the command.
     * @param cmd    The command that was sent. This is unused.
     * @param label  The core command that has been invoked.
     * @param args   Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        trace.report("command", null, Collections.singletonMap("name", cmd.getName()));
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand(messageService, this);
            return defaultCommand.execute(sender);
        }

        return ponder.getCommandService().interpretAndExecuteCommand(sender, label, args);
    }

    public String getVersion() {
        return pluginVersion;
    }

    private void checkFilesVersion() {
        log.debug("Checking config and message files for version compatibility.");
        if (messageService.getlang().getDouble("message-version") != 0.2) {
            log.error("Outdated message.yml! Please backup & update message.yml file and restart server again!!");
        }
        if (configService.getConfig().getDouble("config-version") != 0.2) {
            log.error("Outdated config.yml! Please backup & update config.yml file and restart server again!!");
        }
    }

    /**
     * Refuses to run on a server older than the 1.13 "flattening", where the material names every
     * skill looks up did not yet exist.
     * <p>
     * The version is read from XSeries rather than from Ponder's {@code NMSAssistant}. That class
     * derived the version by parsing the relocated {@code org.bukkit.craftbukkit.vX_Y_RZ} package
     * name, which Spigot no longer versions, so it threw {@link NumberFormatException} on every
     * startup and this check never reached either branch. It cannot be fixed upstream either: the
     * whole {@code preponderous.ponder.minecraft.nms} package was removed from Ponder in 2022 and
     * survives only in the v0.14 pinned here. XSeries is used instead because it is already shaded
     * in, is maintained, and is the same version detection every material lookup already depends
     * on — so this check cannot disagree with the lookups it is guarding.
     * </p>
     */
    private void performNMSChecks() {
        try {
            if (XMaterial.supports(MINIMUM_MAJOR_VERSION, MINIMUM_MINOR_VERSION)) {
                log.info("Loading data for Minecraft " + XMaterial.getVersionMajor() + "."
                        + XMaterial.getVersionMinor());
            } else {
                log.warning("The server version is not suitable to load the plugin");
                log.warning("This plugin requires at least Minecraft " + MINIMUM_MAJOR_VERSION + "."
                        + MINIMUM_MINOR_VERSION + ".");
                Bukkit.getServer().getPluginManager().disablePlugin(this);
            }
        } catch (Exception | LinkageError e) {
            // A version this build cannot recognise is not a per-event condition, so it is reported
            // once here rather than left to surface as a failure on every material lookup later.
            log.warning("Failed to determine the server version. Some features may not work correctly. Error: " + e);
        }
    }

    private void setupMetrics() {
        log.debug("Setting up bStats metrics for SimpleSkills.");
        int pluginId = 13470;
        Metrics metrics = new Metrics(this, pluginId);

        double configVersion = configService.getConfig().getDouble("config-version");
        int defaultMaxLevel = configService.getConfig().getInt("defaultMaxLevel");
        int defaultBaseExperienceRequirement = configService.getConfig().getInt("defaultBaseExperienceRequirement");
        double defaultExperienceIncreaseFactor = configService.getConfig().getDouble("defaultExperienceIncreaseFactor");
        boolean levelUpAlert = configService.getConfig().getBoolean("levelUpAlert");
        boolean benefitAlert = configService.getConfig().getBoolean("benefitAlert");

        metrics.addCustomChart(new Metrics.SimplePie("config_version", () -> String.valueOf(configVersion)));
        metrics.addCustomChart(new Metrics.SimplePie("default_max_level", () -> String.valueOf(defaultMaxLevel)));
        metrics.addCustomChart(new Metrics.SimplePie("default_base_experience_requirement", () -> String.valueOf(defaultBaseExperienceRequirement)));
        metrics.addCustomChart(new Metrics.SimplePie("default_experience_increase_factor", () -> String.valueOf(defaultExperienceIncreaseFactor)));
        metrics.addCustomChart(new Metrics.SimplePie("level_up_alert", () -> String.valueOf(levelUpAlert)));
        metrics.addCustomChart(new Metrics.SimplePie("benefit_alert", () -> String.valueOf(benefitAlert)));
    }


    /**
     * Usage reporting: one event now, one per command; see config.yml. The
     * block is written to disk first if an upgraded server does not have it,
     * so the switch is where the console line says it is, and the outcome is
     * logged either way.
     */
    private void setupUsageReporting() {
        log.debug("Setting up usage reporting.");
        configService.saveUsageReportingDefaultsIfNotPresent();
        trace = TraceClient.builder(configService.getUsageReportingEndpoint(), getName())
                .key(configService.getUsageReportingKey())
                .enabled(configService.isUsageReportingEnabled())
                .serverWideConfig(getDataFolder().getParentFile())
                .logger(getLogger())
                .build();
        if (trace.isEnabled()) {
            getLogger().info("Usage reporting is on: " + getName() + " sends its name, version and command names to "
                    + "https://trace.danielstephenson.dev - nothing about players or the server. Turn it off with "
                    + "usage-reporting.enabled: false in this plugin's config.yml, or for every plugin with "
                    + "enabled: false in plugins/trace/config.yml. "
                    + "Details: https://github.com/Stephenson-Software/trace#usage-reporting");
        } else {
            getLogger().info("Usage reporting is off (" + trace.disabledReason() + ").");
        }
        trace.report("startup", null, Collections.singletonMap("version", getDescription().getVersion()));
    }

    private void setTabCompleterForCoreCommands() {
        log.debug("Setting up tab completers for core commands.");
        for (String key : getDescription().getCommands().keySet()) {
            PluginCommand command = getCommand(key);
            if (command == null) {
                continue;
            }
            command.setTabCompleter(new TabCommand());
        }
    }

    private void registerEventListeners() {
        log.debug("Registering events...");
        for (AbstractSkill skill : skillRepository.getActiveSkills()) {
            log.debug("Registering events for skill: " + skill.getName());
            skill.register();
        }

        Bukkit.getPluginManager().registerEvents(playerJoinEventListener, this);
        Bukkit.getPluginManager().registerEvents(worldSaveEventListener, this);
        // Registered after skills so its BlockBreakEvent cleanup (also MONITOR priority) runs
        // after the skills' own handlers have checked PlacedBlockListener.isPlayerPlaced().
        Bukkit.getPluginManager().registerEvents(placedBlockListener, this);
    }

    private void initializeCommandService() {
        log.debug("Initializing command service...");
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(messageService),
                new InfoCommand(playerRecordRepository, messageService, skillRepository, configService, experienceCalculator, log),
                new StatsCommand(messageService, playerRecordRepository, skillRepository),
                new ForceCommand(playerRecordRepository, skillRepository, configService, storageService),
                new SkillCommand(messageService, skillRepository),
                new TopCommand(playerRecordRepository, messageService, skillRepository),
                new ReloadCommand(messageService, configService)
        ));
        ponder.getCommandService().initialize(commands, "That command wasn't found.");
    }

    private void initializeSkills() {
        log.debug("Initializing skills...");
        skillRepository.addSkill(new Athlete(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Boating(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Breeding(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Cardio(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Crafting(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Digging(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Dueling(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Enchanting(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Farming(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Fishing(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Floriculture(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Gliding(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Hardiness(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Woodcutting(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Mining(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new MonsterHunting(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Pyromaniac(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Quarrying(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Riding(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
        skillRepository.addSkill(new Strength(configService, log, playerRecordRepository, this, messageService, chanceCalculator));
    }

    private void scheduleAutoSave() {
        log.debug("Scheduling autosave task to run every 5 minutes.");
        // Schedule autosave to run every 5 minutes (6000 ticks = 300 seconds)
        // Delay of 6000 ticks before first run to give the server time to fully start
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            log.debug("Running scheduled autosave.");
            storageService.save();
        }, 6000L, 6000L);
    }

}