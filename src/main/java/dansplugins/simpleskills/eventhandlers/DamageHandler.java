package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Daniel Stephenson
 */
public class DamageHandler extends SkillHandler {

    @EventHandler()
    public void handle(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        incrementExperience(player, SupportedSkill.HARDINESS.ordinal());
    }

}