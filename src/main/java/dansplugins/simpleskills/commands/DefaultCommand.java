package dansplugins.simpleskills.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import dansplugins.simpleskills.SimpleSkills;
import preponderous.ponder.misc.AbstractCommand;

/**
 * @author Daniel Stephenson
 */
public class DefaultCommand extends AbstractCommand {

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "SimpleSkills " + SimpleSkills.getInstance().getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/SimpleSkills/wiki");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}