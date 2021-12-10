package dansplugins.simpleskills.api.preponderous.ponder.misc.specification;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface ICommand {

    ArrayList<String> names = null;
    ArrayList<String> permissions = null;

    default ArrayList<String> getNames() {
        return names;
    }

    default ArrayList<String> getPermissions() {
        return permissions;
    }

    boolean execute(CommandSender sender);
    boolean execute(CommandSender sender, String[] args);
    boolean sendMessageIfNoArguments(String message, String[] args, CommandSender sender, ChatColor color);
    int getIntSafe(String line, int orElse);
    boolean safeEquals(boolean matchCase, String what, String... goals);

}
