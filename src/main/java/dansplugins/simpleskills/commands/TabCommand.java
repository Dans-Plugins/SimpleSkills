package dansplugins.simpleskills.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCommand implements TabCompleter {


    List<String> arg = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (arg.isEmpty()) {
            arg.add("help");
            arg.add("info");
            arg.add("skill");
            arg.add("top");
            arg.add("stats");
        }
        List<String> result = new ArrayList<String>();
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
