package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * @author Daniel Stephenson
 */
public class CraftingHandler extends SkillHandler {

    @EventHandler()
    public void handle(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        incrementExperience(player, SupportedSkill.CRAFTING.ordinal());
    }

}