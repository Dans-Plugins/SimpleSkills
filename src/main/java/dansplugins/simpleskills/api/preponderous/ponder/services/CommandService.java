/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package preponderous.ponder.services;

import java.util.ArrayList;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.Ponder;
import preponderous.ponder.misc.specification.ICommand;

public class CommandService {
    private Ponder ponder;
    private ArrayList<ICommand> commands = new ArrayList();
    private Set<String> coreCommands;
    private String notFoundMessage;

    public CommandService(Ponder ponder) {
        this.ponder = ponder;
        this.coreCommands = ponder.getPlugin().getDescription().getCommands().keySet();
    }

    public void initialize(ArrayList<ICommand> commands, String notFoundMessage) {
        this.commands = commands;
        this.notFoundMessage = notFoundMessage;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (!this.coreCommands.contains(label)) {
            return false;
        }
        if (args.length == 0) {
            return false;
        }
        String subCommand = args[0];
        String[] arguments = this.ponder.getToolbox().getArgumentParser().dropFirstArgument(args);
        for (ICommand command : this.commands) {
            if (!command.getNames().contains(subCommand)) continue;
            if (!this.ponder.getToolbox().getPermissionChecker().checkPermission(sender, command.getPermissions())) {
                return false;
            }
            if (arguments.length == 0) {
                return command.execute(sender);
            }
            return command.execute(sender, arguments);
        }
        sender.sendMessage((Object)ChatColor.RED + this.notFoundMessage);
        return false;
    }
}

