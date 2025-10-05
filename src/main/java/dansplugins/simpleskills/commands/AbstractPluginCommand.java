package dansplugins.simpleskills.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for plugin commands
 * @author Daniel Stephenson
 */
public abstract class AbstractPluginCommand implements PluginCommand {
    
    private final List<String> commandNames;
    private final List<String> requiredPermissions;
    
    /**
     * Constructor with command names and permissions
     * @param commandNames List of command names/aliases
     * @param requiredPermissions List of required permissions
     */
    public AbstractPluginCommand(List<String> commandNames, List<String> requiredPermissions) {
        this.commandNames = new ArrayList<>(commandNames);
        this.requiredPermissions = new ArrayList<>(requiredPermissions);
    }
    
    @Override
    public List<String> getCommandNames() {
        return commandNames;
    }
    
    @Override
    public List<String> getRequiredPermissions() {
        return requiredPermissions;
    }
    
    @Override
    public abstract boolean execute(CommandSender sender);
    
    @Override
    public abstract boolean execute(CommandSender sender, String[] args);
}
