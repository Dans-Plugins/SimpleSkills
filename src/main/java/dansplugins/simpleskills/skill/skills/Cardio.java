package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.abs.AbstractMovementSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.logging.Logger;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 23:45
 */
public class Cardio extends AbstractMovementSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Cardio skill is levelled by sprinting around!
     * @param chanceCalculator
     */
    public Cardio(ConfigService configService, Logger logger, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, playerRecordRepository, simpleSkills, messageService, "Cardio");
        this.chanceCalculator = chanceCalculator;
    }

    /**
     * Method to obtain the skill type for the skill.
     *
     * @return {@link MovementSkillType}
     */
    @Override
    public MovementSkillType getSkillType() {
        return MovementSkillType.SPRINTING;
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
        if (player.hasPotionEffect(PotionEffectType.SPEED)) player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1, true, false));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Cardio"))));
    }

}
