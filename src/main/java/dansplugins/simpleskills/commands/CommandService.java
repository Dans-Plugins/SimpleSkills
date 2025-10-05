package dansplugins.simpleskills.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Command service to replace Ponder's CommandService
 * @author Daniel Stephenson
 */
public class CommandService {
    
    private final List<PluginCommand> commands = new ArrayList<>();
    private String commandNotFoundMessage = "Command not found.";
    
    /**
     * Initialize the command service with a list of commands
     * @param commands List of plugin commands
     * @param commandNotFoundMessage Message to display when command is not found
     */
    public void initialize(List<PluginCommand> commands, String commandNotFoundMessage) {
        this.commands.clear();
        this.commands.addAll(commands);
        this.commandNotFoundMessage = commandNotFoundMessage;
    }
    
    /**
     * Interpret and execute a command
     * @param sender The command sender
     * @param label The command label (unused, for compatibility)
     * @param args Command arguments
     * @return true if command was found and executed
     */
    public boolean interpretAndExecuteCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        
        String commandName = args[0].toLowerCase();
        
        // Find matching command
        for (PluginCommand command : commands) {
            if (command.getCommandNames().contains(commandName)) {
                // Check permission
                if (!command.hasPermission(sender)) {
                    sender.sendMessage("You don't have permission to use this command.");
                    return true;
                }
                
                // Execute with remaining args
                String[] commandArgs = new String[args.length - 1];
                System.arraycopy(args, 1, commandArgs, 0, args.length - 1);
                return command.execute(sender, commandArgs);
            }
        }
        
        // Command not found
        sender.sendMessage(commandNotFoundMessage);
        return false;
    }
    
    /**
     * Get all registered commands
     * @return List of commands
     */
    public List<PluginCommand> getCommands() {
        return new ArrayList<>(commands);
    }
}
