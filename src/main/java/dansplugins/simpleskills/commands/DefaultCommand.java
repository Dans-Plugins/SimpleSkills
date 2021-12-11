package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import preponderous.ponder.misc.AbstractCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class DefaultCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("default"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.default"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "SimpleSkills " + SimpleSkills.getInstance().getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/SimpleSkills/wiki");
        commandSender.sendMessage("");
        commandSender.sendMessage(ChatColor.AQUA + "To view a list of commands, type /ss help.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}