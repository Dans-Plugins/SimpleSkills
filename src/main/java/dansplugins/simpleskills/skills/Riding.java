package dansplugins.simpleskills.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.ConfigService;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.skills.abs.AbstractMovementSkill;
import dansplugins.simpleskills.utils.ChanceCalculator;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 23:48
 */
public class Riding extends AbstractMovementSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Riding skill is levelled through riding entities.
     */
    public Riding(ConfigService configService, Logger logger, PersistentData persistentData, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, persistentData, simpleSkills, messageService, "Riding");
        this.chanceCalculator = chanceCalculator;
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
        if (!chanceCalculator.roll(record, this, 0.10)) return;
        if (!(player.getVehicle() instanceof Creature)) return;
        final Creature vehicle = (Creature) player.getVehicle();
        if (vehicle.hasPotionEffect(PotionEffectType.SPEED)) vehicle.removePotionEffect(PotionEffectType.SPEED);
        vehicle.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1, true, false));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Riding"))));
    }

}
