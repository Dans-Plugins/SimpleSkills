package dansplugins.simpleskills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import dansplugins.simpleskills.commands.DefaultCommand;
import dansplugins.simpleskills.commands.HelpCommand;
import preponderous.ponder.AbstractPonderPlugin;
import preponderous.ponder.misc.PonderAPI_Integrator;
import preponderous.ponder.misc.specification.ICommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class SimpleSkills extends AbstractPonderPlugin {

    private static SimpleSkills instance;

    public static SimpleSkills getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        ponderAPI_integrator = new PonderAPI_Integrator(this);
        toolbox = getPonderAPI().getToolbox();
        initializeConfigService();
        initializeConfigFile();
        registerEventHandlers();
        initializeCommandService();
        getPonderAPI().setDebug(false);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return getPonderAPI().getCommandService().interpretCommand(sender, label, args);
    }

    private void initializeConfigService() {
        HashMap<String, Object> configOptions = new HashMap<>();
        configOptions.put("debugMode", false);
        getPonderAPI().getConfigService().initialize(configOptions);
    }

    private void initializeConfigFile() {
        if (!(new File("./plugins/SimpleSkills/config.yml").exists())) {
            getPonderAPI().getConfigService().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                getPonderAPI().getConfigService().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }
    }

    private void registerEventHandlers() {
        /*
        ArrayList<Listener> listeners = new ArrayList<>();
        getToolbox().getEventHandlerRegistry().registerEventHandlers(listeners, this);
        */
    }

    private void initializeCommandService() {
        ArrayList<ICommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand()
        ));
        getPonderAPI().getCommandService().initialize(commands, "That command wasn't found.");
    }
}
