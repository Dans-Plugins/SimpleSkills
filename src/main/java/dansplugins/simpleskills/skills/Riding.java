package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.AbstractMovementSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 23:48
 */
public class Riding extends AbstractMovementSkill {

    /**
     * The Riding skill is levelled through riding entities.
     */
    public Riding() {
        super("Riding");
    }

    /**
     * Method to obtain the skill type for the skill.
     *
     * @return {@link MovementSkillType}
     */
    @Override
    public MovementSkillType getSkillType() {
        return MovementSkillType.RIDING;
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
        if (!(player.getVehicle() instanceof Creature)) return;
        final Creature vehicle = (Creature) player.getVehicle();
        if (vehicle.hasPotionEffect(PotionEffectType.SPEED)) vehicle.removePotionEffect(PotionEffectType.SPEED);
        vehicle.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1, true, false));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        player.sendMessage("§bYour companion has received a §aSpeed Boost§b!");
    }

}
