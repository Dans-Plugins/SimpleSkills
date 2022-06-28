package dansplugins.simpleskills;

import dansplugins.simpleskills.bstats.Metrics;
import dansplugins.simpleskills.commands.*;
import dansplugins.simpleskills.commands.tab.TabCommand;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.services.StorageService;
import dansplugins.simpleskills.skills.abs.AbstractSkill;
import dansplugins.simpleskills.utils.ChanceCalculator;
import dansplugins.simpleskills.utils.ExperienceCalculator;

import dansplugins.simpleskills.utils.Logger;
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

    private final Logger logger = new Logger(this);
    private final MessageService messageService = new MessageService(this);
    private final ConfigService configService = new ConfigService(this);
    private final ExperienceCalculator experienceCalculator = new ExperienceCalculator();
    private final PersistentData persistentData = new PersistentData(logger, messageService, configService, experienceCalculator, chanceCalculator, this);
    private final StorageService storageService = new StorageService(persistentData, messageService, configService, experienceCalculator, logger);
    private final ChanceCalculator chanceCalculator = new ChanceCalculator(persistentData, configService);


    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        this.ponder = new PonderMC(this);
        performNMSChecks();
        handleIntegrations();
        setTabCompleterForCoreCommands();
        configService.createconfig();
        storageService.load();
        messageService.createlang();
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

    private void handleIntegrations() {
        // bStats
        int pluginId = 13470;
        Metrics metrics = new Metrics(this, pluginId);
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
        for (AbstractSkill skill : persistentData.getSkills()) {
            skill.register();
        }
    }

    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(messageService),
                new InfoCommand(persistentData, messageService),
                new StatsCommand(messageService, persistentData),
                new ForceCommand(persistentData),
                new SkillCommand(messageService, persistentData),
                new TopCommand(persistentData, messageService),
                new ReloadCommand(messageService, configService)
        ));
        ponder.getCommandService().initialize(commands, "That command wasn't found.");
    }

}