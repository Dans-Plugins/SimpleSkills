package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.SupportedSkill;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    @EventHandler()
    public void handle(EntityDamageByEntityEvent event) {
        Entity damagedEntity = event.getEntity();
        Entity damagingEntity = event.getDamager();
        if (damagingEntity instanceof Player && damagedEntity instanceof Player) {
            if (damagedEntity.isDead()) {
                Player player = (Player) damagingEntity;
                incrementExperience(player, SupportedSkill.DUELING.ordinal());
            }
        }
    }

}