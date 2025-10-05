package dansplugins.simpleskills.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * UUID validation utility to replace Ponder's UUIDChecker
 * @author Daniel Stephenson
 */
public class UUIDValidator {
    
    /**
     * Check if a UUID string is valid
     * @param uuidString The UUID string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidUUID(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get a player's UUID from their name
     * @param playerName The player's name
     * @return The player's UUID, or null if not found
     */
    @SuppressWarnings("deprecation")
    public static UUID getUUIDFromPlayerName(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return player.hasPlayedBefore() ? player.getUniqueId() : null;
    }
    
    /**
     * Get a player's name from their UUID
     * @param uuid The player's UUID
     * @return The player's name, or null if not found
     */
    public static String getPlayerNameFromUUID(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.getName();
    }
}
