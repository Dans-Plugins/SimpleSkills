package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * @author Daniel Stephenson
 */
public class PermissionChecker {

    /**
     * Method to check whether or not a player has a permission.
     *
     * @param sender to verify.
     * @param permission to check.
     * @return A {@link boolean} signifying whether or not the player has the specified permission.
     */
    public boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "In order to use this command, you need the following permission: '" + permission + "'");
            return false;
        }
        return true;
    }

    /**
     * Method to check whether or not a player has a permission.
     *
     * @param sender to verify.
     * @param permissions to check.
     * @return A {@link boolean} signifying whether or not the player has the specified permission.
     */
    public boolean checkPermission(CommandSender sender, ArrayList<String> permissions) {
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "In order to use this command, you need one of the the following permissions: " + permissions + "");
        return false;
    }

}
