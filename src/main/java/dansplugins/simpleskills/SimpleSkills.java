package dansplugins.simpleskills;

import dansplugins.simpleskills.bstats.Metrics;
import dansplugins.simpleskills.commands.*;
import dansplugins.simpleskills.eventhandlers.*;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.services.StorageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import preponderous.ponder.AbstractPonderPlugin;
import preponderous.ponder.misc.PonderAPI_Integrator;
import preponderous.ponder.misc.specification.ICommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel Stephenson
 */
public class SimpleSkills extends AbstractPonderPlugin {
    private static SimpleSkills instance;
    private final String version = "v1.1-alpha-8";

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

        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);

        // create/load config
        if (!(new File("./plugins/SimpleSkills/config.yml").exists())) {
            ConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        StorageService.getInstance().load();
    }

    @Override
    public void onDisable() {
        StorageService.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
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
        return ConfigService.getInstance().getBoolean("debugMode");
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
