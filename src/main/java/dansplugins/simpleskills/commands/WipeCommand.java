package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class WipeCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("wipe"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.wipe"));

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
        if (commandSender instanceof Player) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be used from the console.");
        }
        PersistentData.getInstance().getPlayerRecords().clear();
        commandSender.sendMessage("Player records have been cleared.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}