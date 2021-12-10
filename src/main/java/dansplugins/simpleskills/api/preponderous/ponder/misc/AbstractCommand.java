package dansplugins.simpleskills.api.preponderous.ponder.misc;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import dansplugins.simpleskills.api.preponderous.ponder.misc.specification.ICommand;

import java.util.Arrays;

/**
 * @author Daniel Stephenson
 */
public abstract class AbstractCommand implements ICommand {

    /**
     * Method to drop the first argument from an Array of Strings.
     *
     * @author Daniel Stephenson
     * @since 10/12/2021
     * @param message to send.
     * @param args to check.
     * @param sender to send message to.
     * @param color of the message.
     * @return Boolean signifying whether there were no arguments.
     */
    public boolean sendMessageIfNoArguments(String message, String[] args, CommandSender sender, ChatColor color) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + message);
            return true;
        }
        return false;
    }

    /**
     * Method to get an Integer from a String.
     *
     * @author Callum Johnson
     * @since 05/05/2021 - 12:18
     * @param line to convert into an Integer.
     * @param orElse if the conversion fails.
     * @return {@link Integer} numeric.
     */
    public int getIntSafe(String line, int orElse) {
        try {
            return Integer.parseInt(line);
        } catch (Exception ex) {
            return orElse;
        }
    }

    /**
     * Method to test if something matches any goal string.
     *
     * @author Callum Johnson
     * @since 05/05/2021 - 12:18
     * @param what to test
     * @param goals to compare with
     * @param matchCase for the comparison (or not)
     * @return {@code true} if something in goals matches what.
     */
    public boolean safeEquals(boolean matchCase, String what, String... goals) {
        return Arrays.stream(goals).anyMatch(goal ->
                matchCase && goal.equals(what) || !matchCase && goal.equalsIgnoreCase(what)
        );
    }

}
