package dansplugins.simpleskills;

import dansplugins.simpleskills.bstats.Metrics;
import dansplugins.simpleskills.commands.*;
import dansplugins.simpleskills.eventhandlers.*;
import dansplugins.simpleskills.nms.NMSVersion;
import dansplugins.simpleskills.services.LocalConfigService;
import dansplugins.simpleskills.services.LocalStorageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.AbstractPonderPlugin;
import preponderous.ponder.misc.PonderAPI_Integrator;
import preponderous.ponder.misc.specification.ICommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Daniel Stephenson
 */
public class SimpleSkills extends AbstractPonderPlugin {
    private static SimpleSkills instance;
    private final String pluginVersion = "v" + getDescription().getVersion();
    private final String nmsVersion = NMSVersion.getNMSVersion();
    private final String minecraftVersion = NMSVersion.formatNMSVersion(nmsVersion);

    public static SimpleSkills getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

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

        // bStats
        int pluginId = 13470;
        Metrics metrics = new Metrics(this, pluginId);

        getCommand("ss").setTabCompleter(new TabCommand());
        getCommand("simpleskills").setTabCompleter(new TabCommand());

        ponderAPI_integrator = new PonderAPI_Integrator(this);
        toolbox = getPonderAPI().getToolbox();

        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);

        // create/load config
        if (!(new File("./plugins/SimpleSkills/config.yml").exists())) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        LocalStorageService.getInstance().load();
    }

    @Override
    public void onDisable() {
        LocalStorageService.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return pluginVersion;
    }

    public boolean isVersionMismatched() {
        String configVersion = getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return true;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    public boolean isDebugEnabled() {
        return LocalConfigService.getInstance().getBoolean("debugMode");
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
        getToolbox().getEventHandlerRegistry().registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<ICommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(),
                new InfoCommand(),
                new StatsCommand(),
                new WipeCommand(),
                new SkillCommand(),
                new TopCommand(),
                new ConfigCommand()
        ));
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }
}
