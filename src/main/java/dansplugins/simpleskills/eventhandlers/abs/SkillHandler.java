package dansplugins.simpleskills.eventhandlers.abs;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * @author Daniel Stephenson
 */
public abstract class SkillHandler implements Listener {
    protected void incrementExperience(Player player, int skillID) {
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());
        if (playerRecord == null) {
            Logger.getInstance().log("A player record wasn't found for " + player.getName() + ".");
            return;
        }
        playerRecord.incrementExperience(skillID);
    }
}