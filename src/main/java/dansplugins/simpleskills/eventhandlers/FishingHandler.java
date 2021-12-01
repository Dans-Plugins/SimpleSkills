package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * @author Daniel Stephenson
 */
public class FishingHandler extends SkillHandler {

    @EventHandler()
    public void handle(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            incrementExperience(player, SupportedSkill.FISHING.ordinal());
        }
    }

}