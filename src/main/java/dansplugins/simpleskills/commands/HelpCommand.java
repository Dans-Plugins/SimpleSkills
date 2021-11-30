package dansplugins.simpleskills.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class HelpCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("help"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.help"));

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
        commandSender.sendMessage(ChatColor.AQUA + "/ss help");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
