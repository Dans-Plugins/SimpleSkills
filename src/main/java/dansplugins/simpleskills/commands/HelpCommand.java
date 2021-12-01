package dansplugins.simpleskills.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class HelpCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("help"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.help"));

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
        commandSender.sendMessage(ChatColor.AQUA + "/ss help - View a list of helpful commands.");
        commandSender.sendMessage(ChatColor.AQUA + "/ss info <IGN> - View your record or another player's record.");
        commandSender.sendMessage(ChatColor.RED + "/ss skill \"skill name\" - View information about a skill.");
        commandSender.sendMessage(ChatColor.RED + "/ss top \"skill name\" - View the leaderboard for a skill.");
        commandSender.sendMessage(ChatColor.AQUA + "/ss stats - View various statistics associated with the plugin.");
        commandSender.sendMessage(ChatColor.RED + "/ss config - View or set config options.");
        commandSender.sendMessage(ChatColor.AQUA + "/ss wipe - Wipe the data for the plugin.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
