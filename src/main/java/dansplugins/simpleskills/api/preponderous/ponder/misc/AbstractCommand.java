/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package preponderous.ponder.misc;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.specification.ICommand;

public abstract class AbstractCommand
implements ICommand {
    @Override
    public boolean sendMessageIfNoArguments(String message, String[] args, CommandSender sender, ChatColor color) {
        if (args.length == 0) {
            sender.sendMessage((Object)ChatColor.RED + message);
            return true;
        }
        return false;
    }

    @Override
    public int getIntSafe(String line, int orElse) {
        try {
            return Integer.parseInt(line);
        }
        catch (Exception ex) {
            return orElse;
        }
    }

    @Override
    public boolean safeEquals(boolean matchCase, String what, String ... goals) {
        return Arrays.stream(goals).anyMatch(goal -> matchCase && goal.equals(what) || !matchCase && goal.equalsIgnoreCase(what));
    }
}

