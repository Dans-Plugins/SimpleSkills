package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * @author Daniel Stephenson
 */
public class FishingHandler implements Listener {

    @EventHandler()
    public void handle(PlayerFishEvent event) {
        Player player = event.getPlayer();
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());

        if (playerRecord == null) {
            Logger.getInstance().log("A player record wasn't found for " + player.getName() + ".");
            return;
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            playerRecord.incrementExperience(8); // fishing
        }
    }

}