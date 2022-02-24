package dansplugins.simpleskills.skills;

import com.cryptomorin.xseries.XMaterial;
import dansplugins.simpleskills.AbstractBlockSkill;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.services.LocalMessageService;
import dansplugins.simpleskills.utils.ChanceCalculator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Callum Johnson
 * @since 09/01/2022 - 16:34
 */
public class Pyromaniac extends AbstractBlockSkill {

    /**
     * The pyromaniac skill is levelled through fire :)
     */
    public Pyromaniac() {
        super("Pyromaniac");
    }

    /**
     * Method to determine if the item provided is valid.
     *
     * @param item        to check.
     * @param targetBlock to do sub-checks with.
     * @param context     of which the event happened.
     * @return {@code true} if it is.
     */
    @Override
    public boolean isRequiredItem(@NotNull ItemStack item, @NotNull Block targetBlock, @NotNull String context) {
        return item.getType() == XMaterial.FLINT_AND_STEEL.parseMaterial() || item.getType() == XMaterial.FIRE_CHARGE.parseMaterial();
    }

    /**
     * Method to determine if an item is required to be in the hand of the participant.
     *
     * @return {@code true} if it is.
     */
    @Override
    public boolean isItemRequired() {
        return true;
    }

    /**
     * Method to determine if the Block skill exclusivity.
     *
     * @return {@link BlockSkillType} for this skill.
     */
    @Override
    public @NotNull BlockSkillType getBlockSkillType() {
        return BlockSkillType.PLACE_SPECIFIC;
    }

    /**
     * Method to determine if the Block material is valid for this Skill.
     *
     * @param material to test.
     * @return {@code true} if it is.
     */
    @Override
    public boolean isValidMaterial(@NotNull Material material) {
        return true;
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
        player.sendMessage(LocalMessageService.getInstance().convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang().getString("Skills.Pyromaniac"))));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 5, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 320, 5, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 320, 2, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 640, 5, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0, false, false));
    }

}
