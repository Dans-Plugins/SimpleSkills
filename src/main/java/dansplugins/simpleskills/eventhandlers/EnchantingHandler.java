package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * @author Daniel Stephenson
 */
public class EnchantingHandler extends SkillHandler {

    @EventHandler()
    public void handle(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        incrementExperience(player, SupportedSkill.ENCHANTING.ordinal());
    }

}