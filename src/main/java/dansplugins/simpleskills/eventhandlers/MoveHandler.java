package dansplugins.simpleskills.eventhandlers;

import dansplugins.simpleskills.enums.SupportedSkill;
import dansplugins.simpleskills.eventhandlers.abs.SkillHandler;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveHandler extends SkillHandler {

    @EventHandler()
    public void handle(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }
        if (!event.getFrom().getChunk().equals(event.getTo().getChunk())) {
            Player player = event.getPlayer();
            if ((player.isSprinting() && !player.isInsideVehicle() && !player.isFlying()) || player.isSwimming()) {
                incrementExperience(player, SupportedSkill.CARDIO.ordinal());
            }
            if (player.isInsideVehicle() && player.getVehicle() instanceof Boat) {
                incrementExperience(player, SupportedSkill.BOATING.ordinal());
            }
            if (player.isInsideVehicle() && player.getVehicle() instanceof Horse) {
                incrementExperience(player, SupportedSkill.HORSEBACK_RIDING.ordinal());
            }
        }
    }

}