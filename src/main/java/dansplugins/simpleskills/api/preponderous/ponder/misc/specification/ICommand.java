/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package preponderous.ponder.misc.specification;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public interface ICommand {
    public static final ArrayList<String> names = null;
    public static final ArrayList<String> permissions = null;

    default public ArrayList<String> getNames() {
        return names;
    }

    default public ArrayList<String> getPermissions() {
        return permissions;
    }

    public boolean execute(CommandSender var1);

    public boolean execute(CommandSender var1, String[] var2);

    public boolean sendMessageIfNoArguments(String var1, String[] var2, CommandSender var3, ChatColor var4);

    public int getIntSafe(String var1, int var2);

    public boolean safeEquals(boolean var1, String var2, String ... var3);
}

