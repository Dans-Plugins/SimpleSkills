package dansplugins.simpleskills.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class SkillCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("skill"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.skill"));

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
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }
}