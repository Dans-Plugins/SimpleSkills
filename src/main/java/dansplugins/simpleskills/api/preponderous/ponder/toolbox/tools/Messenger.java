package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

/**
 * @author Daniel Stephenson
 */
public class Messenger {

    /**
     * Method to message every online player.
     *
     */
    public void sendAllPlayersOnServerMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Method to send a message to players within a certain number of blocks from a player.
     *
     */
    public int sendMessageToPlayersWithinDistance(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();

        int numPlayersWhoHeard = 0;

        // for every online player
        for (Player potentialPlayer : getServer().getOnlinePlayers()) {

            // if not in world
            if (potentialPlayer.getLocation().getWorld().getName() != playerLocation.getWorld().getName()) {
                return -1;
            }

            // if not within the specified distance
            if (!(potentialPlayer.getLocation().distance(playerLocation) < distance)) {
                return -1;
            }

            numPlayersWhoHeard++;
            potentialPlayer.sendMessage(message);
        }
        return numPlayersWhoHeard;
    }

}
