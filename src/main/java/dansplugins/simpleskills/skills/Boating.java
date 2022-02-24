package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.AbstractMovementSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.services.LocalMessageService;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 23:55
 */
public class Boating extends AbstractMovementSkill {

    /**
     * The Boating skill is obtained by boating around in a ..... boat!!!!
     */
    public Boating() {
        super("Boating");
    }

    /**
     * Method to obtain the skill type for the skill.
     *
     * @return {@link MovementSkillType}
     */
    @Override
    public MovementSkillType getSkillType() {
        return MovementSkillType.BOATING;
    }

    /**
     * Method to get the chance of a skill incrementing or levelling.
     *
     * @return double chance (1-100).
     * @see #randomExpGainChance()
     */
    @Override
    public double getChance() {
        return 0;
    }

    /**
     * Method to determine if a skill is chance-incremented/levelled.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean randomExpGainChance() {
        return false;
    }

    /**
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    @Override
    public void executeReward(@NotNull Player player, Object... skillData) {
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        if (!ChanceCalculator.getInstance().roll(record, this, 0.10)) return;
        if (!(player.getVehicle() instanceof Boat)) return;
        final Boat boat = (Boat) player.getVehicle();
        boat.setVelocity(boat.getVelocity().multiply(4));
        player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.Boating"))));
        player.playSound(player.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 5, 2);
    }

}
