package dansplugins.simpleskills.api.preponderous.ponder.services;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import dansplugins.simpleskills.api.preponderous.ponder.Ponder;
import dansplugins.simpleskills.api.preponderous.ponder.misc.specification.ICommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Daniel Stephenson
 */
public class CommandService {

    private Ponder ponder;

    private ArrayList<ICommand> commands = new ArrayList<>();
    private Set<String> coreCommands;
    private String notFoundMessage;

    public CommandService(Ponder ponder) {
        this.ponder = ponder;
        coreCommands = ponder.getPlugin().getDescription().getCommands().keySet();
    }

    /**
     * Method to initialize the command service.
     *
     */
    public void initialize(ArrayList<ICommand> commands, String notFoundMessage) {
        this.commands = commands;
        this.notFoundMessage = notFoundMessage;
    }

    /**
     * Method to interpret and execute a command.
     *
     */
    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (!coreCommands.contains(label)) {
            return false;
        }

        if (args.length == 0) {
            return false;
        }

        String subCommand = args[0];

        String[] arguments = ponder.getToolbox().getArgumentParser().dropFirstArgument(args);

        for (ICommand command : commands) {
            if (command.getNames().contains(subCommand)) {
                if (!ponder.getToolbox().getPermissionChecker().checkPermission(sender, command.getPermissions())) {
                    return false;
                }
                if (arguments.length == 0) {
                    return command.execute(sender);
                }
                else {
                    return command.execute(sender, arguments);
                }
            }
        }
        sender.sendMessage(ChatColor.RED + notFoundMessage);
        return false;
    }

}
