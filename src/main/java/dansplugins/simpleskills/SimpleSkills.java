package dansplugins.simpleskills;

import dansplugins.simpleskills.bstats.Metrics;
import dansplugins.simpleskills.commands.*;
import dansplugins.simpleskills.commands.tab.TabCommand;
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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import preponderous.ponder.minecraft.bukkit.PonderMC;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.nms.NMSAssistant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Daniel Stephenson
 */
public class SimpleSkills extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();
    private PonderMC ponder;

    private final Log log = new Log(this);
    private final MessageService messageService = new MessageService(this);
    private final ConfigService configService = new ConfigService(this);
    private final ExperienceCalculator experienceCalculator = new ExperienceCalculator();
    private final PlayerRecordRepository playerRecordRepository = new PlayerRecordRepository();
    private final SkillRepository skillRepository = new SkillRepository();
    private final StorageService storageService = new StorageService(playerRecordRepository, skillRepository, messageService, configService, experienceCalculator, log);
    private final ChanceCalculator chanceCalculator = new ChanceCalculator(playerRecordRepository, configService, skillRepository, messageService, experienceCalculator, log);


    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        this.ponder = new PonderMC(this);
        performNMSChecks();
        setTabCompleterForCoreCommands();
        configService.createconfig();
        setupMetrics();
        storageService.load();
        messageService.createlang();
        initializeSkills();
        registerEvents();
        initializeCommandService();
        checkFilesVersion();
    }

    /**
     * This runs when the sever stops.
     */
    @Override
    public void onDisable() {
        storageService.save();
        messageService.savelang();
        configService.saveconfig();
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
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand(messageService, this);
            return defaultCommand.execute(sender);
        }

        return ponder.getCommandService().interpretAndExecuteCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     *
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if debug is enabled.
     *
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return configService.getconfig().getBoolean("debugMode");
    }

    private void checkFilesVersion() {
        if (messageService.getlang().getDouble("message-version") != 0.2) {
            getLogger().log( Level.SEVERE, "Outdated message.yml! Please backup & update message.yml file and restart server again!!");
        }
        if (configService.getconfig().getDouble("config-version") != 0.1) {
            getLogger().log( Level.SEVERE, "Outdated config.yml! Please backup & update config.yml file and restart server again!!");
        }
    }

    private void performNMSChecks() {
        final NMSAssistant nmsAssistant = new NMSAssistant();
        if (nmsAssistant.isVersionGreaterThan(12)) {
            getLogger().log(Level.INFO, "Loading Data For " + nmsAssistant.getNMSVersion().toString());
        } else {
            getLogger().warning("The server version is not suitable to load the plugin");
            getLogger().warning("Support version 1.13.x - 1.18.x");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void setupMetrics() {
        int pluginId = 13470;
        Metrics metrics = new Metrics(this, pluginId);

        double configVersion = configService.getconfig().getDouble("config-version");
        int defaultMaxLevel = configService.getconfig().getInt("defaultMaxLevel");
        int defaultBaseExperienceRequirement = configService.getconfig().getInt("defaultBaseExperienceRequirement");
        double defaultExperienceIncreaseFactor = configService.getconfig().getDouble("defaultExperienceIncreaseFactor");
        boolean levelUpAlert = configService.getconfig().getBoolean("levelUpAlert");
        boolean benefitAlert = configService.getconfig().getBoolean("benefitAlert");

        metrics.addCustomChart(new Metrics.SimplePie("config_version", () -> String.valueOf(configVersion)));
        metrics.addCustomChart(new Metrics.SimplePie("default_max_level", () -> String.valueOf(defaultMaxLevel)));
        metrics.addCustomChart(new Metrics.SimplePie("default_base_experience_requirement", () -> String.valueOf(defaultBaseExperienceRequirement)));
        metrics.addCustomChart(new Metrics.SimplePie("default_experience_increase_factor", () -> String.valueOf(defaultExperienceIncreaseFactor)));
        metrics.addCustomChart(new Metrics.SimplePie("level_up_alert", () -> String.valueOf(levelUpAlert)));
        metrics.addCustomChart(new Metrics.SimplePie("benefit_alert", () -> String.valueOf(benefitAlert)));
    }


    private void setTabCompleterForCoreCommands() {
        for (String key : getDescription().getCommands().keySet()) {
            PluginCommand command = getCommand(key);
            if (command == null) {
                continue;
            }
            command.setTabCompleter(new TabCommand());
        }
    }

    private void registerEvents() {
        for (AbstractSkill skill : skillRepository.getSkills()) {
            skill.register();
        }
    }

    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(messageService),
                new InfoCommand(playerRecordRepository, messageService, skillRepository, configService, experienceCalculator, log),
                new StatsCommand(messageService, playerRecordRepository, skillRepository),
                new ForceCommand(playerRecordRepository, skillRepository),
                new SkillCommand(messageService, skillRepository),
                new TopCommand(playerRecordRepository, messageService, skillRepository),
                new ReloadCommand(messageService, configService)
        ));
        ponder.getCommandService().initialize(commands, "That command wasn't found.");
    }

    private void initializeSkills() {
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

}