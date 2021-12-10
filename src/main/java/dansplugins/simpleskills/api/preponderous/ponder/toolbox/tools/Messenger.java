/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package preponderous.ponder.toolbox.tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Messenger {
    public void sendAllPlayersOnServerMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public int sendMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();
        int numPlayersWhoHeard = 0;
        for (Player potentialPlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (potentialPlayer.getLocation().getWorld().getName() != playerLocation.getWorld().getName()) {
                return -1;
            }
            if (!(potentialPlayer.getLocation().distance(playerLocation) < (double)distance)) {
                return -1;
            }
            ++numPlayersWhoHeard;
            potentialPlayer.sendMessage(message);
        }
        return numPlayersWhoHeard;
    }
}

