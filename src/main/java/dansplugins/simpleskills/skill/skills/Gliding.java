package dansplugins.simpleskills.skill.skills;

import com.cryptomorin.xseries.XMaterial;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.abs.AbstractMovementSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.logging.Logger;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 12/01/2022 - 00:01
 */
public class Gliding extends AbstractMovementSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Gliding skill is levelled by gliding with an elytra.
     */
    public Gliding(ConfigService configService, Logger logger, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, logger, playerRecordRepository, simpleSkills, messageService, "Gliding");
        this.chanceCalculator = chanceCalculator;
    }

    /**
     * Method to obtain the skill type for the skill.
     *
     * @return {@link MovementSkillType}
     */
    @Override
    public MovementSkillType getSkillType() {
        return MovementSkillType.GLIDING;
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
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        final ItemStack stack = new ItemStack(Objects.requireNonNull(XMaterial.FIREWORK_ROCKET.parseItem()));
        final ItemMeta itemMeta = stack.getItemMeta();
        if (itemMeta == null) return;
        if (!(itemMeta instanceof FireworkMeta)) return;
        final FireworkMeta meta = (FireworkMeta) itemMeta;
        meta.addEffect(FireworkEffect.builder().withFlicker().withColor(Color.PURPLE).withTrail().build());
        meta.setPower(3);
        stack.setItemMeta(meta);
        player.getInventory().addItem(stack);
        player.sendMessage(messageService.convert(Objects.requireNonNull(messageService.getlang().getString("Skills.Gliding"))));
    }

}
