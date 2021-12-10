package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import org.bukkit.ChatColor;

/**
 * @author Daniel Stephenson
 */
public class ColorChecker {

    /**
     * Method to get a chat color by name.
     *
     * @param color to get.
     * @return {@link ChatColor}
     */
    public ChatColor getColorByName(String color) {
        for (ChatColor value : ChatColor.values()) {
            if (value.name().equalsIgnoreCase(color)) {
                return value;
            }
        }
        return ChatColor.WHITE;
    }

}
