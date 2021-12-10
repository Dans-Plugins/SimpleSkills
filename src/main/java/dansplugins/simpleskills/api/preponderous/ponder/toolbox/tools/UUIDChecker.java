package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UUIDChecker {

    /**
     * Method to obtain the Name of a Player based on their UUID.
     * <p>
     *     This method utilises {@link Bukkit#getOfflinePlayer(UUID)} and {@link Bukkit#getPlayer(UUID)} to obtain the
     *     player object, to then uses {@link OfflinePlayer} and {@link Player} to obtain the <em>known</em> name for
     *     that player.
     * </p>
     *
     * @param playerUUID used to find the name.
     * @return Name of the player as a {@link String}
     * @throws IllegalArgumentException if the UUID provided is null.
     */
    public String findPlayerNameBasedOnUUID(UUID playerUUID) {
        if (playerUUID == null) throw new IllegalArgumentException("Player UUID cannot be null!");
        final Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) return player.getName();
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        final String name = offlinePlayer.getName();
        return name == null||!offlinePlayer.hasPlayedBefore() ? "" : name;
    }

    /**
     * Method to obtain the UUID of a player based on their <em>known</em> name.
     * <p>
     *     This method utilises {@link Bukkit#getOfflinePlayer(String)} and {@link Bukkit#getPlayer(String)} to obtain
     *     the player object, to then uses {@link OfflinePlayer} and {@link Player} to obtain UUID for that player.
     *     <br>
     *     As of 1.8, UUID are the only <em>accurate</em> way of identifying a player, therefore, the existing
     *     methodology to obtain players by name etc. etc. are deprecated.
     * </p>
     *
     * @param playerName used to find the UUID.
     * @return {@link UUID} (Unique ID) of the Player.
     * @throws IllegalArgumentException if the name is null.
     */
    @SuppressWarnings("deprecation")
    public UUID findUUIDBasedOnPlayerName(String playerName) {
        if (playerName == null) throw new IllegalArgumentException("Player Name cannot be null!");
        final Player player = Bukkit.getPlayer(playerName);
        if (player != null) return player.getUniqueId();
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        final String name = offlinePlayer.getName();
        return name == null||!offlinePlayer.hasPlayedBefore() ? null : offlinePlayer.getUniqueId();
    }

}
