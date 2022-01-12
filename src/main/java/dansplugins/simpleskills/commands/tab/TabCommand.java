package dansplugins.simpleskills.commands.tab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCommand implements TabCompleter {

    private final List<String> arg = new ArrayList<>();

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (arg.isEmpty()) {
            arg.add("help");
            arg.add("info");
            arg.add("skill");
            arg.add("top");
            arg.add("stats");
            arg.add("reload");
            if (sender instanceof ConsoleCommandSender){
                arg.add("force");
            }
        }
        List<String> result = new ArrayList<>();
        if (args.length == 1){
            for (String a : arg){
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }
            return result;
        }
        return null;
    }
}
