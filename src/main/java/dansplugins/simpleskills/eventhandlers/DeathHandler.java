package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author Daniel Stephenson
 */
public class DeathHandler extends SkillHandler {

    @EventHandler()
    public void handle(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player killer = player.getKiller();
            if (killer == null) {
                return;
            }
            incrementExperience(killer, SupportedSkill.DUELING.ordinal());
        }
        else if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            Player killer = monster.getKiller();
            if (killer == null) {
                return;
            }
            incrementExperience(killer, SupportedSkill.MONSTER_HUNTING.ordinal());
        }

    }

}