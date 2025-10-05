package dansplugins.simpleskills.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Interface for plugin commands to replace Ponder's AbstractPluginCommand
 * @author Daniel Stephenson
 */
public interface PluginCommand {
    
    /**
     * Execute the command
     * @param sender The command sender
     * @return true if command executed successfully
     */
    boolean execute(CommandSender sender);
    
    /**
     * Execute the command with arguments
     * @param sender The command sender
     * @param args Command arguments
     * @return true if command executed successfully
     */
    boolean execute(CommandSender sender, String[] args);
    
    /**
     * Get the command names/aliases
     * @return List of command names
     */
    List<String> getCommandNames();
    
    /**
     * Get the required permissions for this command
     * @return List of permissions
     */
    List<String> getRequiredPermissions();
    
    /**
     * Check if sender has permission to execute this command
     * @param sender The command sender
     * @return true if sender has permission
     */
    default boolean hasPermission(CommandSender sender) {
        List<String> permissions = getRequiredPermissions();
        if (permissions == null || permissions.isEmpty()) {
            return true;
        }
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}
