/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package preponderous.ponder.toolbox.tools;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PermissionChecker {
    public boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage((Object)ChatColor.RED + "In order to use this command, you need the following permission: '" + permission + "'");
            return false;
        }
        return true;
    }

    public boolean checkPermission(CommandSender sender, ArrayList<String> permissions) {
        for (String permission : permissions) {
            if (!sender.hasPermission(permission)) continue;
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + "In order to use this command, you need one of the the following permissions: " + permissions + "");
        return false;
    }
}

