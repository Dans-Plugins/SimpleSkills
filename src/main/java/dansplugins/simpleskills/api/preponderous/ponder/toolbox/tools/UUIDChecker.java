/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package preponderous.ponder.toolbox.tools;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class UUIDChecker {
    public String findPlayerNameBasedOnUUID(UUID playerUUID) {
        if (playerUUID == null) {
            throw new IllegalArgumentException("Player UUID cannot be null!");
        }
        Player player = Bukkit.getPlayer((UUID)playerUUID);
        if (player != null) {
            return player.getName();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)playerUUID);
        String name = offlinePlayer.getName();
        return name == null || !offlinePlayer.hasPlayedBefore() ? "" : name;
    }

    public UUID findUUIDBasedOnPlayerName(String playerName) {
        if (playerName == null) {
            throw new IllegalArgumentException("Player Name cannot be null!");
        }
        Player player = Bukkit.getPlayer((String)playerName);
        if (player != null) {
            return player.getUniqueId();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((String)playerName);
        String name = offlinePlayer.getName();
        return name == null || !offlinePlayer.hasPlayedBefore() ? null : offlinePlayer.getUniqueId();
    }
}

