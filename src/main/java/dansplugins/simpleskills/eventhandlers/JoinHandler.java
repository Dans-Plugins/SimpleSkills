package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Daniel Stephenson
 */
public class JoinHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerRecord playerRecord = new PlayerRecord(player.getUniqueId());
        if (PersistentData.getInstance().getPlayerRecord(player.getUniqueId()) == null) {
            PersistentData.getInstance().addPlayerRecord(playerRecord);
            Logger.getInstance().log(player.getName() + " was assigned a player record.");
        }
    }

}