package dansplugins.simpleskills;

import dansplugins.simpleskills.bstats.Metrics;
import dansplugins.simpleskills.commands.*;
import dansplugins.simpleskills.commands.tab.TabCommand;
import dansplugins.simpleskills.eventhandlers.*;
import dansplugins.simpleskills.nms.NMSVersion;
import dansplugins.simpleskills.services.LocalConfigService;
import dansplugins.simpleskills.services.LocalMessageService;
import dansplugins.simpleskills.services.LocalStorageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.abs.PonderPlugin;
import preponderous.ponder.minecraft.spigot.tools.EventHandlerRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Daniel Stephenson
 */
public class SimpleSkills extends PonderPlugin {
    private static SimpleSkills instance;
    private final String pluginVersion = "v" + getDescription().getVersion();
    private final String nmsVersion = NMSVersion.getNMSVersion();
    private final String minecraftVersion = NMSVersion.formatNMSVersion(nmsVersion);

    /**
     * This can be utilized to access the self-managed instance of SimpleSkills.
     * @return The self-managed instance of this class.
     */
    public static SimpleSkills getInstance() {
        return instance;
    }

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        instance = this;
        performNMSChecks();
        handleIntegrations();
        setTabCompleterForCoreCommands();
        registerEventHandlers();
        initializeCommandService();
        LocalConfigService.getInstance().createconfig();
        LocalStorageService.getInstance().load();
        LocalMessageService.getInstance().createlang();
        getPonderAPI().setDebug(false);
        checkMessageAndConfigVersions();
    }

    /**
     * This runs when the sever stops.
     */
    @Override
    public void onDisable() {
        LocalStorageService.getInstance().save();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }
        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if the version is mismatched.
     * @return A boolean indicating if the version is mismatched.
     */
    public boolean isVersionMismatched() {
        String configVersion = getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return true;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    /**
     * Checks if debug is enabled.
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return LocalConfigService.getInstance().getconfig().getBoolean("debugMode");
    }

    private void checkMessageAndConfigVersions(){
        if (LocalMessageService.getInstance().getlang().getDouble("message-version") != 0.1) {
            getLogger().warning("Outdated message.yml! Please backup & update message.yml file and restart server again!!");
        }
        if (LocalConfigService.getInstance().getconfig().getDouble("config-version") != 0.1) {
            getLogger().warning("Outdated config.yml! Please backup & update config.yml file and restart server again!!");
        }
    }

    private void performNMSChecks() {
        if (nmsVersion.contains("v1_13_R1")
                || nmsVersion.contains("v1_13_R2")
                || nmsVersion.contains("v1_14_R1")
                || nmsVersion.contains("v1_15_R1")
                || nmsVersion.contains("v1_16_R1")
                || nmsVersion.contains("v1_16_R2")
                || nmsVersion.contains("v1_16_R3")
                || nmsVersion.contains("v1_17_R1")
                || nmsVersion.contains("v1_18_R1")) {
            getLogger().log(Level.INFO, "Loading Data For " + minecraftVersion);
        }
        else {
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

    private void registerEventHandlers() {
        ArrayList<Listener> listeners = new ArrayList<>(Arrays.asList(
                new JoinHandler(),
                new BlockHandler(),
                new CraftingHandler(),
                new DamageHandler(),
                new FishingHandler(),
                new EnchantingHandler(),
                new MoveHandler(),
                new BreedingHandler(),
                new DeathHandler()
        ));
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry(getPonderAPI());
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(),
                new InfoCommand(),
                new StatsCommand(),
                new ForceCommand(),
                new SkillCommand(),
                new TopCommand(),
                new ReloadCommand()
        ));
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }
}