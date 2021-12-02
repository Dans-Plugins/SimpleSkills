package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class StatsCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("stats"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.stats"));

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
        commandSender.sendMessage(ChatColor.AQUA + "=== SimpleSkills Stats ===");
        commandSender.sendMessage(ChatColor.AQUA + "Number of skills: " + PersistentData.getInstance().getSkills().size());
        commandSender.sendMessage(ChatColor.AQUA + "Number of player records: " + PersistentData.getInstance().getPlayerRecords().size());
        commandSender.sendMessage(ChatColor.AQUA + "Unknown skills: " + PersistentData.getInstance().getNumUnknownSkills());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
