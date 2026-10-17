package dansplugins.simpleskills.commands.tab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCommand implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("help", "info", "skill", "top", "stats", "reload");

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return null;
        }
        List<String> arg = new ArrayList<>(SUBCOMMANDS);
        if (sender instanceof ConsoleCommandSender) {
            arg.add("force");
        }
        List<String> result = new ArrayList<>();
        for (String a : arg) {
            if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                result.add(a);
        }
        return result;
    }
}
