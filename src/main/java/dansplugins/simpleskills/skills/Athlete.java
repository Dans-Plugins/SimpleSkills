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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 12/01/2022 - 00:00
 */
public class Athlete extends AbstractMovementSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Athlete skill is levelled by swimming.
     * @param chanceCalculator
     */
    public Athlete(ConfigService configService, Logger logger, PersistentData persistentData, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, persistentData, simpleSkills, messageService, "Athlete");
        this.chanceCalculator = chanceCalculator;
    }

    /**
     * Method to obtain the skill type for the skill.
     *
     * @return {@link MovementSkillType}
     */
    @Override
    public MovementSkillType getSkillType() {
        return MovementSkillType.SWIMMING;
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
        if (player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 600, 1, true, false));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Athlete"))));
    }

}
