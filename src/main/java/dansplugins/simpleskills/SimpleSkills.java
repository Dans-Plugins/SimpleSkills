package dansplugins.simpleskills;

import dansplugins.simpleskills.bstats.Metrics;
import dansplugins.simpleskills.commands.*;
import dansplugins.simpleskills.config.PluginConfig;
import dansplugins.simpleskills.eventhandlers.*;
import dansplugins.simpleskills.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.AbstractPonderPlugin;
import preponderous.ponder.misc.PonderAPI_Integrator;
import preponderous.ponder.misc.specification.ICommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Daniel Stephenson
 */
public class SimpleSkills extends AbstractPonderPlugin {
    private static SimpleSkills instance;
    private String version = "v1.1-alpha-6";

    public static SimpleSkills getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // bStats
        int pluginId = 13470;
        Metrics metrics = new Metrics(this, pluginId);

        ponderAPI_integrator = new PonderAPI_Integrator(this);
        toolbox = getPonderAPI().getToolbox();

        PluginConfig.getInstance().getConfig().initializePlugin(this);

        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);

        StorageManager.getInstance().load();
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    private void initializeConfigService() {
        HashMap<String, Object> configOptions = new HashMap<>();

        getPonderAPI().getConfigService().initialize(configOptions);
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
                new TopCommand()
        ));
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }
}
