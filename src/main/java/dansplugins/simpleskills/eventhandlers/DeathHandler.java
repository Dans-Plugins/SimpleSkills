package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathHandler extends SkillHandler {

    @EventHandler()
    public void handle(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player killer = player.getKiller();
        if (killer == null) {
            return;
        }
        incrementExperience(killer, SupportedSkill.DUELING.ordinal());
    }

}